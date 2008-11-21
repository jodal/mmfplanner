/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.ui;

import java.awt.Color;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.table.TableColumn;

import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;
import no.ntnu.mmfplanner.ui.action.DeleteCategoryAction;
import no.ntnu.mmfplanner.ui.action.DeleteMmfAction;
import no.ntnu.mmfplanner.ui.action.HeuristicSortAction;
import no.ntnu.mmfplanner.ui.action.HideTabAction;
import no.ntnu.mmfplanner.ui.action.LoadTestProjectAction;
import no.ntnu.mmfplanner.ui.action.MoveTabAction;
import no.ntnu.mmfplanner.ui.action.NewCategoryAction;
import no.ntnu.mmfplanner.ui.action.NewMmfAction;
import no.ntnu.mmfplanner.ui.action.NewProjectAction;
import no.ntnu.mmfplanner.ui.action.OpenProjectAction;
import no.ntnu.mmfplanner.ui.action.OptimalSortAction;
import no.ntnu.mmfplanner.ui.action.PrecursorSortAction;
import no.ntnu.mmfplanner.ui.action.SaveProjectAction;
import no.ntnu.mmfplanner.ui.action.SwimlaneSortAction;
import no.ntnu.mmfplanner.ui.graph.GraphCanvas;
import no.ntnu.mmfplanner.ui.graph.NpvChart;
import no.ntnu.mmfplanner.ui.graph.SaNpvChart;
import no.ntnu.mmfplanner.ui.model.CategoryComboModel;
import no.ntnu.mmfplanner.ui.model.CategoryTableModel;
import no.ntnu.mmfplanner.ui.model.MmfTableModel;
import no.ntnu.mmfplanner.ui.model.ProjectPropertiesAdapter;
import no.ntnu.mmfplanner.ui.model.RelativeTableColumnModel;
import no.ntnu.mmfplanner.ui.model.RevenueTableModel;
import no.ntnu.mmfplanner.ui.model.RoiTableModel;
import no.ntnu.mmfplanner.ui.model.SaNpvTableModel;
import no.ntnu.mmfplanner.ui.renderer.CategoryComboCellRenderer;
import no.ntnu.mmfplanner.ui.renderer.CategoryTableCellRenderer;
import no.ntnu.mmfplanner.ui.renderer.ColorComboCellRenderer;
import no.ntnu.mmfplanner.ui.renderer.MmfTableCellRenderer;
import no.ntnu.mmfplanner.ui.renderer.RevenueTableCellRenderer;
import no.ntnu.mmfplanner.ui.renderer.RoiTableCellRenderer;
import no.ntnu.mmfplanner.util.TangoColor;
import edu.umd.cs.piccolox.swing.PScrollPane;

/**
 * The MainFrame class makes the GUI for the program. It also includes some test
 * data for quick demonstration
 * 
 * It initializes the models to be used for GUI, the graphs and all GUI elements
 * It also has some action listeners
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    Project project;

    private GraphCanvas precedenceGraph;
    private GraphCanvas decompositionGraph;
    private SaNpvChart sanpvChart;
    private NpvChart npvChart;
    private NpvChart npvChartWaterfall;

    private boolean changed;

    private TabPanePanelPlacement placement;

    private ProjectPropertiesAdapter projectPropertiesAdapter;

    /** Creates new form MainFrame */
    public MainFrame() {
        initComponents();
        postInitComponents();
    }

    public void initTestData1() {
        Project project = new Project();
        String[] mmfStrings = new String[] { "A=1=4=0=MMF graph display=",
                "B=1=6=2=ROI table and NPV display",
                "D=2=5=0=Automatic sorting of MMFs=B",
                "F=2=4=0=Drag-and-drop MMFs and precursors=A",
                "G=2=7=2=Economical graphs=B", "H=3=6=2=Reports=B,G",
                "I=2=2=1=Project save/load=",
                "J=3=3=1=Project import/export=I",
                "K=3=1=1=Copy/paste spreadsheets and MMFs=",
                "L=3=1=1=Workspace=I" };
        int cost[] = new int[] { -13, -8, -3, -5, -5, -3, -99, -99, -5, -5 };

        try {
            project.setInterestRate(0.008);
            project.setPeriods(12);
            project.addCategory(new Category("Graph display and manipulation",
                    TangoColor.CHAMELEON_1, null));
            project.addCategory(new Category("Miscellaneous",
                    TangoColor.BUTTER_1, project.getCategory(0)));
            project.addCategory(new Category("Economical information",
                    TangoColor.SCARLET_RED_1, project.getCategory(0)));

            for (String mmfString : mmfStrings) {
                String[] s = mmfString.split("=");
                Mmf mmf = new Mmf(s[0], s[4]);
                mmf.setPeriod(Integer.parseInt(s[1]));
                mmf.setSwimlane(Integer.parseInt(s[2]));
                mmf.setCategory(project.getCategory(Integer.parseInt(s[3])));
                if (s.length > 5) {
                    for (String dep : s[5].split(",")) {
                        mmf.addPrecursor(project.get(dep));
                    }
                }
                mmf.setRevenue(1, cost[project.size()]);
                for (int i = 3; i <= project.getPeriods(); i++) {
                    mmf.setRevenue(i, s[0].charAt(0) - 'A');
                }
                project.add(mmf);
            }
        } catch (Exception e) {
            // we ignore all exceptions in test method
            e.printStackTrace();
        }
        setModel(project);
    }

    public void initTestData2() {
        fileLoadTestDataMenuItem.getAction().actionPerformed(null);
    }

    public boolean queryProjectCloseSave() {
        if (!changed) {
            return false;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Do you want to save this project before you close it?",
                "Save?", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            SaveProjectAction save = new SaveProjectAction(this);
            return !save.save();
        } else if (answer == JOptionPane.CANCEL_OPTION) {
            return true;
        } else {
            return false;
        }

    }

    public void updateTitle() {
        if ((project != null) && (project.getName() != null)
                && !"".equals(project.getName())) {
            setTitle(project.getName() + " - MMF Planner");
        } else {
            setTitle("MMF Planner");
        }
    }

    public void setModel(Project project) {
        this.project = project;
        changed = false;

        // XXX: should be changed to handle modifications better
        project.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                changed = true;
                updateTitle();
            }
        });
        updateTitle();

        // Project properties
        projectPropertiesAdapter.setModel(project);

        // Category
        CategoryTableModel categoryTableModel = new CategoryTableModel(project);
        categoryTable.setModel(categoryTableModel);
        categoryTable.setBackground(Color.WHITE);
        categoryTable.setDefaultRenderer(Object.class,
                new CategoryTableCellRenderer());
        TableColumn colorColumn = categoryTable.getColumnModel().getColumn(
                categoryTableModel.findColumn(CategoryTableModel.COLUMN_COLOR));
        JComboBox comboBox = new JComboBox(TangoColor.TANGO_COLORS);
        comboBox.setBorder(null);
        comboBox.setRenderer(new ColorComboCellRenderer());
        colorColumn.setCellEditor(new DefaultCellEditor(comboBox));
        colorColumn.setCellRenderer(new ColorComboCellRenderer());

        categoryComboBox.setModel(new CategoryComboModel(project));

        // MMF
        mmfTable.setBackground(Color.WHITE);
        MmfTableModel mmfTableModel = new MmfTableModel(project);
        mmfTable.setModel(mmfTableModel);
        mmfTable.setDefaultRenderer(Object.class, new MmfTableCellRenderer());

        // Revenue
        revenueTable.setBackground(Color.WHITE);
        revenueTable.setModel(new RevenueTableModel(project));
        revenueTable.setDefaultRenderer(Object.class,
                new RevenueTableCellRenderer());

        // SANPV
        sanpvTable.setModel(new SaNpvTableModel(project));

        // ROI
        roiTable.setModel(new RoiTableModel(project, false));
        roiWaterfallTable.setModel(new RoiTableModel(project, true));

        // Graphs
        decompositionGraph.setModel(project,
                GraphCanvas.GRAPH_TYPE_DECOMPOSITION);
        precedenceGraph.setModel(project, GraphCanvas.GRAPH_TYPE_PRECEDENCE);

        // Charts
        sanpvChart.setModel(project);
        npvChart.setModel(project);
        npvChartWaterfall.setModel(project);
    }

    /**
     * Will be run after initComponents() and is used to set up extra models and
     * renderers.
     */
    private void postInitComponents() {
        URL iconUrl = getClass().getClassLoader().getResource("res/mmf.png");
        if (iconUrl != null) {
            setIconImage(Toolkit.getDefaultToolkit().getImage(iconUrl));
        }

        projectPropertiesAdapter = new ProjectPropertiesAdapter(
                periodsTextField, interestRateTextField, projectNameTextField,
                maxMmfsPerPeriodTextField, null);

        // Tables
        categoryTable.setDefaultEditor(Category.class, new DefaultCellEditor(
                categoryComboBox));
        categoryTable.setDefaultRenderer(Category.class,
                new CategoryComboCellRenderer());
        categoryTable
                .addMouseListener(new PopupListener(categoryTablePopupMenu));

        mmfTable.setDefaultEditor(Category.class, new DefaultCellEditor(
                categoryComboBox));
        mmfTable.setDefaultRenderer(Category.class,
                new CategoryComboCellRenderer());
        mmfTable.addMouseListener(new PopupListener(mmfTablePopupMenu));
        mmfTable.setColumnModel(new RelativeTableColumnModel(new int[] { 0, 1,
                2, 3, 4 }, new int[] { 20, 200, 50, 8, 50 }));
        revenueTable.setColumnModel(new RelativeTableColumnModel(
                new int[] { 0 }, new int[] { 400 }));

        roiTable.setDefaultRenderer(Object.class, new RoiTableCellRenderer());
        roiTable.setColumnModel(new RelativeTableColumnModel(
                new int[] { 0, -1 }, new int[] { 400, 200 }));

        roiWaterfallTable.setDefaultRenderer(Object.class,
                new RoiTableCellRenderer());
        roiWaterfallTable.setColumnModel(new RelativeTableColumnModel(
                new int[] { 0, -1 }, new int[] { 400, 200 }));

        sanpvTable.setColumnModel(new RelativeTableColumnModel(new int[] { 0 },
                new int[] { 400 }));

        // Graphs
        decompositionGraph = new no.ntnu.mmfplanner.ui.graph.GraphCanvas();
        decompositionScrollPane.setViewportView(decompositionGraph);
        precedenceGraph = new no.ntnu.mmfplanner.ui.graph.GraphCanvas();
        precedenceScrollPane.setViewportView(precedenceGraph);

        // Charts
        sanpvChart = new SaNpvChart();
        npvChart = new NpvChart(false);
        npvChartWaterfall = new NpvChart(true);

        // Actions
        categoryDeleteMenuItem.setAction(new DeleteCategoryAction(this));
        mmfDeleteMenuItem.setAction(new DeleteMmfAction(this));
        fileNewProjectMenuItem.setAction(new NewProjectAction(this));
        fileSaveProjectMenuItem.setAction(new SaveProjectAction(this));
        fileOpenProjectMenuItem.setAction(new OpenProjectAction(this));
        fileLoadTestDataMenuItem.setAction(new LoadTestProjectAction(this));
        editNewMmfMenuItem.setAction(new NewMmfAction(this));
        editNewCategoryMenuItem.setAction(new NewCategoryAction(this));
        sortPrettyMenuItem.setAction(new SwimlaneSortAction(this));
        sortPrecursorMenuItem.setAction(new PrecursorSortAction(this));
        sortGreedyMenuItem.setAction(new OptimalSortAction(this));
        sortHeuristicMenuItem.setAction(new HeuristicSortAction(this));

        // tabs
        placement = new TabPanePanelPlacement(viewMenu);
        placement.addPane(TabPanePanelPlacement.PLACEMENT_UPPER,
                upperTabbedPane);
        placement.addPane(TabPanePanelPlacement.PLACEMENT_LOWER,
                lowerTabbedPane);

        placement.add("projectPropPanel", "Project Properties",
                TabPanePanelPlacement.TYPE_INPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, projectPropPanel);
        placement.add("mmfTablePanel", "MMF Table",
                TabPanePanelPlacement.TYPE_INPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, mmfTablePanel);
        placement.add("revenueTablePanel", "MMF Revenue",
                TabPanePanelPlacement.TYPE_INPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, revenueTablePanel);
        placement.add("decompositionScrollPane", "Decomposition Graph",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true,
                decompositionScrollPane);
        placement.add("precedenceScrollPane", "Precedence Graph",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true,
                precedenceScrollPane);
        placement.add("sanpvTablePanel", "SANPV Table",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, sanpvTablePanel);
        placement.add("sanpvChart", "SANPV Chart",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true, sanpvChart);
        placement.add("npvChart", "NPV Chart",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true, npvChart);
        placement.add("npvChartWaterfall", "NPV Waterfall Chart",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_UPPER, true, npvChartWaterfall);
        placement.add("roiTablePanel", "NPV Table",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, roiTablePanel);
        placement.add("roiWaterfallPanel", "NPV Waterfall Table",
                TabPanePanelPlacement.TYPE_OUTPUT,
                TabPanePanelPlacement.PLACEMENT_LOWER, true, roiWaterfallPanel);

        upperTabbedPane.setSelectedIndex(0);
        lowerTabbedPane.setSelectedIndex(0);

        // Upper popup menu to move and hide tabs
        upperPaneMoveMenuItem.setAction(new MoveTabAction(this,
                upperTabbedPane, TabPanePanelPlacement.PLACEMENT_LOWER));
        upperPaneHideMenuItem.setAction(new HideTabAction(placement,
                upperTabbedPane));
        TabPopupListener upperPanePopupListener = new TabPopupListener(
                upperPanePopupMenu);
        upperTabbedPane.addMouseListener(upperPanePopupListener);

        // Lower popup menu to move and hide tabs
        lowerPaneMoveMenuItem.setAction(new MoveTabAction(this,
                lowerTabbedPane, TabPanePanelPlacement.PLACEMENT_UPPER));
        lowerPaneHideMenuItem.setAction(new HideTabAction(placement,
                lowerTabbedPane));
        TabPopupListener lowerPanePopupListener = new TabPopupListener(
                lowerPanePopupMenu);
        lowerTabbedPane.addMouseListener(lowerPanePopupListener);

        sortHeuristicMenuItem.setEnabled(false);
    }

    /**
     * Get the placement instance that keeps the order of all panels.
     * 
     * @return placement instance used
     */
    public TabPanePanelPlacement getTabPanePanelPlacement() {
        return placement;
    }

    /**
     * The projects precedenceGraphPanel.
     * 
     * @return Project's precedenceGraphPanel
     */
    public PScrollPane getPrecedenceGraphPanel() {
        return precedenceScrollPane;
    }

    public Project getProject() {
        return project;
    }

    public JTable getCategoryTable() {
        return categoryTable;
    }

    public JTable getMmfTable() {
        return mmfTable;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {

        projectPropPanel = new javax.swing.JPanel();
        projectNameLabel = new javax.swing.JLabel();
        projectNameTextField = new javax.swing.JTextField();
        periodsLabel = new javax.swing.JLabel();
        periodsTextField = new javax.swing.JFormattedTextField();
        interestRateLabel = new javax.swing.JLabel();
        interestRateTextField = new javax.swing.JTextField();
        categoryLabel = new javax.swing.JLabel();
        categoryTableScrollPane = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        maxMmfsPerPeriodLabel = new javax.swing.JLabel();
        maxMmfsPerPeriodTextField = new javax.swing.JFormattedTextField();
        mmfTablePanel = new javax.swing.JPanel();
        mmfTableScrollPane = new javax.swing.JScrollPane();
        mmfTable = new javax.swing.JTable();
        revenueTablePanel = new javax.swing.JPanel();
        revenueTableScrollPanel = new javax.swing.JScrollPane();
        revenueTable = new javax.swing.JTable();
        sanpvTablePanel = new javax.swing.JPanel();
        sanpvTableScrollPane = new javax.swing.JScrollPane();
        sanpvTable = new javax.swing.JTable();
        roiTablePanel = new javax.swing.JPanel();
        roiTableScrollPane = new javax.swing.JScrollPane();
        roiTable = new javax.swing.JTable();
        roiWaterfallPanel = new javax.swing.JPanel();
        roiWaterfallTableScrollPane = new javax.swing.JScrollPane();
        roiWaterfallTable = new javax.swing.JTable();
        decompositionScrollPane = new edu.umd.cs.piccolox.swing.PScrollPane();
        precedenceScrollPane = new edu.umd.cs.piccolox.swing.PScrollPane();
        categoryComboBox = new javax.swing.JComboBox();
        categoryTablePopupMenu = new javax.swing.JPopupMenu();
        categoryDeleteMenuItem = new javax.swing.JMenuItem();
        mmfTablePopupMenu = new javax.swing.JPopupMenu();
        mmfDeleteMenuItem = new javax.swing.JMenuItem();
        upperPanePopupMenu = new javax.swing.JPopupMenu();
        upperPaneMoveMenuItem = new javax.swing.JMenuItem();
        upperPaneHideMenuItem = new javax.swing.JMenuItem();
        lowerPanePopupMenu = new javax.swing.JPopupMenu();
        lowerPaneMoveMenuItem = new javax.swing.JMenuItem();
        lowerPaneHideMenuItem = new javax.swing.JMenuItem();
        mainSplitPane = new javax.swing.JSplitPane();
        upperTabbedPane = new javax.swing.JTabbedPane();
        lowerTabbedPane = new javax.swing.JTabbedPane();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        fileNewProjectMenuItem = new javax.swing.JMenuItem();
        fileSeparator1 = new javax.swing.JSeparator();
        fileOpenProjectMenuItem = new javax.swing.JMenuItem();
        fileSeparator3 = new javax.swing.JSeparator();
        fileLoadTestDataMenuItem = new javax.swing.JMenuItem();
        fileSaveProjectMenuItem = new javax.swing.JMenuItem();
        fileSeparator2 = new javax.swing.JSeparator();
        fileExitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editNewCategoryMenuItem = new javax.swing.JMenuItem();
        editNewMmfMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        sortMenu = new javax.swing.JMenu();
        sortPrecursorMenuItem = new javax.swing.JMenuItem();
        sortPrettyMenuItem = new javax.swing.JMenuItem();
        menuSeparator = new javax.swing.JSeparator();
        sortGreedyMenuItem = new javax.swing.JMenuItem();
        sortHeuristicMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpAboutMenuItem = new javax.swing.JMenuItem();

        projectPropPanel.setName("Project Properties"); // NOI18N

        projectNameLabel.setText("Project name:");

        projectNameTextField.setMinimumSize(new java.awt.Dimension(100, 21));

        periodsLabel.setText("Number of periods:");

        periodsTextField
                .setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                        new javax.swing.text.NumberFormatter(
                                new java.text.DecimalFormat("#0"))));
        periodsTextField.setMinimumSize(new java.awt.Dimension(100, 21));

        interestRateLabel.setText("Interest rate (%):");

        interestRateTextField.setMinimumSize(new java.awt.Dimension(100, 21));

        categoryLabel.setText("Categories:");

        categoryTableScrollPane.setViewportView(categoryTable);

        maxMmfsPerPeriodLabel
                .setText("<html>NPV sorting,<br>concurrent developed MMFs:</html>");

        maxMmfsPerPeriodTextField
                .setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                        new javax.swing.text.NumberFormatter(
                                new java.text.DecimalFormat("#0"))));
        maxMmfsPerPeriodTextField
                .setToolTipText("Only applies to NPV Sorting algorithms");
        maxMmfsPerPeriodTextField
                .setMinimumSize(new java.awt.Dimension(100, 21));
        maxMmfsPerPeriodTextField.setPreferredSize(new java.awt.Dimension(100,
                20));

        GroupLayout projectPropPanelLayout = new GroupLayout(projectPropPanel);
        projectPropPanel.setLayout(projectPropPanelLayout);
        projectPropPanelLayout
                .setHorizontalGroup(projectPropPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                projectPropPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                projectPropPanelLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                periodsLabel)
                                                        .addComponent(
                                                                projectNameLabel)
                                                        .addComponent(
                                                                interestRateLabel)
                                                        .addComponent(
                                                                categoryLabel))
                                        .addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                projectPropPanelLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                categoryTableScrollPane,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                502,
                                                                Short.MAX_VALUE)
                                                        .addGroup(
                                                                projectPropPanelLayout
                                                                        .createParallelGroup(
                                                                                GroupLayout.Alignment.TRAILING,
                                                                                false)
                                                                        .addGroup(
                                                                                projectPropPanelLayout
                                                                                        .createSequentialGroup()
                                                                                        .addGroup(
                                                                                                projectPropPanelLayout
                                                                                                        .createParallelGroup(
                                                                                                                GroupLayout.Alignment.LEADING)
                                                                                                        .addComponent(
                                                                                                                interestRateTextField,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                131,
                                                                                                                Short.MAX_VALUE)
                                                                                                        .addComponent(
                                                                                                                periodsTextField,
                                                                                                                GroupLayout.Alignment.TRAILING,
                                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                                125,
                                                                                                                Short.MAX_VALUE))
                                                                                        .addPreferredGap(
                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(
                                                                                                maxMmfsPerPeriodLabel,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                182,
                                                                                                GroupLayout.PREFERRED_SIZE)
                                                                                        .addPreferredGap(
                                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(
                                                                                                maxMmfsPerPeriodTextField,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                129,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(
                                                                                projectNameTextField,
                                                                                GroupLayout.Alignment.LEADING,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                466,
                                                                                GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap()));
        projectPropPanelLayout
                .setVerticalGroup(projectPropPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                projectPropPanelLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                projectPropPanelLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                        .addComponent(
                                                                projectNameLabel)
                                                        .addComponent(
                                                                projectNameTextField,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                projectPropPanelLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.TRAILING)
                                                        .addGroup(
                                                                projectPropPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(
                                                                                projectPropPanelLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(
                                                                                                periodsLabel)
                                                                                        .addComponent(
                                                                                                periodsTextField,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addGroup(
                                                                                projectPropPanelLayout
                                                                                        .createParallelGroup(
                                                                                                GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(
                                                                                                interestRateLabel)
                                                                                        .addComponent(
                                                                                                interestRateTextField,
                                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                                GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(
                                                                maxMmfsPerPeriodTextField,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(
                                                                maxMmfsPerPeriodLabel))
                                        .addPreferredGap(
                                                LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(
                                                projectPropPanelLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                categoryLabel)
                                                        .addComponent(
                                                                categoryTableScrollPane,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                170,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));

        mmfTablePanel.setName("MMF Table"); // NOI18N

        mmfTableScrollPane.setDoubleBuffered(true);
        mmfTableScrollPane.setViewportView(mmfTable);

        GroupLayout mmfTablePanelLayout = new GroupLayout(mmfTablePanel);
        mmfTablePanel.setLayout(mmfTablePanelLayout);
        mmfTablePanelLayout.setHorizontalGroup(mmfTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        mmfTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        mmfTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 573,
                                        Short.MAX_VALUE).addContainerGap()));
        mmfTablePanelLayout.setVerticalGroup(mmfTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        GroupLayout.Alignment.TRAILING,
                        mmfTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        mmfTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 248,
                                        Short.MAX_VALUE).addContainerGap()));

        revenueTablePanel.setName("Revenue Table"); // NOI18N

        revenueTableScrollPanel.setViewportView(revenueTable);

        GroupLayout revenueTablePanelLayout = new GroupLayout(revenueTablePanel);
        revenueTablePanel.setLayout(revenueTablePanelLayout);
        revenueTablePanelLayout.setHorizontalGroup(revenueTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        revenueTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        revenueTableScrollPanel,
                                        GroupLayout.DEFAULT_SIZE, 573,
                                        Short.MAX_VALUE).addContainerGap()));
        revenueTablePanelLayout.setVerticalGroup(revenueTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        revenueTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        revenueTableScrollPanel,
                                        GroupLayout.DEFAULT_SIZE, 248,
                                        Short.MAX_VALUE).addContainerGap()));

        sanpvTablePanel.setName("SANPV Table"); // NOI18N

        sanpvTableScrollPane.setViewportView(sanpvTable);

        GroupLayout sanpvTablePanelLayout = new GroupLayout(sanpvTablePanel);
        sanpvTablePanel.setLayout(sanpvTablePanelLayout);
        sanpvTablePanelLayout.setHorizontalGroup(sanpvTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        sanpvTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        sanpvTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 573,
                                        Short.MAX_VALUE).addContainerGap()));
        sanpvTablePanelLayout.setVerticalGroup(sanpvTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        sanpvTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        sanpvTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 248,
                                        Short.MAX_VALUE).addContainerGap()));

        roiTablePanel.setName("ROI/NPV Table"); // NOI18N

        roiTableScrollPane.setViewportView(roiTable);

        GroupLayout roiTablePanelLayout = new GroupLayout(roiTablePanel);
        roiTablePanel.setLayout(roiTablePanelLayout);
        roiTablePanelLayout.setHorizontalGroup(roiTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        roiTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        roiTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 573,
                                        Short.MAX_VALUE).addContainerGap()));
        roiTablePanelLayout.setVerticalGroup(roiTablePanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        roiTablePanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        roiTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 248,
                                        Short.MAX_VALUE).addContainerGap()));

        roiWaterfallPanel.setName("ROI/NPV Waterfall"); // NOI18N

        roiWaterfallTableScrollPane.setViewportView(roiWaterfallTable);

        GroupLayout roiWaterfallPanelLayout = new GroupLayout(roiWaterfallPanel);
        roiWaterfallPanel.setLayout(roiWaterfallPanelLayout);
        roiWaterfallPanelLayout.setHorizontalGroup(roiWaterfallPanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        roiWaterfallPanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        roiWaterfallTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 573,
                                        Short.MAX_VALUE).addContainerGap()));
        roiWaterfallPanelLayout.setVerticalGroup(roiWaterfallPanelLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                        roiWaterfallPanelLayout.createSequentialGroup()
                                .addContainerGap().addComponent(
                                        roiWaterfallTableScrollPane,
                                        GroupLayout.DEFAULT_SIZE, 248,
                                        Short.MAX_VALUE).addContainerGap()));

        decompositionScrollPane.setBorder(null);
        decompositionScrollPane.setName("Decomposition"); // NOI18N

        precedenceScrollPane.setBorder(null);
        precedenceScrollPane.setName("Precedence"); // NOI18N

        categoryComboBox.setBorder(null);
        categoryComboBox.setRenderer(new CategoryComboCellRenderer());

        categoryTablePopupMenu.add(categoryDeleteMenuItem);

        mmfTablePopupMenu.add(mmfDeleteMenuItem);

        upperPanePopupMenu.add(upperPaneMoveMenuItem);

        upperPaneHideMenuItem.setText("Item");
        upperPanePopupMenu.add(upperPaneHideMenuItem);

        lowerPaneMoveMenuItem.setText("Item");
        lowerPanePopupMenu.add(lowerPaneMoveMenuItem);

        lowerPaneHideMenuItem.setText("Item");
        lowerPanePopupMenu.add(lowerPaneHideMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MMF Planner");

        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.7);
        mainSplitPane.setLeftComponent(upperTabbedPane);
        mainSplitPane.setRightComponent(lowerTabbedPane);
        lowerTabbedPane.getAccessibleContext().setAccessibleName("");

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        fileMenu.add(fileNewProjectMenuItem);
        fileMenu.add(fileSeparator1);
        fileMenu.add(fileOpenProjectMenuItem);
        fileMenu.add(fileSeparator3);
        fileMenu.add(fileLoadTestDataMenuItem);
        fileMenu.add(fileSaveProjectMenuItem);
        fileMenu.add(fileSeparator2);

        fileExitMenuItem.setMnemonic('x');
        fileExitMenuItem.setText("Exit");
        fileExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileExitAction(evt);
            }
        });
        fileMenu.add(fileExitMenuItem);

        mainMenuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");
        editMenu.add(editNewCategoryMenuItem);
        editMenu.add(editNewMmfMenuItem);

        mainMenuBar.add(editMenu);

        viewMenu.setMnemonic('v');
        viewMenu.setText("View");
        mainMenuBar.add(viewMenu);

        sortMenu.setMnemonic('s');
        sortMenu.setText("Sort");
        sortMenu.add(sortPrecursorMenuItem);
        sortMenu.add(sortPrettyMenuItem);
        sortMenu.add(menuSeparator);
        sortMenu.add(sortGreedyMenuItem);
        sortMenu.add(sortHeuristicMenuItem);

        mainMenuBar.add(sortMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        helpAboutMenuItem.setMnemonic('A');
        helpAboutMenuItem.setText("About...");
        helpAboutMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        helpAboutAction(evt);
                    }
                });
        helpMenu.add(helpAboutMenuItem);
        helpAboutMenuItem.getAccessibleContext().setAccessibleName(
                "About MMF Planner...");

        mainMenuBar.add(helpMenu);
        helpMenu.getAccessibleContext().setAccessibleName("");

        setJMenuBar(mainMenuBar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                        mainSplitPane, GroupLayout.DEFAULT_SIZE, 800,
                        Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap().addComponent(
                        mainSplitPane, GroupLayout.DEFAULT_SIZE, 600,
                        Short.MAX_VALUE).addContainerGap()));

        pack();
    }

    private void fileExitAction(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void helpAboutAction(java.awt.event.ActionEvent evt) {
        new AboutDialog(this, true).setVisible(true);
    }

    private javax.swing.JComboBox categoryComboBox;
    private javax.swing.JMenuItem categoryDeleteMenuItem;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JTable categoryTable;
    private javax.swing.JPopupMenu categoryTablePopupMenu;
    private javax.swing.JScrollPane categoryTableScrollPane;
    private edu.umd.cs.piccolox.swing.PScrollPane decompositionScrollPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editNewCategoryMenuItem;
    private javax.swing.JMenuItem editNewMmfMenuItem;
    private javax.swing.JMenuItem fileExitMenuItem;
    private javax.swing.JMenuItem fileLoadTestDataMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileNewProjectMenuItem;
    private javax.swing.JMenuItem fileOpenProjectMenuItem;
    private javax.swing.JMenuItem fileSaveProjectMenuItem;
    private javax.swing.JSeparator fileSeparator1;
    private javax.swing.JSeparator fileSeparator2;
    private javax.swing.JSeparator fileSeparator3;
    private javax.swing.JMenuItem helpAboutMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel interestRateLabel;
    private javax.swing.JTextField interestRateTextField;
    private javax.swing.JMenuItem lowerPaneHideMenuItem;
    private javax.swing.JMenuItem lowerPaneMoveMenuItem;
    private javax.swing.JPopupMenu lowerPanePopupMenu;
    private javax.swing.JTabbedPane lowerTabbedPane;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JLabel maxMmfsPerPeriodLabel;
    private javax.swing.JFormattedTextField maxMmfsPerPeriodTextField;
    private javax.swing.JSeparator menuSeparator;
    private javax.swing.JMenuItem mmfDeleteMenuItem;
    private javax.swing.JTable mmfTable;
    private javax.swing.JPanel mmfTablePanel;
    private javax.swing.JPopupMenu mmfTablePopupMenu;
    private javax.swing.JScrollPane mmfTableScrollPane;
    private javax.swing.JLabel periodsLabel;
    private javax.swing.JFormattedTextField periodsTextField;
    private edu.umd.cs.piccolox.swing.PScrollPane precedenceScrollPane;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JTextField projectNameTextField;
    private javax.swing.JPanel projectPropPanel;
    private javax.swing.JTable revenueTable;
    private javax.swing.JPanel revenueTablePanel;
    private javax.swing.JScrollPane revenueTableScrollPanel;
    private javax.swing.JTable roiTable;
    private javax.swing.JPanel roiTablePanel;
    private javax.swing.JScrollPane roiTableScrollPane;
    private javax.swing.JPanel roiWaterfallPanel;
    private javax.swing.JTable roiWaterfallTable;
    private javax.swing.JScrollPane roiWaterfallTableScrollPane;
    private javax.swing.JTable sanpvTable;
    private javax.swing.JPanel sanpvTablePanel;
    private javax.swing.JScrollPane sanpvTableScrollPane;
    private javax.swing.JMenuItem sortGreedyMenuItem;
    private javax.swing.JMenuItem sortHeuristicMenuItem;
    private javax.swing.JMenu sortMenu;
    private javax.swing.JMenuItem sortPrecursorMenuItem;
    private javax.swing.JMenuItem sortPrettyMenuItem;
    private javax.swing.JMenuItem upperPaneHideMenuItem;
    private javax.swing.JMenuItem upperPaneMoveMenuItem;
    private javax.swing.JPopupMenu upperPanePopupMenu;
    private javax.swing.JTabbedPane upperTabbedPane;
    private javax.swing.JMenu viewMenu;

}
