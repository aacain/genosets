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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that holds a list of listeners of some type. Replacement of
 * EventListListener, that solves performance issue #20715
 *
 * @author rm111737
 */
class ListenerList<T> {

    private final List<T> listenerList;
    private List<T> copy = null;

    ListenerList() {
        listenerList = new ArrayList<T>();
    }

    /**
     * Adds the listener .
     *
     */
    public synchronized boolean add(T listener) {
        if (listener == null) {
            throw new NullPointerException();
        }

        copy = null;

        return listenerList.add(listener);
    }

    /**
     * Removes the listener .
     *
     */
    public synchronized boolean remove(T listener) {
        copy = null;

        return listenerList.remove(listener);
    }

    /**
     * Passes back the event listener list
     */
    public synchronized List<T> getAllListeners() {
        if (listenerList.isEmpty()) {
            return Collections.emptyList();
        }
        if (copy == null) {
            copy = new ArrayList<T>(listenerList);
        }
        return copy;
    }

    public synchronized boolean hasListeners() {
        return !listenerList.isEmpty();
    }

    static <T> List<T> allListeners(ListenerList<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.getAllListeners();
    }
}
