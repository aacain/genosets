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
package edu.uncc.genosets.queries;

import java.io.Serializable;
import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author aacain
 */
public abstract class DimensionObject extends Object implements Serializable, Lookup.Provider {


    /**
     * Get the name of this DimensionObject.
     *
     * @return name
     */
    public abstract String getName();

    /**
     * Get the full resource path of this object starting from the root. Folders
     * are separated with forward slashes. The root folder's path is the empty
     * string. The path of a folder never ends with a slash.
     * <p>Subclasses are strongly encouraged to override this method.
     *
     * @return the path, for example <samp>path/from/root</samp>
     */
    public String getPath() {
        StringBuilder[] buf = {null};
        constructName(buf, '/', 0);
        return buf[0].toString();
    }

    

    /**
     * Constructs path of file.
     *
     * @param arr to place the string buffer
     * @param sepChar separator character
     */
    private void constructName(StringBuilder[] arr, char sepChar, int lengthSoFar) {
        String myName = getName();
        int myLen = lengthSoFar + myName.length();

        DimensionObject parent = getParent();

        if ((parent != null) && !parent.isRoot()) {
            parent.constructName(arr, sepChar, myLen + 1);
            arr[0].append(sepChar);
        } else {
            assert arr[0] == null;
            arr[0] = new StringBuilder(myLen);
        }
        arr[0].append(getName());
    }

    /**
     * Creates a new DimensionFolder as a child of this DimensionObject.
     * Recursively creates path if path does not exist.
     *
     * @param name
     * @return newly created DimensionObject
     */
    public abstract DimensionObject createDimensionFolder(String name);

    /**
     * Creates a new DimensionItem as a child of this DimensionObject.
     * Recursively creates path if path does not exist.
     *
     * @param Group - the group to add a dimension item for
     * @return newly created DimensionObject
     */
    public abstract DimensionObject createDimensionItem(Group group);

    /**
     * Deletes this DimensionObject. If the object is a folder it will
     * recursively delete all children.
     */
    public abstract void delete();

    /**
     * Get all the children of this folder (DimensionFolder and DimensionItems).
     * If the object does not have children (does not exist or is not a folder)
     * then and empty collection should be returned. No particular order is
     * assumed.
     *
     * @param isRecursive - whether all subchildren should be returned.
     * @return collection of child dimension objects
     */
    public abstract Collection<? extends DimensionObject> getChildren(boolean isRecursive);

    /**
     * Get the child object with the given name. If no child exists, null is
     * returned
     *
     * @return child with name
     */
    public abstract DimensionObject getChild(String name);

    /**
     * Get all the child DimensionItems of this folder.If the object does not
     * have children (does not exist or is not a folder) then and empty
     * collection should be returned. No particular order is assumed.
     *
     * @param = whether we should get the children of all subfolders.
     * @return collection of child DimensionItems
     */
    public abstract Collection<? extends DimensionObject> getDimensionItemChildren(boolean isRecursive);

    /**
     * Get all the child folders of this folder. If the object does not have
     * children (does not exist or is not a folder) then and empty collection
     * should be returned. No particular order is assumed.
     *
     * @return collection of child DimensionFolder
     */
    public abstract Collection<? extends DimensionObject> getFolderChildren();

    /**
     *
     * @return parent parent DimensionFolder of this object or <code>null</code>
     * if this object {@link AbstractMethodError#isRoot}.
     */
    public abstract DimensionObject getParent();

    /**
     * Test whether this is a DimensionItem This is exclusive with
     * {@link #isFolder()}
     *
     * @return true if the dimension object is a DimensionItem.
     */
    public abstract boolean isItem();

    /**
     * Test whether this file is a folder This is exclusive with
     * {@link #isItem()}
     *
     * @return true if the dimension object is a folder (i.e., can have
     * children)
     */
    public abstract boolean isFolder();

    /**
     * Test whether this object is the root folder. The root should always be a
     * folder.
     *
     * @return true if the object is the root of the dimensions
     */
    public abstract boolean isRoot();
    
    public abstract void addToLookup(Object obj);

    /**
     * Add a listener to listen for changes in DimensionObjects.
     *
     * @param dcl
     */
    public abstract void addDimensionChangeListener(DimensionChangeListener dcl);

    /**
     * removes listeners from this object
     *
     * @param dcl
     */
    public abstract void removeDimensionChangeListener(DimensionChangeListener dcl);

    /**
     * Fires event when a dimension is added
     *
     * @param evt
     */
    public abstract void fireDimensionAddedEvent(DimensionEvent evt);

    /**
     * Fires event when a dimension is removed
     *
     * @param evt
     */
    public abstract void fireDimensionRemovedEvent(DimensionEvent evt);

    /**
     * Fires event when a dimension is changed (renamed)
     *
     * @param evt
     */
    public abstract void fireDimensionChangedEvent(DimensionEvent evt);
    
    /**
     * Fires event when a dimension is initialized
     *
     * @param evt
     */
    public abstract void fireDimensionInitializedEvent(DimensionEvent evt);
}
