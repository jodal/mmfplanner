/*
 * $Id: CategoryNode.java 1403 2007-11-17 14:19:58Z erikbagg $
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.util.GuiUtil;
import no.ntnu.mmfplanner.util.TangoColor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Draw the given Category within the given bounds. This does not need to listen
 * for events from the Category, ProjectGraphNode will handle invalidation and
 * layout.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class CategoryNode extends PNode {
    private static final long serialVersionUID = 1L;

    public static final double WIDTH = 130.0;
    public static final double HEIGHT = WIDTH / Math.E;
    public static final double PADDING_WIDTH = WIDTH * 0.2;
    public static final double PADDING_HEIGHT = HEIGHT * 0.6;

    public static final Stroke STROKE_PLAIN = new BasicStroke(1.0f);
    public static final Stroke STROKE_SELECTED = new BasicStroke(2.0f);

    public static final Font FONT_NAME = new Font("Dialog", Font.PLAIN, 12);

    private Category category;

    public CategoryNode(Category category) {
        super();
        this.category = category;
    }

    /**
     * Paints a Category node, with the given category color as background color
     * and a black border. Draws the name of category mmf centered in the node.
     * Chopos of the name if its too long to fit in the node.
     *
     * @see GuiUtil#drawCenteredString()
     */
    @Override
    protected void paint(PPaintContext paintContext) {
        Graphics2D g2 = paintContext.getGraphics();

        Rectangle2D rect = new Rectangle2D.Double(getX(), getY(), getWidth(),
                getHeight());

        Color backColor = TangoColor.ALUMINIUM_2;
        Color fontColor = GuiUtil.getBlackWhiteColor(backColor);

        // rectangle
        g2.setColor(backColor);
        g2.setPaint(new GradientPaint((float) rect.getMinX(), 0, backColor,
                (float) rect.getCenterX(), 0, GuiUtil
                        .getBlackWhiteColor(fontColor), true));
        g2.setPaint(new GradientPaint(0, (float)rect.getMinY(), backColor,
                0, (float)rect.getCenterY(), GuiUtil
                        .getBlackWhiteColor(fontColor), true));
        g2.fill(rect);
        g2.setStroke(STROKE_PLAIN);
        g2.setColor(Color.BLACK);
        g2.draw(rect);

        g2.setFont(FONT_NAME);
        g2.setColor(fontColor);
        GuiUtil.drawCenteredString(g2, rect, category.getName());
    }

    public Category getCategory() {
        return this.category;
    }

}