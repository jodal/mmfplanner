/*
 * Copyright (C) 2007 Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen,
 * Erik Bagge Ottesen, Ralf Bjarne Taraldset, and Iterate AS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 */

package no.ntnu.mmfplanner.model;

/**
 * Exception to throw when you get an unexpected error in the MMF models. This
 * is only used for errors that should be presented to the user.
 */
public class MmfException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public MmfException() {
    }

    /**
     * @param message
     */
    public MmfException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MmfException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MmfException(String message, Throwable cause) {
        super(message, cause);
    }

}
