/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.treemap;

import prefuse.data.Node;
import prefuse.data.Schema;

/**
 *
 * @author aacain
 */
public class GOEdgeSchema extends Schema {

    public static final String SOURCE = "SOURCE";
    public static final String TARGET = "TARGET";
    public static final String RELATIONSHIP_TYPE = "RELATIONSHIP_TYPE";

    public GOEdgeSchema() {
        this.addColumn(SOURCE, int.class);
        this.addColumn(TARGET, int.class);
        this.addColumn(RELATIONSHIP_TYPE, String.class);
    }
}
