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

import no.ntnu.mmfplanner.util.TangoColor;

/**
 * CellRenderer for use in Revenue Table.
 */
public class RevenueTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public RevenueTableCellRenderer() {
        super();
    }

    /**
     * Returns a correct JLabel for a Revenue table
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

        // First column should have Aluminium background color
        if ((table.convertColumnIndexToModel(column) == 0) && !isSelected) {
            this.setBackground(TangoColor.ALUMINIUM_1);
        } else if (!isSelected) {
            this.setBackground(table.getBackground());
        }

        // Right align numbers
        if(value instanceof Number) {
            this.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            this.setHorizontalAlignment(SwingConstants.LEFT);
        }

        return this;
    }
}
