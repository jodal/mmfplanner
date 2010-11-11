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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.MainFrame;

/**
 * Deletes the category/categories currently selected in
 * mainFrame.categoryTable.
 */
public class DeleteCategoryAction extends MainAbstractAction implements
        ListSelectionListener {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_NAME = "Delete category";

    public static final int ACTION_KEY = KeyEvent.VK_D;

    public static final String ACTION_DESCRIPTION = "Delete the selected category";

    public DeleteCategoryAction(MainFrame mainFrame) {
        super(mainFrame, ACTION_NAME, ACTION_KEY, null, ACTION_DESCRIPTION);
        mainFrame.getCategoryTable().getSelectionModel()
                .addListSelectionListener(this);
        valueChanged(null);
    }

    public void actionPerformed(ActionEvent e) {
        // Get categoryTable from mainFrame and find the selected rows,
        // then delete the corresponding categories from the current project
        JTable table = mainFrame.getCategoryTable();
        Project project = mainFrame.getProject();
        int[] selectedRows = table.getSelectedRows();

        List<Category> categoriesToDelete = new ArrayList<Category>();
        for (int selectedRow : selectedRows) {
            // Not supported in JDK5
            // selectedRow = table.convertRowIndexToModel(selectedRow);
            if (selectedRow >= project.getCategorySize()) {
                continue;
            }
            categoriesToDelete.add(project.getCategory(selectedRow));
        }

        for (Category category : categoriesToDelete) {
            project.removeCategory(category);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        JTable table = mainFrame.getCategoryTable();
        boolean enabled = (table.getSelectedRowCount() > 0)
                && (table.getSelectedRow() < table.getRowCount() - 1);
        setEnabled(enabled);
    }

}
