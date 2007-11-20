/*
 * $Id: MmfTableModel.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.MmfException;
import no.ntnu.mmfplanner.model.Project;

/**
 * Table model used as a wrapper for MMFs for use in JTable. This class handles
 * changes between model and GUI. Works like an adapter for this two elements
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class MmfTableModel extends AbstractTableModel implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_PERIOD = "Period";
    public static final String COLUMN_LOCKED= "Locked";
    public static final String COLUMN_PRECURSORS = "Precursors";
    public static final String COLUMN_CATEGORY = "Category";

    public static String COLUMNS[] = new String[] { COLUMN_ID, COLUMN_NAME,
            COLUMN_PERIOD, COLUMN_LOCKED, COLUMN_PRECURSORS,
            COLUMN_CATEGORY };

    private Project project;

    private Mmf newMmf;

    /**
     * Constructor for the model
     *
     * @param project The project this will work as a wrapper for
     */
    public MmfTableModel(Project project) {
        super();
        this.project = project;
        project.addPropertyChangeListener(this);

        this.newMmf = new Mmf(null, "");
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * The number of rows is equal to the number of MMFs in the project plus one
     * for the "new" MMF at the last row.
     */
    public int getRowCount() {
        return project.size() + 1;
    }

    /**
     * Method for handling changes in underlying model Inserts the right values
     * from the model to the GUI. For the "new" MMF, only null values are
     * returned.
     *
     * @param rowIndex
     * @param columnIndex
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= getRowCount() - 1) {
            return null;
        }
        Mmf mmf = project.get(rowIndex);

        String columnName = getColumnName(columnIndex);
        if (COLUMN_ID.equals(columnName)) {
            return mmf.getId();
        } else if (COLUMN_NAME.equals(columnName)) {
            return mmf.getName();
        } else if (COLUMN_PERIOD.equals(columnName)) {
            return mmf.getPeriod();
        } else if (COLUMN_LOCKED.equals(columnName)) {
            return mmf.isLocked();
        } else if (COLUMN_PRECURSORS.equals(columnName)) {
            return mmf.getPrecursorString();
        } else if (COLUMN_CATEGORY.equals(columnName)) {
            return mmf.getCategory();
        } else {
            return "INVALID COLUMN INDEX";
        }
    }

    /**
     * Method for handling changes in the GUI and updating the model. Changes to
     * the last row will cause the "new" MMF to be added to the project, and a
     * new object set as "new" MMF.
     *
     * Ignores any attempt to set id.
     *
     * @param value
     * @param rowIndex
     * @param columnIndex
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {

        if (rowIndex >= getRowCount() - 1) {
            if ((null == value) || ("".equals(value))) {
                return;
            }
            project.add(newMmf);
            this.newMmf = new Mmf(null, "");
        }
        Mmf mmf = project.get(rowIndex);

        String columnName = getColumnName(columnIndex);
        if (COLUMN_ID.equals(columnName)) {
            // we ignore attempts to set ID
            // mmf.setId((String) value);
        } else if (COLUMN_NAME.equals(columnName)) {
            mmf.setName((String) value);
        } else if (COLUMN_PERIOD.equals(columnName)) {
            try {
                mmf.setPeriod((Integer) value);
            } catch (MmfException e) {
                JOptionPane.showMessageDialog(null, "Period was not valid\n"
                        + e.getMessage(), "Invalid period",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if (COLUMN_LOCKED.equals(columnName)) {
            mmf.setLocked((Boolean) value);
        } else if (COLUMN_PRECURSORS.equals(columnName)) {
            try {
                mmf.setPrecursorString((String) value);
            } catch (MmfException e) {
                JOptionPane.showMessageDialog(null, "Precursor was not valid\n"
                        + e.getMessage(), "Invalid precursor",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if (COLUMN_CATEGORY.equals(columnName)) {
            try {
                mmf.setCategory((Category) value);
            } catch (MmfException e) {
                JOptionPane.showMessageDialog(null, "Category was not valid\n"
                        + e.getMessage(), "Invalid category",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Method for getting the class of what is in the cell in the table
     *
     * @param columnIndex
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (COLUMN_CATEGORY.equals(columnName)) {
            return Category.class;
        } else {
            Object value = getValueAt(0, columnIndex);
            return (value == null ? Object.class : value.getClass());
        }
    }

    /**
     * Retrieves the editable status of the cell. All cells except id are
     * editable, except for the last column where only name can be edited.
     *
     * @param rowIndex
     * @param columnIndex
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        return !COLUMN_ID.equals(columnName)
                && ((COLUMN_NAME.equals(columnName) || (rowIndex < getRowCount() - 1)));
    }

    public void propertyChange(PropertyChangeEvent evt) {
        fireTableDataChanged();
    }

}
