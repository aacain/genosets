/*
 */
package edu.uncc.genosets.datamanager.toolbar;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 *
 * @author lucy
 */

//@ActionID(
//category = "View",
//id = "edu.uncc.genosets.datamanager.toolbar.DatabaseToolbarAction")
//@ActionRegistration(displayName = "#CTL_DatabaseToolbarAction")
//@ActionReferences({
//@ActionReference(path="Toolbars/Database")
//})
public class DatabaseToolbarAction extends AbstractAction implements Presenter.Toolbar {

    private JLabel databaseLabel = new JLabel();

    public DatabaseToolbarAction() {
        DataManager mgr = DataManager.getDefault();
        String databaseName = mgr.getDatabaseName();
        databaseLabel.setText("Database: " + (databaseName == null ? "Not connected" : databaseName));
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public Component getToolbarPresenter() {
        return databaseLabel;
    }
    
}
