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
package com.ebmwebsourcing.easycommons.soap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.XMLConstants;

import jakarta.xml.soap.SOAPConstants;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.xml.IgnoreIrrelevantNodesDifferenceListener;



public final class SoapComparator {

    private SoapComparator() {
    }

    public static boolean isEquivalent(String s1, String s2) {
        return isEquivalent(new ByteArrayInputStream(s1.getBytes()), new ByteArrayInputStream(s2.getBytes()));
    }

    public static boolean isEquivalent(InputStream is1, InputStream is2) {
        try {
            XMLUnit.setIgnoreAttributeOrder(true);
            XMLUnit.setIgnoreWhitespace(true);

            Diff diff = XMLUnit.compareXML(new InputStreamReader(is1), new InputStreamReader(is2));
            diff.overrideDifferenceListener(new IgnoreIrrelevantNodesDifferenceListener() {

                @Override
                protected boolean isIrrelevantChildNode(Node node) {
                    assert node != null;
                    if (SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE.equals(node.getNamespaceURI()) &&
                            "Header".equals(node.getLocalName()) &&
                            !node.hasChildNodes()) return true;
                    return false;
                }

                @Override
                protected boolean isIrrelevantAttribute(Attr att) {
                    assert att != null;
                    return XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(att.getNamespaceURI()) ||
                        XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(att.getNamespaceURI());
                }
            });
            if (!diff.identical()) {
                System.err.println(new DetailedDiff(diff));
            }

            return diff.identical();
        } catch (SAXException se) {
            throw new UncheckedException(se);
        } catch (IOException ioe) {
            throw new UncheckedException(ioe);
        }
    }




}
