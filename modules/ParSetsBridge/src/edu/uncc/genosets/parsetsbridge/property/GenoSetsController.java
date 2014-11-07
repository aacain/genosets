/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.parsetsbridge.property;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.queries.DimensionChangeListener;
import edu.uncc.genosets.queries.DimensionEvent;
import edu.uncc.genosets.queries.DimensionItem;
import edu.uncc.genosets.queries.DimensionObject;
import edu.uncc.genosets.queries.DimensionUtil;
import edu.uncc.genosets.queries.Group;
import edu.uncc.genosets.studyset.*;
import edu.uncc.parsets.data.CategoryHandle;
import edu.uncc.parsets.data.CategoryNode;
import edu.uncc.parsets.data.DataType;
import edu.uncc.parsets.data.DimensionHandle;
import edu.uncc.parsets.gui.Controller;
import edu.uncc.parsets.parsets.ParSetsView;
import edu.uncc.parsets.parsets.PopupPresenter;
import edu.uncc.parsets.parsets.SelectionChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JMenuItem;
import org.openide.util.NbPreferences;

/**
 *
 * @author aacain
 */
public class GenoSetsController extends Controller implements DimensionChangeListener {

    private GenoSetsDataSet ds;
    private ConcurrentHashMap<Group, GenoSetsDimensionHandle> dimsByGroup;

    public GenoSetsController() {
        this.addPopupPresenter(new PopupPresenter() {
            @Override
            public JMenuItem getJMenuItem() {
                return new JMenuItem("Add StudySet");
            }

            @Override
            public void selectionChanged(SelectionChangeEvent sce) {
                addStudySet(sce);
            }
        });
        this.parSetsView = new ParSetsView(this);
    }

    public void setDataSet(GenoSetsDataSet dataset) {
        this.ds = dataset;
    }

    public GenoSetsDataSet getDataSet() {
        return ds;
    }

    private void addStudySet(SelectionChangeEvent evt) {
               
        //create query
        CategoryNode node = evt.getSelectedCategory();
        List<GenoSetsCategoryHandle> selectedCats = new ArrayList<GenoSetsCategoryHandle>();
        List<edu.uncc.genosets.queries.Category> selectedCategories = new ArrayList<edu.uncc.genosets.queries.Category>();
        //see if on the category bar
        if (evt.isOnCategoryBar()) {
            if (node.getToCategory() instanceof GenoSetsCategoryHandle) {
                selectedCats.add(0, (GenoSetsCategoryHandle) node.getToCategory());
                selectedCategories.add(((GenoSetsCategoryHandle) node.getToCategory()).getCategory());
            }
        } else {
            while (node.getParent() != null) {
                if (node.getToCategory() instanceof GenoSetsCategoryHandle) {
                    selectedCats.add(0, (GenoSetsCategoryHandle) node.getToCategory());
                    selectedCategories.add(((GenoSetsCategoryHandle) node.getToCategory()).getCategory());
                }
                node = node.getParent();
            }
        }

        List<GenoSetsCategoryHandle> filteredCats = (List<GenoSetsCategoryHandle>) (List<? extends CategoryHandle>) evt.getFilteredCategories();
        List<edu.uncc.genosets.queries.Category> filteredCategories = new ArrayList<edu.uncc.genosets.queries.Category>();
        for (GenoSetsCategoryHandle cat : filteredCats) {
            filteredCategories.add(cat.getCategory());
        }
        CreateStudySetAction createAction = new CreateStudySetAction(this.ds.getFocusEntity(), (HashSet<Integer>) this.ds.lookupCategories(selectedCategories, filteredCategories));
        createAction.actionPerformed(null);
    }

    public ParSetsView getView() {
        return this.parSetsView;
    }

    public void addAxis(DimensionHandle dimension) {
        parSetsView.addAxis(dimension);
    }

    public void removeAxis(DimensionHandle dimension) {
        parSetsView.removeAxis(dimension);
    }

    public void addCategory(DimensionHandle dimension, CategoryHandle category) {
        parSetsView.addCategory(dimension, category);
    }

    public void removeCategory(DimensionHandle dimension, CategoryHandle category) {
        parSetsView.removeCategory(dimension, category);
    }

    @Override
    public void dimensionChanged(DimensionEvent de) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void dimensionAdded(DimensionEvent de) {
        System.gc();
        return;
    }

    @Override
    public void dimensionRemoved(DimensionEvent de) {
        DimensionObject dimensionObject = de.getDimensionObject();
        if (dimensionObject.isItem()) {
            DimensionItem dimItem = (DimensionItem) dimensionObject;
            this.removeDimension(Collections.singletonList(dimItem.getGroup()));
        }
    }

    @Override
    public void dimensionInitalized(DimensionEvent de) {
        DimensionObject dimensionObject = de.getDimensionObject();
        if (dimensionObject.isItem()) {
            DimensionItem dimItem = (DimensionItem) dimensionObject;
            this.addDimension(Collections.singletonList(dimItem.getGroup()));
        }
    }

    public List<GenoSetsDimensionHandle> removeDimension(List<Group> groupsRemoved) {
        List<GenoSetsDimensionHandle> removed = new ArrayList(groupsRemoved.size());
        for (Group group : groupsRemoved) {
            GenoSetsDimensionHandle remove = getGroupsMap().remove(group);
            this.ds.removeDimension(remove);
            removed.add(remove);
            this.removeAxis(remove);
        }
        return removed;
    }

    public List<GenoSetsDimensionHandle> addDimension(List<Group> groupsAdded) {
        List<GenoSetsDimensionHandle> added = new ArrayList(groupsAdded.size());
        for (Group group : groupsAdded) {
            GenoSetsDimensionHandle dimHandle = new GenoSetsDimensionHandle(group.getGroupDescription(), group.toString(), DataType.categorical, group.getGroupId(), ds, group, group.getPath());
            this.ds.addDimension(dimHandle);
            getGroupsMap().put(group, dimHandle);
            added.add(dimHandle);
            String path = group.getPath().isEmpty() ? group.getGroupDescription() : group.getPath() + "/" + group.getGroupDescription();
            DimensionObject child = DimensionUtil.getRootDimension().getChild(path);
            child.addToLookup(dimHandle);
        }
        return added;
    }

    private ConcurrentHashMap<Group, GenoSetsDimensionHandle> getGroupsMap() {
        synchronized (this) {
            if (dimsByGroup == null) {
                List<GenoSetsDimensionHandle> dimHandles = this.ds.getDimHandles();
                dimsByGroup = new ConcurrentHashMap();
                for (GenoSetsDimensionHandle dim : dimHandles) {
                    dimsByGroup.put(dim.getGroup(), dim);
                }
            }
        }
        return dimsByGroup;
    }

    public void setFocusEntity(FocusEntity focusEntity) {
        this.ds.setFocusEntity(focusEntity);
        NbPreferences.forModule(GenoSetsDataSet.class).put("FocusEntity", focusEntity.getEntityName());
        this.parSetsView.rebuildTree();
    }
}
