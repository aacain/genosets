/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.autoupdate;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.netbeans.api.autoupdate.*;
import org.netbeans.api.autoupdate.InstallSupport.Installer;
import org.netbeans.api.autoupdate.InstallSupport.Validator;
import org.netbeans.api.autoupdate.OperationContainer.OperationInfo;
import org.netbeans.api.autoupdate.OperationSupport.Restarter;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;

/**
 * Class that installs updates when starting the application. Register in layer
 * file as a warm-up task so that the updates will install when starting the
 * application.
 *
 * @author aacain - taken from Heiko Bock - The Definitive Guide to Netbeans
 * Platform 7
 */
public class AutoInstaller implements Runnable {

    private static final Logger LOG = Logger.getLogger(AutoInstaller.class.getName());

    @Override
    public void run() {
        RequestProcessor.getDefault().post(new AutoInstallerImpl(), 1000);
    }

    private static final class AutoInstallerImpl implements Runnable {

        private static final String UC_NAME = "edu_uncc_genosets_datamanager_update_center";
        private List<UpdateElement> install = new ArrayList<UpdateElement>();
        private List<UpdateElement> update = new ArrayList<UpdateElement>();
        private boolean isRestartRequested = false;

        @Override
        public void run() {
            searchNewAndUpdatedModulesInDedicatedUC();
            OperationContainer<InstallSupport> installContainer =
                    addToContainer(OperationContainer.createForInstall(), install);
            installModules(installContainer);
            OperationContainer<InstallSupport> updateContainer =
                    addToContainer(OperationContainer.createForUpdate(), update);
            installModules(updateContainer);
        }

        /**
         * Search all registered update centers. Search for both the latest and
         * the updated modules.
         */
        public void searchNewAndUpdatedModules() {
            //determine all providers and refresh
            for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
                try {
                    provider.refresh(null, true);
                } catch (IOException ex) {
                    LOG.severe(ex.getMessage());
                }
            }
            //get the update units
            for (UpdateUnit unit : UpdateManager.getDefault().getUpdateUnits()) {
                if (!unit.getAvailableUpdates().isEmpty()) {
                    //filter all modules that are already installed and are not updated
                    if (unit.getInstalled() == null) {
                        install.add(unit.getAvailableUpdates().get(0));
                    } else {
                        update.add(unit.getAvailableUpdates().get(0));
                    }
                }
            }
        }

        public void searchNewAndUpdatedModulesInDedicatedUC() {
            for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
                try {
                    if (provider.getName().equals(UC_NAME)) {
                        provider.refresh(null, true);
                        for (UpdateUnit u : provider.getUpdateUnits()) {
                            if (!u.getAvailableUpdates().isEmpty()) {
                                if (u.getInstalled() == null) {
                                    install.add(u.getAvailableUpdates().get(0));
                                } else {
                                    update.add(u.getAvailableUpdates().get(0));
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    LOG.severe(ex.getMessage());
                }
            }
        }

        /**
         * Operations on modules are executed via an Operation Container. This
         * is the helper method that creates the container that is responsible
         * for handling update or install modules.
         *
         * @param container - container to add the modules to
         * @param modules - the modules to add to the container
         * @return the container with the modules added
         */
        public OperationContainer<InstallSupport> addToContainer(OperationContainer<InstallSupport> container, List<UpdateElement> modules) {
            for (UpdateElement e : modules) {
                if (container.canBeAdded(e.getUpdateUnit(), e)) {
                    OperationInfo<InstallSupport> operationInfo = container.add(e);
                    if (operationInfo != null) {
                        container.add(operationInfo.getRequiredElements());
                    }
                }
            }
            return container;
        }

        /**
         *
         * @param container
         */
        public void installModules(OperationContainer<InstallSupport> container) {
            try {
                InstallSupport support = container.getSupport();
                if (support != null) {
                    Validator vali = support.doDownload(null, true);
                    Installer inst = support.doValidate(vali, null);
                    Restarter restarter = support.doInstall(inst, null);
                    if (restarter != null) {
                        support.doRestartLater(restarter);
                        if (!isRestartRequested) {
                            NotificationDisplayer.getDefault().notify(
                                    "The application has been updated",
                                    ImageUtilities.loadImageIcon("edu/uncc/genosets/datamanager/resources/updateAction.gif", false),
                                    "Click here to restart",
                                    new RestartAction(support, restarter));
                            isRestartRequested = true;
                        }
                    }
                }
            } catch (OperationException ex) {
                LOG.severe(ex.getMessage());
            }
        }
    }

    /**
     * Class that handles restarts.
     */
    private static final class RestartAction implements ActionListener {

        private InstallSupport support;
        private OperationSupport.Restarter restarter;

        public RestartAction(
                InstallSupport support,
                OperationSupport.Restarter restarter) {
            this.support = support;
            this.restarter = restarter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                support.doRestart(restarter, null);
            } catch (OperationException ex) {
                LOG.severe(ex.getMessage());
            }
        }
    }
}
