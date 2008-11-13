/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract class for project sorters.
 */
public abstract class ProjectSorter implements Runnable {
    public static class Result {
        public int periods[];
        public String sequence;
        public int npv;
        public double optimal;
        public int loss;

        @Override
        public String toString() {
            return sequence + ": " + npv + " " + Arrays.toString(periods);
        }
    }

    /**
     * Update the sequence string, but only if not already set
     *
     * @param r the result to update the sequence string for
     */
    public void updateSequence(Result r) {
        if (r.sequence != null) {
            return;
        }

        StringBuffer sb = new StringBuffer(r.periods.length);
        for (int p = 1; p <= project.getPeriods(); p++) {
            for (int i = 0; i < r.periods.length; i++) {
                if (r.periods[i] == p) {
                    sb.append(project.get(i).getId());
                }
            }
        }
        r.sequence = sb.toString();
    }

    /**
     * Update the optimal and loss values according to maxNpv
     *
     * @param maxNpv the maximal npv among all results
     */
    private static void updateOptimalLoss(Result r, int maxNpv) {
        r.loss = maxNpv - r.npv;
        r.optimal = (double) r.npv / maxNpv;
    }

    public static class ResultComparator implements Comparator<Result> {
        public int compare(Result o1, Result o2) {
            return o2.npv - o1.npv;
        }
    }

    private static final int MAX_RESULTS = 40;

    private static final Comparator<? super Result> RESULT_COMPARATOR = new ResultComparator();

    private List<Result> results;
    protected int minResultNpv;

    private long progress;
    private long progressMax;

    private boolean stopFlag;

    private boolean done;

    protected Project project;

    public ProjectSorter(Project project) {
        this.project = project;

        this.results = new ArrayList<Result>();
        this.progress = 0;
        this.progressMax = 1;
        this.minResultNpv = -1;
    }

    /**
     * Returns how far the sorting progress has come. Should not return the same
     * value as getProgressMax() until isDone() returns true.
     *
     * @return the sort progress between 0 and getProgressMax()
     */
    public synchronized long getProgress() {
        return this.progress;
    }

    /**
     *
     * @return maximal progress value
     */
    public synchronized long getProgressMax() {
        return this.progressMax;
    }

    /**
     * List of ordered results, where the topmost is the most optimal.
     */
    public synchronized List<Result> getResults() {
        if (results.size() > 0) {
            Collections.sort(results, RESULT_COMPARATOR);
            int maxNpv = results.get(0).npv;
            String lastSequence = null;
            int lastNpv = 0;

            for (int i = results.size() - 1; i >= 0; i--) {
                Result result = results.get(i);
                updateSequence(result);

                // remove duplicates (can occur when AB and BA is both placed in
                // the same period)
                if ((result.npv == lastNpv)
                        && result.sequence.equals(lastSequence)) {
                    results.remove(i);
                    continue;
                }
                lastNpv = result.npv;
                lastSequence = result.sequence;

                if (isDone() && !isStopFlag()) {
                    updateOptimalLoss(result, maxNpv);
                }
            }
        }

        return new ArrayList<Result>(results);
    }

    /**
     * @return true if done, false otherwise
     */
    public synchronized boolean isDone() {
        return done;
    }

    /**
     * Adds a new Result with the given values
     *
     * @param npv the total npv of the sequence
     * @param periods the given periods for all mmfs in the project
     */
    public synchronized void addResult(int npv, int periods[]) {
        if (npv <= minResultNpv) {
            return;
        }

        Result result = new Result();
        result.npv = npv;
        result.periods = periods;
        results.add(result);

        // remove result with minimum npv
        if (results.size() > MAX_RESULTS) {
            int min = Integer.MAX_VALUE;
            Result remove = null;
            for (Result r : results) {
                if (r.npv < min) {
                    min = r.npv;
                    remove = r;
                }
            }
            minResultNpv = min;
            results.remove(remove);
        }
    }

    public synchronized void setProgress(long progress) {
        this.progress = progress;
    }

    public synchronized void setProgressMax(long progressMax) {
        this.progressMax = progressMax;
    }

    /**
     * Sets the stop flag, attempting to stop the sorting algorithm while
     * running.
     */
    public synchronized void setStopFlag() {
        this.stopFlag = true;
    }

    public synchronized void setDone(boolean done) {
        this.done = done;
    }

    protected synchronized boolean isStopFlag() {
        return stopFlag;
    }

    public Project getProject() {
        return this.project;
    }

    protected abstract void sort();

    /**
     *
     * @param threaded true if the sorter should run as a thread, false
     *        otherwise.
     */
    public void start(boolean threaded) {
        stopFlag = false;
        setDone(false);
        if (threaded) {
            new Thread(this).start();
        } else {
            run();
        }
    }

    public void run() {
        try {
            sort();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDone(true);
    }
}
