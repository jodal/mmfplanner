/*
 * $Id: MmfNode.java 1403 2007-11-17 14:19:58Z erikbagg $
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
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.util.GuiUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Draw the given MMF within the given bounds. This does not need to listen for
 * events from the Mmf, ProjectGraphNode will handle invalidation and layout.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class MmfNode extends PNode {
	private static final long serialVersionUID = 1L;

	public static final Stroke STROKE_PLAIN = new BasicStroke(1.0f);
	public static final Stroke STROKE_SELECTED = new BasicStroke(2.0f);

	public static final double WIDTH = 130.0;
	public static final double HEIGHT = WIDTH / (Math.E);
	public static final double PADDING_WIDTH = WIDTH * 0.4;
	public static final double PADDING_HEIGHT = HEIGHT * 0.4;
	public static final double PADDING_TEXT = 5.0;
	public static final double RUND_RADIUS = 20.0;

	public static final Font FONT_ID = new Font("Dialog", Font.BOLD, 14);
	public static final Font FONT_NAME = new Font("Dialog", Font.PLAIN, 12);

	private Mmf mmf;

	public MmfNode(Mmf mmf) {
		super();
		this.mmf = mmf;
	}

	/**
	 * Paints a MMF node, with the given category color as background color,
	 * with a border wich is rounded at the corners. Draws the ID of the MMF in
	 * the upper left corner. Draws the name of the mmf centered in the node.
	 * Chopos of the name if its too long to fit in the node.
	 *
	 * @see GuiUtil#drawCenteredString()
	 */
	@Override
	protected void paint(PPaintContext paintContext) {
		Graphics2D g2 = paintContext.getGraphics();

		RoundRectangle2D rect = new RoundRectangle2D.Double(getX(), getY(),
				getWidth(), getHeight(), RUND_RADIUS, RUND_RADIUS);

		// rectangle
		Color rectBackgroundColor;
		if ((null != mmf.getCategory()) && (null != mmf.getCategory().getColor())) {
			rectBackgroundColor = mmf.getCategory().getColor();
		} else {
			rectBackgroundColor = Color.WHITE;
		}

		g2.setColor(rectBackgroundColor);
		g2.fill(rect);
		g2.setStroke(STROKE_PLAIN);
		g2.setColor(Color.BLACK);
		g2.draw(rect);

		g2.setColor(GuiUtil.getBlackWhiteColor(rectBackgroundColor));
		g2.setFont(FONT_ID);

		// draw id
		double idWidth = g2.getFont().getStringBounds(mmf.getId(),
				g2.getFontRenderContext()).getWidth()
				+ PADDING_TEXT + 2;
		double idHeight = g2.getFont().getStringBounds(mmf.getId(),
				g2.getFontRenderContext()).getHeight();
		g2.drawString(mmf.getId(), (float) (getX() + PADDING_TEXT),
				(float) (getY() + idHeight));

		// draw name
		Rectangle2D nameRectangle = new Rectangle2D.Double(getX() + idWidth,
				getY(), getWidth() - idWidth, getHeight());
		g2.setFont(FONT_NAME);
		GuiUtil.drawCenteredString(g2, nameRectangle, mmf.getName());
	}

	public Mmf getMmf() {
		return this.mmf;
	}
}