/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lucy
 */
public class EmblParserImplTest {
    private String emblAccession = "JH601123.1";

    public EmblParserImplTest() {
    }

//    /**
//     * Test of parse method, of class EmblParserImpl.
//     */
//    @Test
//    public void testParse_File_AnnotationMethod() throws Exception {
//        System.out.println("parse");
//        File file = null;
//        AnnotationMethod method = null;
//        EmblParserImpl instance = new EmblParserImpl();
//        instance.parse(file, method);
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of parse method, of class EmblParserImpl.
     */
    @Test
    public void testParse_String_AnnotationMethod() {
        System.out.println("parse");
        System.out.println("\tdownloading test file " + emblAccession);
        EmblClient embl = EmblClient.instantiate();
        String emblString = embl.getEmblFile(emblAccession);
        AnnotationMethod method = new AnnotationMethod();
        EmblParserImpl instance = new EmblParserImpl();
        instance.parse(emblString, method);
        System.out.println(instance.getContigLocation());
    }

//    /**
//     * Test of getProject method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetProject() {
//        System.out.println("getProject");
//        EmblParserImpl instance = new EmblParserImpl();
//        String expResult = "";
//        String result = instance.getProject();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTaxon method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetTaxon() {
//        System.out.println("getTaxon");
//        EmblParserImpl instance = new EmblParserImpl();
//        String expResult = "";
//        String result = instance.getTaxon();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAssUnitName method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetAssUnitName() {
//        System.out.println("getAssUnitName");
//        EmblParserImpl instance = new EmblParserImpl();
//        String expResult = "";
//        String result = instance.getAssUnitName();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getHeaderMap method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetHeaderMap() {
//        System.out.println("getHeaderMap");
//        EmblParserImpl instance = new EmblParserImpl();
//        Map expResult = null;
//        Map result = instance.getHeaderMap();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFeatureTableMap method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetFeatureTableMap() {
//        System.out.println("getFeatureTableMap");
//        EmblParserImpl instance = new EmblParserImpl();
//        Map expResult = null;
//        Map result = instance.getFeatureTableMap();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNucSequence method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetNucSequence() {
//        System.out.println("getNucSequence");
//        EmblParserImpl instance = new EmblParserImpl();
//        String expResult = "";
//        String result = instance.getNucSequence();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEmblFormatRelease method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetEmblFormatRelease() {
//        System.out.println("getEmblFormatRelease");
//        EmblParserImpl instance = new EmblParserImpl();
//        Integer expResult = null;
//        Integer result = instance.getEmblFormatRelease();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExistingAssUnit method, of class EmblParserImpl.
//     */
//    @Test
//    public void testGetExistingAssUnit() {
//        System.out.println("getExistingAssUnit");
//        EmblParserImpl instance = new EmblParserImpl();
//        Integer expResult = null;
//        Integer result = instance.getExistingAssUnit();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}