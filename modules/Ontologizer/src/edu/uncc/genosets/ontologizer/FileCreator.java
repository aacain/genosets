/*
 * 
 * 
 */
package edu.uncc.genosets.ontologizer;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public abstract class FileCreator implements QueryCreator{

    public static final String GO_ENRICHMENT_ROOT = "GO_enrichment";
    public static final String ANNO_FILE_NAME = "goAnno.gaf";
    public static final String POP_FILE_NAME = "population.txt";
    public static final String LAST_SAMPLE = "testSample.sample";
    public static final String OBO = "gene_ontology_edit.obo";
    protected static final int INDEX_DB = 0; //required
    protected static final int INDEX_DB_OBJECT_ID = 1; //required
    protected static final int INDEX_DB_OBJECT_SYMBOL = 2; //required
    protected static final int INDEX_QUALIFIER = 3;
    protected static final int INDEX_GO_ID = 4; //required
    protected static final int INDEX_DB_REFERENCE = 5; //required
    protected static final int INDEX_EVIDENCE_CODE = 6; //required
    protected static final int INDEX_WITH_OR_FROM = 7;
    protected static final int INDEX_ASPECT = 8; //required
    protected static final int INDEX_DB_OBJECT_NAME = 9;
    protected static final int INDEX_OBJECT_SYNONYM = 10;
    protected static final int INDEX_DB_OBJECT_TYPE = 11; //required
    protected static final int INDEX_TAXON = 12; //required
    protected static final int INDEX_DATE = 13; //required
    protected static final int INDEX_ASSIGNED_BY = 14; //required
    protected static final int INDEX_ANNOTATION_EXTENSION = 15;
    protected static final int INDEX_GENE_PRODUCT_FORM = 16;
    protected static final int NUMBER_COLUMNS = 17;

    public abstract FileObject createAnnotationFile();

    public abstract FileObject createPopulationFile(List<Integer> features);

    public abstract FileObject createSampleFile(Collection<Integer> features);

    public abstract void getEnrichment(Collection<Integer> sample, Collection<Integer> population);

    public static FileCreator instantiate() {
        return new FileCreatorImpl();
    }

    private static class FileCreatorImpl extends FileCreator {

        private final FileObject projectRoot;
        private final String DATABASE_NAME;

        public FileCreatorImpl() {
            FileObject root = FileUtil.getConfigFile(GO_ENRICHMENT_ROOT);
            FileObject tempRoot = null;
            if (root == null) {
                try {
                    root = FileUtil.createFolder(FileUtil.getConfigRoot(), GO_ENRICHMENT_ROOT);
                } catch (IOException ex) {
                }
            }
            DataManager mgr = DataManager.getDefault();
            DATABASE_NAME = mgr.getConnectionId();
            try {
                tempRoot = FileUtil.createFolder(root, DATABASE_NAME);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            projectRoot = tempRoot;
        }
        
        @Override
        public FileObject createAnnotationFile() {
            FileObject annoFile = projectRoot.getFileObject(ANNO_FILE_NAME);
            if (annoFile == null) {
                BufferedWriter writer = null;
                try {
                    annoFile = FileUtil.createData(projectRoot, ANNO_FILE_NAME);
                    writer = new BufferedWriter(new OutputStreamWriter(annoFile.getOutputStream()));
                    String queryString = "Select fact.Feature, o.TaxonomyIdentifier,"
                            + " go.ClusterName, go.goName "
                            + " FROM temp_go as fact, organism as o, cluster_go_term as go "
                            + " WHERE fact.FeatureCluster = go.FeatureClusterId AND "
                            + " fact.Organism = o.OrganismId "
                            + " GROUP BY fact.FeatureCluster, fact.Feature";
                    DataManager mgr = DataManager.getDefault();
                    List<Object[]> result = mgr.createNativeQuery(queryString);
                    //List<Object[]> result = mgr.createQuery(queryString);
                    writer.write("!gaf-version: 2.0");
                    writer.newLine();
                    StringBuilder[] bldrs;
                    for (Object[] o : result) {
                        bldrs = new StringBuilder[NUMBER_COLUMNS];
                        bldrs[INDEX_DB] = new StringBuilder(DATABASE_NAME);
                        bldrs[INDEX_DB_OBJECT_ID] = new StringBuilder(((Integer) o[0]).toString());
                        bldrs[INDEX_DB_OBJECT_SYMBOL] = new StringBuilder(bldrs[INDEX_DB_OBJECT_ID]);
                        bldrs[INDEX_GO_ID] = new StringBuilder((CharSequence) o[2]);
                        bldrs[INDEX_DB_REFERENCE] = new StringBuilder(DATABASE_NAME + bldrs[INDEX_DB_OBJECT_ID]);
                        bldrs[INDEX_DB_REFERENCE] = new StringBuilder("IMP");
                        bldrs[INDEX_ASPECT] = new StringBuilder('F');
                        bldrs[INDEX_OBJECT_SYNONYM] = new StringBuilder(bldrs[INDEX_DB_OBJECT_ID]);
                        bldrs[INDEX_DB_OBJECT_TYPE] = new StringBuilder("protein");
                        if (o[1] != null) {
                            bldrs[INDEX_TAXON] = new StringBuilder(((Integer) o[1]).toString());
                        }
                        bldrs[INDEX_DATE] = new StringBuilder((new Date()).toString());
                        bldrs[INDEX_ASSIGNED_BY] = new StringBuilder('g');
                        StringBuilder line = new StringBuilder();
                        int i = 0;
                        for (StringBuilder b : bldrs) {
                            if (b == null) {
                                line.append('-');
                            } else {
                                line.append(b);
                            }
                            if (i < NUMBER_COLUMNS - 1) {
                                line.append("\t");
                            }
                        }
                        writer.write(line.toString());
                        writer.newLine();
                    }

                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            return annoFile;
        }

        @Override
        public FileObject createPopulationFile(List<Integer> features) {
            FileObject fo = projectRoot.getFileObject(POP_FILE_NAME);
            if (fo != null) {
                try {
                    fo.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            BufferedWriter writer = null;
            try {
                fo = FileUtil.createData(projectRoot, POP_FILE_NAME);
                writer = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                for (Integer feature : features) {
                    writer.write(feature.toString());
                    writer.newLine();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return fo;
        }

        @Override
        public FileObject createSampleFile(Collection<Integer> features) {
            FileObject fo = projectRoot.getFileObject(LAST_SAMPLE);
            if (fo != null) {
                try {
                    fo.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            BufferedWriter writer = null;
            try {
                fo = FileUtil.createData(projectRoot, "testSample.sample");
                writer = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                for (Integer feature : features) {
                    writer.write(feature.toString());
                    writer.newLine();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    writer.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return fo;
        }

        @Override
        public void getEnrichment(Collection<Integer> sample, Collection<Integer> population) {
            createSampleFile(sample);
        }
    }
}
