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
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.stream.EasyByteArrayOutputStream;

/**
 * Utilities methods for XML operations
 * 
 * @author Mathieu CARROLLE - EBM WebSourcing
 * @author Nicolas Oddoux - EBM WebSourcing
 * 
 */
public final class XMLHelper {

    private static final String XML_STR_ENCODING = "UTF-8";

    /**
     * Create a {@link String} from a Node list
     * 
     * @param list
     *            a node list
     * @return the string representing the node list
     */
    public static final String toString(NodeList list) {
        final StringBuilder sb = new StringBuilder();
        
        if (list != null) {
            Node cNode = null;
            for (int i = 0; i < list.getLength(); i++) {
                cNode = list.item(i);
                if (cNode.getNodeType() == Node.TEXT_NODE
                        || cNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                    sb.append(cNode.getTextContent());
                } else {
                    sb.append(XMLPrettyPrinter.prettyPrint(list.item(i)));
                }
            }
        }
        
        return sb.toString();
    }   
    
    /**
     * Create a String result from a DOM document
     * 
     * @param document
     *            the DOM Document. It cannot be null
     * @return a String representation of the DOM Document
     * @throws TransformerException
     *             if an unrecoverable error occurs during the course of the
     *             transformation
     * @throws PoolException
     *             This method uses a transformer got from a transformer pool.
     *             This unchecked exception is thrown if the current thread
     *             waits for a transformer of the pool and is interrupted (as
     *             the pool policy is WAIT)
     */
    public static final String createStringFromDOMDocument(Node document)
            throws TransformerException {
        return createStringFromDOMNode(document, false);
    }
    
    /**
     * Create a String result from a DOM Node
     * 
     * @param node
     *            the DOM Node. It cannot be null
     * @return a String representation of the DOM Document
     * @throws TransformerException
     *             if an unrecoverable error occurs during the course of the
     *             transformation
     * @throws PoolException
     *             This method uses a transformer got from a transformer pool.
     *             This unchecked exception is thrown if the current thread
     *             waits for a transformer of the pool and is interrupted (as
     *             the pool policy is WAIT)
     */
    public static final String createStringFromDOMNode(Node node) throws TransformerException {
        return createStringFromDOMNode(node, true);
    }

    /**
     * Create a String result from a DOM Node
     * 
     * @param node
     *            the DOM Node. It cannot be null
     * @param omitDeclaration
     *            a flag to indicate to omit the XML declaration
     * @return a String representation of the DOM Document
     * @throws TransformerException
     *             if an unrecoverable error occurs during the course of the
     *             transformation
     * @throws PoolException
     *             This method uses a transformer got from a transformer pool.
     *             This unchecked exception is thrown if the current thread
     *             waits for a transformer of the pool and is interrupted (as
     *             the pool policy is WAIT)
     */
    public static final String createStringFromDOMNode(Node node, boolean omitDeclaration)
            throws TransformerException {
        assert node != null;
        
        node.normalize();
        
        Transformer transformer = Transformers.takeTransformer();
        try (EasyByteArrayOutputStream out = new EasyByteArrayOutputStream()) {
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            if (omitDeclaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            } else {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            }

            transformer.transform(new DOMSource(node), new StreamResult(out));

            return out.toString();
        } finally {
            Transformers.releaseTransformer(transformer);
        }
    }

    /**
     * Write a document to an output stream. The specified output stream and
     * document cannot be null.
     * 
     * @param document
     *            the document to write to the output stream
     * @param outputStream
     *            the output stream to write the document
     * 
     * @throws TransformerException
     *             if an unrecoverable error occurs during the course of the
     *             transformation
     * @throws PoolException
     *             This method uses a transformer got from a transformer pool.
     *             This unchecked exception is thrown if the current thread
     *             waits for a transformer of the pool and is interrupted (as
     *             the pool policy is WAIT)
     */
    public static final void writeDocument(Document document, OutputStream outputStream) throws TransformerException {
        assert document != null;
        assert outputStream != null;
        
        Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } finally {
            Transformers.releaseTransformer(transformer);
        }
    }

    /**
     * Create a DOM document from the specified XML string
     * 
     * @param xml
     *            a XML string
     * 
     * @return the DOM document representing the specified XML string
     * 
     * @throws IOException
     *             If any IO errors occur
     * @throws SAXException
     *             If any parse errors occur
     * @throws PoolException
     *             This method uses a document builder got from a document
     *             builder pool. This unchecked exception is thrown if the
     *             current thread waits for a document builder of the pool and
     *             is interrupted (as the pool policy is WAIT)
     */
    public static final Document createDocumentFromString(final String xml) throws SAXException,
            IOException {
        assert xml != null;       
        
        final Reader in = new StringReader(xml);
        final InputSource inputSource = new InputSource(in);

        final DocumentBuilder documentBuilder = DocumentBuilders.takeDocumentBuilder();
        try {
            final Document document = documentBuilder.parse(inputSource);
            document.normalize();
            
            return document;
        } finally {
            DocumentBuilders.releaseDocumentBuilder(documentBuilder);
        }
    }

    /**
     * Search for the first child of the specified parent node with the
     * specified namespace URI and local name. If recursive, first, search in all the
     * children of first level, then if not found, search in the second level of
     * the first child, ...
     * 
     * @param parentNode
     *            a parent node
     * @param namespaceURI
     *            the namespace URI of the node to search. if null, the namespace
     *            is ignored
     * @param nodeName
     *            the local name of the node to search
     * @param recursive
     *            a flag to know the search is recursive in the XML tree
     * 
     * @return a node if found or null if not found
     */
    public static final Node findChild(Node parentNode, String namespaceURI, String nodeName, 
            boolean recursive) {
        Node resultNode = null;
        
        if (parentNode != null && nodeName != null) {
            parentNode.normalize();
            
            NodeList nodeList = parentNode.getChildNodes();
            resultNode = lookUpNodeInNodeList(nodeName, namespaceURI, nodeList);
            
            // now, search recursively if required
            if (resultNode == null && recursive) {
                for (int i = 0; i < nodeList.getLength() && resultNode == null; i++) {
                    resultNode = findChild(nodeList.item(i), namespaceURI, nodeName, true);
                }
            }
        }
        
        return resultNode;
    }

    /**
     * Look up the first node with the specified namespace URI and local name in a
     * specified node list.
     * 
     * @param nodeName
     *            the local name of the node
     * @param namespaceURI
     *            the namespace URI of the node. If null, the namespace is ignored.
     * @param nodeList
     *            the list of nodes
     * @return the first node with the specified namespace and name or null if not found
     */
    private static final Node lookUpNodeInNodeList(String nodeName, String namespaceURI,
            NodeList nodeList) {
        Node resultNode = null;
        
        for (int i = 0; i < nodeList.getLength() && resultNode == null; i++) {
            Node tmpNode = nodeList.item(i);
            if (namespaceURI != null && tmpNode.getNamespaceURI() != null
                    && tmpNode.getNamespaceURI().equals(namespaceURI)
                    && nodeName.equals(tmpNode.getLocalName())) {
                resultNode = tmpNode;
            } else if (nodeName.equals(tmpNode.getLocalName())) {
                resultNode = tmpNode;
            }
        }
        
        return resultNode;
    }

    /**
     * Gets the direct children elements of a node, not recursively, matching the given namespace.
     * 
     * @param parent
     *            The {@link Node} containing the children elements to find. Not {@code null}.
     * @param namespaceURI
     *            The namespace to match. Not {@code null}
     * @return The children elements matching the given namespace. If no child element is found, an empty list is
     *         returned.
     */
    public static List<Element> getChildrenElementNS(final Node parent, final String namespaceURI) {
        assert parent != null;
        assert namespaceURI != null;

        final List<Element> childrenElts = new ArrayList<>();
        final NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && namespaceURI.equals(child.getNamespaceURI())) {
                childrenElts.add((Element) child);
            }
        }
        return childrenElts;
    }

    /**
     * Gets the direct children elements of a node, not recursively, matching given local name and namespace.
     * 
     * @param parent
     *            The {@link Node} containing the children elements to find. Not {@code null}.
     * @param namespaceURI
     *            The namespace to match. Not {@code null}
     * @param localName
     *            The local-name to match. Not {@code null}
     * @return The children elements matching the given namespace and local-name. If no child element is found, an empty
     *         list is returned.
     */
    public static List<Element> getChildrenElementNS(final Node parent, final String namespaceURI,
            final String localName) {
        assert parent != null;
        assert namespaceURI != null;
        assert localName != null;

        final List<Element> nodeList = new ArrayList<>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE && localName.equals(child.getLocalName())
                    && namespaceURI.equals(child.getNamespaceURI())) {
                nodeList.add((Element) child);
            }
        }

        return nodeList;
    }
}
