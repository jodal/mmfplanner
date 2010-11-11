/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Node that handles links between different nodes in the decomposition graph.
 * Links are drawn has straight-edged lines.
 */
public class LinkNode extends PNode {
    private static final long serialVersionUID = 1L;

    private static final Stroke STROKE_LINE = new BasicStroke(2,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    private PNode source, target;

    private GeneralPath line;

    public LinkNode(PNode source, PNode target) {
        super();
        this.source = source;
        this.target = target;

        setPickable(false);
        updateLine();
    }

    /**
     * update the line with the bounds given by source and target
     */
    public void updateLine() {
        float x1 = (float) source.getBounds().getCenterX();
        float y1 = (float) source.getBounds().getMaxY();
        float x2 = (float) target.getBounds().getCenterX();
        float y2 = (float) target.getBounds().getMinY();
        float y12 = (y2 + y1) / 2;

        line = new GeneralPath();
        line.moveTo(x1, y1);
        if (x1 != x2) {
            line.lineTo(x1, y12);
            line.lineTo(x2, y12);
        }
        line.lineTo(x2, y2);
        setBounds((x1 < x2 ? x1 : x2), y1, (x1 < x2 ? x2 - x1 : x1 - x2) + 1,
                y2 - y1);
    }

    public PNode getSource() {
        return this.source;
    }

    public PNode getTarget() {
        return this.target;
    }

    @Override
    protected void paint(PPaintContext pc) {
        Graphics2D g2 = pc.getGraphics();
        g2.setPaint(Color.BLACK);
        g2.setStroke(STROKE_LINE);

        g2.draw(line);
    }
}
