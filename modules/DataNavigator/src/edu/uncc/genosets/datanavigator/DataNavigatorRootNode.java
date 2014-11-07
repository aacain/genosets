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
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class DataNavigatorRootNode extends AbstractNode implements PropertyChangeListener {
    
    public DataNavigatorRootNode() {
        super(Children.create(new OrganismChildren(), true));
        this.setName("organisms");
        this.setDisplayName("organisms");
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(this, DataManager.getDefault()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DataManager.PROP_DB_CHANGED)) {
            SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    setChildren(Children.create(new OrganismChildren(), true));
                }
                
            });
            
        }
    }
}
