/*
 * 
 * 
 */
package edu.uncc.genosets.dimensionhandler;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.dimensionhandler.CombinedProperty.PropertyCriteria;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author aacain
 */
public class DimensionWriter {

    public FileObject root;
    public String database;
    public static final String DIMENSION_FILE = "dim";
    private Document doc;
    public static final String PROPERTY = "property";
    public static final String COMBINED_PROPERTY = "combined-property";
    public static final String SUB_PROPERTY_NAME = "property_name";
    public static final String CRIT_VALUE = "crit-value";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "displayName";
    public static final String ALIAS = "alias";
    public static final String PROPERTY_TYPE = "property-type";
    public static final String OPERATOR = "operator";
    public static final String CRITERIA = "criteria";
    public static final String CRITERIA_PROPERTY = "criteria-property";

    public DimensionWriter() {
        try {
            DataManager mgr = DataManager.getDefault();
            database = mgr.getConnectionId();
            FileObject dimObject = FileUtil.getConfigRoot().getFileObject(DIMENSION_FILE);
            if (dimObject == null) {
                dimObject = FileUtil.createFolder(FileUtil.getConfigRoot(), DIMENSION_FILE);
            }
            root = dimObject.getFileObject(database);
            if (root == null) {
                root = FileUtil.createData(dimObject, database + ".xml");
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void save(Property property) {
        if (doc == null) {
            doc = XMLUtil.createDocument("root", null, null, null);
        }
        saveProperty(property);
        OutputStream oOut = null;
        try {
            oOut = root.getOutputStream();
            XMLUtil.write(doc, oOut, "UTF-8"); //document, fos, "UTF-8"
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                oOut.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void saveProperty(Property property) {
        OutputStream oOut = null;
        try {
            oOut = root.getOutputStream();
            createDoc(property);
            XMLUtil.write(doc, oOut, "UTF-8"); //document, fos, "UTF-8"
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                oOut.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public List<Property> load(FileObject fo) {
        List<Property> props = new LinkedList<Property>();
        InputStream oIn = null;
        doc = null;
        try {
            oIn = fo.getInputStream();
            doc = XMLUtil.parse(new InputSource(oIn), true, true, null, null); 
            props = readDocument(doc);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                oIn.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return props;
    }

    public List<Property> loadAll() {
        return this.load(root);
    }

    public List<Property> loadAll(String dimensionType){

        return this.load(root);
    }

    private void createCombinedDoc(Element propElement, CombinedProperty combinedProperty) {
        propElement.setAttribute(OPERATOR, combinedProperty.getOperator());
        propElement.setAttribute(PROPERTY_TYPE, combinedProperty.getPropertyType());
        Element critGroup = doc.createElement(CRITERIA);
        propElement.appendChild(critGroup);
        for (PropertyCriteria crit : combinedProperty.getPropertyList()) {
            Element c = doc.createElement(CRITERIA_PROPERTY);
            c.setAttribute(SUB_PROPERTY_NAME, crit.getProperty().getAlias());
            c.setAttribute(CRIT_VALUE, crit.getValue());
            critGroup.appendChild(c);
        }
    }

    private void createDoc(Property property) {
        Node allElement = doc.getFirstChild();
        Element propElement = null;
        if (property instanceof CombinedProperty) {
            propElement = doc.createElement(COMBINED_PROPERTY);
        } else {
            propElement = doc.createElement(PROPERTY);
        }
        allElement.appendChild(propElement);
        propElement.setAttribute(NAME, property.getName());
        propElement.setAttribute(DISPLAY_NAME, property.getDisplayName());
        propElement.setAttribute(ALIAS, property.getAlias());
        propElement.setAttribute(PROPERTY_TYPE, property.getPropertyType());
        if (property instanceof CombinedProperty) {
            createCombinedDoc(propElement, (CombinedProperty) property);
        }
    }

    private List<Property> readDocument(Document doc) {
        List<Property> props = new LinkedList<Property>();
        HashMap<String, Property> map = new HashMap<String, Property>();
        NodeList propNodes = doc.getElementsByTagName(PROPERTY);
        for (int i = 0; i < propNodes.getLength(); i++) {
            Node prop = propNodes.item(i);
            NamedNodeMap attMap = prop.getAttributes();
            Property p = new Property(attMap.getNamedItem(NAME).getNodeValue(),
                    attMap.getNamedItem(ALIAS).getNodeValue(),
                    attMap.getNamedItem(DISPLAY_NAME).getNodeValue(),
                    attMap.getNamedItem(PROPERTY_TYPE).getNodeValue());
            map.put(p.getAlias(), p);
            props.add(p);
        }

        //get combined properties
        propNodes = doc.getElementsByTagName(COMBINED_PROPERTY);
        for (int i = 0; i < propNodes.getLength(); i++) {
            Node prop = propNodes.item(i);
            NamedNodeMap attMap = prop.getAttributes();
            CombinedProperty p = new CombinedProperty(attMap.getNamedItem(NAME).getNodeValue(),
                    attMap.getNamedItem(ALIAS).getNodeValue(),
                    attMap.getNamedItem(DISPLAY_NAME).getNodeValue(),
                    attMap.getNamedItem(OPERATOR).getNodeValue(),
                    attMap.getNamedItem(PROPERTY_TYPE).getNodeValue());
            NodeList childNodes = prop.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node c = childNodes.item(j);
                String name = c.getNodeName();
                if (CRITERIA.equals(name)) {
                    NodeList crits = c.getChildNodes();
                    for (int k = 0; k < crits.getLength(); k++) {
                        Node crit = crits.item(k);
                        if ((CRITERIA_PROPERTY).equals(crit.getNodeName())) {
                            NamedNodeMap atts = crit.getAttributes();
                            String parentName = atts.getNamedItem(SUB_PROPERTY_NAME).getNodeValue();
                            Property parent = map.get(parentName);
                            p.addProperty(parent, atts.getNamedItem(CRIT_VALUE).getNodeValue());
                        }
                    }
                }
            }

            props.add(p);
        }

        return props;
    }
}
