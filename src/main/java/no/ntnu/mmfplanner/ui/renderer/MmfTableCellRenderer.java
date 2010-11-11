/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */
package no.ntnu.mmfplanner.ui.renderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import no.ntnu.mmfplanner.ui.model.MmfTableModel;
import no.ntnu.mmfplanner.util.TangoColor;

/**
 * CellRenderer for use in MMF Table.
 */
public class MmfTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public MmfTableCellRenderer() {
        super();
    }

    /**
     * Returns a correct JLabel for a MmfTable
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

        // First column should have aluminium background color
        if (!isSelected && (table.convertColumnIndexToModel(column) == 0)) {
            this.setBackground(TangoColor.ALUMINIUM_1);
        } else if (!isSelected) {
            this.setBackground(table.getBackground());
        }

        // Numbers should be right aligned
        if(value instanceof Number) {
            this.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            this.setHorizontalAlignment(SwingConstants.LEFT);
        }

        // Precursors column should have help text when empty
        int precursorsColumn = ((MmfTableModel) table.getModel()).findColumn(MmfTableModel.COLUMN_PRECURSORS);
        if ((precursorsColumn == table.convertColumnIndexToModel(column)) && "".equals(value)) {
            this.setText("Enter precursor IDs");
        }

        // Last row in Name column should have help text when empty
        int nameColumn = ((MmfTableModel) table.getModel()).findColumn(MmfTableModel.COLUMN_NAME);
        if((nameColumn == table.convertColumnIndexToModel(column)) && (row == table.getRowCount()-1)) {
            this.setText("Enter name of new MMF here");
        }

        return this;
    }


}
