/*
 */
package edu.uncc.genosets.datamanager.toolbar;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author lucy
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class DatabaseStatusLine implements StatusLineElementProvider, PropertyChangeListener{

    private JLabel databaseLabel = new JLabel();
    private JPanel panel = new JPanel(new BorderLayout());
    

    public DatabaseStatusLine() {
        DataManager mgr = DataManager.getDefault();
        String databaseName = mgr.getDatabaseName();
        databaseLabel.setText("Database: " + (databaseName == null ? "Not connected" : databaseName));
        if(mgr.isDatabaseSet()){
            databaseLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/datamanager/resources/database.gif")));
        }else{
            databaseLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/datamanager/resources/alert_16.png")));
        }
        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(databaseLabel, BorderLayout.CENTER);
        mgr.addPropertyChangeListener(WeakListeners.propertyChange(this, mgr));
    }
    
    

    @Override
    public Component getStatusLineElement() {
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())){
            databaseLabel.setText(DataManager.getDefault().getDatabaseName());
            if(!DataManager.getDefault().isDatabaseSet()){
                databaseLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/datamanager/resources/alert_16.png")));
            }else{
                databaseLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/datamanager/resources/database.gif")));
            }
        }
    }
}
