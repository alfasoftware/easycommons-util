/**
 * Copyright (c) 2007-2012 EBM WebSourcing, 2012-2023 Linagora
 * 
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the New BSD License (3-clause license).
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the New BSD License (3-clause license)
 * for more details.
 *
 * You should have received a copy of the New BSD License (3-clause license)
 * along with this program/library; If not, see http://directory.fsf.org/wiki/License:BSD_3Clause/
 * for the New BSD License (3-clause license).
 */
package com.ebmwebsourcing.easycommons.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * <p>
 * This class is used to format various logs. it can be used as a static class or as a wrapper object of a logger. The
 * methods add to the resulting message a "ClassName-MethodName" information. (only for DEBUG and INFO level)
 * </p>
 *
 * @author Victor Noël - Linagora
 * @author Adrien Louis - EBM WebSourcing
 */
public class LoggingUtil {

    protected final Logger log;

    protected final String name;

    public LoggingUtil(final Logger logger) {
        this(logger, "");
    }

    public LoggingUtil(final Logger logger, final String loggerName) {
        this.log = logger;

        if ((loggerName != null) && (loggerName.trim().length() > 0)) {
            this.name = "[" + loggerName + "] ";
        } else {
            this.name = "";
        }
    }

    public void call() {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-CALL-" + classAndMethod());
        }
    }

    public void call(Object msg) {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-CALL-" + classAndMethod() + " " + msg);
        }
    }

    public void start() {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-START-" + classAndMethod());
        }
    }

    public void start(Object msg) {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-START-" + classAndMethod() + " " + msg);
        }
    }

    public void end() {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-END-" + classAndMethod());
        }
    }

    public void end(Object msg) {
        if (this.isTraceEnabled()) {
            this.log.log(Level.FINEST, this.name + "-END-" + classAndMethod() + " " + msg);
        }
    }
    
    public void trace(Object message) {
        this.log(Level.FINEST, message, null);
    }

    public void debug(Object message) {
        this.log(Level.FINE, message, null);
    }

    public void debug(Object message, Throwable throwable) {
        this.log(Level.FINE, message, throwable);
    }
   
    public void config(Object message) {
        this.log(Level.CONFIG, message, null);
    }

    public void info(Object message) {
        this.log(Level.INFO, message, null);
    }

    public void info(Object message, Throwable throwable) {
        this.log(Level.INFO, message, throwable);
    }

    public void warning(Object message) {
        this.log(Level.WARNING, message, null);
    }

    public void warning(Object message, Throwable throwable) {
        this.log(Level.WARNING, message, throwable);
    }

    public void error(Object message) {
        this.log(Level.SEVERE, message, null);
    }

    public void error(Object message, Throwable throwable) {
        this.log(Level.SEVERE, message, throwable);
    }

    private void log(final Level level, final Object message, final Throwable throwable) {
        if (this.isLevelEnabled(level)) {
            final String msg;
            if (this.log.isLoggable(Level.FINER)) {
                msg = this.name + classAndMethod() + " " + message;
            } else {
                msg = this.name + message;
            }
            if (throwable != null) {
                this.log.log(level, msg, throwable);
            } else {
                this.log.log(level, msg);
            }
        }
    }

    public void assertOrLog(final boolean assertion, final Object message) {
        assert assertion : message.toString();
        if (!assertion) {
            this.error("Assertion failed", new AssertionError(message.toString()));
        }
    }

    public boolean isTraceEnabled() {
        return this.isLevelEnabled(Level.FINEST);
    }

    public boolean isDebugEnabled() {
        return this.isLevelEnabled(Level.FINE);
    }
    
    public boolean isConfigEnabled() {
        return this.isLevelEnabled(Level.CONFIG);
    }

    public boolean isInfoEnabled() {
        return this.isLevelEnabled(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return this.isLevelEnabled(Level.WARNING);
    }

    public boolean isErrorEnabled() {
        return this.isLevelEnabled(Level.SEVERE);
    }

    private boolean isLevelEnabled(final Level level) {
        return (this.log != null) && this.log.isLoggable(level);
    }

    // //////////////////////////////////////////////////////////
    // Static part
    // //////////////////////////////////////////////////////////

    /**
     * Create an exception and analyze it in order to find the class and method
     * that called the LoggingUtil method.
     * 
     * @return
     */
    private static String classAndMethod() {
        String result = null;

        // throw and Parse an exception to find in the stack
        // the method that called the Loggingutil

        Throwable t = new Throwable();

        StackTraceElement[] ste = t.getStackTrace();

        if ((ste != null) && (ste.length > 2)) {
            StackTraceElement element = ste[2];

            // If the 2nd element in the stack is this class, get the 3rd
            if (element.getClassName().endsWith(LoggingUtil.class.getName())) {
                element = ste[3];
            }
            String className = element.getClassName();

            // remove the package name of the ClassName
            int index = className.lastIndexOf(".");

            if (index > -1) {
                className = className.substring(index + 1, className.length());
            }

            result = className + "." + element.getMethodName() + "()";
        }
        return result;
    }

    public String getName() {
        return this.log.getName();
    }

	public Logger getLogger() {
		return this.log;
	}

}
