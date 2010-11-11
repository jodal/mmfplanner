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
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import no.ntnu.mmfplanner.util.GuiUtil;

/**
 * Used as a renderer for both table cells and comboboxes. Specifically in
 * MainFrame.categoryTable. Displays colored background according to the given
 * color, with the name of the color (or a hexadecimal value if no name is
 * found).
 */
public class ColorComboCellRenderer extends JLabel implements ListCellRenderer,
        TableCellRenderer {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the initial component
     */
    public ColorComboCellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    /**
     * Sets the property of a component to be used in example a JComboBox
     *
     * @return a Component to be used in a ListCell
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            setBackground(Color.WHITE);
            setText("None");
            return this;
        }

        Color color = (Color) value;
        String colorName = GuiUtil.getColorName(color);
        setText(colorName);
        setBackground(color);

        setForeground(GuiUtil.getBlackWhiteColor(color));

        if (isSelected) {
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        }

        return this;
    }

    /**
     * Renders a JLabel with the correct background color according to input
     * value
     *
     * @return a Component to be used in a TableCell
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Color color;
        if (value == null) {
            color = table.getBackground();
            if (row == table.getRowCount() - 1) {
                setText("");
            } else {
                setText("Click to choose a color");
            }
        } else {
            color = (Color) value;
            String colorName = GuiUtil.getColorName(color);
            setText(colorName);
        }

        if (isSelected) {
            color = table.getSelectionBackground();
        }

        setBackground(color);
        setForeground(GuiUtil.getBlackWhiteColor(color));

        return this;
    }
}