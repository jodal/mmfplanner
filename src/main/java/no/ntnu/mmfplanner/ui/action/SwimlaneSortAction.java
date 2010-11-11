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

import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.util.ProjectSorterUtil;

/**
 * Action for performing a swimlane sort, see {@link ProjectSorterUtil#sortSwimlanes(no.ntnu.mmfplanner.model.Project)}
 */
public class SwimlaneSortAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Pretty Sort";

    public static final int ACTION_KEY = KeyEvent.VK_R;

    public static final String ACTION_DESCRIPTION = "Use pretty sort algorithm to place the MMFs in a visually appealing way";

    public SwimlaneSortAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_KEY, null, ACTION_DESCRIPTION);
    }

    /**
     * Start the swimlane sorter
     */
    public void actionPerformed(ActionEvent e) {
        ProjectSorterUtil.sortSwimlanes(mainFrame.getProject());
    }

}
