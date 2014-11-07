/*
 */
package edu.uncc.genosets.datamanager.taxonomy;

import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.exceptions.RemoteServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author lucy
 */
public class EnaTaxonomyLookup implements TaxonomyLookup {

    private static final String ENA_URL = "http://www.ebi.ac.uk/ena/data/view/Taxon:";

    @Override
    public boolean lookupByTaxId(Organism org) throws RemoteServiceException {
        if (org.getTaxonomyIdentifier() != null) {
            Document doc = getDocument(ENA_URL + org.getTaxonomyIdentifier() + "&display=xml");
            if (doc != null) {
                NodeList lineages = doc.getElementsByTagName("lineage");
                for (int i = 0; i < lineages.getLength(); i++) {
                    Node item = lineages.item(i);
                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) item;
                        NodeList taxonNodes = element.getElementsByTagName("taxon");
                        for (int j = 0; j < taxonNodes.getLength(); j++) {
                            Node taxItem = taxonNodes.item(j);
                            loadNode(taxItem, org);
                        }
                        loadNode(item, org);
                    }
                }

                //now get the name for this taxon id
                NodeList taxons = doc.getElementsByTagName("taxon");
                if (taxons.getLength() > 0) {
                    Node item = taxons.item(0);
                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) item;
                        org.setStrain(element.getAttribute("scientificName"));
                    }
                }
            }
            if (org.getKingdom() != null) {
                return true;
            }
        }
        return false;
    }

    private void loadNode(Node item, Organism org) {
        if (item.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) item;
            String rank = element.getAttribute("rank");
            String sName = element.getAttribute("scientificName");
            if (rank != null) {
                if (("species").equals(rank)) {
                    org.setSpecies(sName);
                } else if (("genus").equals(rank)) {
                    org.setGenus(sName);
                } else if (("family").equals(rank)) {
                    org.setFamily(sName);
                } else if (("order").equals(rank)) {
                    org.setTaxOrder(sName);
                } else if (("class").equals(rank)) {
                    org.setTaxClass(sName);
                } else if (("phylum").equals(rank)) {
                    org.setPhylum(sName);
                } else if (("superkingdom").equals(rank)) {
                    org.setKingdom(sName);
                }
            }
        }
    }

    private Document getDocument(String urlString) throws RemoteServiceException {
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
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                throw new RemoteServiceException("EMBL website is down. The complete taxonomy for this organism was not downloaded.", ex);
            }
        }
        return document;
    }
}
