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
package edu.uncc.genosets.util.gff;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import edu.uncc.genosets.util.spi.CommandLineService;

/**
 *
 * @author aacain
 */
public class GffTester implements CommandLineService {

    @Override
    public void run(String[] args) {
        JSAP jsap = new JSAP();
        JSAPResult config = jsap.parse(args);
        
        if (!config.success()) {
            System.err.println("Usage: java -jar " + jsap.getUsage());
            System.out.println();
            System.exit(1);
        }
    }

    @Override
    public String getName() {
        return "GffTester";
    }
}
