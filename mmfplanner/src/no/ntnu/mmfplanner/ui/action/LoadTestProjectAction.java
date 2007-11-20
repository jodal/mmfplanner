/*
 * $Id: LoadTestProjectAction.java 1403 2007-11-17 14:19:58Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;

/**
 * Loads a test project with associated data. This is the project used to
 * calculate the ROI table on page 87 in Software by Numbers, and is used when
 * demonstrating the ROI table.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class LoadTestProjectAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Load Test Project";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_T;

    public static final String ACTION_ACCELERATOR = "ctrl T";

    public static final String ACTION_DESCRIPTION = "Loads a test project";

    public LoadTestProjectAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, ACTION_ACCELERATOR,
                ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        boolean cancel = mainFrame.queryProjectCloseSave();
        if (cancel) {
            return;
        }

        int[][] inRevenue = new int[][] {
                new int[] { -50, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                        10, 10, 10, 10 },
                new int[] { -20, 10, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 0, 0 },
                new int[] { -20, -20, 10, 13, 16, 19, 22, 25, 25, 25, 25, 25,
                        25, 25, 25, 25 },
                new int[] { -20, -20, 10, 14, 18, 22, 26, 30, 34, 38, 40, 40,
                        40, 40, 40, 40 },
                new int[] { -50, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35,
                        35, 35, 35, 35 } };
        int[] inPeriod = new int[] { 3, 4, 1, 1, 3 };
        int[] inSwimlane = new int[] { 1, 1, 1, 2, 2 };

        // setup project
        Project project = new Project();
        project
                .setName("ROI and Discounted Cash Flow of Parallel Sequence (p.87)");
        try {
            for (int i = 0; i < inRevenue.length; i++) {
                Mmf mmf = new Mmf("" + (char) ('A' + i), "MMF "
                        + (char) ('A' + i));
                mmf.setPeriod(inPeriod[i]);
                mmf.setSwimlane(inSwimlane[i]);
                for (int j = 0; j < inRevenue[i].length; j++) {
                    mmf.setRevenue(j + 1, inRevenue[i][j]);
                }
                project.add(mmf);
            }
            project.get("E").addPrecursor(project.get("D"));
            project.get("B").addPrecursor(project.get("A"));
            project.setPeriods(16);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame,
                    "An error occured while loading project:\n" + ex, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        mainFrame.setModel(project);
    }

}
