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

/**
 *
 * @author aacain
 */
public class DimensionFolder extends AbstractDimensionObject {

    public DimensionFolder(AbstractDimensionObject parent, String name, boolean isFolder) {
        super(parent, name, isFolder);
    }

    @Override
    public DimensionObject getChild(String name) {
        String[] path = name.split("/");
        DimensionFolder current = this;
        for (String string : path) {
            current = current.lookupChild(string);
            if(current == null){
                return null;
            }
        }
        return current;
    }
    
    private DimensionFolder lookupChild(String name){
        return (DimensionFolder) super.getChild(name);
    }
    

    @Override
    public DimensionObject createDimensionFolder(String name) {
        String[] path = name.split("/");
        return createFolder(path, 0);
    }

    private DimensionObject createFolder(String[] path, int index) {
        if (index+1 > path.length) {
            return this;
        }
        DimensionFolder folder = (DimensionFolder) getChild(path[index]);
        if (folder == null) {
            folder = new DimensionFolder(this, path[index], true);
            registerChild(folder);
        }
        return folder.createFolder(path, index+1);
    }

    @Override
    public DimensionObject createDimensionItem(Group group) {
        String[] path = group.getPath().split("/");
        return createItem(path, 0, group);
    }

    private DimensionObject createItem(String[] path, int index, Group group) {
        DimensionFolder folder = (DimensionFolder) createFolder(path, index);
        DimensionItem item = (DimensionItem) folder.getChild(group.getGroupDescription());
        if (item == null) {
            item = new DimensionItem(group, folder, group.getGroupDescription());
            folder.registerChild(item);
        }
        return item;

    }

    @Override
    public void delete() {
        if (isFolder()) {
            for (DimensionObject dimObj : this.getChildren(false)) {
                dimObj.delete();
            }
        }
        synchronized (parent) {
            parent.deregisterChild(this);
        }
    }
}
