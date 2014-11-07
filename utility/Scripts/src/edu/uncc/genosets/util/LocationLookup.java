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
package edu.uncc.genosets.util;

import edu.uncc.genosets.datamanager.entity.Location;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class LocationLookup {

    HashMap<String, List<Location>> lookup = new HashMap<String, List<Location>>();
    Comparator<Location> comparator;

    public LocationLookup() {
        comparator = new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                int starts = o1.getStartPosition().compareTo(o2.getStartPosition());
                if (starts == 0) {
                    int ends = o1.getEndPosition().compareTo(o2.getEndPosition());
                    if (ends == 0) {
                        return o1.getIsForward().compareTo(o2.getIsForward());
                    }
                    return ends;
                }
                return starts;
            }
        };
    }
    
    public List<Location> lookupSeqId(String seqId){
        List<Location> get = lookup.get(seqId);
        if(get == null){
            get = new LinkedList<Location>();
            lookup.put(seqId, get);
        }
        return get;
    }

    public Location lookup(String seqId, Location newLocation) {
        List<Location> lookupSeqId = lookupSeqId(seqId);
        int binarySearch = Collections.binarySearch(lookupSeqId, newLocation, comparator);
        if (binarySearch >= 0) {
                return lookupSeqId.get(binarySearch);
        }
        lookupSeqId.add(-binarySearch - 1, newLocation);

        return newLocation;
    }
}
