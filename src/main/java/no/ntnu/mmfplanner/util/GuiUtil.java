/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Utility functions for the Graphical User Interface.
 */
public class GuiUtil {

    /**
     * Finds the correct grayscale for the given color. Uses human visual
     * priorities on each of the color parts, specifically green is weighted
     * higher and blue lower. This method is deterministic.
     *
     * @param color the color to calculate grayscale for
     * @return a value from 0 to 255 giving the grayscale value of the given
     *         color.
     */
    public static int getGrayscale(Color color) {
        return (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
    }

    /**
     * Returns the appropriate foreground color for the given background color.
     * This uses the getGrayscale() method and returns Color.BLACK for the 3/8
     * darkest colors, and Color.WHITE for the lightest colors.
     *
     * @param color the given background color
     * @return the appropriate foreground/text color
     */
    public static Color getBlackWhiteColor(Color color) {
        return getGrayscale(color) < 96 ? Color.WHITE : Color.BLACK;
    }

    /**
     * Returns a string representation of the given color. For now this contains
     * only names of TangoColors. For all unknown colors a hexadecimal
     * representation is given.
     *
     * @param color
     * @return string representation either as a proper name or hexadecimal
     *         notation.
     */
    public static final String getColorName(Color color) {
        for (int i = 0; i < TangoColor.TANGO_COLORS.length; i++) {
            if (color.equals(TangoColor.TANGO_COLORS[i])) {
                return TangoColor.TANGO_NAMES[i];
            }
        }
        // If color is not found, default to hex color
        return getColorString(color);
    }

    /**
     * Returns a hexadecimal string representation of the given color.
     *
     * @param color
     * @return string representation in hexadecimal notation.
     */
    public static final String getColorString(Color color) {
        return Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
    }

    /**
     * Draws a horizontal/vertical centered string inside a rectangle on a
     * graphics Chops off the string if it too long and adds "..."
     *
     * @param g2 Graphic to draw on
     * @param rectangle Rectangle to draw centered in
     * @param text String to draw
     * @see wrapString()
     */
    public static void drawCenteredString(Graphics2D g2, Rectangle2D rectangle,
            String text) {
        if ((text == null) || "".equals(text)) {
            return;
        }
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        Font font = g2.getFont();
        double fontHeight = font.getStringBounds(text,
                g2.getFontRenderContext()).getHeight();
        int maxLines = (int) (rectangle.getHeight() / fontHeight);
        String[] lines = GuiUtil.wrapString(g2, text, rectangle.getWidth());
        int linesToDraw = Math.min(lines.length, maxLines);

        // initial y-pos
        double yPos = rectangle.getY()
                + (rectangle.getHeight() - linesToDraw * fontHeight) / 2;
        yPos -= fontHeight - g2.getFontMetrics().getMaxAscent();

        // chop lastline and adds "..."
        if (lines.length > maxLines) {
            String lastLine = lines[maxLines - 1];

            lastLine += "...";
            int j = 1;
            while (font.getStringBounds(lastLine, g2.getFontRenderContext())
                    .getWidth() > rectangle.getWidth()) {
                lastLine = lastLine.substring(0, lastLine.length() - 3 - j)
                        + "...";
                j++;
            }
            lines[maxLines - 1] = lastLine;
        }

        // draw lines
        for (int i = 0; i < linesToDraw; i++) {
            double lineWidth = g2.getFont().getStringBounds(lines[i],
                    g2.getFontRenderContext()).getWidth();
            yPos += fontHeight;
            double xPos = rectangle.getX()
                    + (int) ((rectangle.getWidth() - lineWidth) / 2);
            g2.drawString(lines[i], (float) (xPos), (float) yPos);
        }
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Wraps a string according to width
     *
     * @param g2 Graphic to get font from
     * @param string String to wrap
     * @param width Width the string must fit into
     * @return
     */
    public static String[] wrapString(Graphics2D g2, String string, double width) {
        FontRenderContext frc = g2.getFontRenderContext();
        Font font = g2.getFont();

        // Split string into tokens, split by spaces
        StringTokenizer st = new StringTokenizer(string, " ");

        ArrayList<String> wrappedString = new ArrayList<String>();

        int i;
        while (st.hasMoreTokens()) {
            i = wrappedString.size() - 1;
            String nextToken = st.nextToken();

            // Place next token on current line if possible,
            // else add a new line with the token.
            if ((i > -1)
                    && (font.getStringBounds(
                            wrappedString.get(i) + " " + nextToken, frc)
                            .getWidth() < width)) {
                String nextLineString = wrappedString.get(i) + " " + nextToken;
                wrappedString.set(i, nextLineString);
            } else {
                // Wrap words that are too long...
                while (font.getStringBounds(nextToken, frc).getWidth() > width) {
                    String part = "";
                    int j;
                    for (j = 0; font.getStringBounds(part, frc).getWidth() < width; j++) {
                        part = nextToken.substring(0, j);
                    }
                    nextToken = nextToken.substring(j - 1);
                    wrappedString.add(part);
                }
                // Insert nextToken or the part that is left after wordwrap
                wrappedString.add(nextToken);
            }
        }

        return wrappedString.toArray(new String[0]);
    }

}