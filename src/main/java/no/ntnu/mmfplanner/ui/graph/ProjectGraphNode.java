/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.graph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import no.ntnu.mmfplanner.model.Project;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract class that handles a GraphNode describing an entire project. This
 * usually contains nodes for all MMFs and possibly all categories, as well as
 * links or dependencies. The actual implementations are given in
 * DecomopositionGraphNode and PrecedenceGraphNode.
 */
public abstract class ProjectGraphNode extends PNode implements
        PropertyChangeListener {
    private static final long serialVersionUID = 1L;

    protected Project project;

    protected boolean invalidModel;

    public ProjectGraphNode(Project project) {
        super();
        this.project = project;

        project.addPropertyChangeListener(this);
        propertyChange(null);
    }

    /**
     * This is used to update all the nodes when an update occurs to the model.
     * Subclasses should override to invalidate the entire model.
     */
    protected abstract void invalidateModel();

    /**
     * Listener for changes to the project. Subclasses can override this if they
     * want more specialized handling (i.e. not update the model constantly)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        invalidModel = true;
        invalidateFullBounds();
    }

    /**
     * This method is called after a change in the model has caused an
     * invalidateFullBounds() and causes the model to be invalidated by a call
     * to invalidateModel() if a change has occurred.
     */
    @Override
    protected boolean validateFullBounds() {
        if (invalidModel) {
            invalidModel = false;
            invalidateModel();
        }
        return super.validateFullBounds();
    }
}
