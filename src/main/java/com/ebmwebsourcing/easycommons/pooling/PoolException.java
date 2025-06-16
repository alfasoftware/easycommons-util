/**
 * Copyright (c) 2010-2012 EBM WebSourcing, 2012-2023 Linagora
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
package com.ebmwebsourcing.easycommons.pooling;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;

/**
 * General pool exception.
 *         
 * @see GenericResourcePool
 * 
 * @author aruffie 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class PoolException extends UncheckedException {

    private static final long serialVersionUID = 5337876521563081165L;

    /**
     * Create a pool exception
     * 
     * @param message the message for the exception
     */
    public PoolException(final String message) {
        super(message);
    }

    /**
     * Create a pool exception
     * 
     * @param cause the cause of the exception
     */
    public PoolException(final Throwable cause) {
        super(cause);
    }

    /**
     * Create a pool exception
     * 
     * @param message the message for the exception
     * @param cause the cause of the exception
     */
    public PoolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
