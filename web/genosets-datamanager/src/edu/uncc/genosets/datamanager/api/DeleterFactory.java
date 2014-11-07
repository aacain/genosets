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
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class DeleterFactory {

    public static Deleter organismDeleter(Organism organism) {
        return new OrganismDeleter(organism);
    }
}

class OrganismDeleter implements Deleter, QueryCreator {

    private final Organism organism;
    private Boolean canDelete;

    public OrganismDeleter(Organism organism) {
        this.organism = organism;
    }

    @Override
    public void delete() throws DeleteException {
        if (!canDelete()) {
            throw new DeleteException("Could not delete organism" + organism.getOrganismId());
        } else {
            ArrayList<String> statements = new ArrayList(7);
            statements.add("DELETE FROM  fact_location_annofact_detail WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  fact_location_anno_fact WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  fact_feature_go_anno WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  location WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  feature WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  assembled_unit WHERE Organism = " + organism.getOrganismId());
            statements.add("DELETE FROM  organism WHERE OrganismId = " + organism.getOrganismId());
            try {
                DataManager.getDefault().createNativeStatement(statements, true);
            } catch (Exception ex) {
                throw new DeleteException(ex);
            }
        }
    }

    @Override
    public boolean canDelete() {
        if (canDelete == null) {
            //check orthomcl results
            StringBuilder bldr = new StringBuilder("SELECT count(*) FROM OrthoFact as f WHERE f.organismId = ");
            bldr.append(organism.getOrganismId());
            List<? extends Long> result = DataManager.getDefault().createQuery(bldr.toString());
            for (Long l : result) {
                if (l.longValue() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return canDelete;
        }
    }
}
