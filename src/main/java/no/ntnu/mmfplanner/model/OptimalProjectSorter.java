/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.util.List;

import no.ntnu.mmfplanner.util.ProjectSorterUtil;

/**
 * This class uses brute-force to find the optimal NPV of a project. This is the
 * only method that will give an accurate list of all the top X results.
 *
 * Due to the high complexity of O(n!), this is fairly slow on large projects.
 * The class has been optimized some, and as such it will now handle up to about
 * 13-15 MMFs in a project with several precursors. Without any precursors any
 * more than 10 MMFs will take some time to complete.
 */
public class OptimalProjectSorter extends ProjectSorter {
    private int[][] sanpv;
    private int maxMmfs;
    private int maxPeriod;
    private int mmfLengths[];
    private int mmfPrecursors[][];

    /**
     * @param project
     */
    public OptimalProjectSorter(Project project) {
        super(project);
    }

    /**
     * Perform the actual brute-force sorting. Uses chooseNext() and
     * handleFinishedOrder() to find and add the actual results.
     */
    @Override
    protected void sort() {
        // initialize fields
        mmfLengths = new int[project.size()];
        mmfPrecursors = new int[project.size()][];
        int initialCount[] = new int[project.getPeriods()];
        int initialPeriods[] = new int[project.size()];
        int initialNpv = 0;
        int unusedCount = project.size();
        sanpv = project.getSaNpvTable();
        maxMmfs = project.getMaxMmfsPerPeriod();
        maxPeriod = project.getPeriods();
        boolean mmfUsed[] = new boolean[project.size()];

        // find information about all mmfs in order to save work later
        // specifically we find the information for locked mmfs here, as these
        // can not be moved later anyway
        for (int i = 0; i < project.size(); i++) {
            Mmf mmf = project.get(i);

            // if the project has no revenue we can ignore it
            boolean hasRevenue = false;
            for (int p = 1; p <= mmf.getRevenueLength(); p++) {
                if (mmf.getRevenue(p) != 0) {
                    hasRevenue = true;
                    break;
                }
            }
            if (!hasRevenue) {
                initialPeriods[i] = maxPeriod + 1;
                mmfUsed[i] = true;
                unusedCount--;
                continue;
            }

            // find development length (minimum 1)
            mmfLengths[i] = mmf.getPeriodCount();

            // precursor matrix
            List<Mmf> pre = mmf.getPrecursors();
            if (pre.size() > 0) {
                mmfPrecursors[i] = new int[pre.size()];
                for (int j = 0; j < mmfPrecursors[i].length; j++) {
                    mmfPrecursors[i][j] = project.getMmfs().indexOf(pre.get(j));
                }
            }

            // locked MMFs are added directly to initial* and marked as used
            if (mmf.isLocked()) {
                initialPeriods[i] = mmf.getPeriod();
                for (int p = initialPeriods[i] - 1; p < initialPeriods[i]
                        + mmfLengths[i] - 1; p++) {
                    initialCount[p]++;
                }
                if (initialPeriods[i] <= maxPeriod) {
                    initialNpv += sanpv[i][initialPeriods[i] - 1];
                }
                mmfUsed[i] = true;
                unusedCount--;
            }
        }

        setProgressMax(alternativeCount(unusedCount));

        chooseNext(mmfUsed, unusedCount, new int[unusedCount], 0, initialCount,
                initialPeriods, new int[] { 1, initialNpv });
    }

    /**
     * Returns the number of alternative orderings for a project with size MMFs.
     * This does not take into account precursors or locked MMFs.
     *
     * @param size the number of unlocked MMFs
     * @return the number of alternative orderings
     */
    private long alternativeCount(int size) {
        long result = 1;
        if (size > 0) {
            result = 2 * ProjectSorterUtil.factorial(size);
        }
        if (size >= 2) {
            long emptyAdd = 1;
            for (int i = 3; i <= size; i++) {
                emptyAdd = emptyAdd * i + 1;
            }
            result += emptyAdd;
        }
        return result;
    }

    /**
     * Tries all possible orderings for the given level, calling itself
     * recursively for subsequent levels.
     */
    private void chooseNext(boolean mmfUsed[], int unusedCount, int order[],
            int orderLen, int initialCount[], int initialPeriods[],
            int initialPeriod_npv[]) {
        if (isStopFlag()) {
            throw new RuntimeException("Stop flag set");
        }

        // copy initial data and add order
        int count[] = new int[initialCount.length];
        System.arraycopy(initialCount, 0, count, 0, count.length);
        int periods[] = new int[initialPeriods.length];
        System.arraycopy(initialPeriods, 0, periods, 0, periods.length);
        int period_npv[] = new int[initialPeriod_npv.length];
        System
                .arraycopy(initialPeriod_npv, 0, period_npv, 0,
                        period_npv.length);
        handleFinishedOrder(order, orderLen, count, periods, period_npv);

        // if we have no mmfs left to place, or there was no room for the last
        // mmf, return
        if (unusedCount <= 0) {
            return;
        } else if (period_npv[0] <= 0) {
            setProgress(getProgress() + alternativeCount(unusedCount) - 1);
            return;
        }

        for (int mmfIndex = 0; mmfIndex < mmfUsed.length; mmfIndex++) {
            if (mmfUsed[mmfIndex]) {
                continue;
            }

            if (!checkPrecursors(mmfIndex, mmfUsed, periods, period_npv)) {
                setProgress(getProgress() + alternativeCount(unusedCount - 1));
                continue;
            }

            mmfUsed[mmfIndex] = true;
            order[orderLen] = mmfIndex;
            chooseNext(mmfUsed, unusedCount - 1, order, orderLen + 1, count,
                    periods, period_npv);
            mmfUsed[mmfIndex] = false;
        }
    }

    /**
     * Check that the ordering is valid, specifically that precursors are before
     * its successor. Returns true of valid, false if invalid.
     */
    private boolean checkPrecursors(int mmfIndex, boolean[] mmfUsed,
            int[] periods, int[] period_npv) {
        if (mmfPrecursors[mmfIndex] != null) {
            for (int i = 0; i < mmfPrecursors[mmfIndex].length; i++) {
                int pre = mmfPrecursors[mmfIndex][i];
                if (!mmfUsed[pre]) {
                    return false;
                }
                period_npv[0] = Math.max(period_npv[0], periods[pre]
                        + mmfLengths[pre]);
            }
        }
        return true;
    }

    /**
     * Find the npv and sequence for the given ordering. Adds it to the result
     * (if npv is high enough).
     *
     * @see ProjectSorter#addResult(int, int[])
     */
    private void handleFinishedOrder(int order[], int orderLen, int count[],
            int periods[], int period_npv[]) {
        setProgress(getProgress() + 1);

        // place the first mmf in period 1
        int period = period_npv[0];
        int npv = period_npv[1];

        if (orderLen > 0) {
            // index and length of current mmf
            int mmfIndex = order[orderLen - 1];
            int mmfLength = mmfLengths[mmfIndex];

            // find the first position where we have room
            for (int p = period - 1; p < period + mmfLength - 1; p++) {
                // if we can't place the mmf, no point in continuing
                if (period + mmfLength > maxPeriod) {
                    period_npv[0] = 0;
                    return;
                }
                // if there is no room, try placing the mmf starting at the next
                // position
                if (count[p] >= maxMmfs) {
                    period = p + 2;
                }
            }

            // place the mff in the given period
            periods[mmfIndex] = period;
            for (int j = period - 1; j < period + mmfLength - 1; j++) {
                count[j]++;
            }
            // increase npv
            npv += sanpv[mmfIndex][period - 1];
        }

        // add the final result
        addResult(npv, periods);

        // update period and npv
        period_npv[0] = period;
        period_npv[1] = npv;
    }
}
