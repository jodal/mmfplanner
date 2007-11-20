/*
 * $Id: DecompositionGraphNode.java 1406 2007-11-17 14:44:28Z erikbagg $
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.umd.cs.piccolo.PNode;
import no.ntnu.mmfplanner.model.Category;
import no.ntnu.mmfplanner.model.Mmf;
import no.ntnu.mmfplanner.model.Project;

/**
 * This class handles movement and updating of the decomposition graph.
 * 
 * @version $Revision: 1406 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public class DecompositionGraphNode extends ProjectGraphNode {
    /**
     * @param project
     */
    public DecompositionGraphNode(Project project) {
        super(project);
        setPaint(Color.WHITE);
    }

    private static final long serialVersionUID = 1L;

    /**
     * Removes all nodes and re-adds them to the graph. Piccolo will call
     * layoutChildren() afterwards to place the children appropriately.
     * 
     * All categories and MMFs that have a parent category are added as children
     * of that categories nodes. This ensure that layoutChildren() will work
     * correctly.
     */
    @Override
    protected void invalidateModel() {
        // XXX: should we update only the relevant nodes? in a project with
        // dependencies this could be quite a lot of nodes anyway -bagge
        removeAllChildren();

        // add all categories
        HashMap<Category, CategoryNode> catNodes = new HashMap<Category, CategoryNode>();
        while (catNodes.size() < project.getCategorySize()) {
            for (int i = 0; i < project.getCategorySize(); i++) {
                Category category = project.getCategory(i);
                CategoryNode parent = catNodes.get(category.getParent());
                if (catNodes.containsKey(category)
                        || ((null == parent) && (null != category.getParent()))) {
                    continue;
                }
                CategoryNode node = new CategoryNode(category);
                catNodes.put(category, node);
                if (null == parent) {
                    addChild(node);
                } else {
                    parent.addChild(node);
                    // link category->category
                    addChild(new LinkNode(parent, node));
                }
            }
        }

        // move all childless category nodes to the beginning of the list
        int pos = 0;
        for (int i = 0; i < getChildrenCount(); i++) {
            PNode node = getChild(i);
            if (node instanceof CategoryNode) {
                if (node.getChildrenCount() == 0) {
                    removeChild(node);
                    addChild(pos, node);
                    pos++;
                }
            }
        }

        // add all MMFs
        for (int i = 0; i < project.size(); i++) {
            Mmf mmf = project.get(i);
            CategoryNode parent = catNodes.get(mmf.getCategory());
            MmfNode node = new MmfNode(mmf);
            if (null == parent) {
                addChild(node);
            } else {
                parent.addChild(node);
                // link category->mmf
                addChild(new LinkNode(parent, node));
            }
        }
    }

    /**
     * Lays out all children in a hierarchical decomposition graph. Nodes are
     * arranged using layoutNode() and edges are updated afterwards. This also
     * updates it's own bounds according to the placement of the children.
     */
    @Override
    protected void layoutChildren() {
        // layout all top nodes (not LinkNodes)
        double firstX[] = new double[project.getCategorySize() + 2];
        for (int i = 0; i < getChildrenCount(); i++) {
            PNode node = getChild(i);
            if (!(node instanceof LinkNode)) {
                layoutNode(node, 0, firstX);
            }
        }

        // Update all LinkNodes
        for (int i = 0; i < getChildrenCount(); i++) {
            PNode node = getChild(i);
            if (node instanceof LinkNode) {
                ((LinkNode) node).updateLine();
            }
        }

        // Update bounds
        double maxX = 0;
        for (int i = 0; i < firstX.length; i++) {
            if (firstX[i] > maxX) {
                maxX = firstX[i];
            } else if (firstX[i] == 0) {
                double height = i
                        * (CategoryNode.HEIGHT + CategoryNode.PADDING_HEIGHT);
                setBounds(0, 0, maxX, height);
                break;
            }
        }
    }

    /**
     * Arranges the layout for a single node. Recursively calls itself to
     * arrange all children first, and then the parent. Leaves are simply placed
     * at the first available spot. As all nodes are arranged from left to
     * right, this ensures the leaves are grouped together under their parent.
     * 
     * This version tries to always align the parent in the exact center of it's
     * leftmost and rightmost children.
     * 
     * @param node the node to arrange
     * @param level the current level (0 = top)
     * @param firstX first available absolute x-position
     */
    @SuppressWarnings("unchecked")
    private void layoutNode(PNode node, int level, double firstX[]) {
        double x = 0;
        if (0 == node.getChildrenCount()) {
            // leaves are placed at first available spot
            x = firstX[level];
        } else {
            // sort the children and find the first x-position for next level
            List<PNode> children = sortChildrenNonMoving(node
                    .getChildrenReference());
            firstX[level + 1] += CategoryNode.PADDING_WIDTH / 2;
            double max = firstX[level]
                    - ((children.size() - 1)
                            * (CategoryNode.WIDTH + CategoryNode.PADDING_WIDTH / 2) / 2.0);
            firstX[level + 1] = Math.max(firstX[level + 1], max);
            double xx[] = new double[children.size()];

            // lay out all the children in a consecutive row, might contain gaps
            // due to other children
            int xi = 0;
            for (PNode child : children) {
                layoutNode(child, level + 1, firstX);
                xx[xi] = firstX[level + 1];
                xi++;
            }

            // reposition leave nodes to fill out blank spaces
            int first = 0, last = 0;
            for (int i = 1; i < children.size(); i++) {
                int childCount = children.get(i).getChildrenCount();
                if ((childCount == 0) && (first == 0)) {
                    first = i;
                } else if (childCount > 0) {
                    last = i - 1;
                    break;
                }
            }
            if ((first > 0) && (last >= first)) {
                double dx = (xx[last + 1] - xx[last] - (CategoryNode.WIDTH + CategoryNode.PADDING_WIDTH / 2))
                        / (last - first + 2);
                if (dx > 1.0) {
                    for (int i = first; i <= last; i++) {
                        children.get(i).setX(
                                children.get(i).getX() + dx * (i - first + 1));
                    }
                }
            }

            // place this node in the center of all the children
            x = (xx[0] + xx[xx.length - 1]) / 2
                    - (CategoryNode.WIDTH + CategoryNode.PADDING_WIDTH / 2);
        }

        firstX[level] = (x + CategoryNode.WIDTH + CategoryNode.PADDING_WIDTH / 2);
        double px = x + CategoryNode.PADDING_WIDTH / 4;
        double py = level * (CategoryNode.HEIGHT + CategoryNode.PADDING_HEIGHT)
                + CategoryNode.PADDING_HEIGHT / 2;
        node.setBounds(px, py, CategoryNode.WIDTH, CategoryNode.HEIGHT);
    }

    /**
     * Places the first half of the children with grandchildren at the beginning
     * of the row, then all the childless children, and last the rest of the
     * children with grandchildren. Note that this does not rearrange nodes, and
     * will produce a wider graph. However it doesn't have the problem of
     * rearranging the children when adding or removing grandchildren.
     * 
     * @param children
     * @return sorted list according to the above criteria
     */
    private List<PNode> sortChildrenNonMoving(List<PNode> children) {
        List<PNode> childrenNC = new ArrayList<PNode>();
        List<PNode> childrenWC = new ArrayList<PNode>();

        // separate children with grandchildren from childless
        for (PNode child : children) {
            int childCount = child.getChildrenCount();
            if (childCount > 0) {
                childrenWC.add(child);
            } else {
                childrenNC.add(child);
            }
        }
        // add the first half of the children with children at the beginning
        // of the graph, and the other half at the end
        int first = Math.max(1, childrenWC.size() / 2);
        for (int i = 0; i < childrenWC.size(); i++) {
            if (i < first) {
                childrenNC.add(0, childrenWC.get(i));
            } else {
                childrenNC.add(childrenWC.get(i));
            }
        }
        return childrenNC;
    }
}
