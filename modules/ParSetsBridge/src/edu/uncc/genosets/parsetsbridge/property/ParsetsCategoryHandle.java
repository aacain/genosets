/*
 * 
 * 
 */

package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.genosets.studyset.Condition;
import edu.uncc.parsets.data.CategoryHandle;
import edu.uncc.parsets.data.DimensionHandle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class ParsetsCategoryHandle extends CategoryHandle{
    protected HashMap<Condition, Condition> conditions = new HashMap<Condition, Condition>();
    private Set<Integer> lines = new HashSet<Integer>();

    public ParsetsCategoryHandle(String name, String handle, int num, DimensionHandle dim, int count) {
        super(name, handle, num, dim, count);
    }

    public void addCondition(Condition condition){
        this.conditions.put(condition, condition);
    }

    public Collection<Condition> getConditions(){
        return conditions.values();
    }

    public Set<Integer> getLines() {
        return lines;
    }

    public void setLines(Set<Integer> lines) {
        this.lines = lines;
    }

}
