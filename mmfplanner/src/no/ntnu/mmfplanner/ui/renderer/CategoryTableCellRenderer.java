/*
 * $Id$
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */
package no.ntnu.mmfplanner.ui.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import no.ntnu.mmfplanner.ui.model.CategoryTableModel;

/**
 * CellRenderer for use in the category table.
 *
 * @version $Revision$
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class CategoryTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public CategoryTableCellRenderer() {
        super();
    }

    /**
     * Returns right aligned numbers and help text for last column
     *
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // use the default values from the parent
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);

        // Right align numbers
        if(value instanceof Number) {
            this.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            this.setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        // Set white background color
        setBackground(Color.WHITE);

        // Last name column should have help text
        int nameColumn = ((CategoryTableModel) table.getModel()).findColumn(CategoryTableModel.COLUMN_NAME);
        if((nameColumn == table.convertColumnIndexToModel(column)) && (row == table.getRowCount()-1)) {
            this.setText("Enter name of new category here");
        }

        return this;
    }
}