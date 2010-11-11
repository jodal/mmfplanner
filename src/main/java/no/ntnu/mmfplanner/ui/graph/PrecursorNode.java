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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Draws a precursor arrow between two MmfNodes in the precursor graph.
 */
public class PrecursorNode extends PNode {
    private static final long serialVersionUID = 1L;

    private static final int[] END_DELTA = new int[] { 0, 7, 12, 16, 19, 21, 22 };

    private static final BasicStroke STROKE_ARROW = new BasicStroke(2,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    private MmfNode source, target;

    private Line2D line;

    private Polygon arrowHead;

    public PrecursorNode(MmfNode source, MmfNode target) {
        super();
        this.source = source;
        this.target = target;

        setPickable(false);
    }

    public void updateLine() {
        PBounds sourceBounds = source.getBounds();
        PBounds targetBounds = target.getBounds();
        double x1, y1, x2, y2;

        if (Math.abs(sourceBounds.getCenterX() - targetBounds.getCenterX()) < 1.0) {
            // source and target on the same vertical position
            x1 = x2 = sourceBounds.getCenterX();
            y1 = sourceBounds.getMaxY();
            y2 = targetBounds.getMinY();
            y2 -= STROKE_ARROW.getLineWidth();

            if (y1 > y2) {
                y1 = sourceBounds.getMinY();
                y2 = targetBounds.getMaxY();
                y2 += STROKE_ARROW.getLineWidth();
            }
        } else {
            // source and target in different vertical positions
            x1 = sourceBounds.getMaxX();
            y1 = sourceBounds.getCenterY();
            x2 = targetBounds.getMinX();
            y2 = targetBounds.getCenterY();
            x2 -= STROKE_ARROW.getLineWidth();

            if (x1 > x2) {
                x1 = sourceBounds.getMinX();
                x2 = targetBounds.getMaxX();
                x2 += STROKE_ARROW.getLineWidth();
            }

            // move the ends a bit if we are not in the same vertical position
            int ydi = (int) Math.round((y2 - y1)
                    / (MmfNode.HEIGHT + MmfNode.PADDING_HEIGHT));
            double yd = (ydi < 0 ? -1 : 1)
                    * END_DELTA[Math.min(Math.abs(ydi), END_DELTA.length - 1)];
            y1 += yd;
            y2 -= yd;
        }

        line = new Line2D.Double(x1, y1, x2, y2);
        arrowHead = getArrowHead((int) x1, (int) y1, (int) x2, (int) y2);

        Rectangle bounds = line.getBounds();
        bounds.add(arrowHead.getBounds());
        setBounds(bounds);
    }

    public MmfNode getSource() {
        return this.source;
    }

    public MmfNode getTarget() {
        return this.target;
    }

    @Override
    protected void paint(PPaintContext pc) {
        Graphics2D g2 = pc.getGraphics();
        g2.setColor(Color.BLACK);
        g2.setStroke(STROKE_ARROW);
        g2.draw(line);

        g2.draw(arrowHead);
        g2.fill(arrowHead);
    }

    public static Polygon getArrowHead(int xFrom, int yFrom, int x, int y) {
        double aDir = Math.atan2(xFrom - x, yFrom - y);

        Polygon tmpPoly = new Polygon();
        double i1 = 9; // + (int) (stroke * 2);
        double i2 = 4.5; // + (int) stroke;
        tmpPoly.addPoint(x, y); // arrow tip
        tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
        tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
        tmpPoly.addPoint(x, y); // arrow tip
        return tmpPoly;
    }

    private static int yCor(double i2, double dir) {
        return (int) (i2 * Math.cos(dir));
    }

    private static int xCor(double i2, double dir) {
        return (int) (i2 * Math.sin(dir));
    }
}
