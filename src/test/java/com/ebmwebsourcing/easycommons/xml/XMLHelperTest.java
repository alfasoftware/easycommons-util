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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.stream.EasyByteArrayOutputStream;

public class XMLHelperTest {

    @Test
    public void testCreateStringFromNodeList() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\"><ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject><ns0:body><customElt>[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt></ns0:body></ns0:mail>";
        String expected = "<customElt xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\">[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        String result = XMLHelper.toString(findBodyNode(doc).getChildNodes());
        assertTrue(XMLComparator.isEquivalent(expected, result));
    }

    @Test
    public void testCreateStringFromNodeListWithIllegalXmlCharEscaped() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\"><ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject><ns0:body>&lt;customElt&gt;[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload&lt;/customElt&gt;</ns0:body></ns0:mail>";
        String expected = "<customElt>[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        String result = XMLHelper.toString(findBodyNode(doc).getChildNodes());
        assertTrue(XMLComparator.isEquivalent(expected, result));
    }

    @Test
    public void testCreateStringFromNodeListWithCDATA() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\">"
                + "<ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject>"
                + "<ns0:body>"
                + "<![CDATA[<customElt>[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt>]]>"
                + "</ns0:body></ns0:mail>";
        String expected = "<customElt>[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        String result = XMLHelper.toString(findBodyNode(doc).getChildNodes());
        assertTrue(XMLComparator.isEquivalent(expected, result));
    }
    
    @Test
    public void testCreateStringFromDOMDocument() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\">"
                + "<ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject>"
                + "<ns0:body>"
                + "<![CDATA[<customElt>[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload</customElt>]]>"
                + "</ns0:body></ns0:mail>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        String result = XMLHelper.createStringFromDOMDocument(doc);
        assertTrue(XMLComparator.isEquivalent(message, result));
        // the end of the tag (?>) is omitted in the test because extra attributes can be added by the transformer
        assertTrue(result.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\""));
    }
    
    @Test
    public void testCreateStringFromDOMNode() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\"><ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject><ns0:body>&lt;customElt&gt;[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload&lt;/customElt&gt;</ns0:body></ns0:mail>";
        String expected = "<ns0:body xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\">&lt;customElt&gt;[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload&lt;/customElt&gt;</ns0:body>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        String result = XMLHelper.createStringFromDOMNode(findBodyNode(doc));
        assertTrue(XMLComparator.isEquivalent(expected, result));
        assertTrue(!result.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
    }
    
    @Test
    public void testWriteDocument() throws Exception {
        String message = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\"><ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject><ns0:body>&lt;customElt&gt;[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload&lt;/customElt&gt;</ns0:body></ns0:mail>";
        
        Document doc = createDOMDocumentFromMessage(message);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLHelper.writeDocument(doc, baos);
        String result = baos.toString();
        assertTrue(XMLComparator.isEquivalent(message, result));
        // the end of the tag (?>) is omitted in the test because extra attributes can be added by the transformer
        assertTrue(result.toLowerCase().startsWith("<?xml version=\"1.0\" encoding=\"utf-8\""));
    }
    
    @Test
    public void testCreateDocumentFromString() throws Exception {
        String xml = "<ns0:mail xmlns:ns0=\"http://petals.ow2.org/components/mail/version-3\"><ns0:host>192.168.1.206</ns0:host><ns0:from>user2@devmail.com</ns0:from><ns0:reply>user2@devmail.com</ns0:reply><ns0:to>user2@devmail.com</ns0:to><ns0:subject>Integration mail test(GenericService). MESSAGE TYPE: XML</ns0:subject><ns0:body>&lt;customElt&gt;[#IMAP-PAYLOAD-XML]Sending mail with addressing in the message payload&lt;/customElt&gt;</ns0:body></ns0:mail>";
        
        Document document = XMLHelper.createDocumentFromString(xml);
        
        Transformer transformer = Transformers.takeTransformer();
        try (EasyByteArrayOutputStream out = new EasyByteArrayOutputStream()) {
            transformer.transform(new DOMSource(document), new StreamResult(out));

            assertTrue(XMLComparator.isEquivalent(xml, out.toString()));
        } finally {
            Transformers.releaseTransformer(transformer);
        }
    }
    
    @Test
    public void testFindChildRecursiveFound() throws Exception {
        String xml = "<test:directory xmlns:test=\"http://petals.ow2.org/test/\">" +
            "<test:person><test:name>dupont</test:name><test:firstName>roger</test:firstName><test:address><test:street>rue de la fontaine</test:street><test:town>Marseille</test:town></test:address></test:person>" +
            "<test:person><test:name>durand</test:name><test:firstName>bernard</test:firstName><test:address><test:street>route de Paris</test:street><test:town>Lyon</test:town></test:address></test:person>" +
            "<test:person><test:name>dupond</test:name><test:firstName>jean</test:firstName><test:address><test:street>rue de l'URSS</test:street><test:town>Lille</test:town></test:address></test:person>" +
            "</test:directory>";
        
        Document document = XMLHelper.createDocumentFromString(xml);
        Node node = XMLHelper.findChild(document, "http://petals.ow2.org/test/", "street", true);

        assertNotNull(node);
        assertNotNull(node.getNamespaceURI() != null);
        assertEquals("http://petals.ow2.org/test/", node.getNamespaceURI());
        assertNotNull(node.getLocalName() != null);
        assertEquals("street", node.getLocalName());
        assertNotNull(node.getTextContent() != null);
        assertEquals("rue de la fontaine", node.getTextContent());
    }
    
    @Test
    public void testFindChildRecursiveNotFound() throws Exception {
        String xml = "<test:directory xmlns:test=\"http://petals.ow2.org/test/\">" +
        	"<test:person><test:name>dupont</test:name><test:firstName>roger</test:firstName><test:address><test:street>rue de la fontaine</test:street><test:town>Marseille</test:town></test:address></test:person>" +
        	"<test:person><test:name>durand</test:name><test:firstName>bernard</test:firstName><test:address><test:street>route de Paris</test:street><test:town>Lyon</test:town></test:address></test:person>" +
        	"<test:person><test:name>dupond</test:name><test:firstName>jean</test:firstName><test:address><test:street>rue de l'URSS</test:street><test:town>Lille</test:town></test:address></test:person>" +
        	"</test:directory>";
        
        Document document = XMLHelper.createDocumentFromString(xml);
        assertNull(XMLHelper.findChild(document, "http://petals.ow2.org/test/", "stret", true));
    }
    
    @Test
    public void testFindChildNotRecursiveNotFound() throws Exception {
        String xml = "<test:directory xmlns:test=\"http://petals.ow2.org/test/\">" +
            "<test:person><test:name>dupont</test:name><test:firstName>roger</test:firstName><test:address><test:street>rue de la fontaine</test:street><test:town>Marseille</test:town></test:address></test:person>" +
            "<test:person><test:name>durand</test:name><test:firstName>bernard</test:firstName><test:address><test:street>route de Paris</test:street><test:town>Lyon</test:town></test:address></test:person>" +
            "<test:person><test:name>dupond</test:name><test:firstName>jean</test:firstName><test:address><test:street>rue de l'URSS</test:street><test:town>Lille</test:town></test:address></test:person>" +
            "</test:directory>";
        
        Document document = XMLHelper.createDocumentFromString(xml);
        assertNull(XMLHelper.findChild(document, "http://petals.ow2.org/test/", "street", false));
    }
    
    @Test
    public void testFindChildNotRecursiveFound() throws Exception {
        String xml = "<test:directory xmlns:test=\"http://petals.ow2.org/test/\">" +
            "<test:person><test:name>dupont</test:name><test:firstName>roger</test:firstName><test:address><test:street>rue de la fontaine</test:street><test:town>Marseille</test:town></test:address></test:person>" +
            "<test:person><test:name>durand</test:name><test:firstName>bernard</test:firstName><test:address><test:street>route de Paris</test:street><test:town>Lyon</test:town></test:address></test:person>" +
            "<test:person><test:name>dupond</test:name><test:firstName>jean</test:firstName><test:address><test:street>rue de l'URSS</test:street><test:town>Lille</test:town></test:address></test:person>" +
            "</test:directory>";
        
        Document document = XMLHelper.createDocumentFromString(xml);
        Node node = XMLHelper.findChild(document.getDocumentElement(), null, "person", false);

        assertNotNull(node);
        assertNotNull(node.getNamespaceURI());
        assertEquals("http://petals.ow2.org/test/", node.getNamespaceURI());
        assertNotNull(node.getLocalName());
        assertEquals("person", node.getLocalName());
        assertNotNull(node.getFirstChild());
        assertNotNull(node.getFirstChild().getTextContent());
        assertEquals("dupont", node.getFirstChild().getTextContent());
    }
    
    private static final Document createDOMDocumentFromMessage(String message) throws SAXException, IOException {
        DocumentBuilder documentBuilder = null;
        
        Document doc = null;
        try {
            documentBuilder = DocumentBuilders.takeDocumentBuilder();
            doc = documentBuilder.parse(new ByteArrayInputStream(message.getBytes()));
        } finally {
            if (documentBuilder != null) {
                DocumentBuilders.releaseDocumentBuilder(documentBuilder);
            }
        }
        
        return doc;
    }
    
    private static final Node findBodyNode(Document doc) {
        Element rootElt = doc.getDocumentElement();
        NodeList rootChildNodes = rootElt.getChildNodes();
        for (int i = 0; i < rootChildNodes.getLength(); i++) {
            String n = rootChildNodes.item(i).getNodeName();
            if (n != null) {
                if (n.endsWith("body")) {
                    return rootChildNodes.item(i);
                }
            }
        }
        
        return null;
    }

    /**
     * Unit tests of {@link XMLHelper#getChildrenElementNS(Node, String)}
     */
    @Test
    public void getChildrenElementNS() throws SAXException, IOException {

        final URL xmlUri = Thread.currentThread().getContextClassLoader().getResource("getChildrenElementNS.xml");
        assertNotNull(xmlUri);
        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        try {
            final Document document = docBuilder.parse(xmlUri.openStream());
            final Element rootElement = document.getDocumentElement();

            final List<Element> childrenEltsOfRoot = XMLHelper.getChildrenElementNS(rootElement,
                    "http://petals.ow2.org/test/");
            assertNotNull(childrenEltsOfRoot);
            assertEquals(3, childrenEltsOfRoot.size());
            for (final Element childElt : childrenEltsOfRoot) {
                assertEquals("person", childElt.getLocalName());
            }

            assertTrue(XMLHelper.getChildrenElementNS(rootElement, "http://petals.ow2.org/test/1").isEmpty());

            final List<Element> childrenEltsOf1stPerson_1 = XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0),
                    "http://petals.ow2.org/test/1");
            assertNotNull(childrenEltsOf1stPerson_1);
            assertEquals(2, childrenEltsOf1stPerson_1.size());

            final List<Element> childrenEltsOf1stPerson_2 = XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0),
                    "http://petals.ow2.org/test/2");
            assertNotNull(childrenEltsOf1stPerson_2);
            assertEquals(1, childrenEltsOf1stPerson_2.size());
            assertEquals("address", childrenEltsOf1stPerson_2.get(0).getLocalName());

            assertTrue(XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0), "uri:another-namespace").isEmpty());

            final List<Element> childrenEltsOf1stPersonAdress = XMLHelper
                    .getChildrenElementNS(childrenEltsOf1stPerson_2.get(0), "http://petals.ow2.org/test/1");
            assertNotNull(childrenEltsOf1stPersonAdress);
            assertEquals(2, childrenEltsOf1stPersonAdress.size());
        } finally {
            DocumentBuilders.releaseDocumentBuilder(docBuilder);
        }
    }

    /**
     * Unit tests of {@link XMLHelper#getChildrenElementNS(Node, String, String)}
     */
    @Test
    public void getChildrenElementNS_withLocalName() throws SAXException, IOException {

        final URL xmlUri = Thread.currentThread().getContextClassLoader().getResource("getChildrenElementNS.xml");
        assertNotNull(xmlUri);
        final DocumentBuilder docBuilder = DocumentBuilders.takeDocumentBuilder();
        try {
            final Document document = docBuilder.parse(xmlUri.openStream());
            final Element rootElement = document.getDocumentElement();

            final List<Element> childrenEltsOfRoot = XMLHelper.getChildrenElementNS(rootElement,
                    "http://petals.ow2.org/test/", "person");
            assertNotNull(childrenEltsOfRoot);
            assertEquals(3, childrenEltsOfRoot.size());
            for (final Element childElt : childrenEltsOfRoot) {
                assertEquals("person", childElt.getLocalName());
            }

            assertTrue(XMLHelper.getChildrenElementNS(rootElement, "http://petals.ow2.org/test/1", "person").isEmpty());

            final List<Element> childrenEltsOf1stPerson_1 = XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0),
                    "http://petals.ow2.org/test/1", "firstName");
            assertNotNull(childrenEltsOf1stPerson_1);
            assertEquals(1, childrenEltsOf1stPerson_1.size());
            assertEquals("firstName", childrenEltsOf1stPerson_1.get(0).getLocalName());

            final List<Element> childrenEltsOf1stPerson_2 = XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0),
                    "http://petals.ow2.org/test/2", "address");
            assertNotNull(childrenEltsOf1stPerson_2);
            assertEquals(1, childrenEltsOf1stPerson_2.size());
            assertEquals("address", childrenEltsOf1stPerson_2.get(0).getLocalName());

            assertTrue(XMLHelper.getChildrenElementNS(childrenEltsOfRoot.get(0), "uri:another-namespace", "firstName")
                    .isEmpty());

            final List<Element> childrenEltsOf1stPersonAdress = XMLHelper
                    .getChildrenElementNS(childrenEltsOf1stPerson_2.get(0), "http://petals.ow2.org/test/1", "street");
            assertNotNull(childrenEltsOf1stPersonAdress);
            assertEquals(1, childrenEltsOf1stPersonAdress.size());
            assertEquals("street", childrenEltsOf1stPersonAdress.get(0).getLocalName());
        } finally {
            DocumentBuilders.releaseDocumentBuilder(docBuilder);
        }
    }
}
