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

import no.ntnu.mmfplanner.ui.TabPanePanelPlacement;
import no.ntnu.mmfplanner.ui.TabPanePanelPlacement.PanelInfo;

/**
 * Toggle the visibility of a panel defined in MainFrame's availablePanels[].
 */
public class ToggleTabAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_DESCRIPTION = "Show/hide the given tab";

    TabPanePanelPlacement panePanel;

    String panelId;

    String title;

    public ToggleTabAction(TabPanePanelPlacement panePanel, String id, String title) {
        super(null, title, 0, null, ACTION_DESCRIPTION);
        this.panePanel = panePanel;
        this.panelId = id;
        this.title = title;
    }

    /**
     * Ask TabPanePanelPlacement to either show or hide the given panel.
     *
     */
    public void actionPerformed(ActionEvent e) {
        // get the currently selected panel
        PanelInfo info = panePanel.getPanelInfo(panelId);
        panePanel.setVisible(panelId, !info.visible);
    }
}