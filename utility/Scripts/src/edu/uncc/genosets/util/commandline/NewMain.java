/*
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.util.commandline;

import edu.uncc.genosets.util.CommandExecutor;
import edu.uncc.genosets.util.ConvertText;
import edu.uncc.genosets.util.RunBash;

/**
 *
 * @author aacain
 */
public class NewMain {

    public static void main(String[] args) {
        if (args.length > 0) {
            if ("convertText".equals(args[0])) {
                ConvertText.main(args);
            } else if ("commandExecutor".equals(args[0])) {
                CommandExecutor.main(args);
            } else if ("runCommandView".equals(args[0])) {
                CommandLinePanel.main(args);
            } else if ("runBash".equals(args[0])) {
                RunBash.main(args);
            } else {
                System.err.println("Invalid program");
                System.err.println("valid programs convertText, commandExecutor, runCommandView, runBash");
                System.exit(-1);
            }
        } else {
            System.err.println("Invalid program");
            System.err.println("valid programs convertText, commandExecutor, runCommandView, runBash");
            System.exit(-1);
        }
    }
}
