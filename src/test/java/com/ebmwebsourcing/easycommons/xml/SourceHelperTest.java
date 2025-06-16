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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.io.IOHelper;

/**
 * 
 * @author Mathieu CARROLLE - EBM WebSourcing
 * 
 */
public class SourceHelperTest {

    private static final Source[] newTestSources(String xmlMessage)
            throws XMLStreamException, FactoryConfigurationError {
        Source streamSource = new StreamSource(new ByteArrayInputStream(xmlMessage.getBytes()));
        Source streamSource2 = new StreamSource(new StringReader(xmlMessage));
        Source domSource = DOMHelper.parseAsDOMSource(new ByteArrayInputStream(xmlMessage
                .getBytes()));
        Source saxSource = new SAXSource(new InputSource(new ByteArrayInputStream(xmlMessage.getBytes())));
        Source staxSource = new StAXSource(
                XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xmlMessage.getBytes())));
        
        return new Source[] { streamSource, streamSource2, domSource, saxSource, staxSource };
    }
    
    @Test
    public void testSourceToFile() throws Exception {
        for (String xmlMessage : new String[] { "<toto></toto>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><toto></toto>" }) {
            for (Source source : newTestSources(xmlMessage)) {
                File fileToWrite = File.createTempFile("SourceHelperTest", ".xml");
                SourceHelper.toFile(source, fileToWrite);
                
                assertTrue(fileToWrite.exists());
                String actualFileContent = IOHelper.readFileAsString(fileToWrite);
                assertTrue(XMLComparator.isEquivalent(xmlMessage, actualFileContent));
            }
        }
    }
    
    @Test
    public void testSourceToString() throws Exception {
        for (String xmlMessage : new String[] { "<toto></toto>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><toto></toto>" }) {
            for (Source source : newTestSources(xmlMessage)) {
                String sourceAsString = SourceHelper.toString(source);
                assertTrue(XMLComparator.isEquivalent(xmlMessage, sourceAsString));
            }
        }
    }

    @Test(expected = TransformerException.class)
    public void testMalformedStreamSourceToString() throws Exception {
        String xmlMessage = "<tot";
        ByteArrayInputStream bais = new ByteArrayInputStream(xmlMessage.getBytes());
        Source source = new StreamSource(bais);
        SourceHelper.toString(source);
    }

    @Test
    public void testForkAllowedSources() throws Exception {
        for (String xmlMessage : new String[] { "<toto></toto>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><toto></toto>" }) {
            for (Source source : newTestSources(xmlMessage)) {

                if (source instanceof StAXSource)
                    continue;

                Source forkedSource = SourceHelper.fork(source);
                assertEquals(SourceHelper.toString(source), SourceHelper.toString(forkedSource));
            }
        }
    }

    @Test
    public void testForkUnallowedSources() throws Exception {
        for (String xmlMessage : new String[] { "<toto></toto>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><toto></toto>" }) {
            for (Source source : newTestSources(xmlMessage)) {

                if (source instanceof DOMSource)
                    continue;
                if (source instanceof StreamSource)
                    continue;
                if (source instanceof SAXSource)
                    continue;

                try {
                    SourceHelper.fork(source);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
                fail();
            }
        }
    }

    public void testForkMalformedStreamSource() throws TransformerException, IOException {
        String xmlMessage = "<tot";
        ByteArrayInputStream bais = new ByteArrayInputStream(xmlMessage.getBytes());
        Source source = new StreamSource(bais);
        Source forkedSource = SourceHelper.fork(source);
        try {
            SourceHelper.toString(source);
        } catch (TransformerException te1) {
            try {
                SourceHelper.toString(forkedSource);
            } catch (TransformerException te2) {
                return;
            }
        }
        fail();
    }

    
    public void testDOMSourceToInputSource() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document doc = factory.newDocumentBuilder().parse(Thread.currentThread().getContextClassLoader().getResource("test.xml").openStream());
        DOMSource s = new DOMSource(doc);
        InputSource is = SourceHelper.sourceToInputSource(s, null, s.getSystemId());
        Assert.assertNotNull(is);
    }
    
    public void testSAXSourceToInputSource() throws SAXException, IOException, ParserConfigurationException {
        SAXSource s = new SAXSource(new InputSource(Thread.currentThread().getContextClassLoader().getResource("test.xml").openStream()));
        InputSource is = SourceHelper.sourceToInputSource(s, null, s.getSystemId());
        Assert.assertNotNull(is);
    }
    
    public void testStreamSourceToInputSource() throws SAXException, IOException, ParserConfigurationException {
        StreamSource s = new StreamSource(Thread.currentThread().getContextClassLoader().getResource("test.xml").openStream());
        InputSource is = SourceHelper.sourceToInputSource(s, null, s.getSystemId());
        Assert.assertNotNull(is);
    }

    @Test
    public void testSourceToByteArray() throws Exception {
        for (String xmlMessage : new String[] { "<toto></toto>",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><toto></toto>" }) {
            for (Source source : newTestSources(xmlMessage)) {
                byte[] sourceAsString = SourceHelper.toByteArray(source);
                assertTrue(XMLComparator.isEquivalent(xmlMessage, new String(sourceAsString)));
            }
        }
    }

}
