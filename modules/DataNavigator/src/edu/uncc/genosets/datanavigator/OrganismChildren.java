/*
 * 
 * 
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.SwingUtilities;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class OrganismChildren extends ChildFactory<Organism> implements PropertyChangeListener {

    public OrganismChildren() {
    }

    @Override
    protected boolean createKeys(List<Organism> toPopulate) {
        for (Organism organism : DataManager.getDefault().getOrganisms()) {
            toPopulate.add(organism);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Organism key) {
        OrganismNode node = new OrganismNode(key);
        return node;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DataManager.PROP_ORGANISM_ADD)) {
            this.refresh(false);
        }
    }
}
