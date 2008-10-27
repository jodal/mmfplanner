/*
 * $Id: PrecursorSortAction.java 1406 2007-11-17 14:44:28Z erikbagg $
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

import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.util.ProjectSorterUtil;

/**
 *
 * Action for performing a precursor sort, see {@link ProjectSorterUtil#sortDependencies(no.ntnu.mmfplanner.model.Project)}
 *
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class PrecursorSortAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Precursor Sort";

    public static final int ACTION_KEY = KeyEvent.VK_P;

    public static final String ACTION_DESCRIPTION = "Use precursor sort algorithm to place all MMFs after it's precursors";

    public PrecursorSortAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_KEY, null, ACTION_DESCRIPTION);
    }

    /**
     * Start the precursor sorter
     */
    public void actionPerformed(ActionEvent e) {
        ProjectSorterUtil.sortDependencies(mainFrame.getProject());
    }

}
