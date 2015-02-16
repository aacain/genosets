/*
 * Copyright (C) 2015 Aurora Cain
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

package edu.uncc.genosets.datamanager.lookup;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.AssembledUnitAquisition;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import java.util.List;



/**
 *
 * @author aacain
 */
public class DatabaseLookup {
    public List<? extends AssembledUnit> findAssembledUnit(String assUnitName){
        return DataManager.getDefault().createQuery("SELECT assUnit from AssembledUnitAquisition as assFact inner join assFact.assembledUnit as assUnit WHERE assFact.assembledUnitName like '" + assUnitName + "'", AssembledUnit.class);
    }
    public List<? extends MolecularSequence> findMolecularSequence(String assUnitName){
        return DataManager.getDefault().createQuery("SELECT mole from AssembledUnitAquisition as assFact inner join assFact.molecularSequence as mole WHERE assFact.assembledUnitName like '" + assUnitName + "'", MolecularSequence.class);
    }
    
    public List<? extends AssembledUnitAquisition> findFactAssembledUnit(String assUnitName){
        return DataManager.getDefault().createQuery("from AssembledUnitAquisition as assFact WHERE assFact.assembledUnitName like '" + assUnitName + "'", AssembledUnitAquisition.class);
    }
    
    public static void main(String[] args) throws InvalidConnectionException{
        DataManager.openConnection(new Connection("myConnection", "myConnection", "localhost:3306/rast", "uncc", "uncc", false, Connection.TYPE_DIRECT_DB, false));
        DatabaseLookup lookup = new DatabaseLookup();
        
        List list = lookup.findMolecularSequence("NODE_27_length_48874_cov_11.1709_ID_53");
        System.out.println(list.size());
        list = lookup.findFactAssembledUnit("NODE_27_length_48874_cov_11.1709_ID_53");
        System.out.println(list.size());
        
        list = lookup.findAssembledUnit("NODE_27_length_48874_cov_11.1709_ID_53");
        System.out.println(list.size());
    }
}
