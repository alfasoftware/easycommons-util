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
package com.ebmwebsourcing.easycommons.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Unit tests of {@link QNameHelper}
 * 
 * @author Christophe DENEUX - Linagora
 *
 */
public class QNameHelperTest {

    private static final DocumentBuilder DOM_DOCUMENT_BUILDER;

    private static final String NS_ROOT = "http://petals.ow2.org/root";

    private static final String NS_VALUE = "http://petals.ow2.org/value";

    private static final String NS_PREFIX = "prefix";

    private static final String LOCAL_PART_VALUE = "local-part-value";

    private static final String ATTRIBUTE_NAME = "my-attribute-name";

    static {
        try {
            final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DOM_DOCUMENT_BUILDER = docFactory.newDocumentBuilder();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check conversion of a DOM node content value when it is correctly set as a QName.
     */
    @Test
    public void convertDomNodeContentValue_nominal() {

        final Element element = createDomElement();
        element.setTextContent(NS_PREFIX + ":" + LOCAL_PART_VALUE);

        assertEquals(new QName(NS_VALUE, LOCAL_PART_VALUE), QNameHelper.fromString(element));
    }

    /**
     * Check conversion of a DOM node content value not set.
     */
    @Test
    public void convertDomNodeContentValue_missingContentValue() {

        final Element element = createDomElement();

        assertEquals(new QName(""), QNameHelper.fromString(element));
    }

    /**
     * Check conversion of a DOM node content value set to an empty value.
     */
    @Test
    public void convertDomNodeContentValue_emptyContentValue() {

        final Element element = createDomElement();
        element.setTextContent("");

        assertEquals(new QName(""), QNameHelper.fromString(element));
    }

    /**
     * Check conversion of a DOM node content value set to a value without prefix.
     */
    @Test
    public void convertDomNodeContentValue_noPrefix() {

        final Element element = createDomElement();
        element.setTextContent(LOCAL_PART_VALUE);

        assertEquals(new QName(NS_ROOT, LOCAL_PART_VALUE), QNameHelper.fromString(element));
    }

    /**
     * Check conversion of a DOM node content value set to a value containing 2 colons.
     */
    @Test()
    public void convertDomNodeContentValue_twoColons() {

        final Element element = createDomElement();
        final String invalidLocalValue = "value-with-colon:end";
        element.setTextContent(NS_PREFIX + ":" + invalidLocalValue);

        try {
            QNameHelper.fromString(element);
            fail("IllegalArgumentException should be thrown !");
        } catch (final IllegalArgumentException e) {
            if (!e.getMessage().contains(invalidLocalValue)) {
                fail("Unexpected exception: " + e.getMessage());
            }
        }
    }

    /**
     * Check conversion of a DOM attribute value when it is correctly set as a QName.
     */
    @Test
    public void convertDomAttributeValue_nominal() {

        final Element element = createDomElementWithAttribute(NS_PREFIX + ":" + LOCAL_PART_VALUE);

        assertEquals(new QName(NS_VALUE, LOCAL_PART_VALUE), QNameHelper.fromAttribute(element, ATTRIBUTE_NAME));
    }

    /**
     * Check conversion of a DOM attribute value not set.
     */
    @Test
    public void convertDomAttributeValue_missingContentValue() {

        final Element element = createDomElementWithAttribute(null);

        assertEquals(new QName(""), QNameHelper.fromAttribute(element, ATTRIBUTE_NAME));
    }

    /**
     * Check conversion of a DOM attribute value set to an empty value.
     */
    @Test
    public void convertDomAttributeValue_emptyContentValue() {

        final Element element = createDomElementWithAttribute("");
        element.setTextContent("");

        assertEquals(new QName(""), QNameHelper.fromAttribute(element, ATTRIBUTE_NAME));
    }

    /**
     * Check conversion of a DOM attribute value set to a value without prefix.
     */
    @Test
    public void convertDomAttributeValue_noPrefix() {

        final Element element = createDomElementWithAttribute(LOCAL_PART_VALUE);

        assertEquals(new QName(NS_ROOT, LOCAL_PART_VALUE), QNameHelper.fromAttribute(element, ATTRIBUTE_NAME));
    }

    /**
     * Check conversion of a DOM attribute value set to a value containing 2 colons.
     */
    @Test()
    public void convertDomAttributeValue_twoColons() {

        final String invalidLocalValue = "value-with-colon:end";
        final Element element = createDomElementWithAttribute(NS_PREFIX + ":" + invalidLocalValue);

        try {
            QNameHelper.fromAttribute(element, ATTRIBUTE_NAME);
            fail("IllegalArgumentException should be thrown !");
        } catch (final IllegalArgumentException e) {
            if (!e.getMessage().contains(invalidLocalValue)) {
                fail("Unexpected exception: " + e.getMessage());
            }
        }
    }

    private static Element createDomElement() {

        final Document doc = DOM_DOCUMENT_BUILDER.newDocument();
        final Element element = doc.createElementNS(NS_ROOT, "root");
        element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + NS_PREFIX, NS_VALUE);
        doc.appendChild(element);
        return element;
    }

    private static Element createDomElementWithAttribute(final String attributeValue) {

        final Element element = createDomElement();
        element.setAttribute(ATTRIBUTE_NAME, attributeValue);
        return element;
    }

}
