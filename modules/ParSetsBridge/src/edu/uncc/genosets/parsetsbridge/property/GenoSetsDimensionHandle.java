/*
 * 
 * 
 */
package edu.uncc.genosets.parsetsbridge.property;


import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import edu.uncc.parsets.data.CategoryHandle;
import edu.uncc.parsets.data.DataSet;
import edu.uncc.parsets.data.DataType;
import edu.uncc.parsets.data.DimensionHandle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author aacain
 */
public class GenoSetsDimensionHandle extends DimensionHandle {

    protected List<GenoSetsCategoryHandle> categories = null;
    private GenoSetsDataSet ds;
    private String handle;
    private String path;
    private Group group;
    private HashMap<edu.uncc.genosets.queries.Category, GenoSetsCategoryHandle> categoryLookup;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public GenoSetsDimensionHandle(String name, String handle, DataType dataType, int dimNum, GenoSetsDataSet dataset, Group group) {
        super(name, handle, dataType, dimNum, null);
        this.ds = dataset;
        this.handle = handle;
        path = "";
        this.group = group;
    }

    public GenoSetsDimensionHandle(String name, String handle, DataType dataType, int dimNum, GenoSetsDataSet dataset, Group group, String path) {
        super(name, handle, dataType, dimNum, null);
        this.ds = dataset;
        this.handle = handle;
        this.path = path;
        this.group = group;
    }

    public void addCategory(GenoSetsCategoryHandle category) {
        getCategories().add(category);
    }

    @Override
    public List<CategoryHandle> getCategories() {
        if(categories == null){
            categories = new CopyOnWriteArrayList<GenoSetsCategoryHandle>();
            List<edu.uncc.genosets.queries.Category> groupCategories = group.getEntityQuery().getCategories(group);
            categoryLookup = new HashMap<edu.uncc.genosets.queries.Category, GenoSetsCategoryHandle>();
            int i = 0;
            for (edu.uncc.genosets.queries.Category category : groupCategories) {
                i++;
                GenoSetsCategoryHandle gsCategory = new GenoSetsCategoryHandle(category.getCategoryName(), category.getCategoryId().toString(), i, this, 0, category);
                categories.add(gsCategory);
                categoryLookup.put(category, gsCategory);
            }
//            ds.getDbHandler().getCategories(this);
        }
        return (List<CategoryHandle>) (List<? extends CategoryHandle>) categories;
    }

    @Override
    public Iterator<CategoryHandle> iterator() {
        return (Iterator<CategoryHandle>) (Iterator<? extends CategoryHandle>) getCategories().iterator();
    }

    @Override
    public CategoryHandle num2Handle(int num) {
        return getCategories().get(num);
    }

    @Override
    public DataSet getDataSet() {
        return this.ds;
    }

    @Override
    public String getHandle() {
        return this.handle;
    }

    public String getPath() {
        return path;
    }

    public Group getGroup() {
        return group;
    }  
    
    public GenoSetsCategoryHandle getCategory(Category category){
        return categoryLookup.get(category);
    }
    
}
