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

import bioio.GoAnnotationFileFormat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.util.Exceptions;

/**
 * Persists GAF files
 *
 * @author aacain
 */
public class GeneOntologyGafImpl extends SimpleTask {

    private final GoAnnotationFileFormat gaf;
    private final GoTermPersister termPersister;
    private final AnnotationMethod method;

    public GeneOntologyGafImpl(GoTermPersister termPersister, AnnotationMethod method, GoAnnotationFileFormat gaf, String name) {
        super(name);
        this.termPersister = termPersister;
        this.method = method;
        this.gaf = gaf;
    }

    List<Persister> getPersisters() {
        List<Persister> persisters = new ArrayList<Persister>(gaf.getAnnotations().size());
        for (GoAnnotationFileFormat.Annotation anno : gaf.getAnnotations()) {
            try {
                String stringId = anno.getId();
                String[] splitIds = stringId.split("\\|");
                if (splitIds.length > 1) {
                    stringId = splitIds[splitIds.length - 1];
                }
                Integer id = Integer.parseInt(stringId);
                Location l = (Location) DataManager.getDefault().get(Location.DEFAULT_NAME, id);
                FeatureCluster cluster = termPersister.getGoTerm(anno.getGoIdentifier());
                if (cluster != null) {
                    FactFeaturePersister p = FactFeaturePersister.instantiate();
                    FactFeature fact = new FactFeature();
                    //set what we know
                    fact.setFeatureId(l.getFeatureId());
                    fact.setAssembledUnitId(l.getAssembledUnitId());
                    fact.setOrganismId(l.getOrganismId());

                    //setup persister
                    fact.setFeatureClusterId(cluster.getFeatureClusterId());
                    p.setup(cluster, method, fact, GeneOntology.ENTITY_NAME_CLUSTER, AnnotationMethod.DEFAULT_NAME, GeneOntology.ENTITY_NAME_FACT);
                    persisters.add(p);
                } else {
                    TaskLogFactory.getDefault().log("Could not find GO term: " + anno.getGoIdentifier(), GeneOntology.class.getName(), "The GO term was not found.  This annotation was not added.", TaskLog.WARNING, new Date());
                }
            } catch (NumberFormatException ex) {
                TaskLogFactory.getDefault().log("Error loading GAF file: " + name, GeneOntology.class.getName(), ex.getMessage(), TaskLog.ERROR, new Date());
                Exceptions.printStackTrace(ex);
            }
        }
        return persisters;
    }

    @Override
    public void performTask() throws TaskException {
        try {
            List<Persister> persisters = getPersisters();
            DataManager.getDefault().persist(persisters);
            TaskLogFactory.getDefault().log("Persisted GOA annotations for " + this.name, this.getClass().toString(), "Success", TaskLog.INFO, new Date());
        } catch (Exception ex) {
            TaskLogFactory.getDefault().log("Error loading annotations from file: " + this.name, this.getClass().toString(), "Exception during loading" + ex.getLocalizedMessage(), TaskLog.ERROR, new Date());
            Exceptions.printStackTrace(ex);
        }
    }
}
