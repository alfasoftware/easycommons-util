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

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class SoapComparatorTest {

    public static Logger log = Logger.getLogger(SoapComparatorTest.class.getName());

    private static final void assertCommutativeEquivalence(boolean isEquivalent, String operand1,
            String operand2) {
        assertEquals(isEquivalent, SoapComparator.isEquivalent(operand1, operand2));
        assertEquals(isEquivalent, SoapComparator.isEquivalent(operand2, operand1));
    }

    @Test
    public void testIsEquivalent0() throws Exception {
        String xml1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Header/><SOAP-ENV:Body><echoString/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        String xml2 = "<S:Envelope xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Header/><S:Body><echoString/></S:Body></S:Envelope>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent1() throws Exception {
        String xml1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Header/><SOAP-ENV:Body><echoString/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        String xml2 = "<S:Envelope xmlns:toto='http://unusednamespacemapping' "
                + "xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Header/><S:Body><echoString/></S:Body></S:Envelope>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent2() throws Exception {
        String xml1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Header/><SOAP-ENV:Body><echoString/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        String xml2 = "<S:Envelope xmlns:toto='http://unusednamespacemapping' "
                + "xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Body><echoString/></S:Body></S:Envelope>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent3() throws Exception {
        String xml1 = "<SOAP-ENV:Envelope xmlns:SOAP-ENV='http://schemas.xmlsoap.org/soap/envelope/'><SOAP-ENV:Header/><SOAP-ENV:Body><echoString/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
        String xml2 = "<S:Envelope xmlns:toto='http://unusednamespacemapping' "
                + "xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Header><m:Trans xmlns:m=\"http://www.w3schools.com/transaction/\"\r\n"
                + "  S:mustUnderstand=\"1\">234\r\n"
                + "  </m:Trans></S:Header><S:Body><echoString/></S:Body></S:Envelope>";
        assertCommutativeEquivalence(false, xml1, xml2);
    }

    @Test
    public void testIsEquivalent4() throws Exception {
        String xml1 = "<S:Envelope "
                + "xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Header><m:Trans xmlns:m=\"http://www.w3schools.com/transaction/\"\r\n"
                + "  S:mustUnderstand=\"1\">234\r\n"
                + "  </m:Trans></S:Header><S:Body><echoString2/></S:Body></S:Envelope>";

        String xml2 = "<S:Envelope xmlns:toto='http://unusednamespacemapping' "
                + "xmlns:S='http://schemas.xmlsoap.org/soap/envelope/'><S:Header><m:Trans xmlns:m=\"http://www.w3schools.com/transaction/\"\r\n"
                + "  S:mustUnderstand=\"1\">234\r\n"
                + "  </m:Trans></S:Header><S:Body><echoString/></S:Body></S:Envelope>";
        assertCommutativeEquivalence(false, xml1, xml2);
    }
}
