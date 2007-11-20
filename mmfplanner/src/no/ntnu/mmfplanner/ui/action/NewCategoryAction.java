/*
 * $Id: NewCategoryAction.java 1023 2007-10-30 10:02:58Z jodal $
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

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;

/**
 *
 * Adds a MMF to the project
 *
 * @version $Revision: 1023 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class NewCategoryAction extends MainAbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "New Category";

    public static final int ACTION_MNEMONIC = KeyEvent.VK_C;

    public static final String ACTION_DESCRIPTION = "Add a new category to current project";

    public NewCategoryAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_MNEMONIC, null, ACTION_DESCRIPTION);
    }

    public void actionPerformed(ActionEvent e) {
        Project project = mainFrame.getProject();
        project.addCategory(new Category("Category "
                + (project.getCategorySize() + 1), null, null));
        mainFrame.getTabPanePanelPlacement().setVisible("projectPropPanel",
                true);
    }
}
