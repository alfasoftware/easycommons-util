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

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class IgnoreIrrelevantNodesDifferenceListener implements DifferenceListener {

    public IgnoreIrrelevantNodesDifferenceListener() {
    }

    protected abstract boolean isIrrelevantAttribute(Attr att);

    protected abstract boolean isIrrelevantChildNode(Node node);

    @Override
    public void skippedComparison(Node arg0, Node arg1) {
    }

    private final boolean haveSameRelevantChildNodes(Node controlNode, Node testNode) {
        int c = 0;
        int t = 0;
        NodeList controlChildNodes = controlNode.getChildNodes();
        NodeList testChildNodes = testNode.getChildNodes();
        while (true) {
            if (c >= controlChildNodes.getLength())
                break;
            if (t >= testChildNodes.getLength())
                break;
            Node controlChildNode = controlChildNodes.item(c);
            Node testChildNode = controlChildNodes.item(t);
            if (isIrrelevantChildNode(controlChildNode)) {
                ++c;
                continue;
            }
            if (isIrrelevantChildNode(testChildNode)) {
                ++t;
                continue;
            }
            // this is used for comparing sequence of child nodes, we can just
            // consider child node types
            // and names
            if (controlNode.getNodeType() != testNode.getNodeType())
                return false;
            QName controlNodeQName = new QName(controlNode.getNamespaceURI(),
                    controlNode.getLocalName());
            QName testNodeQName = new QName(testNode.getNamespaceURI(),
                    controlNode.getLocalName());
            if (!controlNodeQName.equals(testNodeQName))
                return false;
            ++c;
            ++t;
        }
        if ((c >= controlChildNodes.getLength()) && (t >= controlChildNodes.getLength()))
            return true;
        return false;
    }

    private final int getRelevantChildNodesCount(Node node) {
        NodeList childNodes = node.getChildNodes();
        int count = 0;
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            if (isIrrelevantChildNode(childNode))
                continue;
            ++count;
        }
        return count;
    }

    private final boolean haveSameRelevantAttributes(Element controlElement, Element testElement) {
        NamedNodeMap attNodes = controlElement.getAttributes();
        for (int i = 0; i < attNodes.getLength(); ++i) {
            Attr attr = (Attr) attNodes.item(i);
            if (isIrrelevantAttribute(attr))
                continue;
            if (!testElement.hasAttributeNS(attr.getNamespaceURI(), attr.getLocalName())) {
                return false;
            }
        }
        return true;
    }

    private final int getRelevantAttributesCount(Element e) {
        // ignore xmlns declarations which are not used and xsi:type
        NamedNodeMap attNodes = e.getAttributes();
        int count = 0;
        for (int i = 0; i < attNodes.getLength(); ++i) {
            Attr attr = (Attr) attNodes.item(i);
            if (isIrrelevantAttribute(attr))
                continue;
            ++count;
        }
        return count;
    }

    @Override
    public int differenceFound(Difference d) {
        if (d.getId() == DifferenceConstants.CHILD_NODELIST_LENGTH_ID) {
            Node controlNode = d.getControlNodeDetail().getNode();
            Node testNode = d.getTestNodeDetail().getNode();
            if (getRelevantChildNodesCount(controlNode) == getRelevantChildNodesCount(testNode)) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }

        if (d.getId() == DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID) {
            Node controlNode = d.getControlNodeDetail().getNode();
            Node testNode = d.getTestNodeDetail().getNode();
            if (haveSameRelevantChildNodes(controlNode, testNode)) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }

        }

        if (d.getId() == DifferenceConstants.CHILD_NODE_NOT_FOUND_ID) {
            Node controlNode = d.getControlNodeDetail().getNode();
            Node testNode = d.getTestNodeDetail().getNode();
            if (((controlNode != null) && isIrrelevantChildNode(controlNode))
                    || ((testNode != null) && isIrrelevantChildNode(testNode))) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }

        if (d.getId() == DifferenceConstants.ELEMENT_NUM_ATTRIBUTES_ID) {
            Element controlElement = (Element) d.getControlNodeDetail().getNode();
            Element testElement = (Element) d.getTestNodeDetail().getNode();
            if (getRelevantAttributesCount(controlElement) == getRelevantAttributesCount(testElement)) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }
        if (d.getId() == DifferenceConstants.ATTR_NAME_NOT_FOUND_ID) {
            Element controlElement = (Element) d.getControlNodeDetail().getNode();
            Element testElement = (Element) d.getTestNodeDetail().getNode();
            if (haveSameRelevantAttributes(controlElement, testElement)
                    && haveSameRelevantAttributes(testElement, controlElement)) {
                return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
            }
        }
        if (d.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID) {
            return RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
        }
        return RETURN_ACCEPT_DIFFERENCE;
    }
}
