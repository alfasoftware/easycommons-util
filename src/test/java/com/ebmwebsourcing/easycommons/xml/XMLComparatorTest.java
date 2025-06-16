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

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class XMLComparatorTest {

    public static Logger log = Logger.getLogger(XMLComparatorTest.class.getName());

    private static final void assertCommutativeEquivalence(boolean isEquivalent, String operand1,
            String operand2) {
        assertEquals(isEquivalent, XMLComparator.isEquivalent(operand1, operand2));
        assertEquals(isEquivalent, XMLComparator.isEquivalent(operand2, operand1));
    }

    @Test
    public void testIsEquivalent0() throws Exception {
        String xml1 = "<a/>";
        String xml2 = "<a></a>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent1() throws Exception {
        String xml1 = "<a xmlns:pf1='http://ns1'/>";
        String xml2 = "<a></a>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent2() throws Exception {
        String xml1 = "<a att1='value'/>";
        String xml2 = "<a></a>";
        assertCommutativeEquivalence(false, xml1, xml2);
    }

    @Test
    public void testIsEquivalent3() throws Exception {
        String xml1 = "<getBusinessMessage xmlns='http://petals.ow2.org/FaultWS/'>"
                + "<in xmlns='' xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='xsd:string'>0</in>"
                + "</getBusinessMessage>";
        String xml2 = "<tns:getBusinessMessage xmlns:tns='http://petals.ow2.org/FaultWS/'>"
                + "  <in xmlns:xsd='http://www.w3.org/2001/XMLSchema' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='xsd:string'>0</in>"
                + "</tns:getBusinessMessage>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent4() throws Exception {
        String xml1 = "<a att1='value' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='TOTO'/>";
        String xml2 = "<a att1='value'></a>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent5() throws Exception {
        String xml1 = "<a att1='value'/>";
        String xml2 = "<a att1='value' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='TOTO'></a>";
        assertCommutativeEquivalence(true, xml1, xml2);
    }

    @Test
    public void testIsEquivalent6() throws Exception {
        String xml1 = "<a att1='value' toto='tutu'/>";
        String xml2 = "<a att1='value' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:type='TOTO'></a>";
        assertCommutativeEquivalence(false, xml1, xml2);
    }
}
