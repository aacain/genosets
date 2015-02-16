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
package edu.uncc.genosets.geneontology.api;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactFeature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.persister.FactFeaturePersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.SimpleTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class GeneOntologySimpleImpl extends SimpleTask {

    private final GoTermPersister termPersister;
    private final AnnotationMethod method;
    private final Map<Location, ? extends Collection<String>> annoMap;

    public GeneOntologySimpleImpl(GoTermPersister termPersister, AnnotationMethod method, String name, Map<Location, ? extends Collection<String>> annoMap) {
        super(name);
        this.termPersister = termPersister;
        this.method = method;
        this.annoMap = annoMap;
    }

    @Override
    public void performTask() throws TaskException {
        try {
            List<Persister> persisters = getPersisters();
            DataManager.getDefault().persist(persisters);
            TaskLogFactory.getDefault().log("Persisted GO annotations for " + this.name + "(" + persisters.size() + "total annotations)", this.getClass().toString(), "Success", TaskLog.INFO, new Date());
            if(persisters.isEmpty()){
                TaskLogFactory.getDefault().log("No GO annotations found for " + this.name, this.getClass().toString(), "Check file.", TaskLog.WARNING, new Date());
            }
        } catch (Exception ex) {
            TaskLogFactory.getDefault().log("Error loading annotations from file: " + this.name, this.getClass().toString(), "Exception during loading" + ex.getLocalizedMessage(), TaskLog.ERROR, new Date());
            Exceptions.printStackTrace(ex);
        }
    }

    private List<Persister> getPersisters() {
        List<Persister> persisters = new LinkedList<Persister>();
        for (Map.Entry<Location, ? extends Collection<String>> entry : annoMap.entrySet()) {
            Location location = entry.getKey();
            for (String goString : entry.getValue()) {
                FeatureCluster cluster = termPersister.getGoTerm(goString);
                if (cluster != null) {
                    FactFeaturePersister p = FactFeaturePersister.instantiate();
                    FactFeature fact = new FactFeature();
                    //set what we know
                    fact.setFeatureId(location.getFeatureId());
                    fact.setAssembledUnitId(location.getAssembledUnitId());
                    fact.setOrganismId(location.getOrganismId());

                    //setup persister
                    fact.setFeatureClusterId(cluster.getFeatureClusterId());
                    p.setup(cluster, method, fact, GeneOntology.ENTITY_NAME_CLUSTER, AnnotationMethod.DEFAULT_NAME, GeneOntology.ENTITY_NAME_FACT);
                    persisters.add(p);
                } else {
                    TaskLogFactory.getDefault().log("Could not find GO term: " + goString, this.getClass().getName(), "The GO term was not found.  This annotation was not added.", TaskLog.WARNING, new Date());
                }
            }
        }
        return persisters;
    }
}
