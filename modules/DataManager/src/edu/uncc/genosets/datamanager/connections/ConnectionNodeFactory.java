/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class ConnectionNodeFactory extends ChildFactory<Connection> implements PropertyChangeListener {

    private final ConnectionManager connMgr;

    public ConnectionNodeFactory(ConnectionManager connMgr) {
        this.connMgr = connMgr;
        this.connMgr.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, this, connMgr));
    }

    @Override
    protected boolean createKeys(List<Connection> toPopulate) {
        toPopulate.addAll(connMgr.getSavedConnections());
        return true;
    }

    @Override
    protected Node createNodeForKey(Connection key) {
        return new ConnectionNode(key);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        refresh(true);
    }

    public static class ConnectionNode extends AbstractNode implements PropertyChangeListener {

        public ConnectionNode(Connection connection) {
            super(Children.LEAF, Lookups.singleton(connection));
            if (connection.isIsDefault()) {
                this.setIconBaseWithExtension("edu/uncc/genosets/datamanager/connections/default_16.png");
            } else {
                this.setIconBaseWithExtension("edu/uncc/genosets/datamanager/connections/default_one_16.png");
            }
            this.setName(connection.getConnectionId());
            this.setDisplayName(connection.getConnectionName());
            connection.addPropertyChangeListener(this);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Connection connection = Lookup.getDefault().lookup(Connection.class);
            if (connection != null) {
                if (connection.isIsDefault() == true) {
                    this.setIconBaseWithExtension("edu/uncc/genosets/datamanager/connections/default_16.png");
                } else {
                    this.setIconBaseWithExtension("edu/uncc/genosets/datamanager/connections/default_one_16.png");
                }
                this.setDisplayName(connection.getConnectionName());
            }
        }

        @Override
        public boolean canDestroy() {
            return Boolean.TRUE;
        }

        @Override
        public void destroy() throws IOException {
            super.destroy();
        }
    }
}
