/*
 * $Id: MainAbstractAction.java 1403 2007-11-17 14:19:58Z erikbagg $
 *
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 */

package no.ntnu.mmfplanner.ui.action;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import no.ntnu.mmfplanner.ui.MainFrame;

/**
 * An abstract Action class that all actions in this project should extend. This
 * forces all actions to give the name, mnemonic and description (or at least
 * think about these fields). Keeps the mainFrame as a protected variable, and
 * sends the rest of the parameters up to AbstractAction.
 *
 * @version $Revision: 1403 $
 * @author Snorre Gylterud
 * @author Stein Magnus Jodal
 * @author Johannes Knutsen
 * @author Erik Bagge Ottesen
 * @author Ralf Bjarne Taraldset
 */
public abstract class MainAbstractAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    protected MainFrame mainFrame;

    public MainAbstractAction(MainFrame mainFrame, String name, int mnemonic, String accelerator,
            String description) {
        super(name);

        this.mainFrame = mainFrame;

        // Not supported in JDK5
        // putValue(AbstractAction.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
        putValue(Action.SHORT_DESCRIPTION, description);
    }
}
