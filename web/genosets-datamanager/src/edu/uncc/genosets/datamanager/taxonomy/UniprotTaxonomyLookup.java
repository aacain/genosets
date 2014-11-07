/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.taxonomy;

import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.exceptions.RemoteServiceException;
import edu.uncc.genosets.taskmanager.TaskLog;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author aacain
 */
public class UniprotTaxonomyLookup implements TaxonomyLookup{

    private static final String UNIPROT_TAXONOMY_URL = "http://purl.uniprot.org/taxonomy/";


    @Override
    public boolean lookupByTaxId(Organism org) throws RemoteServiceException {
        if (org.getTaxonomyIdentifier() != null) {
            List<Document> taxList = getHierarchy(org.getTaxonomyIdentifier().toString());
            Map<String, String> taxMap = new HashMap();
            int i = 0;
            for (Document document : taxList) {
                String rank = getRank(document);
                if (rank != null || i == 0) {
                    //get the taxon scientific name
                    if (rank == null) {
                        rank = LOWLEVEL;
                    }
                    NodeList list = document.getElementsByTagName(SCIENTIFIC_NAME);
                    if (list.getLength() > 0) {
                        taxMap.put(rank, list.item(0).getTextContent());
                    }
                }
                i++;
            }

            //now add to organism
            org.setKingdom(taxMap.get(SUPERKINGDOM));
            org.setPhylum(taxMap.get(PHYLUM));
            org.setTaxClass(taxMap.get(TAXCLASS));
            org.setTaxOrder(taxMap.get(TAXORDER));
            org.setFamily(taxMap.get(FAMILY));
            org.setGenus(taxMap.get(GENUS));
            org.setSpecies(taxMap.get(SPECIES));
            org.setStrain(taxMap.get(LOWLEVEL));
            if (org.getStrain() == null) {
                org.setStrain(org.getSpecies());
            }
        }
        if(org.getKingdom() != null){
            return true;
        }
        return false;
    }

    private List<Document> getHierarchy(String taxonId) throws RemoteServiceException {
        Document doc = getDocument(getRdfUrl(taxonId), 0);
        LinkedList<Document> hierList = new LinkedList();
        if (doc != null) {
            getHierarchy(doc, hierList);
        }
        return hierList;
    }

    private void getHierarchy(Document document, List<Document> ancestorList) throws RemoteServiceException {
        NodeList nodeList = document.getElementsByTagName("rdfs:subClassOf");
        if (nodeList.getLength() > 0) {
            Node n = nodeList.item(0);
            ancestorList.add(document);
            //get the resource URL string
            String urlString = getAtt(RESOURCE, n);
            if (urlString != null) {
                Document doc = getDocument(urlString + ".rdf", 0);
                if (doc != null) {
                    getHierarchy(doc, ancestorList);
                }
            }
        }
    }

    private String getAtt(String attribute, Node node) {
        String value = null;

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node att = attributes.getNamedItem(attribute);
            if (att != null) {
                return att.getNodeValue();
            }
        }

        return value;
    }

    private Document getDocument(String urlString, int numTries) throws RemoteServiceException {
        //TODO: Important = EMBL server is locking up.
        Document document = null;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            is = url.openStream();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse(is);
            return document;
        } catch (Exception ex) {
            System.out.println("Taxonomy failed " + urlString);
            try {
                //            NotifyDescriptor d = new NotifyDescriptor.Message
                //                    ("EMBL website is down. The complete taxonomy for this organism was not downloaded.  This should not affect the annotation download.",
                //                    NotifyDescriptor.WARNING_MESSAGE);
                //            DialogDisplayer.getDefault().notify(d);
                //wait and then try again
                if (numTries < 2) {
                    Thread.sleep(1000);
                    getDocument(urlString, numTries + 1);
                }else{
                    throw new RemoteServiceException("EMBL website is down. The complete taxonomy for this organism was not downloaded.");
                }
            } catch (InterruptedException ex1) {
                throw new RemoteServiceException("EMBL website is down. The complete taxonomy for this organism was not downloaded.", ex1);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                //Logger.getLogger(TaxonomyLookup.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return document;
    }

    private String getRdfUrl(final String taxonId) {
        return UNIPROT_TAXONOMY_URL + taxonId + ".rdf";
    }

    public String getRank(Document document) {
        String rank = null;
        NodeList nodeList = document.getElementsByTagName("rank");
        if (nodeList.getLength() > 0) {
            Node n = nodeList.item(0);
            String resourceAtt = getAtt(RESOURCE, n);
            if (resourceAtt == null) {
                return rank;
            } else {
                //parse the ending value
                String[] ss = resourceAtt.split("/");
                rank = ss[ss.length - 1];
            }
        }
        return rank;
    }
}
