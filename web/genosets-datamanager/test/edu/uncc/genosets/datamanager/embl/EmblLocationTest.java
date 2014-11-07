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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lucy
 */
public class EmblLocationTest {

    public EmblLocationTest() {
    }

    /**
     * Test of asEmblString method, of class EmblLocation.
     */
    @Test
    public void testAsEmblString() {
        System.out.println("asEmblString");
        EmblLocation instance = new EmblLocation("join(J16732:50,gap(unk100),complement(<113..>500), gap(500),445, 102.>110, 123^124, join(1..495, 500.500), J16543.50:444.445)");
        String expResult = "join(J16732:50,gap(unk100),complement(<113..>500),gap(500),445,102.>110,123^124,join(1..495,500.500),J16543.50:444.445)";
        String result = instance.asEmblString();
        System.out.println("Expected: \t" + expResult);
        System.out.println("Value: \t\t" + result);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMapping method, of class EmblLocation.
     */
    @Test
    public void testGetMapping() throws Exception {
        EmblLocation instance = new EmblLocation("join(complement(A1:1..10), join(complement(B1:6..15), C1:6..15))");
        System.out.println("getMapping");
        EmblLocation.Mapping result = instance.getMapping("A1", 1);
        assertArrayEquals(new Object[]{10, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("A1", 10);
        assertArrayEquals(new Object[]{1, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 6);
        assertArrayEquals(new Object[]{20, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 15);
        assertArrayEquals(new Object[]{11, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});

        instance = new EmblLocation("complement(join(A1:1..10, B1:6..15, C1:1..10))");
        result = instance.getMapping("A1", 1);
        assertArrayEquals(new Object[]{30, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("A1", 10);
        assertArrayEquals(new Object[]{21, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 6);
        assertArrayEquals(new Object[]{20, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 15);
        assertArrayEquals(new Object[]{11, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("C1", 1);
        assertArrayEquals(new Object[]{10, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("C1", 10);
        assertArrayEquals(new Object[]{1, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});



        instance = new EmblLocation("join(A1:1..10, complement(join(B1:6..15, C1:1..10)))");
        result = instance.getMapping("A1", 1);
        assertArrayEquals(new Object[]{1, false}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("A1", 10);
        assertArrayEquals(new Object[]{10, false}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 6);
        assertArrayEquals(new Object[]{30, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("B1", 15);
        assertArrayEquals(new Object[]{21, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("C1", 1);
        assertArrayEquals(new Object[]{20, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
        result = instance.getMapping("C1", 10);
        assertArrayEquals(new Object[]{11, true}, new Object[]{result.getMappedIndex(), result.isIsComplement()});
    }
}