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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class QNameHelper {

    private QNameHelper() {
        // Utility class --> No constructor
    }

    public static String getPrefix(final String name) {
        String res = null;
        if ((name != null) && (name.indexOf(':') != -1)) {
            res = name.substring(0, name.indexOf(':'));
        }
        return res;
    }

    public static String getLocalPartWithoutPrefix(final String name) {
        String res = name;
        if (name.indexOf(':') != -1) {
            res = name.substring(name.indexOf(':') + 1, name.length());
        }
        return res;
    }

    /**
     * Convert a {@link String} {@code xxxx:yyyy} into a {@link QName} with the given prefix definitions
     * 
     * @param container
     *            A DOM node containing prefix definitions. Not {@code null}
     * @param text
     *            The {@link String to convert}. Not {@code null)}
     * @param defaultNs
     *            A default namespace to use if no prefix is included in the {@link String} to convert. If {@code null},
     *            the namespace {@link XMLConstants#NULL_NS_URI} will be used.
     * @return
     * @throws IllegalArgumentException
     *             Invalid {@link String} given
     */
    public static final QName fromString(final Node container, final String text, final String defaultNs) {

        assert container != null;
        assert text != null;

        if (text.trim().isEmpty()) {
            return new QName("");
        } else {
            final String[] texts = text.split(":");
            final int size = texts.length;
            final String localName;
            final String namespaceURI;
            if (size == 1) {
                localName = texts[0];
                namespaceURI = defaultNs;
            } else if (size == 2) {
                namespaceURI = container.lookupNamespaceURI(texts[0]);
                localName = texts[1];
            } else {
                throw new IllegalArgumentException("Invalid QName string '" + text + "'");
            }
            return new QName(namespaceURI, localName);
        }
    }

    /**
     * <p>
     * Convert a {@link String} {@code xxxx:yyyy} into a {@link QName} with the given prefix definitions.
     * </p>
     * <p>
     * The default namespace used is the namespace of the DOM node.
     * </p>
     * 
     * @param container
     *            A DOM node containing prefix definitions. Not {@code null}
     * @param text
     *            The {@link String} to convert. Not {@code null}
     * @return
     * @throws IllegalArgumentException
     *             Invalid {@link String} given
     */
    public static final QName fromString(final Node container, final String text) {
        assert container != null;
        assert text != null;

        return fromString(container, text, container.getNamespaceURI());
    }

    /**
     * <p>
     * Convert a DOM node content value as {@link String} {@code xxxx:yyyy} into a {@link QName} with the given prefix
     * definitions.
     * </p>
     * <p>
     * The default namespace used is the namespace of the DOM node.
     * </p>
     * 
     * @param node
     *            The DOM node containing the value to convert and prefix definitions. Not {@code null}.
     * @return
     * @throws IllegalArgumentException
     *             Invalid DOM node content value
     */
    public static final QName fromString(final Node node) {

        assert node != null;

        final String textContent = node.getTextContent();
        return fromString(node, textContent);
    }

    /**
     * <p>
     * Convert a DOM attribute value into a {@link QName}. Prefix definitions are extracted from the given DOM element.
     * </p>
     * <p>
     * The default namespace used is the namespace of the DOM element.
     * </p>
     * 
     * @param element
     *            The DOM element containing the attribute. Not {@code null}
     * @param attributeName
     *            The attribute name for which the value must be converted into {@link QName}. Not {@code null}
     * @return
     * @throws IllegalArgumentException
     *             Invalid DOM attribute content value.
     */
    public static final QName fromAttribute(final Element element, final String attributeName) {
        assert element != null;
        assert attributeName != null;

        return fromString(element, element.getAttribute(attributeName));
    }
    
}
