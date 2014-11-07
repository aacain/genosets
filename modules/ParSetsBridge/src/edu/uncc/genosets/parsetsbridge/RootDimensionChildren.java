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

package edu.uncc.genosets.parsetsbridge;

import edu.uncc.genosets.parsetsbridge.DimensionObjectChildren.DimensionPathNode;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsController;
import edu.uncc.genosets.queries.DimensionObject;
import edu.uncc.genosets.queries.DimensionUtil;
import java.util.ArrayList;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class RootDimensionChildren extends Children.Keys<DimensionObject> {

    private final GenoSetsController controller;

    RootDimensionChildren(GenoSetsController controller) {
        this.controller = controller;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        this.setKeys(DimensionUtil.getRootDimension().getChildren(false));
    }

   

    @Override
    protected Node[] createNodes(DimensionObject key) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        Node node = new DimensionPathNode(key, controller);
        nodeList.add(node);
        //addDimGroupChildren(key.path, dimGroup);

        return nodeList.toArray(new Node[nodeList.size()]);
    }
    
    

}
