/*
 * $Id: SortTableModel.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import no.ntnu.mmfplanner.model.ProjectSorter;
import no.ntnu.mmfplanner.model.ProjectSorter.Result;

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
public class SortTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    // From the book: NPV Rank, Sequence, NPV, % Optimal, Loss
    public static final String COLUMN_NPV_RANK = "NPV Rank";
    public static final String COLUMN_SEQUENCE = "Sequence";
    public static final String COLUMN_NPV = "NPV";
    public static final String COLUMN_PERCENT_OPTIMAL = "% Optimal";
    public static final String COLUMN_LOSS = "Loss";

    public static String COLUMNS[] = new String[] { COLUMN_NPV_RANK,
            COLUMN_SEQUENCE, COLUMN_NPV, COLUMN_PERCENT_OPTIMAL, COLUMN_LOSS };

    private ProjectSorter sorter;

    private List<Result> lastResult;

    private String initialSequence;

    /**
     * Constructor for the model
     *
     * @param sorter The sorter this will work as a wrapper for
     */
    public SortTableModel(ProjectSorter sorter, String initialSequence) {
        super();
        this.sorter = sorter;
        this.initialSequence = initialSequence;
    }

    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * The number of rows is equal to the number of results in lastResult list.
     */
    public int getRowCount() {
        if (lastResult == null) {
            return 0;
        } else {
            return lastResult.size() + 1;
        }
    }

    /**
     * Gets the results for each row from lastResult list.
     *
     * @param rowIndex
     * @param columnIndex
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        Result result;

        // initial result
        if (rowIndex == 0) {
            if (COLUMN_NPV_RANK.equals(columnName)) {
                return "Current";
            } else if (COLUMN_SEQUENCE.equals(columnName)) {
                return initialSequence;
            }
        } else {
            result = lastResult.get(rowIndex - 1);
            if (COLUMN_NPV_RANK.equals(columnName)) {
                return rowIndex;
            } else if (COLUMN_SEQUENCE.equals(columnName)) {
                return result.sequence;
            } else if (COLUMN_NPV.equals(columnName)) {
                return result.npv;
            } else if (COLUMN_PERCENT_OPTIMAL.equals(columnName)) {
                return (result.optimal == 0.0 ? null : (int) (result.optimal * 100)
                        + "%");
            } else if (COLUMN_LOSS.equals(columnName)) {
                return (result.loss == 0 ? null : result.loss);
            }
        }
        return null;
    }

    /**
     * Method for getting the class of what is in the cell in the table
     *
     * @param columnIndex
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object value = getValueAt(0, columnIndex);
        return (value == null ? Object.class : value.getClass());
    }

    /**
     * Retrieves the editable status of the cell. No cells are editable.
     *
     * @param rowIndex
     * @param columnIndex
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Is called to notify that it should check for new results. Normally called
     * by a timer, but could also use a refresh button.
     */
    public void update() {
        List<Result> newResult = sorter.getResults();
        if (!newResult.equals(lastResult) || sorter.isDone()) {
            lastResult = newResult;
            fireTableDataChanged();
        }
    }
}
