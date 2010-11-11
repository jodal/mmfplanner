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
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.model.ProjectRoi;

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
import org.jfree.util.ShapeUtilities;

/**
 * Draws the NPV Chart for the development process. Along the X-axis goes the
 * periods (time) and along the Y-axis Discounted Cash. Uses the JFreeChart
 * library.
 */

public class NpvChart extends ChartPanel implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;
    private Project project;
    private boolean waterfall;
    private boolean modelValid;
    private static Paint[] chartColor = ChartColor.createDefaultPaintArray();
    private static Stroke STROKE_LINE = new BasicStroke(2);

    public NpvChart(boolean waterfall) {
        super(null);
        this.waterfall = waterfall;
        createChart();
    }

    /**
     * Creates the main chart, but does not fill inn any data
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

        plot.getDomainAxis().setTickMarksVisible(false);

        setChart(chart);
        setMouseZoomable(false);
    }

    /**
     * Sets the model used for the project and invalidates the current chart
     */
    public void setModel(Project project) {
        if (null != this.project) {
            this.project.removePropertyChangeListener(this);
        }
        this.project = project;
        if (null != project) {
            project.addPropertyChangeListener(this);
        }
        invalidateModel();
    }

    /**
     * Checks if the model is valid, and if not updates the model.
     */
    @Override
    public void paint(Graphics g) {
        if (!modelValid) {
            updateModel();
        }
        super.paint(g);
    }

    /**
     * Invalidates the model and requests a repaint. Will cause updateModel() to
     * be called on the next paint.
     */
    private void invalidateModel() {
        modelValid = false;
        repaint();
    }

    /**
     * Updates the chart with data from the model.
     */
    private void updateModel() {
        modelValid = true;

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        renderer.setLegendLine(new Rectangle2D.Double(0.0, 0.0, 6.0, 0.0));
        renderer.setUseFillPaint(true);

        // the x=0 line
        addLineX(dataset, renderer);

        // rolling npv line (iterative)
        ProjectRoi projectRoi = ProjectRoi.getRoiTable(project, project
                .getInterestRate(), false);
        addNpvLine(projectRoi, "NPV Iterative", chartColor[0], dataset,
                renderer);

        // rolling npv line (waterfall)
        if (waterfall) {
            ProjectRoi projectRoiWaterfall = ProjectRoi.getRoiTable(project,
                    project.getInterestRate(), true);
            addNpvLine(projectRoiWaterfall, "NPV Waterfall", chartColor[1],
                    dataset, renderer);
        }

        // legend (break even and self funding)
        addLegendElements(dataset, renderer);

        XYPlot plot = ((XYPlot) getChart().getPlot());
        plot.setDataset(dataset);
        plot.setRenderer(renderer);

        plot.setDomainGridlinesVisible(false);
    }

    /**
     * Helper method for updateModel(). Adds the legend elements for self
     * funding and break even.
     */
    private void addLegendElements(XYSeriesCollection dataset,
            XYLineAndShapeRenderer renderer) {
        // self funding legend
        XYSeries selfFundingLegend = new XYSeries("Self Funding");
        int series = dataset.getSeriesCount();
        dataset.addSeries(selfFundingLegend);
        renderer.setSeriesPaint(series, Color.black);
        renderer.setSeriesShape(series, ShapeUtilities.createUpTriangle(3.5f));

        // break even legend
        XYSeries breakEvenLegend = new XYSeries("Break Even");
        series = dataset.getSeriesCount();
        dataset.addSeries(breakEvenLegend);
        renderer.setSeriesPaint(series, Color.black);
        renderer.setSeriesShape(series, ShapeUtilities.createDiamond(3.5f));
    }

    /**
     * Helper method for updateModel(). Adds a rolling npv line with self
     * funding and break even, as well as adding legend elements
     */
    private void addNpvLine(ProjectRoi projectRoi, String caption, Paint paint,
            XYSeriesCollection dataset, XYLineAndShapeRenderer renderer) {
        // adds the rolling npvseries and sets approperiate render properties
        XYSeries rollingNpv = new XYSeries(caption);
        rollingNpv.add(0.5, 0.0);
        for (int i = 0; i < projectRoi.rollingNpv.length; i++) {
            rollingNpv.add(i + 1.5, projectRoi.rollingNpv[i]);
        }

        int series = dataset.getSeriesCount();
        dataset.addSeries(rollingNpv);
        renderer.setSeriesShapesVisible(series, false);
        renderer.setSeriesStroke(series, STROKE_LINE);
        renderer.setSeriesPaint(series, paint);
        renderer.setSeriesVisibleInLegend(series, true);

        // break even
        if (projectRoi.breakevenPeriod > 0) {
            XYSeries breakEven = new XYSeries("Break Even");
            breakEven.add(projectRoi.breakevenRegression - 0.5, 0.0);

            series = dataset.getSeriesCount();
            dataset.addSeries(breakEven);
            renderer.setSeriesLinesVisible(series, false);
            renderer.setSeriesPaint(series, paint);
            renderer.setSeriesVisibleInLegend(series, false);
            renderer.setSeriesShape(series, ShapeUtilities.createDiamond(3.5f));
        }

        // selfFunding
        if (projectRoi.selfFundingPeriod > 1) {
            XYSeries selfFunding = new XYSeries("Self Funding");
            double x = projectRoi.selfFundingPeriod - 0.5;
            double y = projectRoi.rollingNpv[projectRoi.selfFundingPeriod - 2];
            selfFunding.add(x, y);

            series = dataset.getSeriesCount();
            dataset.addSeries(selfFunding);
            renderer.setSeriesLinesVisible(series, false);
            renderer.setSeriesPaint(series, paint);
            renderer.setSeriesVisibleInLegend(series, false);
            renderer.setSeriesShape(series, ShapeUtilities
                    .createUpTriangle(3.5f));
        }

    }

    /**
     * Helper method for updateModel(). Adds the gray line at x=0.
     */
    private void addLineX(XYSeriesCollection dataset,
            XYLineAndShapeRenderer renderer) {
        XYSeries line = new XYSeries("");
        line.add(0.5, 0.0);
        line.add(project.getPeriods() + 0.5, 0.0);

        int series = dataset.getSeriesCount();
        dataset.addSeries(line);
        renderer.setSeriesPaint(series, Color.GRAY);
        renderer.setSeriesShapesVisible(series, false);
        renderer.setSeriesLinesVisible(series, true);
        renderer.setSeriesVisibleInLegend(series, false);
    }

    /**
     * Invalidates the model when a change has occured
     */
    public void propertyChange(PropertyChangeEvent evt) {
        invalidateModel();
    }
}