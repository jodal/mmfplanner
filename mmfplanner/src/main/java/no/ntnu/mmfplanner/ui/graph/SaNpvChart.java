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
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Draws the SANPV Chart for all MMFs. Along the X-axis goes the periods and
 * along the Y-axis Discounted Cash. Uses the JFreeChart library.
 */
public class SaNpvChart extends ChartPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Paint[] CHART_COLOR = ChartColor
            .createDefaultPaintArray();
    private static final Stroke SERIES_STROKE = new BasicStroke(2);

    private Project project;

    public SaNpvChart() {
        super(null);
        createChart();
    }

    /**
     * Creates the chart
     */
    private void createChart() {
        JFreeChart chart = ChartFactory.createXYLineChart(null, // chart title
                "Period", // x axis label
                "Discounted Cash", // y axis label
                null, // data
                PlotOrientation.VERTICAL, true, // include legend
                true, // tooltips
                false // urls
                );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setUpperMargin(0.0);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);

        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        setChart(chart);
        setMouseZoomable(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setLegendLine(new Rectangle2D.Double(0.0, 0.0, 6.0, 0.0));
        renderer.setUseFillPaint(true);

        // the x=0 line
        renderer.setSeriesPaint(0, Color.GRAY);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesVisibleInLegend(0, new Boolean(false));

        plot.setRenderer(renderer);
    }

    /**
     * Sets the data and renderer (colors, shapes) for the chart.
     */
    public void setModel(Project project) {
        if (null != this.project) {
            this.project.removePropertyChangeListener(this);
        }
        this.project = project;
        if (null != project) {
            project.addPropertyChangeListener(this);
            updateModel();
        }
    }

    private void updateModel() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYPlot plot = ((XYPlot) getChart().getPlot());
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot
                .getRenderer();

        // the x=0 line
        XYSeries line = new XYSeries("");
        line.add(1.0, 0.0);
        line.add(project.getPeriods(), 0.0);
        dataset.addSeries(line);

        // adds the line plot for each mmf
        int[][] saNpvValues = project.getSaNpvTable();
        for (int mmfI = 0; mmfI < saNpvValues.length; mmfI++) {
            Mmf mmf = project.get(mmfI);
            XYSeries values = new XYSeries(mmf.getId() + ": " + mmf.getName());

            for (int period = 1; period <= saNpvValues[mmfI].length; period++) {
                values.add(period, saNpvValues[mmfI][period - 1]);
            }

            dataset.addSeries(values);
            renderer.setSeriesShapesVisible(mmfI + 1, false);
            renderer.setSeriesStroke(mmfI + 1, SERIES_STROKE);
            renderer.setSeriesPaint(mmfI + 1, CHART_COLOR[mmfI
                    % CHART_COLOR.length]);
        }

        plot.setDataset(dataset);
    }

    /**
     * Method for changes
     */
    public void propertyChange(PropertyChangeEvent evt) {
        updateModel();
    }
}