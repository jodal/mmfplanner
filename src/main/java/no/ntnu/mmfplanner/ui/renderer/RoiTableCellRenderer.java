/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import no.ntnu.mmfplanner.util.TangoColor;

/**
 * Renderer for use in RoiTable or any other table where the leftmost and
 * rightmost columns are to be highlighted with bold text.
 *
 * This also right align all columns except the leftmost as these contain mostly
 * numbers. "X" is centered and bold-faced.
 */
public class RoiTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    /**
     * Returns a JLabel with bold text for the leftmost and rightmost columns as
     * well as "X".
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

        // change the bold value if not set correctly
        boolean bold = (((0 == column) && (row > table.getRowCount() - 8))
                || (table.getColumnCount() - 1 == column)
                || (row == table.getRowCount() - 2)
                || (row == table.getRowCount() - 7) || "X".equals(value));

        if (bold != ((getFont().getStyle() & Font.BOLD) == Font.BOLD)) {
            setFont(table.getFont().deriveFont((bold ? Font.BOLD : Font.PLAIN)));
        }

        // only the left column is left-aligned, all others are right-aligned
        if (0 == column) {
            setHorizontalAlignment(SwingConstants.LEFT);
        } else if ("X".equals(value)) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        // calculated data is gray
        if (!isSelected) {
            boolean gray = (row >= table.getRowCount() - 7);
            setBackground((gray ? TangoColor.ALUMINIUM_1 : Color.WHITE));
        }
        return this;
    }
}
