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

import java.io.IOException;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;

import com.ebmwebsourcing.easycommons.stream.EasyByteArrayOutputStream;

public abstract class AbstractAttachmentUnmarshaller extends AttachmentUnmarshaller {

    protected abstract DataHandler getAttachment(String cid);

    @Override
    public DataHandler getAttachmentAsDataHandler(final String cid) {
        String id;
        if (cid.startsWith("cid:")) {
            id = cid.substring(4);
        } else {
            id = cid;
        }
        return this.getAttachment(id);
    }

    @Override
    public byte[] getAttachmentAsByteArray(final String cid) {
        final DataHandler dh = this.getAttachmentAsDataHandler(cid);
        if (dh != null) {
            try (final EasyByteArrayOutputStream ebaos = new EasyByteArrayOutputStream()) {
                dh.writeTo(ebaos);
                return ebaos.toRawByteArray();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }
}
