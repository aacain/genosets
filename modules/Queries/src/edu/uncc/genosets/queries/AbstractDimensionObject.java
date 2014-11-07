package edu.uncc.genosets.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/*
 * Copyright (C) 2013 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Implementation of the dimension object that simplyfies common tasks with
 * hierarchy of objects for DimensionFolder and DimensionItem.
 *
 * @author aacain
 */
public abstract class AbstractDimensionObject extends DimensionObject {

    /**
     * empty array
     */
    private static final AbstractDimensionObject[] EMPTY_ARRAY = new AbstractDimensionObject[0];
    /**
     * name of the dimension object (only name, no path)
     */
    protected String name;
    /**
     * strong reference to parent (can be null for root)
     */
    protected final AbstractDimensionObject parent;
    /**
     * list of children
     */
    private final List<AbstractDimensionObject> children = new ArrayList<AbstractDimensionObject>();
    /**
     * map of children by name
     *
     */
    private Map<String, AbstractDimensionObject> map = new HashMap<String, AbstractDimensionObject>();
    /**
     * cache to remember if this object is folder or not
     */
    private final boolean folder;
    /**
     * listeners
     */
    private ListenerList<DimensionChangeListener> listeners;
    /**
     * implementation of lookup associated with this file object
     */
    private Lookup lkp;
    private InstanceContent ic;

    public AbstractDimensionObject(AbstractDimensionObject parent, String name, boolean isFolder) {
        this.parent = parent;
        this.name = name;
        this.folder = isFolder;
        this.ic = new InstanceContent();
        this.lkp = new AbstractLookup(ic);
    }

    /* Test whether this object is the root folder.
     * The root should always be a folder.
     * @return true if the object is the root
     */
    @Override
    public final boolean isRoot() {
        return parent == null;
    }

    /**
     * Test whether this is a DimensionItem This is exclusive with
     * {@link #isFolder()}
     *
     * @return true if the dimension object is a DimensionItem.
     */
    @Override
    public final boolean isItem() {
        return !isFolder();
    }
    /* Test whether this object is a folder.
     * @return true if the file object is a folder (i.e., can have children)
     */

    @Override
    public boolean isFolder() {
        return folder;
    }
    /* Get parent folder.
     * The returned object will satisfy {@link #isFolder}.
     *
     * @return the parent folder or <code>null</code> if this object {@link #isRoot}.
     */

    @Override
    public final DimensionObject getParent() {
        return parent;
    }

    /**
     * Get the name of this DimensionObject.
     *
     * @return name
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DimensionObject getChild(String name) {
        synchronized (this) {
            return this.map.get(name);
        }
    }

    /* Remove listener from this object.
     * @param l the listener
     */
    @Override
    public final void removeDimensionChangeListener(DimensionChangeListener fcl) {
        if (listeners != null) {
            listeners.remove(fcl);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends DimensionObject> getChildren(boolean isRecursive) {
        ArrayList<AbstractDimensionObject> list = new ArrayList<AbstractDimensionObject>();
        synchronized (children) {
            if (isRecursive) {
                for (AbstractDimensionObject dimObj : children) {
                    list.add(dimObj);
                    list.addAll((Collection<? extends AbstractDimensionObject>) dimObj.getChildren(isRecursive));
                }
            } else {
                list.addAll(children);
            }
        }
        Collections.sort(list, comparator);
        return list;
    }
    
    private static Comparator<DimensionObject> comparator = new Comparator<DimensionObject>(){

        @Override
        public int compare(DimensionObject o1, DimensionObject o2) {
            if(o1.isFolder() && o2.isItem()){
                return -1;
            }else if(o1.isItem() && o2.isFolder()){
                return 1;
            }
            return o1.getName().compareTo(o2.getName());
        }
        
    };

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends DimensionObject> getDimensionItemChildren(boolean isRecursive) {
        ArrayList<AbstractDimensionObject> list = null;
        synchronized (children) {
            list = new ArrayList();
            for (AbstractDimensionObject dimObj : children) {
                if (dimObj.isItem()) {
                    list.add(dimObj);
                } else {
                    if (isRecursive) {
                        list.addAll((Collection<? extends AbstractDimensionObject>) dimObj.getDimensionItemChildren(true));
                    }
                }
            }
        }
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends DimensionObject> getFolderChildren() {
        ArrayList<AbstractDimensionObject> list = null;
        synchronized (children) {
            list = new ArrayList();
            for (AbstractDimensionObject dimObj : children) {
                if (dimObj.isFolder()) {
                    list.add(dimObj);
                }
            }
        }
        return list;
    }

    protected void registerChild(AbstractDimensionObject dimObject) {
        synchronized (this) {
            children.add(dimObject);
            map.put(dimObject.getName(), dimObject);
            fireDimensionInitializedEvent(new DimensionEvent(this, dimObject));
            fireDimensionAddedEvent(new DimensionEvent(this, dimObject));
        }
    }

    protected void deregisterChild(AbstractDimensionObject dimObject) {
        synchronized (this) {
            children.remove(dimObject);
            map.remove(dimObject.getName());
            fireDimensionRemovedEvent(new DimensionEvent(this, dimObject));
        }
    }

    /* Add new listener to this object.
     * @param l the listener
     */
    @Override
    public final void addDimensionChangeListener(DimensionChangeListener dcl) {
        synchronized (EMPTY_ARRAY) {
            if (listeners == null) {
                listeners = new ListenerList<DimensionChangeListener>();
            }
        }

        listeners.add(dcl);
    }

    @Override
    public void fireDimensionAddedEvent(DimensionEvent evt) {
        synchronized (EMPTY_ARRAY) {
            if (listeners != null) {
                for (DimensionChangeListener listener : listeners.getAllListeners()) {
                    listener.dimensionAdded(evt);
                }
            }
        }
    }

    @Override
    public void fireDimensionRemovedEvent(DimensionEvent evt) {
        synchronized (EMPTY_ARRAY) {
            if (listeners != null) {
                for (DimensionChangeListener listener : listeners.getAllListeners()) {
                    listener.dimensionRemoved(evt);
                }
            }
        }
    }

    @Override
    public void fireDimensionChangedEvent(DimensionEvent evt) {
        synchronized (EMPTY_ARRAY) {
            if (listeners != null) {
                for (DimensionChangeListener listener : listeners.getAllListeners()) {
                    listener.dimensionChanged(evt);
                }
            }
        }
    }

    @Override
    public void fireDimensionInitializedEvent(DimensionEvent evt) {
        synchronized (EMPTY_ARRAY) {
            if (listeners != null) {
                for (DimensionChangeListener listener : listeners.getAllListeners()) {
                    listener.dimensionInitalized(evt);
                }
            }
        }
    }

    @Override
    public Lookup getLookup() {
        return this.lkp; 
    }

    @Override
    public void addToLookup(Object obj) {
        this.ic.add(obj);
    }

    @Override
    public String toString() {
        return this.getName();
    }
    
    
}
