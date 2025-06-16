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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ebmwebsourcing.easycommons.pooling.GenericResourcePool;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;
import com.ebmwebsourcing.easycommons.pooling.ResourceHandler;

/**
 * An util class to prettify XML document
 * 
 * @author ofabre - EBM WebSourcing
 * 
 */
public class XMLPrettyPrinter {
    
    private static class TransformerFactoryResourceHandler implements ResourceHandler<TransformerFactory> {

        @Override
        public TransformerFactory create() {
            return TransformerFactory.newInstance();
        }

        @Override
        public void onTake(TransformerFactory resource) {
        }

        @Override
        public void onRelease(TransformerFactory resource) {
        }
    }
    
    private static GenericResourcePool<TransformerFactory> transformerFactoryResourcePool = new GenericResourcePool<TransformerFactory>(
            new TransformerFactoryResourceHandler(), 1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * parse the xml String and return it pretty-printed (with correct
     * indentations, etc..)
     * 
     * @param xmlDocument
     *            the xml document to pretty print. Must be non null
     * @param encoding
     *            the encoding to use
     * 
     * @return printed string if no error occurs. If an error occurs, return an
     *         empty String
     */
    public static String prettyPrint(final Node xmlDocument, final String encoding) {
		String result = "";
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			XMLPrettyPrinter.prettify(xmlDocument, outStream,encoding);
			result = outStream.toString(encoding);
		} catch (final Exception e) {
			System.err.println("write_dom failed:" + e);
			// if an error occurs, the result will be the original string
		}
		return result;

	}

    public static String prettyPrint(final Source source) throws TransformerException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        XMLPrettyPrinter.prettify(source, outStream);
        return outStream.toString();
    }

    /**
     * parse the xml Document and return it pretty-printed (with correct
     * indentations, etc..). Use the encoding defined at the parsing or in the
     * document (utf8 is used if no encoding is defined)
     * 
     * @param xmlDocument
     *            the xml document to pretty print. Must be non null
     * @return printed string if no error occurs. If an error occurs, return an
     *         empty String
     */
	public static String prettyPrint(final Document xmlDocument) {
		return prettyPrint(xmlDocument,getEncoding(xmlDocument));
	}

    /**
     * parse the xml Document and return it pretty-printed (with correct
     * indentations, etc..). Use the encoding defined at the parsing or in the
     * document (utf8 is used if no encoding is defined)
     * 
     * @param node
     *            the xml document to pretty print. Can be {@code null}.
     * @return printed string if no error occurs. If an error occurs, return an
     *         empty String. {@code null} if the XML document is {@code null}.
     */
	public static String prettyPrint(final Element node) {
		String res = null;
		if(node != null) {
			if(node instanceof Document) {
				res = prettyPrint(node,getEncoding((Document) node));
			} else {
				res = prettyPrint(node,getEncoding(node.getOwnerDocument()));
			}
		}
		return res;
	}

    /**
     * parse the xml Document and return it pretty-printed (with correct
     * indentations, etc..). Use the encoding defined at the parsing or in the
     * document (utf8 is used if no encoding is defined)
     * 
     * @param node
     *            the xml document to pretty print. Can be {@code null}.
     * @return printed string if no error occurs. If an error occurs, return an
     *         empty String. {@code null} if the XML document is {@code null}.
     */
	public static String prettyPrint(final Node node) {
		String res = null;
		if(node != null) {
			if(node instanceof Document) {
				res = prettyPrint(node,getEncoding((Document) node));
			} else {
				res = prettyPrint(node,getEncoding(node.getOwnerDocument()));
			}
		}
		return res;
	}

	/**
	 * Prettify the node into the output stream.
	 */
    public static void prettify(final Node node, final OutputStream out, final String encoding) throws Exception {
        prettify(new DOMSource(node), out, encoding);
	}

	/**
	 * Prettify the node into the output stream.
	 */
    public static void prettify(final Node node, final OutputStream out) throws Exception {
        prettify(node, out, null);
	}
	
	 /**
     * Prettify the xml input stream into the output stream.
     */
    public static void prettify(final InputStream in, final OutputStream out) throws Exception {
        prettify(new StreamSource(in), out);
    }

    public static void prettify(final Source source, final OutputStream out) throws TransformerException {
        prettify(source, out, null);
    }

    public static void prettify(final Source source, final OutputStream out, final String encoding)
            throws TransformerException {
        final Source stylesheetSource = getStyleSheetSource();
        final Transformer transformer = getTransformer(stylesheetSource);
        if (encoding != null) {
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        }
        transformer.transform(source, new StreamResult(out));
    }

    /**
     * Get a transformer from a stylesheet source
     *
     * @param stylesheetSource a stylesheet source
     * 
     * @throws TransformerConfigurationException if there is transformer configuration error
     */
    public static Transformer getTransformer(Source stylesheetSource) throws TransformerConfigurationException  {
        TransformerFactory transformerFactory = transformerFactoryResourcePool.take();
        try {
            Templates templates = transformerFactory.newTemplates(stylesheetSource);
            Transformer transformer = templates.newTransformer();
            return transformer;
        } finally {
            transformerFactoryResourcePool.release(transformerFactory);
        }
    }
    
    public static String getEncoding(Document xmlDocument){
        String encoding = xmlDocument.getInputEncoding();
        if(encoding == null){
            encoding = xmlDocument.getXmlEncoding();
        }
        if(encoding == null){
            encoding = "UTF-8";
        }
        return encoding;
    } 
    
	private static Source getStyleSheetSource() {
		Source stylesheetSource = new StreamSource(XMLPrettyPrinter.class
				.getResourceAsStream("/prettyPrint.xsl"));
		return stylesheetSource;
	}
}
