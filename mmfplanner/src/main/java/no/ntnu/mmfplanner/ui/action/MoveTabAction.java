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

import no.ntnu.mmfplanner.ui.MainFrame;

/**
 * Action to move the selected component from one pane to another. The component
 * to move must be defined in MainFrame.availablePanels.
 *
 * The pane to move components to, must also have a defined position index in
 * the MainFrame.
 */
public class MoveTabAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME_PREFIX = "Move tab to ";
    public static final String ACTION_NAME_SUFFIX = " pane";

    public static final int ACTION_KEY = KeyEvent.VK_M;

    /**
     * The pane to move a currently selected component from
     */
    private JTabbedPane fromPane;

    /**
     * The pane to move a selected component to. Should have a corresponding
     * position index defined in MainFrame.
     */
    private String toPlacement;

    public MoveTabAction(MainFrame mainFrame, JTabbedPane fromPane,
            String toPlacement) {
        super(mainFrame, ACTION_NAME_PREFIX + toPlacement + ACTION_NAME_SUFFIX,
                ACTION_KEY, null, ACTION_NAME_PREFIX + toPlacement
                        + ACTION_NAME_SUFFIX);
        this.toPlacement = toPlacement;
        this.fromPane = fromPane;
    }

    /**
     * Get selected component, and let the mainframe's placement orderer move
     * the component.
     */
    public void actionPerformed(ActionEvent e) {
        Component selectedComponent = fromPane.getSelectedComponent();
        String panelId = mainFrame.getTabPanePanelPlacement().getPanelId(
                selectedComponent);
        mainFrame.getTabPanePanelPlacement().movePanel(panelId, toPlacement);
    }

}
