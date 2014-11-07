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

import edu.uncc.genosets.queries.core.EntityQuery;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class Group {

    private Integer groupId;
    private Set<Integer> queryId;
    private String groupDescription;
    private String type;
    private EntityQuery entityQuery;
    private String path;
    private Object descriptionObject;
    public static final String TYPE_STUDYSET = "TYPE_STUDYSET";
    public static final String TYPE_ANNOTATION_METHOD = "TYPE_ANNOTATION_METHOD";
    public static final String TYPE_NATIVE = "TYPE_NATIVE";
    private static int nextId = 0;

    public Group(Set<Integer> queryId, String groupDescription, String type, EntityQuery entityQuery, String path, Object descriptionObject) {
        this.groupId = nextId++;
        this.queryId = queryId;
        this.groupDescription = groupDescription;
        this.type = type;
        this.entityQuery = entityQuery;
        this.path = path;
        this.descriptionObject = descriptionObject;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EntityQuery getEntityQuery() {
        return entityQuery;
    }

    public void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<Integer> getQueryIds() {
        return queryId;
    }

    public void setQueryIds(Set<Integer> queryId) {
        this.queryId = queryId;
    }

    public Object getDescriptionObject() {
        return descriptionObject;
    }

    public void setDescriptionObject(Object descriptionObject) {
        this.descriptionObject = descriptionObject;
    }

    public static class GenericDescription {

        private final String name;
        private final String shortDescription;

        public GenericDescription(String name, String shortDescription) {
            this.name = name;
            this.shortDescription = shortDescription;
        }

        public String getName() {
            return name;
        }

        public String getShortDescription() {
            return shortDescription;
        }
    }
}
