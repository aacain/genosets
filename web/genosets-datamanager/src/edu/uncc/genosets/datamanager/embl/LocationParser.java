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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aacain
 */
public class LocationParser {
    
    private void parseLocation(String locString) throws IOException{
        List<Integer> startParenList = new LinkedList<Integer>();
        List<Integer> endParenList = new LinkedList<Integer>();
        for (int index = locString.indexOf('('); index >= 0; index = locString.indexOf('(', index + 1)) {
            startParenList.add(index);
        }
        for (int index = locString.indexOf(')'); index >= 0; index = locString.indexOf(')', index + 1)) {
            endParenList.add(index);
        }
        if(startParenList.size() != endParenList.size()){
            throw new IOException("Parentheses invalid.");
        }
    }
    
    private void parseSubLocation(String locString, int start, int end){
        if("join".equals(locString.substring(start-4, start))){
            
        }else if("complement".equals(locString.substring(start-10, start))){
            
        }else if("order".equals(locString.substring(start-5, start))){
            
        }else if("gap".equals(locString.substring(start-3, start))){
            
        }
    }
    
    private void getJoin(String subString){
        String[] split = subString.split(",");
    }
    
    private void parseBasic(String subString){
        Pattern pattern = Pattern.compile("");
        Matcher matcher = pattern.matcher("hello");
        while(matcher.find()){
            
        }
    }
}
