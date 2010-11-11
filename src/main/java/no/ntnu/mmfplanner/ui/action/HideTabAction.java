/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTabbedPane;

import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;

/**
 * Hide the current active panel in the given JTabbedPane.
 */
public class HideTabAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Hide Tab";

    public static final int ACTION_KEY = KeyEvent.VK_H;

    public static final String ACTION_DESCRIPTION = "Hide the current active tab";

    /**
     * When used in a pop-up menu, this is the pane to get the currently selected
     * panel from.
     */
    private JTabbedPane pane;

    private TabPanePanelPlacement panePlacement;

    public HideTabAction(TabPanePanelPlacement panePlacement, JTabbedPane pane) {
        super(null, ACTION_NAME, ACTION_KEY, null, ACTION_DESCRIPTION);
        this.panePlacement = panePlacement;
        this.pane = pane;
    }

    /**
     * Tells the TabPanePanelPlacement object to hide the active panel in the
     * given pane.
     *
     */
    public void actionPerformed(ActionEvent e) {
        // get the currently selected panel
        Component panel = pane.getSelectedComponent();
        String id = panePlacement.getPanelId(panel);
        panePlacement.setVisible(id, false);
    }
}