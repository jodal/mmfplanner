/*
 * $Id: CategoryTableModel.java 1406 2007-11-17 14:44:28Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;

/**
 *
 * TableModel for the category table on project properties panel.
 *
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class CategoryTableModel extends AbstractTableModel implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    // Column header names
    public final static String COLUMN_NAME = "Name";
    public final static String COLUMN_COLOR = "Color";
    public final static String COLUMN_PARENT = "Parent category";

    public static String COLUMNS[] = new String[] { COLUMN_NAME, COLUMN_COLOR,
            COLUMN_PARENT };

    private Project project;

    private Category newCategory;

    public CategoryTableModel(Project project) {
        this.project = project;
        project.addPropertyChangeListener(this);

        this.newCategory = new Category();
    }

    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    public int getRowCount() {
        return project.getCategorySize() + 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Category rowCategory;
        if (rowIndex < getRowCount() - 1) {
            rowCategory = project.getCategory(rowIndex);
        } else {
            rowCategory = newCategory;
        }

        // Get column name from columnIndex
        String colName = getColumnName(columnIndex);

        if (COLUMN_NAME.equals(colName)) {
            return rowCategory.getName();
        } else if (COLUMN_COLOR.equals(colName)) {
            return rowCategory.getColor();
        } else if (COLUMN_PARENT.equals(colName)) {
            return rowCategory.getParent();
        } else {
            return "INVALID COLUMN INDEX";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Get column name from columnIndex
        String colName = getColumnName(columnIndex);

        if (COLUMN_NAME.equals(colName)) {
            return String.class;
        } else if (COLUMN_COLOR.equals(colName)) {
            return Color.class;
        } else if (COLUMN_PARENT.equals(colName)) {
            return Category.class;
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() - 1) {
            if ((null == value) || ("".equals(value))) {
                return;
            }
            project.addCategory(newCategory);
            newCategory = new Category();
        }
        Category rowCategory = project.getCategory(rowIndex);

        // Get column name from columnIndex
        String colName = getColumnName(columnIndex);

        // Set correct value
        if (COLUMN_NAME.equals(colName)) {
            rowCategory.setName((String) value);
        } else if (COLUMN_COLOR.equals(colName)) {
            rowCategory.setColor((Color) value);
        } else if (COLUMN_PARENT.equals(colName)) {
            try {
                rowCategory.setParent((Category) value);
            } catch (MmfException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid parent category selected\n" + e.getMessage(),
                        "Invalid parent category", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return ((0 == columnIndex) || (rowIndex < getRowCount() - 1));
    }

    public void propertyChange(PropertyChangeEvent arg0) {
        fireTableDataChanged();
    }

}