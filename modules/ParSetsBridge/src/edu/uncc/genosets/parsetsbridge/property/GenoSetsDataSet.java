/*
 * 
 * 
 */
package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.queries.core.EntityQuery;
import edu.uncc.genosets.queries.Group;
import edu.uncc.parsets.data.*;
import edu.uncc.parsets.parsets.SelectionChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import edu.uncc.genosets.queries.Category;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

/**
 *
 * @author aacain
 */
public class GenoSetsDataSet extends DataSet {

    public static final String PROP_FOCUS_ENTITY_CHANGED = "PROP_FOCUS_ENTITY_CHANGED";
    public static final String PROP_DIMENSIONS_ADDED = "PROP_DIMENSIONS_ADDED";
    public static final String PROP_DIMENSIONS_REMOVED = "PROP_DIMENSIONS_REMOVED";
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private List<GenoSetsDimensionHandle> dimHandles;
    private int numRecords;
    private List<GenoSetsDimensionHandle> currentDims;
    private String section = "Misc";
    private FocusEntity focusEntity;
    private Set<Integer> allIds;
    private HashMap<Group, HashMap<edu.uncc.genosets.queries.Category, edu.uncc.genosets.queries.Category.CategoryIds>> idLookup;

    public GenoSetsDataSet(FocusEntity entity) {
        this.name = "GenoSets";
        this.focusEntity = entity;
        idLookup = new HashMap();
    }

    public synchronized void setDimensions(List<GenoSetsDimensionHandle> dimensions) {
        this.dimHandles = dimensions;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public synchronized List<GenoSetsDimensionHandle> getDimHandles() {
        return dimHandles;
    }

    public synchronized void addDimension(GenoSetsDimensionHandle dimension) {
        getDimHandles().add(dimension);
    }

    public synchronized void removeDimension(GenoSetsDimensionHandle dimension) {
        getDimHandles().remove(dimension);
    }

    @Override
    public Iterator<DimensionHandle> iterator() {
        List<? extends DimensionHandle> dims2 = null;
        synchronized (this) {
            dims2 = new ArrayList(dimHandles);
        }
        return (Iterator<DimensionHandle>) dims2.iterator();
    }

    @Override
    public CategoryTree getTree(final List<DimensionHandle> dimensions) {
        currentDims = (List<GenoSetsDimensionHandle>) (List<? extends DimensionHandle>) dimensions;
        for (GenoSetsDimensionHandle dimHandle : currentDims) {
            dimHandle.getGroup();
        }
        final CategoryTree tree = new CategoryTree(dimensions.size() + 1);
        final CategoryNode root = new CategoryNode(null, null, 0);
        tree.addtoLevel(0, root);


        ProgressRunnable r = new ProgressRunnable() {
            @Override
            public Object run(ProgressHandle handle) {
                //get allIds
                if (getAllIds() == null) {
                    setAllIds(EntityQuery.getAllIds(focusEntity));
                }
                HashMap<Group, HashMap<edu.uncc.genosets.queries.Category, edu.uncc.genosets.queries.Category.CategoryIds>> usedDimMap = new HashMap();
                createTree(Collections.singletonList(root), tree, 1, dimensions, usedDimMap);
                setIdLookup(usedDimMap);
                return null;
            }
        };
        ProgressUtils.showProgressDialogAndRun(r, "Querying", true);

        return tree;
    }

    private synchronized void setIdLookup(HashMap<Group, HashMap<edu.uncc.genosets.queries.Category, edu.uncc.genosets.queries.Category.CategoryIds>> idLookup) {
        this.idLookup = idLookup;
    }

    /**
     * Recursively gets the children of the parent nodes. When at the bottom it
     * adds the count
     *
     * @param parentNodes
     * @param tree - the built tree
     * @param level - the level in the hierarchy
     * @param dimList The list of selected dimensions
     */
    private void createTree(List<CategoryNode> parentNodes, CategoryTree tree, int level, final List<DimensionHandle> dimList, HashMap<Group, HashMap<edu.uncc.genosets.queries.Category, edu.uncc.genosets.queries.Category.CategoryIds>> usedDimMap) {
        if (level <= dimList.size()) {
            List<CategoryNode> childNodes = new LinkedList<CategoryNode>();
            HashMap<edu.uncc.genosets.queries.Category, edu.uncc.genosets.queries.Category.CategoryIds> categoryMap = null;
            for (CategoryNode parent : parentNodes) {
                GenoSetsDimensionHandle dim = (GenoSetsDimensionHandle) dimList.get(level - 1);
                if (categoryMap == null) {
                    categoryMap = idLookup.get(dim.getGroup());
                    if (categoryMap == null) {
                        categoryMap = new HashMap();
                        List<Category.CategoryIds> idsByGroup = dim.getGroup().getEntityQuery().getIdsByGroup(dim.getGroup(), getAllIds(), focusEntity);
                        for (Category.CategoryIds categoryIds : idsByGroup) {
                            categoryMap.put(categoryIds.getCategory(), categoryIds);
                        }
                    }
                    usedDimMap.put(dim.getGroup(), categoryMap);
                }
                List<GenoSetsCategoryHandle> categories = (List<GenoSetsCategoryHandle>) (List<? extends CategoryHandle>) dim.getCategories();
                for (GenoSetsCategoryHandle cat : categories) {
                    CategoryNode childNode = new CategoryNode(parent, cat, 0);
                    tree.addtoLevel(level, childNode);
                    childNodes.add(childNode);
                }
            }
            createTree(childNodes, tree, level + 1, dimList, usedDimMap);
        } else {//get the count for these nodes because we are at the leaves
            for (CategoryNode node : parentNodes) {
                GenoSetsCategoryHandle cat = (GenoSetsCategoryHandle) node.getToCategory();
                GenoSetsDimensionHandle dimension = (GenoSetsDimensionHandle) cat.getDimension();
                HashMap<Category, Category.CategoryIds> catMap = usedDimMap.get(dimension.getGroup());
                //Set<Integer> lines = catMap.get(cat.getCategory()).getIds();
                Set<Integer> lines = new HashSet<Integer>(catMap.get(cat.getCategory()).getIds());
                getCountForNode(node, lines, usedDimMap);
//                Long count = new Long(lines.size());
                node.setCount(lines.size());
            }
        }
    }

    private void getCountForNode(CategoryNode node, Set<Integer> lines, HashMap<Group, HashMap<Category, Category.CategoryIds>> usedDimMap) {
        if (node.getParent() != null) {
            //if (node.getParent().getToCategory() != null) {
            GenoSetsCategoryHandle cat = (GenoSetsCategoryHandle) node.getToCategory();
            GenoSetsDimensionHandle dim = (GenoSetsDimensionHandle) cat.getDimension();
            HashMap<Category, Category.CategoryIds> catLook = usedDimMap.get(dim.getGroup());
            Category.CategoryIds ids = catLook.get(cat.getCategory());
            lines.retainAll(ids.getIds());
            getCountForNode(node.getParent(), lines, usedDimMap);
            //}
        }
    }

    @Override
    public int getNumRecords() {
        return numRecords;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public int getNumDimensions() {
        return getDimHandles().size();
    }

    @Override
    public int getNumCategoricalDimensions() {
        int num = 0;
        for (GenoSetsDimensionHandle d : getDimHandles()) {
            if (d.getDataType() == DataType.categorical) {
                num++;
            }
        }
        return num;
    }

    @Override
    public int getNumNumericDimensions() {
        return getNumDimensions() - getNumCategoricalDimensions();
    }

    @Override
    public DimensionHandle[] getNumericDimensions() {
        DimensionHandle[] handles = new GenoSetsDimensionHandle[getNumNumericDimensions()];
        int i = 0;
        for (GenoSetsDimensionHandle d : getDimHandles()) {
            if (d.getDataType() != DataType.categorical) {
                handles[i++] = d;
            }
        }
        return handles;
    }

    @Override
    public String getSection() {
        return section;
    }

    @Override
    public void selectionChanged(SelectionChangeEvent sce) {
    }

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getOldValue() != evt.getNewValue()) {
//            if (PROP_FOCUS_ENTITY_CHANGED.equals(evt.getPropertyName())) {
//                synchronized (this) {
//                    this.focusEntity = (FocusEntity) evt.getNewValue();
//                    allIds = null;
//                    setIdLookup(new HashMap());
//                    NbPreferences.forModule(GenoSetsDataSet.class).put("FocusEntity", focusEntity.getEntityName());
//                }
//            } else if (StudySetManager.PROP_STUDYSET_CHANGE.equals(evt.getPropertyName())) {
////                this.dbHandler.updateDimensions();
////                this.pcs.firePropertyChange(evt);
//            } else if (EntityQuery.PROP_GROUPS_ADDED.equals(evt.getPropertyName())) {
//                EntityQuery eq = (EntityQuery) evt.getSource();
//                List<GenoSetsDimensionHandle> addDimension = this.dbHandler.addDimension((List<Group>) evt.getNewValue());
//                this.pcs.firePropertyChange(PROP_DIMENSIONS_ADDED, null, addDimension);
//            } else if (EntityQuery.PROP_GROUPS_REMOVED.equals(evt.getPropertyName())) {
//                List<GenoSetsDimensionHandle> removeDimension = this.dbHandler.removeDimension((List<Group>) evt.getNewValue());
//                this.pcs.firePropertyChange(PROP_DIMENSIONS_REMOVED, null, removeDimension);
//            }
//        }
//    }
    public List<GenoSetsDimensionHandle> getCurrentDims() {
        return currentDims;
    }

    public synchronized FocusEntity getFocusEntity() {
        return this.focusEntity;
    }

    protected synchronized Set<Integer> getAllIds() {
        return allIds;
    }

    protected synchronized void setAllIds(Set<Integer> allIds) {
        this.allIds = allIds;
    }

    public synchronized Set<Integer> lookupCategories(List<Category> categories, List<Category> filteredCategories) {
        HashSet<Integer> result = null;
        if (idLookup != null) {
            for (Category category : categories) {
                HashMap<Category, Category.CategoryIds> get = idLookup.get(category.getGroup());
                Category.CategoryIds ids = get.get(category);
                if (result == null) {
                    result = new HashSet(ids.getIds());
                } else {
                    result.retainAll(ids.getIds());
                }
            }
            for (Category category : filteredCategories) {
                HashMap<Category, Category.CategoryIds> get = idLookup.get(category.getGroup());
                Category.CategoryIds ids = get.get(category);
                if (result == null) {
                    result = new HashSet(ids.getIds());
                } else {
                    result.removeAll(ids.getIds());
                }
            }
        }

        return result;
    }

    public void setFocusEntity(FocusEntity entity) {
        this.focusEntity = entity;
        setAllIds(EntityQuery.getAllIds(focusEntity));
        idLookup.clear();
    }
}
