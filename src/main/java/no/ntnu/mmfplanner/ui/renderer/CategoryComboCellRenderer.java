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

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.util.GuiUtil;

/**
 * Used as a renderer for both table cells and comboboxes. Specifically in
 * MainFrame.mmfTable and MainFrame.categoryTable. Displays colored background
 * according to the given category as well as the name of the category.
 */
public class CategoryComboCellRenderer extends JLabel implements
        ListCellRenderer, TableCellRenderer {
    private static final long serialVersionUID = 1L;

    /**
     * Creates the initial component
     */
    public CategoryComboCellRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    /**
     * Sets the property of a component to be used in example a JComboBox
     *
     * @return a Component to be used in a ListCell
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Category category = (Category) value;
        Color backgroundColor;

        if ((category == null) || (category.getColor() == null)) {
            backgroundColor = Color.WHITE;
        } else {
            backgroundColor = category.getColor();
        }

        setBackground(backgroundColor);
        setForeground(GuiUtil.getBlackWhiteColor(backgroundColor));

        if ((category == null) || (category.getName() == null)
                || category.getName().equals("")) {
            setText("None");
        } else {
            setText(category.getName());
        }

        if (isSelected) {
            setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
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
        Category category = (Category) value;
        Color backgroundColor;

        if ((category == null) || (category.getColor() == null)) {
            backgroundColor = table.getBackground();
        } else {
            backgroundColor = category.getColor();
        }

        if (isSelected) {
            backgroundColor = table.getSelectionBackground();
        }

        setBackground(backgroundColor);
        setForeground(GuiUtil.getBlackWhiteColor(backgroundColor));

        if (category == null) {
            if (row == table.getRowCount() - 1) {
                setText("");
            } else {
                setText("Click to choose a category");
            }
        } else {
            setText(category.getName());
        }
        setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));

        return this;
    }

}
