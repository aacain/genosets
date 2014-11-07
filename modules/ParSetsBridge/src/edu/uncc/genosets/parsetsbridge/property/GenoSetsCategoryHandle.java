/*
 * 
 * 
 */
package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.genosets.studyset.Condition;
import edu.uncc.genosets.studyset.ConditionTree;
import edu.uncc.parsets.data.CategoryHandle;
import edu.uncc.parsets.data.DimensionHandle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import edu.uncc.genosets.queries.Category;

/**
 *
 * @author aacain
 */
public class GenoSetsCategoryHandle extends CategoryHandle {

    protected HashMap<Condition, Condition> conditions = new HashMap<Condition, Condition>();
    protected ConditionTree conditionTree;
    protected Category category;
    private Set<Integer> lines = new HashSet<Integer>();

    public GenoSetsCategoryHandle(String name, String handle, int num, DimensionHandle dim, int count, Category category) {
        super(name, handle, num, dim, count);
        this.category = category;
    }

    public void addCondition(Condition condition) {
        this.conditions.put(condition, condition);
    }

    public Collection<Condition> getConditions() {
        return conditions.values();
    }

    public Set<Integer> getLines() {
        return lines;
    }

    public void setLines(Set<Integer> lines) {
        this.lines = lines;
    }

    public ConditionTree getConditionTree() {
        return conditionTree;
    }

    public void setConditionTree(ConditionTree conditionTree) {
        this.conditionTree = conditionTree;
    }

    public edu.uncc.genosets.queries.Category getCategory() {
        return category;
    }
    
    

    

//    @Override
//    public int compareTo(CategoryHandle o) {
//        return super.compareTo(o);
//    }

    

//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof CategoryHandle) {
//            return compareTo((CategoryHandle) obj) == 0;
//        } else {
//            return false;
//        }
//    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.getHandle() != null ? this.getHandle().hashCode() : 0);
        return hash;
    }
}
