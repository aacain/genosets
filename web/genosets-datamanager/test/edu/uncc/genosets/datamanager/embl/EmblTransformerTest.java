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
import org.junit.Test;

/**
 *
 * @author lucy
 */
public class EmblTransformerTest {

    public EmblTransformerTest() {
    }

    @Test
    public void testDownload() {
        String emblString = EmblClient.instantiate().getEmblFile("JH601123");
        AnnotationMethod method = new AnnotationMethod();
        EmblTransformer instance = EmblTransformer.instantiate();
        instance.transform(emblString, method);
        String contigLocation = instance.getContigLocation();
        System.out.println(contigLocation);
    }
}