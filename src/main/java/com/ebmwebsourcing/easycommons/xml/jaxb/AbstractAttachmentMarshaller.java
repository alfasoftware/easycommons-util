/**
 * Copyright (c) 2017-2023 Linagora
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.attachment.AttachmentMarshaller;

public abstract class AbstractAttachmentMarshaller extends AttachmentMarshaller {

    protected abstract void addAttachment(String cid, DataHandler data);

    @Override
    public String addMtomAttachment(DataHandler data, String elementNamespace, String elementLocalName) {
        final String cid;
        if (data.getName() != null && !data.getName().trim().isEmpty()) {
            cid = data.getName().trim();
        } else {
            cid = UUID.randomUUID().toString();
        }
        this.addAttachment(cid, data);
        return "cid:" + cid;
    }

    @Override
    public String addMtomAttachment(final byte[] data, final int offset, final int length, final String mimeType,
            String elementNamespace, String elementLocalName) {
        DataHandler dh = new DataHandler(new DataSource() {
            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(data, offset, length);
            }

            @Override
            public String getContentType() {
                return mimeType;
            }
        });
        return addMtomAttachment(dh, elementNamespace, elementLocalName);
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        return addMtomAttachment(data, null, null);
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }
}
