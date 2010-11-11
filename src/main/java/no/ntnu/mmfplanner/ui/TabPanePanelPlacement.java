/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import no.ntnu.mmfplanner.ui.action.ToggleTabAction;

/**
 * Helper class for handling placement of panels on different panes. Currently
 * used for handling which panes are placed on the upper or lower pane, as well
 * as handling visibility.
 */
public class TabPanePanelPlacement {
    public static final int TYPE_INPUT = 1;
    public static final int TYPE_OUTPUT = 2;

    public static final String PLACEMENT_UPPER = "upper";
    public static final String PLACEMENT_LOWER = "lower";

    public static class PanelInfo {
        public String id;
        public String title;
        public int type;
        public String placement;
        public boolean visible;
        public Component panel;
        public JMenuItem menuItem;
    }

    private LinkedHashMap<String, PanelInfo> panels = new LinkedHashMap<String, PanelInfo>();
    private Map<String, JTabbedPane> panes = new HashMap<String, JTabbedPane>();

    private ImageIcon inputIcon, outputIcon;

    private JMenuItem viewMenu;

    public TabPanePanelPlacement(JMenuItem viewMenu) {
        this.viewMenu = viewMenu;

        // load icons from jar
        URL iconUrl = getClass().getClassLoader().getResource("res/input.png");
        if (iconUrl != null) {
            inputIcon = new ImageIcon(iconUrl, "Input");
        }
        iconUrl = getClass().getClassLoader().getResource("res/output.png");
        if (iconUrl != null) {
            outputIcon = new ImageIcon(iconUrl, "Output");
        }
    }

    public void addPane(String placement, JTabbedPane pane) {
        panes.put(placement, pane);
    }

    public void add(String id, String title, int type, String placement,
            boolean visible, Component panel) {
        PanelInfo info = new PanelInfo();
        info.id = id;
        info.title = title;
        info.type = type;
        info.placement = placement;
        info.visible = visible;
        info.panel = panel;

        info.menuItem = new JCheckBoxMenuItem();
        info.menuItem.setAction(new ToggleTabAction(this, info.id, info.title));
        viewMenu.add(info.menuItem);

        panels.put(info.id, info);
        setVisible(info.id, info.visible);
    }

    public String getPanelId(Component panel) {
        for (PanelInfo info : panels.values()) {
            if (panel.equals(info.panel)) {
                return info.id;
            }
        }
        return null;
    }

    public PanelInfo getPanelInfo(String id) {
        return panels.get(id);
    }

    public void setVisible(String id, boolean visible) {
        PanelInfo info = getPanelInfo(id);
        info.visible = visible;
        info.menuItem.setSelected(visible);
        movePanel(id, info.placement);
    }

    public void movePanel(String id, String placement) {
        PanelInfo info = getPanelInfo(id);

        // remove from old panel and update info.placement
        if (info.panel.getParent() != null) {
            info.panel.getParent().remove(info.panel);
        }
        info.placement = placement;

        // if visible and pane exists, add to new pane
        JTabbedPane pane = panes.get(info.placement);
        if (info.visible && (pane != null)) {
            int insertIndex = getTabInsertIndex(pane, info.panel);
            pane.insertTab(info.title, (info.type == TYPE_INPUT ? inputIcon
                    : outputIcon), info.panel, info.title, insertIndex);
            pane.setSelectedIndex(insertIndex);
        }

    }

    /**
     * Get index in TabPane of where to place a new panel
     * 
     * @param tabbedPane The Tabbed Pane that the panel should be inserted to
     * @param panel Panel to get an index to in tabbedPane
     * @return index of where to place panel in tabbedPane
     */
    private int getTabInsertIndex(JTabbedPane tabbedPane, Component panel) {
        ArrayList<Component> indexList = new ArrayList<Component>();
        Iterator<PanelInfo> panelList = panels.values().iterator();

        for (int i = 0; panelList.hasNext(); i++) {
            indexList.add(panelList.next().panel);
        }

        int newPlacementIndex = indexList.indexOf(panel);

        // Loop through all tabs in the tabbedPane
        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            if (newPlacementIndex < indexList.indexOf(tabbedPane
                    .getComponentAt(i))) {
                return i;
            }
        }

        // If index not found, place at end of tabbedPane.
        return tabbedPane.getComponentCount();
    }

    /**
     * Return a read-only collection of panels.
     * 
     * @return
     */
    public Collection<PanelInfo> getPanels() {
        return Collections.unmodifiableCollection(panels.values());
    }

    /**
     * Return a read-only collection of panes.
     * 
     * @return
     */
    public Collection<JTabbedPane> getPanes() {
        return Collections.unmodifiableCollection(panes.values());
    }

    /**
     * Set a pane to show a given index
     * 
     * @param paneId Pane to change the selected tab in
     * @param index index to set selected
     */
    public void setSelectedIndex(String paneId, int index) {
        panes.get(paneId).setSelectedIndex(index);
    }

}
