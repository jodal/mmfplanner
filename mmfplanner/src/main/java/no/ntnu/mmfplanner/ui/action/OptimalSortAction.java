/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import no.ntnu.mmfplanner.model.OptimalProjectSorter;
import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.ui.SortDialog;

/**
 * Starts a {@link OptimalProjectSorter} instance as a new thread, and opens the {@link SortDialog}
 */
public class OptimalSortAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Optimal Sort...";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_O;

    public static final int ACTION_KEY = KeyEvent.VK_O;

    public static final String ACTION_DESCRIPTION = "Use optimal sort to get the optimal NPV";

    public OptimalSortAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_KEY, null, ACTION_DESCRIPTION);

    }

    /**
     * Start the brute force sorter
     */
    public void actionPerformed(ActionEvent e) {
        OptimalProjectSorter optimalSorter = new OptimalProjectSorter(mainFrame
                .getProject());
        SortDialog sortDialog = new SortDialog(mainFrame, enabled, optimalSorter);
        optimalSorter.start(true);
        sortDialog.setVisible(true);

    }

}
