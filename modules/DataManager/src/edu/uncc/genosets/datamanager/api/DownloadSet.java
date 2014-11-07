/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 *
 * @author lucy
 */
public class DownloadSet implements Serializable {

    private static final long serialVersionUID = 1L;
    private final FactType factType;
    private String name;
    private List<AnnotationMethod> methodsList = new LinkedList<AnnotationMethod>();
    private transient List<DownloadFormat> formats = new LinkedList<DownloadFormat>();
    private transient FileObject fo;
    private transient PropertyChangeSupport cs = new PropertyChangeSupport(this);
    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_METHODS_LIST = "PROP_METHODS_LIST";
    public static final String PROP_FORMATS_LIST = "PROP_FORMATS_LIST";
    public static final String PROP_FILEOBJECT = "PROP_FILEOBJECT";

    public DownloadSet(FactType factType) {
        this.factType = factType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FactType getFactType() {
        return factType;
    }

    private PropertyChangeSupport getCS() {
        if (cs == null) {
            cs = new PropertyChangeSupport(this);
        }
        return cs;
    }

    /**
     * Get a snapshot of the methods included in this download set
     *
     * @return a snapshot of the methods (or empty list - never null)
     */
    public synchronized List<? extends AnnotationMethod> getMethods() {
        return new ArrayList(methodsList);
    }

    public synchronized void addMethod(AnnotationMethod method) {
        if (methodsList.contains(method)) {
            return;
        }
        List<? extends AnnotationMethod> old = getMethods();
        methodsList.add(method);
        this.cs.firePropertyChange(PROP_METHODS_LIST, old, getMethods());
    }

    public FileObject getRootFileObject() {
        return fo;
    }

    public void setRootFileObject(FileObject fo) {
        this.fo = fo;
    }

    /**
     * Get a snapshot of the formats
     *
     * @return
     */
    public synchronized List<? extends DownloadFormat> getFormats() {
        if (formats == null) {
            formats = new LinkedList<DownloadFormat>();
        }
        return new ArrayList(formats);
    }

    public synchronized void addFormat(DownloadFormat format) {
        List<? extends DownloadFormat> old = getFormats();
        formats.add(format);
        getCS().firePropertyChange(PROP_FORMATS_LIST, old, getFormats());
    }

    public void addPropertyChangeListner(PropertyChangeListener l) {
        getCS().addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        getCS().removePropertyChangeListener(l);
    }

    protected void save(FileObject dir, DownloadSet ds) {
//        try {
//            FileObject dsFolder = dir.createFolder(FileUtil.findFreeFolderName(dir, ds.getName()));
//            FileObject mainFo = dsFolder.createData(dsFolder.getName(), "ds");
//            String path = ds.getRootFileObject().getPath();
//            mainFo.setAttribute("downloadpath", path);
//            ObjectOutputStream oOut = null;
//            try {
//                oOut = new ObjectOutputStream(new BufferedOutputStream(mainFo.getOutputStream()));
//                oOut.writeObject(ds);
//                //now write formats
//                if (formats != null) {
//                    for (DownloadFormat df : formats) {
//                        if (df instanceof Savable) {
//                            Savable s = (Savable) df;
//                            FileObject f = null;
//                            try {
//                                f = dsFolder.createData(FileUtil.findFreeFileName(dsFolder, "format", "shadow"), "shadow");
//                                s.save(f);
//                            } catch (IOException ex) {
//                                //cannot serialize, then delete
//                                f.delete();
//                            }
//                        }
//                    }
//                }
//            } catch (Exception ex) {
//                Exceptions.printStackTrace(ex);
//                Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Could not save download set");
//            } finally {
//                try {
//                    oOut.close();
//                } catch (IOException ex) {
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Could not save download set");
//        }
    }

    public static List<? extends DownloadSet> getSets(String databaseName) {
//        if (databaseName != null) {
//            try {
//                List<DownloadSet> set = new ArrayList<DownloadSet>();
//                FileObject dbFo = FileUtil.getConfigFile(databaseName);
//                FileObject dsRoot = dbFo.getFileObject("datasets");
//                if (dsRoot == null) {
//                    dsRoot = dbFo.createFolder("datasets");
//                }
//                for (FileObject dsFolder : dsRoot.getChildren()) {
//                    if (dsFolder.isFolder()) {
//                        FileObject mainFO = dsFolder.getFileObject(dsFolder.getName(), "ds");
//                        ObjectInputStream oIn = null;
//                        try {
//                            oIn = new ObjectInputStream(mainFO.getInputStream());
//                            DownloadSet s = (DownloadSet) oIn.readObject();
//                            String path = (String) mainFO.getAttribute("downloadpath");
//                            s.setRootFileObject(FileUtil.createFolder(new File(path)));
//                            set.add(s);
//                            //now read formats
//                            for (FileObject dsChildren : dsFolder.getChildren()) {
//                                if (dsChildren.getExt().equals("shadow")) {
//                                    DataObject dobj = DataObject.find(dsChildren);
//                                    Savable saveable = (Savable) dobj.getLookup().lookup(InstanceCookie.class).instanceCreate();
//                                    saveable.load(s, dsChildren);
//                                    if (saveable instanceof DownloadFormat) {
//                                        s.addFormat((DownloadFormat) saveable);
//                                    }
////                                    try {
////                                        fIn = new ObjectInputStream(dsChildren.getInputStream());
////                                        //DownloadFormat f = (DownloadFormat) fIn.readObject();
////                                        //s.addFormat(f);
////                                        fIn.readObject();
////                                    } catch (ClassNotFoundException ex) {
////                                        Exceptions.printStackTrace(ex);
////                                        Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Couldn't load format for dataset");
////                                    } catch (IOException ex) {
////                                        throw ex;
////                                    } finally {
////                                        fIn.close();
////                                    }
//                                }
//                            }
//                        } catch (ClassNotFoundException ex) {
//                            Exceptions.printStackTrace(ex);
//                            Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Couldn't load download sets for database");
//                        } catch (IOException ex) {
//                            throw ex;
//                        } finally {
//                            oIn.close();
//                        }
//                    }
//                }
//                return set;
//            } catch (IOException ex) {
//                Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Couldn't load download sets for database");
//            }
//        }
        return null;
    }

    public static void saveSets(String databaseName, List<? extends DownloadSet> sets) {
//        if (databaseName != null) {
//            try {
//                FileObject dbFo = FileUtil.getConfigFile(databaseName);
//                FileObject dsRoot = dbFo.getFileObject("datasets");
//                if (dsRoot == null) {
//                    dsRoot = dbFo.createFolder("datasets");
//                }
//                //delete children
//                for (FileObject setObject : dsRoot.getChildren()) {
//                    setObject.delete();
//                }
//                //now add new children
//                for (DownloadSet downloadSet : sets) {
//                    downloadSet.save(dsRoot, downloadSet);
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(DownloadSet.class.getName()).log(Level.SEVERE, "Couldn't load download sets for database");
//            }
//        }
    }
}
