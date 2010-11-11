/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.model;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 * TableColumnModel used to give preferred columns widths relative to column
 * position. By default all columns are 100 in width, and an array of column
 * indexes and preferred widths to override this can be given. Negative indexes
 * are counted from the last column (0 is the first column, 1 is the second, -1
 * is the last, -2 the second to last, etc.).
 *
 * Whenever a new column is added, all column widths are reset according to the
 * given input. This TableColumnModel works best together with
 * JTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS).
 */
public class RelativeTableColumnModel extends DefaultTableColumnModel {
    private static final long serialVersionUID = 1L;

    private int[] columns;
    private int[] widths;

    /**
     * Constructs a model with the given parameters. See the class description
     * for a description of how these work.
     *
     * @param columns array of column indexes, negative indexes are from the
     *        last row
     * @param widths
     */
    public RelativeTableColumnModel(int[] columns, int[] widths) {
        super();
        if (columns.length != widths.length) {
            throw new IllegalArgumentException();
        }
        this.columns = columns;
        this.widths = widths;
    }

    /**
     * Whenever a new column is added we resize all the columns.
     */
    @Override
    public void addColumn(TableColumn column) {
        super.addColumn(column);
        resizeColumns();
    }

    /**
     * Whenever a column is removed we resize all the columns
     */
    @Override
    public void removeColumn(TableColumn column) {
        super.removeColumn(column);
        resizeColumns();
    }

    /**
     * Will resize all the columns according to columns[] and widths[]. See the
     * class description for how these are used.
     */
    private void resizeColumns() {
        // set all values to 100
        int count = getColumnCount();
        int columnWidth[] = new int[count];
        for (int i = 0; i < count; i++) {
            columnWidth[i] = 100;
        }

        // find values better values from columns[] and widths[]
        for (int i = 0; i < columns.length; i++) {
            int col = (columns[i] >= 0 ? columns[i] : count + columns[i]);
            if ((0 <= col) && (count > col)) {
                columnWidth[col] = widths[i];
            }
        }

        // apply new values to all columns
        for (int i = 0; i < count; i++) {
            TableColumn column = getColumn(i);
            if (columnWidth[i] < column.getMinWidth()) {
                column.setMinWidth(columnWidth[i]);
            }
            if (columnWidth[i] != column.getPreferredWidth()) {
                column.setPreferredWidth(columnWidth[i]);
            }
        }
    }
}
