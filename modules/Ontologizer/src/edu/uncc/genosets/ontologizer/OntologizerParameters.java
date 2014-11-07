/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer;

import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.xml.XMLUtil;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author aacain
 */
public class OntologizerParameters {

    private String studySetUniqueName;
    private List<StudySet> populationSets;
    //private OboDataObject oboDao;
    private Date date = new Date();
    private boolean useAllPop = false;
    private boolean missingPopulationSet = false;
    private String mtc = "Bonferroni";
    private String calculation = "Term-For-Term";
    private OboDataObject obo;
    private Integer termsByOrthologMethod;

//    public OboDataObject getOboDao() {
//        return oboDao;
//    }
//
//    public void setOboDao(OboDataObject oboDao) {
//        this.oboDao = oboDao;
//    }
    public void setObo(OboDataObject obo) {
        this.obo = obo;
    }

    public OboDataObject getObo() {
        return this.obo;
    }

    public List<StudySet> getPopulationSets() {
        return populationSets;
    }

    public void setPopulationSets(List<StudySet> populationSets) {
        this.populationSets = populationSets;
    }

    public String getStudySetUniqueName() {
        return studySetUniqueName;
    }

    public void setStudySetUniqueName(String studySetUniqueName) {
        this.studySetUniqueName = studySetUniqueName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isMissingPopulationSet() {
        return missingPopulationSet;
    }

    public void setMissingPopulationSet(boolean missingPopulationSet) {
        this.missingPopulationSet = missingPopulationSet;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public String getMtc() {
        return mtc;
    }

    public void setMtc(String mtc) {
        this.mtc = mtc;
    }

    public Integer getTermsByOrthologMethod() {
        return termsByOrthologMethod;
    }

    public void setTermsByOrthologMethod(Integer termsByOrthologMethod) {
        this.termsByOrthologMethod = termsByOrthologMethod;
    }

    public static OntologizerParameters parseParameters(FileObject fo) throws IOException {
        OntologizerParameters params = new OntologizerParameters();
        InputSource source = new InputSource(fo.getInputStream());
        StudySetManager stdyMgr = StudySetManagerFactory.getDefault();
        try {
            Document doc = XMLUtil.parse(source, false, true, null, null);
            NodeList roots = doc.getElementsByTagName("ontologizer");
            for (int i = 0; i < roots.getLength(); i++) {
                Node root = roots.item(i);
                NodeList rootChildren = root.getChildNodes();
                for (int j = 0; j < rootChildren.getLength(); j++) {
                    Node item = rootChildren.item(j);
                    NamedNodeMap attMap = item.getAttributes();
                    if (item.getNodeName().equals("date")) {
                        Node att = attMap.getNamedItem("longValue");
                        if (att != null) {
                            params.setDate(new Date(Long.parseLong(att.getNodeValue())));
                        }
                    } else if (item.getNodeName().equals("obo")) {
                        Node att = attMap.getNamedItem("name");
                        if (att != null) {
                            String oboPath = att.getNodeValue();
                            if (oboPath != null) {
                                //changedAAC
                                //params.setObo(oboPath);
                            }
                        }
                    } else if (item.getNodeName().equals("orthologMethod")) {
                        Node att = attMap.getNamedItem("intValue");
                        if (att != null) {
                            params.setTermsByOrthologMethod(Integer.parseInt(att.getNodeValue()));
                        } else {
                        }
                    } else if (item.getNodeName().equals("populations")) {
                        NodeList sets = item.getChildNodes();
                        List<StudySet> popSets = new ArrayList<StudySet>();
                        for (int k = 0; k < sets.getLength(); k++) {
                            Node set = sets.item(k);
                            if (set.getNodeName().equals("set")) {
                                NamedNodeMap setAtts = set.getAttributes();
                                Node name = setAtts.getNamedItem("name");
                                if (name != null) {
                                    StudySet studySet = stdyMgr.getStudySet(name.getNodeValue());
                                    if (studySet != null) {
                                        popSets.add(studySet);
                                    } else {
                                        params.setMissingPopulationSet(Boolean.TRUE);
                                    }
                                }
                            }
                        }
                        params.setPopulationSets(popSets);
                    }
                }
            }
        } catch (SAXException ex) {
            throw new IOException(ex);
        }
        return params;
    }

    public static void createDetailsFile(FileObject fo, OntologizerParameters params) throws IOException {
        //Document doc = XMLUtil.createDocument("ontologizer", null, "-//GenoSets//DTD Details 1.0//EN", "http://gibas-research.uncc.edu/genosets/dtds/details.dtd");
        Document doc = XMLUtil.createDocument("ontologizer", null, null, null);
        Node root = doc.getElementsByTagName("ontologizer").item(0);
        //version
        Element version = doc.createElement("version");
        version.setAttribute("intValue", "2");
        root.appendChild(version);
        //set date
        Element date = doc.createElement("date");
        date.setAttribute("longValue", Long.toString(params.getDate().getTime()));
        root.appendChild(date);
        //byOrthologMethodId
        if (params.getTermsByOrthologMethod() != null) {
            Element orthoMethodId = doc.createElement("orthologMethod");
            orthoMethodId.setAttribute("intValue", params.getTermsByOrthologMethod().toString());
            root.appendChild(orthoMethodId);
        }
        //obo element
        Element obo = doc.createElement("obo");
        obo.setAttribute("name", params.getObo().getUrl());
        root.appendChild(obo);
        //set populations
        Element pops = doc.createElement("populations");
        root.appendChild(pops);
        if (params.getPopulationSets() != null) {
            for (StudySet ss : params.getPopulationSets()) {
                Element set = doc.createElement("set");
                set.setAttribute("name", ss.getUniqueName());
                pops.appendChild(set);
            }
        }
        OutputStream os = null;
        try {
            os = fo.getOutputStream();
            XMLUtil.write(doc, os, "UTF-8");
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
}
