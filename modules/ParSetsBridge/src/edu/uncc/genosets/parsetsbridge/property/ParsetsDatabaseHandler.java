/*
 * 
 * 
 */
package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.genosets.queries.DimensionChangeListener;
import edu.uncc.genosets.queries.DimensionItem;
import edu.uncc.genosets.queries.DimensionObject;
import edu.uncc.genosets.queries.DimensionUtil;
import edu.uncc.genosets.queries.Group;
import edu.uncc.parsets.data.DataType;
import java.util.*;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class ParsetsDatabaseHandler {


    public static List<GenoSetsDimensionHandle> setupRootProperties(GenoSetsController controller) {
        ArrayList<GenoSetsDimensionHandle> dimensions = new ArrayList<GenoSetsDimensionHandle>();
        Collection<? extends DimensionObject> dimChildren = DimensionUtil.getRootDimension().getChildren(true);
        for (DimensionObject dimObj : dimChildren) {
            if (controller != null) {
                dimObj.addDimensionChangeListener(WeakListeners.create(DimensionChangeListener.class, controller, dimObj));
            }
            if (dimObj.isItem()) {
                DimensionItem item = (DimensionItem) dimObj;
                Group group = item.getGroup();
                GenoSetsDimensionHandle dimHandle = new GenoSetsDimensionHandle(group.getGroupDescription(), group.toString(), DataType.categorical, group.getGroupId(), controller.getDataSet(), group, group.getPath());
                dimensions.add(dimHandle);
                dimObj.addToLookup(dimHandle);
            }
        }
        return dimensions;
    }
}
