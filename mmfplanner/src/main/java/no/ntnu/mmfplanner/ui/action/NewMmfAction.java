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

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.ui.MainFrame;

/**
 * Adds a MMF to the project
 */
public class NewMmfAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "New MMF";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_M;

    public static final String ACTION_ACCELERATOR = "ctrl M";

    public static final String ACTION_DESCRIPTION = "Add a new MMF to current project";

    public NewMmfAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, ACTION_ACCELERATOR, ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        Mmf mmf = new Mmf(null, "");
        mainFrame.getProject().add(mmf);
        mmf.setName("MMF " + mmf.getId());
        mainFrame.getTabPanePanelPlacement().setVisible("mmfTablePanel", true);
    }
}
