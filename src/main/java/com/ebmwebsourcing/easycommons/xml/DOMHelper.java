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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.lang.UncheckedException;
import com.ebmwebsourcing.easycommons.pooling.PoolException;

public class DOMHelper {

    private DOMHelper() {
    }

    /**
     * This shouldn't be used with a {@link Node} already owned by another document.
     * 
     * @param node
     * @return
     */
    public static Document toDocument(final Node node) {
        if (node instanceof Document) {
            return (Document) node;
        } else {
            final Document document = DocumentBuilders.newDocument();
            document.appendChild(node);
            return document;
        }
    }

    public static DOMSource parseAsDOMSource(URL url) {
        assert url != null;
        try {
            return parseAsDOMSource(url.openStream());
        } catch (IOException ioe) {
            throw new UncheckedException(String.format("Cannot open stream for URL '%s'",
                    url.toString()), ioe);
        }
    }

    public static DOMSource parseAsDOMSource(InputStream is) {
        DocumentBuilder documentBuilder = null;
        
        try {
            documentBuilder = DocumentBuilders.takeDocumentBuilder();            
            Document doc = documentBuilder.parse(is);
            DOMSource domSource = new DOMSource(doc);
            return domSource;
        } catch (SAXException se) {
            throw new RuntimeException(se);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (PoolException pe) {
            throw new RuntimeException(pe);
        } finally {
            if(documentBuilder != null) {
                DocumentBuilders.releaseDocumentBuilder(documentBuilder);
            }
        }
    }

    public static DOMSource parseAsDOMSource(String testResourceName) {        
        DocumentBuilder documentBuilder = null;
        
        try {
            URL resourceURL = DOMHelper.class.getResource(testResourceName);
            assert resourceURL != null;
            
            documentBuilder = DocumentBuilders.takeDocumentBuilder();            
            Document doc = documentBuilder.parse(resourceURL.toURI().toString());

            DOMSource domSource = new DOMSource(doc);
            domSource.setSystemId(resourceURL.toString());

            return domSource;
        } catch (SAXException se) {
            throw new RuntimeException(se);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } catch (PoolException pe) {
            throw new RuntimeException(pe);
        } catch (URISyntaxException use) {
            throw new RuntimeException(use);
        } finally {
            if(documentBuilder != null) {
                DocumentBuilders.releaseDocumentBuilder(documentBuilder);
            }
        }
    }

    public static Node stripEmptyTextNodes(Node node) {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        final String xpathStr = "//text()[normalize-space(.) = '']";
        try {
            XPathExpression xpathExp = xpathFactory.newXPath().compile(xpathStr);
            NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(node, XPathConstants.NODESET);

            // Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
            return node;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(String.format("Problem while evaluating xpath '%s'.",
                    xpathStr));
        }
    }

    // TODO : this method is not very elegant... there should be another way to
    // do this
    // through our dependencies (xmlunit...?).
    public static boolean areDOMNodeEqualRegardlessOfNamespaceBindings(Node n1, Node n2) {
        if (n1 == n2) {
            return true;
        }
        if (n1.getNodeType() != n2.getNodeType())
            return false;

        if (n1.getNodeName() == null) {
            if (n1.getNodeName() != null) {
                return false;
            }
        } else if (!n1.getNodeName().equals(n2.getNodeName())) {
            return false;
        }

        if (n1.getLocalName() == null) {
            if (n2.getLocalName() != null) {
                return false;
            }
        } else if (!n1.getLocalName().equals(n2.getLocalName())) {
            return false;
        }

        if (n1.getNamespaceURI() == null) {
            if (n2.getNamespaceURI() != null) {
                return false;
            }
        } else if (!n1.getNamespaceURI().equals(n2.getNamespaceURI())) {
            return false;
        }

        if (n1.getPrefix() == null) {
            if (n2.getPrefix() != null) {
                return false;
            }
        } else if (!n1.getPrefix().equals(n2.getPrefix())) {
            return false;
        }

        if (n1.getNodeValue() == null) {
            if (n2.getNodeValue() != null) {
                return false;
            }
        } else if (!n1.getNodeValue().equals(n2.getNodeValue())) {
            return false;
        }

        // there are many ways to do this test, and there isn't any way
        // better than another. Performance may vary greatly depending on
        // the implementations involved. This one should work fine for us.
        Node child1 = n1.getFirstChild();
        Node child2 = n2.getFirstChild();
        while (child1 != null && child2 != null) {
            if (!child1.isEqualNode(child2)) {
                return false;
            }
            child1 = child1.getNextSibling();
            child2 = child2.getNextSibling();
        }
        if (child1 != child2) {
            return false;
        }

        NamedNodeMap map1 = n1.getAttributes();
        NamedNodeMap map2 = n2.getAttributes();
        int len = map1.getLength();
        for (int i = 0; i < len; i++) {
            Node an1 = map1.item(i);
            assert an1.getLocalName() != null;
            if ((an1.getNamespaceURI() != null)
                    && (an1.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")))
                continue;
            Node an2 = map2.getNamedItemNS(an1.getNamespaceURI(), an1.getLocalName());
            if (an2 == null || !an1.isEqualNode(an2)) {
                return false;
            }
        }
        len = map2.getLength();
        for (int i = 0; i < len; i++) {
            Node an2 = map2.item(i);
            assert an2.getLocalName() != null;
            if ((an2.getNamespaceURI() != null)
                    && (an2.getNamespaceURI().equals("http://www.w3.org/XML/1998/namespace")))
                continue;
            Node an1 = map2.getNamedItemNS(an2.getNamespaceURI(), an2.getLocalName());
            if (an1 == null || !an2.isEqualNode(an1)) {
                return false;
            }
        }
        return true;

    }

    public static void prettyPrint(Node node, OutputStream os) {
        Transformer transformer = null;
        
        try {
            transformer = Transformers.takeTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(os));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } finally {
            if(transformer != null) {
                Transformers.releaseTransformer(transformer);
            }
        }
    }

    public static String findDefaultNamespace(Node node) {
        String nodePrefix = node.getPrefix();
        if (nodePrefix == null)
            return node.getNamespaceURI();
        Node xmlnsNode = node.getAttributes().getNamedItem(XMLConstants.XMLNS_ATTRIBUTE);
        if (xmlnsNode != null)
            return xmlnsNode.getNodeValue();
        Node parentNode = node.getParentNode();
        if (parentNode == null)
            return null;
        return findDefaultNamespace(parentNode);
    }

}
