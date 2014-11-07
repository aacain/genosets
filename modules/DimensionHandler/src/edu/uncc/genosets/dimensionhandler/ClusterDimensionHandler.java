/*
 * 
 * 
 */

package edu.uncc.genosets.dimensionhandler;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public abstract class ClusterDimensionHandler implements DimensionHandler{

    public static ClusterDimensionHandler instantiate(){
        return new ClusterDimensionHandlerImpl();
    }


    private static class ClusterDimensionHandlerImpl extends ClusterDimensionHandler {

        List<Property> properties = new LinkedList<Property>();
        Property idProperty;

        public ClusterDimensionHandlerImpl() {
        }

        @Override
        public List<Property> getProperties(String tableName) {
            if(properties.isEmpty()){
                loadProperties();
            }
            return properties;
        }

        @Override
        public List<Object[]> createQuery(List<Property> selectProps, List<Property> groupByProps, boolean idAsCount) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void writeProperties() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void readProperties() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private void loadProperties(){
//            DimensionWriter writer = new DimensionWriter();
//            properties = writer.loadAll("cluster");


        }
    }
}
