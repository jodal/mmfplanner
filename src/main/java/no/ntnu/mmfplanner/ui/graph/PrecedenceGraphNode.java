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
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Double;
import java.util.HashMap;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.util.TangoColor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Handles layout of MMFs and PrecursorNodes.
 *
 * Paints white/gray areas to separate each period. The first period is drawn in
 * white, the second in gray, etc. The width of a period is given by
 * MmfNode.WIDTH + MmfNode.PADDING_WIDTH.
 */
public class PrecedenceGraphNode extends ProjectGraphNode {
    private static final long serialVersionUID = 1L;

    public PrecedenceGraphNode(Project project) {
        super(project);
    }

    private static final Color PERIOD_COLOR_EVEN = Color.WHITE;
    private static final Color PERIOD_COLOR_ODD = TangoColor.ALUMINIUM_1;

    @Override
    protected void invalidateModel() {
        // XXX: should we update only the relevant nodes? in a project with
        // dependencies this could be quite a lot of nodes anyway -bagge
        removeAllChildren();

        // add all MMFs
        HashMap<Mmf, MmfNode> mmfNodes = new HashMap<Mmf, MmfNode>();
        for (int i = 0; i < project.size(); i++) {
            Mmf mmf = project.get(i);

            MmfNode node = new MmfNode(mmf);
            addChild(node);
            mmfNodes.put(mmf, node);
        }

        // add all precursors
        for (Mmf target : mmfNodes.keySet()) {
            for (Mmf source : target.getPrecursors()) {
                PrecursorNode node = new PrecursorNode(mmfNodes.get(source),
                        mmfNodes.get(target));
                addChild(0, node);
            }
        }
    }

    @Override
    protected void layoutChildren() {
        int maxSwimlane = 0;
        for (int i = 0; i < getChildrenCount(); i++) {
            PNode node = getChild(i);
            if (node instanceof MmfNode) {
                layoutNode((MmfNode) node);
                maxSwimlane = Math.max(maxSwimlane, ((MmfNode) node).getMmf()
                        .getSwimlane());
            }
        }

        for (int i = 0; i < getChildrenCount(); i++) {
            PNode node = getChild(i);
            if (node instanceof PrecursorNode) {
                ((PrecursorNode) node).updateLine();
            }
        }

        // set bounds
        double width = project.getPeriods()
                * (MmfNode.WIDTH + MmfNode.PADDING_WIDTH);
        double height = (maxSwimlane + 1)
                * (MmfNode.HEIGHT + MmfNode.PADDING_HEIGHT);
        setBounds(0, 0, width, height);
    }

    private void layoutNode(MmfNode node) {
        Mmf mmf = node.getMmf();
        double x = (mmf.getPeriod() - 1)
                * (MmfNode.WIDTH + MmfNode.PADDING_WIDTH)
                + MmfNode.PADDING_WIDTH / 2;
        node.setX(x);
        double y = (mmf.getSwimlane() - 1)
                * (MmfNode.HEIGHT + MmfNode.PADDING_HEIGHT)
                + MmfNode.PADDING_HEIGHT / 2;
        node.setY(y);
        node.setWidth(MmfNode.WIDTH);
        node.setHeight(MmfNode.HEIGHT);

    }

    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

        PBounds viewBounds = paintContext.getCamera().getViewBounds();
        // double height = Math.max(getHeight(), viewBounds.getY() +
        // viewBounds.getHeight());
        double height = getHeight();
        double y = Math.min(0, viewBounds.getY());

        // Draw rectangular background
        double periodWidth = MmfNode.WIDTH + MmfNode.PADDING_WIDTH;
        int maxX = (int) Math.ceil(getWidth() / periodWidth);
        Rectangle2D r = new Rectangle2D.Double();

        for (int x = 0; x < maxX; x++) {
            g2.setColor(x % 2 == 0 ? PERIOD_COLOR_EVEN : PERIOD_COLOR_ODD);
            r.setRect(x * periodWidth, y, periodWidth, height);
            g2.fill(localToParent(r));
        }

        // Draw swimlane lines
        double swimlaneHeight = MmfNode.HEIGHT + MmfNode.PADDING_HEIGHT;
        int maxY = (int) Math.ceil(height / swimlaneHeight);
        double x = Math.min(0, viewBounds.getX());
        double width = Math.max(getWidth(), viewBounds.getX()
                + viewBounds.getWidth());
        r = new Rectangle2D.Double();
        Double l = new Line2D.Double();
        for (y = 0; y < maxY; y++) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            l.setLine(x, y * swimlaneHeight, width, y * swimlaneHeight);
            g2.draw(l);
        }

        // Shade periods outside Project settings periods
        if (width - (project.getPeriods() * periodWidth) > 0) {
            g2.setColor(Color.GRAY);
            r.setRect(project.getPeriods() * periodWidth, 0, width
                    - (project.getPeriods() * periodWidth), height);
            g2.fill(localToParent(r));
        }
    }
}
