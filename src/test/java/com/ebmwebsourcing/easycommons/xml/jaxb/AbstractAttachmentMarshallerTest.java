/**
 * Copyright (c) 2019-2023 Linagora
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
package com.ebmwebsourcing.easycommons.xml.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.junit.Test;

public class AbstractAttachmentMarshallerTest {

    private final AbstractAttachmentMarshaller aam = new AbstractAttachmentMarshaller() {

        @Override
        protected void addAttachment(final String cid, final DataHandler data) {
            // NOP
        }
    };

    /**
     * Check Content ID generation on marshaling when the data handler has no name
     */
    @Test
    public void cidForDataHandlerWithoutName() throws IOException {

        final String cid = aam.addMtomAttachment(new DataHandler("an object", "text/plain"), null, null);
        assertNotNull(cid);
        assertFalse(cid.trim().isEmpty());
    }

    /**
     * Check Content ID generation on marshaling when the data handler has a name
     */
    @Test
    public void cidForDataHandlerWithtName() throws IOException {
        final String dhName = "dh-name";
        final ByteArrayDataSource bads = new ByteArrayDataSource("an object", "text/plain");
        bads.setName(dhName);

        final String cid = aam.addMtomAttachment(new DataHandler(bads), null, null);
        assertEquals("cid:" + dhName, cid);
    }

}