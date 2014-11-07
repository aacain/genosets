/*
 * 
 * 
 */

package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.parsets.data.CategoryHandle;
import edu.uncc.parsets.data.DataSet;
import edu.uncc.parsets.data.DataType;
import edu.uncc.parsets.data.DimensionHandle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class ParsetsDimensionHandle extends DimensionHandle{
    protected List<GenoSetsCategoryHandle> categories = new LinkedList<GenoSetsCategoryHandle>();
    private DataSet ds;
    private String handle;

    public ParsetsDimensionHandle(String name, String handle, DataType dataType, int dimNum, DataSet dataset) {
        super(name, handle, dataType, dimNum, null);
        this.ds = dataset;
    }

    public void addCategory(GenoSetsCategoryHandle category){
        getCategories().add(category);
    }

    @Override
    public List<CategoryHandle> getCategories() {
        return (List<CategoryHandle>) (List<? extends CategoryHandle>)categories;
    }

    @Override
    public Iterator<CategoryHandle> iterator() {
        return (Iterator<CategoryHandle>) (Iterator<? extends CategoryHandle>)categories.iterator();
    }

    @Override
    public CategoryHandle num2Handle(int num) {
        return categories.get(num);
    }

    @Override
    public DataSet getDataSet() {
        return this.ds;
    }

    @Override
    public String getHandle() {
        return this.handle;
    }
}
