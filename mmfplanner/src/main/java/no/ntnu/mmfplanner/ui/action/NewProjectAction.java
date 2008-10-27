/*
 * $Id: NewProjectAction.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import javax.swing.JTabbedPane;

import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement.PanelInfo;

/**
 * Create a new project and replace the existing one.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class NewProjectAction extends MainAbstractAction {

    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "New Project";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_N;

    public static final String ACTION_ACCELERATOR = "ctrl N";

    public static final String ACTION_DESCRIPTION = "Create a new project";

    public NewProjectAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, ACTION_ACCELERATOR,
                ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        boolean cancel = mainFrame.queryProjectCloseSave();
        if (cancel) {
            return;
        }

        mainFrame.setModel(new Project());

        // Set all tabs visible
        for (PanelInfo panelInfo : mainFrame.getTabPanePanelPlacement().getPanels()) {
            mainFrame.getTabPanePanelPlacement().setVisible(panelInfo.id, true);
        }

        // Select the first tab on all panes
        for (JTabbedPane tabbedPane : mainFrame.getTabPanePanelPlacement().getPanes()) {
            tabbedPane.setSelectedIndex(0);
        }
    }

}
