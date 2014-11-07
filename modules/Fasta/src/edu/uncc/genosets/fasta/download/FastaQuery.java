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

package edu.uncc.genosets.fasta.download;

import edu.uncc.genosets.bioio.Fasta.FastaItem;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aacain
 */
public class FastaQuery {

    public static List<FastaItem> byLocation(Collection<Integer> locationIds, String prefix, int start, int end){
        StringBuilder bldr = new StringBuilder("SELECT p FROM ProteinSequence as p ");
        bldr.append(" WHERE p.molecularSequenceId IN (");
        boolean first = true;
        for (Integer id : locationIds) {
            if(!first){
                bldr.append(", ");
            }else{
                first = false;
            }
            bldr.append(id);
        }
        bldr.append(")");
        bldr.append(" ORDER BY p.molecularSequenceId asc");
        List<? extends MolecularSequence> seqs = DataManager.getDefault().createQuery(bldr.toString(), MolecularSequence.class, start, end);
        List<FastaItem> items = new ArrayList<FastaItem>(seqs.size());
        for (MolecularSequence seq : seqs) {
            items.add(new FastaItem(prefix == null ? seq.getMolecularSequenceId().toString() : prefix + "|" + seq.getMolecularSequenceId().toString(), seq.getForwardSequence()));
        }
        return items;
    }
    
    public static HashMap<Organism, List<Integer>> groupLocationByOrganism(Collection<Integer> locationIds, FocusEntity focusEntity){
        HashMap<Integer, List<Integer>> byOrgIdMap = new HashMap<Integer, List<Integer>>();
        StringBuilder bldr = new StringBuilder("SELECT l FROM Location as l ");
        bldr.append(" WHERE l.").append(focusEntity.getFactTableId()).append(" IN (");
        boolean first = true;
        for (Integer id : locationIds) {
            if(!first){
                bldr.append(", ");
            }else{
                first = false;
            }
            bldr.append(id);
        }
        bldr.append(") GROUP BY l.locationId");
        List<? extends Location> locs = DataManager.getDefault().createQuery(bldr.toString(), Location.class);
        for (Location loc : locs) {
            List<Integer> get = byOrgIdMap.get(loc.getOrganismId());
            if(get == null){
                get = new LinkedList<Integer>();
                byOrgIdMap.put(loc.getOrganismId(), get);
            }
            get.add(loc.getLocationId());
        }
        HashMap<Organism, List<Integer>> orgMap = new HashMap<Organism, List<Integer>>(byOrgIdMap.size());
        for (Map.Entry<Integer, List<Integer>> entry : byOrgIdMap.entrySet()) {
            Organism org = (Organism) DataManager.getDefault().get("Organism", entry.getKey());
            orgMap.put(org, entry.getValue());
        }
        
        return orgMap;
    }
    
    public static List<FastaItem> byAssembledUnit(Collection<Integer> assembledUnitIds, String prefix, int start, int end){
        StringBuilder bldr = new StringBuilder("SELECT p FROM MolecularSequence as p ");
        bldr.append(" WHERE p.molecularSequenceId IN (");
        boolean first = true;
        for (Integer id : assembledUnitIds) {
            if(!first){
                bldr.append(", ");
            }else{
                first = false;
            }
            bldr.append(id);
        }
        bldr.append(")");
        bldr.append(" ORDER BY p.molecularSequenceId asc");
        List<? extends MolecularSequence> seqs = DataManager.getDefault().createQuery(bldr.toString(), MolecularSequence.class, start, end);
        List<FastaItem> items = new ArrayList<FastaItem>(seqs.size());
        for (MolecularSequence seq : seqs) {
            items.add(new FastaItem(prefix == null ? seq.getMolecularSequenceId().toString() : prefix + "|" + seq.getMolecularSequenceId().toString(), seq.getForwardSequence()));
        }
        return items;
    }
    
    public static HashMap<Organism, List<Integer>> groupAssUnitByOrganism(Collection<Integer> assUnitIds, FocusEntity focusEntity){
        HashMap<Integer, List<Integer>> byOrgIdMap = new HashMap<Integer, List<Integer>>();
        StringBuilder bldr = new StringBuilder("SELECT ass FROM AssembledUnit as ass ");
        bldr.append(" WHERE ass.").append(focusEntity.getFactTableId()).append(" IN (");
        boolean first = true;
        for (Integer id : assUnitIds) {
            if(!first){
                bldr.append(", ");
            }else{
                first = false;
            }
            bldr.append(id);
        }
        bldr.append(") GROUP BY ass.assembledUnitId");
        List<? extends AssembledUnit> assUnits = DataManager.getDefault().createQuery(bldr.toString(), AssembledUnit.class);
        for (AssembledUnit ass : assUnits) {
            List<Integer> get = byOrgIdMap.get(ass.getOrganismId());
            if(get == null){
                get = new LinkedList<Integer>();
                byOrgIdMap.put(ass.getOrganismId(), get);
            }
            get.add(ass.getAssembledUnitId());
        }
        HashMap<Organism, List<Integer>> orgMap = new HashMap<Organism, List<Integer>>(byOrgIdMap.size());
        for (Map.Entry<Integer, List<Integer>> entry : byOrgIdMap.entrySet()) {
            Organism org = (Organism) DataManager.getDefault().get("Organism", entry.getKey());
            orgMap.put(org, entry.getValue());
        }
        
        return orgMap;
    }
}
