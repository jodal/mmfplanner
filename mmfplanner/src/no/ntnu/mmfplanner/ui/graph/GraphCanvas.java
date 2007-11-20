/*
 * $Id: GraphCanvas.java 1406 2007-11-17 14:44:28Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.graph;

import no.ntnu.mmfplanner.model.Project;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Canvas for a project graph. Creates a root node of the given type.
 * 
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class GraphCanvas extends PCanvas {
    private static final long serialVersionUID = 1L;

    public static final int GRAPH_TYPE_DECOMPOSITION = 1;

    public static final int GRAPH_TYPE_PRECEDENCE = 2;

    private ProjectGraphNode projectNode;

    public GraphCanvas() {
        super();

        removeInputEventListener(getPanEventHandler());
        removeInputEventListener(getZoomEventHandler());

        setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);

    }

    public void setModel(Project project, int graphType) {
        if (null != projectNode) {
            getLayer().removeChild(projectNode);
        }

        if (GRAPH_TYPE_DECOMPOSITION == graphType) {
            projectNode = new DecompositionGraphNode(project);
        } else if (GRAPH_TYPE_PRECEDENCE == graphType) {
            projectNode = new PrecedenceGraphNode(project);
        } else {
            throw new IllegalArgumentException();
        }
        getLayer().addChild(projectNode);
    }
}
