/*
 * Copyright (C) 2014 Aurora Cain
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

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public class EntityPersister implements Persister{
    private CustomizableEntity entity;
    private String entityName;

    public EntityPersister(CustomizableEntity entity, String entityName) {
        this.entity = entity;
        this.entityName = entityName;
    }
    

    @Override
    public void persist(StatelessSession session) {
        if(entity.getId() == null){
            session.insert(entityName, entity);
        }
    }
}
