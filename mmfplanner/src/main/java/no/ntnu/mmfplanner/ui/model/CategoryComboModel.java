/*
 * $Id: CategoryComboModel.java 1406 2007-11-17 14:44:28Z erikbagg $
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

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

/**
 * 
 * ComboBoxModel for use in the category table in properties panel, as well as
 * the MMF table.
 * 
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class CategoryComboModel extends AbstractListModel implements
        ComboBoxModel, PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    Project project;
    Category selected;

    public CategoryComboModel(Project project) {
        this.project = project;
        project.addPropertyChangeListener(this);
    }

    public Object getSelectedItem() {
        return selected;
    }

    public void setSelectedItem(Object arg0) {
        selected = (Category) arg0;
    }

    public Object getElementAt(int position) {
        if (position == 0) {
            return null;
        } else {
            return project.getCategory(position - 1);
        }
    }

    public int getSize() {
        return project.getCategorySize() + 1;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Mmf.EVENT_CATEGORY)) {
            selected = (Category) evt.getNewValue();
            fireContentsChanged(this, 0, getSize());
        } else if (evt.getPropertyName().equals(Project.EVENT_CATEGORIES)) {
            fireContentsChanged(this, 0, getSize());
        }
    }

}
