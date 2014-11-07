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
public class DimensionLock {
    /** Determines if lock is locked or if it was released. */
    private boolean locked = true;
    protected Throwable lockedBy;

    public DimensionLock() {
        assert (lockedBy = new Throwable("Locked by:")) != null;  //NOI18N
    }

    /** Release this lock.
    * In typical usage this method will be called in a <code>finally</code> clause.
    */
    public void releaseLock() {
        locked = false;
    }


    /** Test whether this lock is still active, or released.
    * @return <code>true</code> if lock is still active
    */
    public boolean isValid() {
        return locked;
    }

    /** Finalize this object. Calls {@link #releaseLock} to release the lock if the program
    * for some reason failed to.
    */
    @Override
    public void finalize() {
        if(isValid()) {
            releaseLock();
        }
    }

}
