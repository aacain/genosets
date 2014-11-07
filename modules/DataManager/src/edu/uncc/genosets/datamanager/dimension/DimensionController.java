/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension;

import java.util.List;
import java.util.Set;

/**
 *
 * @author aacain
 */
public interface DimensionController {
    public void addDimension(Table table, Dimension dimension);
    public Table getTable(String table);
    public DimensionController instantiate();
    public Set<Dimension> getDimensions(Table table);
    public String createQuery(Table rootTable, List<Dimension> dimensions);
}
