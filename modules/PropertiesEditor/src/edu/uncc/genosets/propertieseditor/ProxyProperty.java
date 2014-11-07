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
package edu.uncc.genosets.propertieseditor;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;

/**
 *
 * @author aacain
 */
public class ProxyProperty<T> extends Property<T> {

    private final Node node;
    private final Property<T> orig;
    private final boolean canEdit;

    public ProxyProperty(Node node, Property<T> orig, boolean canEdit) {
        super(orig.getValueType());
        this.node = node;
        this.orig = orig;
        this.canEdit = canEdit;
        setName(orig.getName());
        setDisplayName(orig.getDisplayName());
        setShortDescription(orig.getShortDescription());
    }

    public Property<T> getOriginal() {
        return orig;
    }
    
    

    @Override
    public boolean canRead() {
        return orig.canRead();
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        return orig.getValue();
    }

    @Override
    public boolean canWrite() {
        return canEdit;
    }

    @Override
    public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        orig.setValue(val);
    }
}
