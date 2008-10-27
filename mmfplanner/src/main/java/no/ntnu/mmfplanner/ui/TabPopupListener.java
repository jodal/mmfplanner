/*
 * $Id: TabPopupListener.java 1403 2007-11-17 14:19:58Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui;

import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;


/**
 *
 * Can be assigned as a mouseListener to JTabbedPanes,
 * to show a popupmenu only when click is registered on an actual
 * tab, and not on the entire JTabbedPane.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class TabPopupListener extends PopupListener {

	public TabPopupListener(JPopupMenu menu) {
		super(menu);
	}

	/**
	 * Show popupmenu if the click was triggered at an actual tab with a tabindex,
	 * and ignore if not.
	 */
	@Override
	protected void showPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	JTabbedPane clickedPane = (JTabbedPane) e.getComponent();
        	int clickedTabIndex = clickedPane.getUI().tabForCoordinate(
        			clickedPane, e.getX(), e.getY());

        	if(clickedTabIndex >= 0) {
        	    clickedPane.setSelectedIndex(clickedTabIndex);
        		popupMenu.show(e.getComponent(), e.getX(), e.getY());
        	}
        }
    }

}