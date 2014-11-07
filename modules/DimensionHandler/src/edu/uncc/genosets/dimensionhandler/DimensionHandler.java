/*
 * 
 * 
 */

package edu.uncc.genosets.dimensionhandler;

import java.util.List;

/**
 *
 * @author aacain
 */
public interface DimensionHandler {
    
    public List<Property> getProperties(String tableName);
    
    public List<Object[]> createQuery(List<Property> selectProps, List<Property> groupByProps, boolean idAsCount);

    public void writeProperties();

    public void readProperties();
}
