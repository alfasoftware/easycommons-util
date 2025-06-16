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
package com.ebmwebsourcing.easycommons.xml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.xml.XMLPrettyPrinter;

public class XmlPrettyPrinterTest {

    public static Logger log = Logger.getLogger(XmlPrettyPrinterTest.class
            .getName());

    private static final DocumentBuilderFactory factory = DocumentBuilderFactory
            .newInstance();

    static {
        factory.setNamespaceAware(true);
    }

    @Test
    public void testPrettyPrinter() throws SAXException, IOException,
            ParserConfigurationException {

        Document doc = factory.newDocumentBuilder().parse(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("test.xml"));

        String res = XMLPrettyPrinter.prettyPrint(doc);

        assertNotNull(res);
    }

}
