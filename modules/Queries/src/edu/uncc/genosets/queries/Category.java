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

import java.util.List;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class Category {
    private Group group;
    private String categoryName;
    private Integer categoryId;
    private boolean nullSet;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String category) {
        this.categoryName = category;
    }    

    public boolean isNullSet() {
        return nullSet;
    }

    public void setNullSet(boolean nullSet) {
        this.nullSet = nullSet;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    
    
    public static class CategoryIds{
        Category category;
        Set<Integer> ids;

        public CategoryIds(Category category, Set<Integer> ids) {
            this.category = category;
            this.ids = ids;
        }

        public Category getCategory() {
            return category;
        }

        public Set<Integer> getIds() {
            return ids;
        }     
    }
}
