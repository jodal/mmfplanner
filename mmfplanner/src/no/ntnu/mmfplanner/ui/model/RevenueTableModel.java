/*
 * $Id: RevenueTableModel.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

/**
 *
 * This class handles changes between revenue model and the GUI.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */

public class RevenueTableModel extends AbstractTableModel implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    public final static String COLUMN_MMF = "MMF";

    private Project project;

    /**
     * Constructor for class
     *
     * @param project
     */
    public RevenueTableModel(Project project) {
        super();
        this.project = project;
        project.addPropertyChangeListener(this);
    }

    /**
     * Method for returning columnCounts
     *
     * Increments numbers of project periods (name of mmf)
     *
     * @see no.ntnu.mmfplanner.model.Project#getPeriods()
     */
    public int getColumnCount() {
        return project.getPeriods() + 1;
    }

    /**
     * Method for determining the name of the column
     *
     * @param column The number of the column determines the name
     */
    @Override
    public String getColumnName(int column) {
        return (column == 0 ? COLUMN_MMF : "+" + column);
    }

    public int getRowCount() {
        return project.size();
    }

    /**
     * Method for getting the revenue value for a mmf at a period
     *
     * If column is 0 returns id and name for the mmf.
     *
     * @param rowIndex the index of the row (mmf)
     * @param columnIndex the index of the column (period)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mmf mmf = project.get(rowIndex);
        if (0 >= columnIndex) {
            return mmf.getId() + ": " + mmf.getName();
        } else if (columnIndex > mmf.getRevenueLength()) {
            return null;
        } else {
            return mmf.getRevenue(columnIndex);
        }
    }

    /**
     * Method for setting the revenue value for a mmf in a period
     *
     * Sets only if columnIndex is higher than 0
     *
     * @param value the revenue value
     * @param rowIndex the index of the row (mmf)
     * @param columnIndex the index of the column (period)
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex > 0) {
            Mmf mmf = project.get(rowIndex);
            mmf.setRevenue(columnIndex, (Integer) value);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex > 0 ? Integer.class : String.class);
    }

    /**
     * Method for checking if cell is editable
     *
     * All cells except first column are editable
     *
     * @param rowIndex the index of the row
     * @param columnIndex the index of the column
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex >= 1;
    }

    /**
     * Method for changes
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (Project.EVENT_PERIODS.equals(evt.getPropertyName())) {
            fireTableStructureChanged();
        } else {
            fireTableDataChanged();
        }
    }
}
