/*
 * $Id: ProjectSorterUtil.java 1523 2007-11-19 15:46:59Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectSorter;

/**
 * Helper methods for use by {@link ProjectSorter} classes, as well as helper
 * functions for sorting by precursor and pretty sorting swimlanes.
 * 
 * @version $Revision: 1523 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class ProjectSorterUtil {

    /**
     * At least try to have some basic logical swimlane placement.
     * 
     * @param project
     */
    public static void sortSwimlanes(Project project) {

        int maxPeriod = 0;
        for (Mmf mmf : project.getMmfs()) {
            if (mmf.getPeriod() > maxPeriod) {
                maxPeriod = mmf.getPeriod();
            }
        }
        int swimlanePositions[] = new int[maxPeriod];

        // Set all initial swimlanePositions to one
        for (int i = 0; i < swimlanePositions.length; i++) {
            swimlanePositions[i] = 1;
        }

        ArrayList<Mmf> placedMmf = new ArrayList<Mmf>();

        List<Mmf> mmfs = new ArrayList<Mmf>();

        for (Mmf mmf : project.getMmfs()) {
            if (mmf.getPrecursors().size() == 0) {
                mmfs.add(mmf);
            }
        }
        mmfs = sortMmfsByPeriodDesc(mmfs);
        Collections.reverse(mmfs);
        for (Mmf mmf : mmfs) {
            placeStrand(mmf, placedMmf, swimlanePositions, project);
        }

    }

    private static void placeStrand(Mmf mmf, ArrayList<Mmf> placedMmf,
            int[] swimlanePositions, Project project) {
        List<Mmf> children = new ArrayList<Mmf>();
        int max = mmf.getPeriod();
        for (Mmf child : project.getMmfs()) {
            if (!placedMmf.contains(child)
                    && child.getPrecursors().contains(mmf)) {
                children.add(child);
                if (child.getPeriod() > max) {
                    max = child.getPeriod();
                }
            }
        }
        int swimlane = 1;
        for (int i = mmf.getPeriod() - 1; i < max; i++) {
            if (swimlanePositions[i] > swimlane) {
                swimlane = swimlanePositions[i];
            }
        }

        for (int i = mmf.getPeriod(); i < max; i++) {
            swimlanePositions[i] = swimlane;
        }

        swimlanePositions[mmf.getPeriod() - 1] = swimlane + 1;

        children = sortMmfsByPeriodDesc(children);
        for (Mmf child : children) {
            placeStrand(child, placedMmf, swimlanePositions, project);
            for (int i = mmf.getPeriod(); i < child.getPeriod(); i++) {
                swimlanePositions[i] = swimlanePositions[child.getPeriod() - 1];
            }
        }
        if ((children.size() > 0)
                && (swimlanePositions[mmf.getPeriod() - 1] == swimlane + 1)) {
            swimlane = children.get(0).getSwimlane();
            swimlanePositions[mmf.getPeriod() - 1] = swimlane + 1;
        }

        try {
            if (swimlane != mmf.getSwimlane()) {
                mmf.setSwimlane(swimlane);
            }
            placedMmf.add(mmf);
        } catch (MmfException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sort a precursor list descending by the period each mmf should be
     * implemented in.
     * 
     * @param prelist List of mmfs to sort
     * @return
     */
    private static List<Mmf> sortMmfsByPeriodDesc(List<Mmf> prelist) {
        if (prelist.size() < 2) {
            return prelist;
        }
        ArrayList<Mmf> sorted = new ArrayList<Mmf>();

        for (Mmf aMmf : prelist) {
            int i;
            for (i = 0; (i < sorted.size())
                    && (sorted.get(i).getPeriod() > aMmf.getPeriod()); i++) {
            }
            sorted.add(i, aMmf);
        }
        return sorted;
    }

    public static void sortDependencies(Project project) {
        List<Mmf> mmfs = project.getMmfs();
        int periods[] = new int[mmfs.size()];
        int mmfLengths[] = new int[mmfs.size()];

        // find all mmf lengths
        for (int i = 0; i < mmfs.size(); i++) {
            Mmf mmf = mmfs.get(i);
            periods[i] = 1;
            mmfLengths[i] = 1;
            for (int p = 2; (p <= mmf.getRevenueLength())
                    && (mmf.getRevenue(p) < 0); p++) {
                mmfLengths[i]++;
            }
        }
        
        // find periods for all mmfs
        boolean modified = true;
        while (modified) {
            modified = false;
            for (int i = 0; i < mmfs.size(); i++) {
                Mmf mmf = mmfs.get(i);
                if (mmf.isLocked()) {
                    continue;
                }
                int maxPeriod = periods[i];
                for (Mmf pre : mmf.getPrecursors()) {
                    int preIdx = mmfs.indexOf(pre);
                    if (preIdx >= 0) {
                        maxPeriod = Math.max(maxPeriod, periods[preIdx] + mmfLengths[preIdx]);
                    }
                }
                if (maxPeriod > periods[i]) {
                    periods[i] = maxPeriod;
                    modified = true;
                }
            }
        }
        
        // update period on all mmfs
        for (int i = 0; i < mmfs.size(); i++) {
            try {
                Mmf mmf = mmfs.get(i);
                mmf.setPeriod(periods[i]);
            } catch (MmfException e) {
                e.printStackTrace();
            }
        }
    }

    public static long factorial(long i) {
        return (i <= 1 ? 1 : i * factorial(i - 1));
    }

}
