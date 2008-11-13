/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

/**
 * This is a class for displaying the Sequense adjusted net present value for a
 * project in a table
 *
 * If the project changes the roitable is set to null; it is only calculated if
 * the user points to its tab and wants to see it
 *
 * @see no.ntnu.mmfplanner.model.Project#getSaNpvTable() Here is the actual
 *      calculations done
 */

public class SaNpvTableModel extends AbstractTableModel implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    public final static String COLUMN_MMF = "MMF";

    private Project project;

    private int[][] sanpv;

    /**
     * Constructor for class
     *
     * @param project
     */
    public SaNpvTableModel(Project project) {
        super();
        this.project = project;
        project.addPropertyChangeListener(this);
    }

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
        return (column == 0 ? COLUMN_MMF : "" + column);
    }

    public int getRowCount() {
        return project.size();
    }

    /**
     * Method for getting the SaNpv value for a mmf at a period
     *
     * If column is 0 returns id and name for the mmf.
     *
     * @see no.ntnu.mmfplanner.model.Project#getSaNpvTable()
     * @param rowIndex the index of the row (mmf)
     * @param columnIndex the index of the column (period)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (null == sanpv) {
            sanpv = project.getSaNpvTable();
        }
        if (0 >= columnIndex) {
            Mmf mmf = project.get(rowIndex);
            return mmf.getId() + ": " + mmf.getName();
        } else {
            return sanpv[rowIndex][columnIndex - 1];
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex > 0 ? Integer.class : String.class);
    }

    /**
     * Method for changes. Sets SaNpv to null since we may need to calculate the
     * SaNpv over again
     */
    public void propertyChange(PropertyChangeEvent evt) {
        sanpv = null;
        if (Project.EVENT_PERIODS.equals(evt.getPropertyName())) {
            fireTableStructureChanged();
        } else {
            fireTableDataChanged();
        }
    }
}
