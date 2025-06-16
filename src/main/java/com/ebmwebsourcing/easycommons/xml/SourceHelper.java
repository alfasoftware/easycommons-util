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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ebmwebsourcing.easycommons.stream.EasyByteArrayOutputStream;
import com.ebmwebsourcing.easycommons.stream.InputStreamForker;
import com.ebmwebsourcing.easycommons.stream.ReaderInputStream;
import com.ebmwebsourcing.easycommons.stream.StreamHelper;

/**
 * 
 * @author Mathieu CARROLLE - EBM WebSourcing
 * @author Victor Noel
 * 
 */
public class SourceHelper {

    public static void toFile(final Source source, final File fileToWrite) throws TransformerException, IOException {
        final FileWriter writer = new FileWriter(fileToWrite);
        final Result result = new StreamResult(writer);
        
        final Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.transform(source, result);
        } finally {
            Transformers.releaseTransformer(transformer);
            writer.close();
        }
    }
    
    public static String toString(final Source source) throws TransformerException {
        final StringWriter writer = new StringWriter();
        final Result result = new StreamResult(writer);
        
        final Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.transform(source, result);
        } finally {
            Transformers.releaseTransformer(transformer);
            // It's not needed to close a StringWriter (has no effect)
        }
        
        return writer.toString();
    }

    private static SAXSource forkSAXSource(final SAXSource source) throws IOException {
        final InputSource inputSource = source.getInputSource();
        final InputSource result;
        if (inputSource.getCharacterStream() != null) {
            final InputStreamForker streamForker = new InputStreamForker(
                    new ReaderInputStream(inputSource.getCharacterStream()));
            // let's replace the input stream with a fork
            inputSource.setCharacterStream(new InputStreamReader(streamForker.fork()));
            result = new InputSource(streamForker.fork());
        } else if (inputSource.getByteStream() != null) {
            final InputStreamForker streamForker = new InputStreamForker(inputSource.getByteStream());
            // let's replace the input stream with a fork
            inputSource.setByteStream(streamForker.fork());
            result = new InputSource(streamForker.fork());
        } else {
            // it's not a stream but a systemId must have been set (for example by InputSource(String)).
            result = new InputSource(inputSource.getSystemId());
        }
        result.setPublicId(inputSource.getPublicId());
        result.setEncoding(inputSource.getEncoding());
        // and return another stream for the fork
        return new SAXSource(source.getXMLReader(), result);
    }

    private static StreamSource forkStreamSource(final StreamSource source) throws IOException {
        final StreamSource result;
        if (source.getInputStream() != null) {
            final InputStreamForker streamForker = new InputStreamForker(source.getInputStream());
            // let's replace the input stream with a fork
            source.setInputStream(streamForker.fork());
            // and return another stream for the fork
            result = new StreamSource(streamForker.fork());
        } else if (source.getReader() != null) {
            final InputStreamForker streamForker = new InputStreamForker(new ReaderInputStream(source.getReader()));
            // let's replace the input stream with a fork
            source.setReader(new InputStreamReader(streamForker.fork()));
            // and return another stream for the fork
            result = new StreamSource(streamForker.fork());
        } else {
            // it's not a stream but a systemId must have been set (for example by StreamSource(File)).
            result = new StreamSource(source.getSystemId());
        }
        result.setPublicId(source.getPublicId());
        return result;
    }

    /**
     * Fork, if necessary, a {@link Source} so that consuming the forked one
     * does not consume the original one.
     * 
     * @param source
     *            {@link Source} to be forked.
     * @return Forked {@link Source}.
     * @throws IOException
     */
    public static Source fork(final Source source) throws IOException {
        if (source instanceof DOMSource) {
            // DOMSource is in-memory
            return source;
        } else if (source instanceof StreamSource) {
            return forkStreamSource((StreamSource) source);
        } else if (source instanceof SAXSource) {
            return forkSAXSource((SAXSource) source);
        } else {
            // staxsources can't be forked as we need to read them to fork them but then we can't reset the staxsource
            // with one of the forks... TODO or we need to work directly at the message exchange level? or anyway
            // staxsource are not meant to be used in the context of something like petals!!! (small sized messages)
            throw new IllegalArgumentException(
                    "Only DOMSource, StreamSource and SAXSource are supported right now, got a "
                            + source.getClass().getName());
        }
    }

    public static InputStream getUnderlyingInputStream(Source source) {
        if (source instanceof StreamSource) {
            return ((StreamSource) source).getInputStream();
        } else if (source instanceof SAXSource) {
            return ((SAXSource) source).getInputSource().getByteStream();
        } else {
            return null;
        }
    }

    public static EasyByteArrayOutputStream toEasyByteArrayOutputStream(Source source) throws TransformerException {
        final EasyByteArrayOutputStream os = new EasyByteArrayOutputStream();

        final Transformer transformer = Transformers.takeTransformer();

        try {
            transformer.transform(source, new StreamResult(os));
        } finally {
            Transformers.releaseTransformer(transformer);
        }

        return os;
    }

    public static byte[] toByteArray(final Source source) throws TransformerException {

        InputStream is = getUnderlyingInputStream(source);

        // hack to avoid useless conversions
        if (is instanceof ByteArrayInputStream) {
            try {
                return StreamHelper.getAllBytes((ByteArrayInputStream) is);
            } catch (IllegalStateException e) {
                // it's ok, let's just do it the other way
            }
        }

        return toEasyByteArrayOutputStream(source).toRawByteArray();
    }

    public static InputStream toInputStream(final Source source) throws TransformerException {
        InputStream is = getUnderlyingInputStream(source);

        if (is != null) {
            return is;
        }

        return toEasyByteArrayOutputStream(source).toByteArrayInputStream();
    }

    public static InputSource toInputSource(final Source source) throws TransformerException {

        if (source instanceof SAXSource) {
            return ((SAXSource) source).getInputSource();
        }

        if (source instanceof StreamSource) {
            final InputSource inputSource = new InputSource();
            inputSource.setPublicId(((StreamSource) source).getPublicId());
            inputSource.setSystemId(source.getSystemId());
            inputSource.setCharacterStream(((StreamSource) source).getReader());
            inputSource.setByteStream(((StreamSource) source).getInputStream());
            return inputSource;
        }

        final InputSource inputSource = new InputSource(toInputStream(source));
        inputSource.setSystemId(source.getSystemId());

        return inputSource;
    }

    public static InputSource toInputSource(final Document document) throws TransformerException {
        return toInputSource(toDOMSource(document));
    }

    public static DOMSource toDOMSource(final Document document) {
        // normalize the document to assure the resolution of each node namespace
        document.normalizeDocument();
        return new DOMSource(document);
    }

    public static Document toDocument(final Source source) throws TransformerException {
        final Document document = DocumentBuilders.newDocument();
        final DOMResult domResult = new DOMResult(document);
        final Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.transform(source, domResult);
        } finally {
            Transformers.releaseTransformer(transformer);
        }
        return document;
    }

    public static DOMSource toDOMSource(final InputSource inputSource) throws IOException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder db = factory.newDocumentBuilder();
            final Document document = db.parse(inputSource);
            final DOMSource res = new DOMSource(document);
            res.setSystemId(inputSource.getSystemId());
            return res;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException(e);
        }
    }

    /**
     * Utility to get the bytes uri
     *
     * @param source the resource to get
     * @param publicId 
     * @param systemId 
     */
    public static InputSource sourceToInputSource(Source source, String publicId, String systemId) {
        InputSource is = null;
        if (source instanceof SAXSource) {
            is = ((SAXSource) source).getInputSource();
        } else if (source instanceof DOMSource) {
            EasyByteArrayOutputStream baos = new EasyByteArrayOutputStream();
            Node node = ((DOMSource) source).getNode();
            if (node instanceof Document) {
                node = ((Document) node).getDocumentElement();
            }
            Element domElement = (Element) node;
            ElementToStream(domElement, baos);
            InputSource isource = new InputSource(source.getSystemId());
            isource.setByteStream(baos.toByteArrayInputStream());
            is = isource;
        } else if (source instanceof StreamSource) {
            StreamSource ss = (StreamSource) source;
            InputSource isource = new InputSource(ss.getSystemId());
            isource.setByteStream(ss.getInputStream());
            isource.setCharacterStream(ss.getReader());
            isource.setPublicId(ss.getPublicId());
            is = isource;
        } else {
            is =  new InputSource(source.getSystemId());
        }
        if(is != null) {
            is.setPublicId(publicId);
            is.setSystemId(systemId);
        }
        return is;
    }

    private static void ElementToStream(Element element, OutputStream out) {
        DOMSource source = new DOMSource(element);
        StreamResult result = new StreamResult(out);
        
        Transformer transformer = Transformers.takeTransformer();
        try {
            transformer.transform(source, result);
        } catch (Exception ex) {
        } finally {
            Transformers.releaseTransformer(transformer);
        }
    }
}
