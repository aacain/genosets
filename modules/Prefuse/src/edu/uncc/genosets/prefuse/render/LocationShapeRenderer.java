/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse.render;

import edu.uncc.genosets.prefuse.FeatureSchema;
import java.awt.Shape;
import prefuse.Constants;
import prefuse.render.ShapeRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * @author aacain
 */
public class LocationShapeRenderer extends ShapeRenderer{
    private int m_axis = Constants.X_AXIS;

    /**
     * 
     * @param axis the length should be used
     */
    public LocationShapeRenderer(int axis){
        super();
        m_axis = axis;
    }

    @Override
    protected Shape getRawShape(VisualItem item){
        int stype = item.getShape();
        double x = item.getX();
        if ( Double.isNaN(x) || Double.isInfinite(x) )
            x = 0;
        double y = item.getY();
        if ( Double.isNaN(y) || Double.isInfinite(y) )
            y = 0;

        double height = item.getSize();
        //double width = this.getBaseSize();//m_baseSize;
        double width = item.getSize();
        if (m_axis == Constants.X_AXIS) {
            height = this.getBaseSize();
        } else {
            width = this.getBaseSize();
        }

        //Adjust size if too small
        if(height < 1)
            height = 1;
        if(width < 1)
            width = 1;

        //Get strand TODO: quick fix for strand
        Character strand = (Character)item.get(FeatureSchema.STRAND);
        // Adjust positions based on height and width
        if ( height > 1 ) {
            if(m_axis == Constants.X_AXIS){
                if(strand.equals('+'))
                    y = y-height;

            }else{
                x = x-width/2;
                y = y-height;
            }
        }

        switch ( stype ) {
        case Constants.SHAPE_NONE:
            return null;
        case Constants.SHAPE_RECTANGLE:
            return rectangle(x, y, width, height);
        case Constants.SHAPE_ELLIPSE:
            return ellipse(x, y, width, height);
        case Constants.SHAPE_TRIANGLE_UP:
            return triangle_up((float)x, (float)y, (float)height);
        case Constants.SHAPE_TRIANGLE_DOWN:
            return triangle_down((float)x, (float)y, (float)height);
        case Constants.SHAPE_TRIANGLE_LEFT:
            return triangle_left((float)x, (float)y, (float)height);
        case Constants.SHAPE_TRIANGLE_RIGHT:
            return triangle_right((float)x, (float)y, (float)height);
        case Constants.SHAPE_CROSS:
            return cross((float)x, (float)y, (float)height);
        case Constants.SHAPE_STAR:
            return star((float)x, (float)y, (float)height);
        case Constants.SHAPE_HEXAGON:
            return hexagon((float)x, (float)y, (float)height);
        case Constants.SHAPE_DIAMOND:
            return diamond((float)x, (float)y, (float)height);
        default:
            throw new IllegalStateException("Unknown shape type: "+stype);
        }
    }
}
