/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.wizards;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.QuickSearch;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.loaders.TemplateWizard;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeNotFoundException;
import org.openide.nodes.NodeOp;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

public final class LoadDataVisualPanel extends JPanel implements PropertyChangeListener {

    public static interface Builder extends ActionListener {

        public Children createTemplatesChildren(DataFolder folder, String filterText);

        public String getTemplatesName();

        public void fireChange();
    }
    public static final String TEMPLATES_FOLDER = "templatesFolder";
    public static final String TARGET_TEMPLATE = "targetTemplate";
    private static final String ATTR_INSTANTIATING_DESC = "instantiatingWizardURL";
    private static final Image PLEASE_WAIT_ICON = ImageUtilities.loadImage("edu/uncc/genosets/datamanager/resources/wait.gif");
    private static final RequestProcessor RP = new RequestProcessor(LoadDataVisualPanel.class);
    private Builder firer;
    private String presetTemplateName = null;
    private Node pleaseWait;
    private WizardDescriptor wiz;
    private String filterText;

    /**
     * Creates new form LoadDataPanelVisualPanel1
     */
    @NbBundle.Messages("TXT_SelectFileType=Select File Type")
    public LoadDataVisualPanel(Builder firer) {
        initComponents();
        this.firer = firer;
        postInitComponents();
        setName(Bundle.TXT_SelectFileType());
//        QuickSearch quickSearch = QuickSearch.attach( panelFilter, BorderLayout.CENTER, createQuickSearchCallback(), true );
//        adjustQuickSearch( quickSearch );
    }

    @NbBundle.Messages("LBL_LoadDataVisualPanel_PleaseWait=Please wait...")
    private void postInitComponents() {
        this.description.setEditorKit(new HTMLEditorKit());
        description.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        // please wait node, see issue 52900
        pleaseWait = new AbstractNode(Children.LEAF) {
            @Override
            public Image getIcon(int ignore) {
                return PLEASE_WAIT_ICON;
            }
        };
        pleaseWait.setName(Bundle.LBL_LoadDataVisualPanel_PleaseWait());
        Children ch = new Children.Array();
        ch.add(new Node[]{pleaseWait});
        final Node root = new AbstractNode(ch);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ((ExplorerProviderPanel) categoriesPanel).setRootNode(root);
            }
        });
        ((ExplorerProviderPanel) this.categoriesPanel).addDefaultActionListener(firer);
        description.addHyperlinkListener(new ClickHyperlinks());
    }

    private void adjustQuickSearch(QuickSearch qs) {
        qs.setAlwaysShown(true);
        Component qsComponent = panelFilter.getComponent(0);
        if (qsComponent instanceof JComponent) {
            ((JComponent) qsComponent).setBorder(BorderFactory.createEmptyBorder());
        }
        JTextField textField = getQuickSearchField();
        if (null != textField) {
            textField.setMaximumSize(null);
        }
    }

    private JTextField getQuickSearchField() {
        Component qsComponent = panelFilter.getComponent(0);
        if (qsComponent instanceof JComponent) {
            for (Component c : ((JComponent) qsComponent).getComponents()) {
                if (c instanceof JTextField) {
                    return (JTextField) c;
                }
            }
        }
        return null;
    }

    void setWizardDescriptor(WizardDescriptor wizard) {
        this.wiz = wizard;
    }

    FileObject getSelectedTemplate() {
        Node[] nodes = ((ExplorerProviderPanel) this.categoriesPanel).getSelectedNodes();
        if (nodes != null && nodes.length == 1) {
            DataObject dobj = nodes[0].getLookup().lookup(DataObject.class);
            if (dobj != null) {
                while (dobj instanceof DataShadow) {
                    dobj = ((DataShadow) dobj).getOriginal();
                }
                if (!(dobj instanceof DataFolder)) {
                    return dobj.getPrimaryFile();
                }
            }
        }
        return null;
    }

    public String getSelectedTemplateName() {
        return ((CategoriesPanel) this.categoriesPanel).getSelectionPath();
    }

    void warmUp(FileObject templatesFolder) {
        if (templatesFolder != null) {
            DataFolder df = DataFolder.findFolder(templatesFolder);
            if (df != null) {
                df.getChildren();
            }
        }
    }

    void doFinished(FileObject temlatesFolder, String category, String template) {
        assert temlatesFolder != null;

        this.categoriesPanel.addPropertyChangeListener(this);

        this.setTemplatesFolder(temlatesFolder);
    }

    public void setTemplatesFolder(final FileObject folder) {
        final DataFolder dobj = DataFolder.findFolder(folder);
        ((ExplorerProviderPanel) this.categoriesPanel).setRootNode(new FilterNode(
                dobj.getNodeDelegate(), this.firer.createTemplatesChildren(dobj, filterText)));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        jScrollPane1.setViewportView(description);
        jLabel3.setLabelFor(description);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        jScrollPane1.setViewportView(null);
        jLabel3.setLabelFor(null);
    }

    @NbBundle.Messages("TemplatesPanelGUI_note_samples=<html>Note that samples are instructional and may not include all<br>security mechanisms required for a production environment.</html>")
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() == this.categoriesPanel) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(event.getPropertyName())) {
                Node[] selectedNodes = (Node[]) event.getNewValue();
                if (selectedNodes != null && selectedNodes.length == 1) {
                    DataObject template = selectedNodes[0].getLookup().lookup(DataObject.class);
                    if (template != null) {
                        URL descURL = getDescription(template);
                        if (descURL != null) {
                            try {
                                //this.description.setPage (descURL);
                                // Set page does not work well if there are mutiple calls to that
                                // see issue #49067. This is a hotfix for the bug which causes                                
                                // synchronous loading of the content. It should be improved later 
                                // by doing it in request processor.

                                //this.description.read( descURL.openStream(), descURL );
                                // #52801: handlig changed charset
                                String charset = findEncodingFromURL(descURL.openStream());
                                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Url " + descURL + " has charset " + charset); // NOI18N
                                if (charset != null) {
                                    description.putClientProperty("charset", charset); // NOI18N
                                }
                                this.description.read(descURL.openStream(), descURL);
                            } catch (ChangedCharSetException x) {
                                Document doc = description.getEditorKit().createDefaultDocument();
                                doc.putProperty("IgnoreCharsetDirective", Boolean.valueOf(true)); // NOI18N
                                try {
                                    description.read(descURL.openStream(), doc);
                                } catch (IOException ioe) {
                                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
                                    this.description.setText(null);
                                }
                            } catch (IOException e) {
                                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                                this.description.setText(null);
                            }
                            description.setCaretPosition(0);
                        } else {
                            this.description.setText(null);
                        }
                    }
                } else {
                    // bugfix #46738, Description in New Project dialog doesn't show description of selected categories
                    this.description.setText(null);
                }
                this.firer.fireChange();
            }
        }
    }

    // encoding support; copied from html/HtmlEditorSupport
    private static String findEncodingFromURL(InputStream stream) {
        try {
            byte[] arr = new byte[4096];
            int len = stream.read(arr, 0, arr.length);
            String txt = new String(arr, 0, (len >= 0) ? len : 0, "ISO-8859-1").toUpperCase(Locale.ENGLISH);
            // encoding
            return findEncoding(txt);
        } catch (IOException x) {
            Logger.getLogger(LoadDataVisualPanel.class.getName()).log(Level.INFO, null, x);
        }
        return null;
    }

    /**
     * Tries to guess the mime type from given input stream. Tries to find
     * <em>&lt;meta http-equiv="Content-Type" content="text/html;
     * charset=iso-8859-1"&gt;</em>
     *
     * @param txt the string to search in (should be in upper case)
     * @return the encoding or null if no has been found
     */
    private static String findEncoding(String txt) {
        int headLen = txt.indexOf("</HEAD>"); // NOI18N
        if (headLen == -1) {
            headLen = txt.length();
        }

        int content = txt.indexOf("CONTENT-TYPE"); // NOI18N
        if (content == -1 || content > headLen) {
            return null;
        }

        int charset = txt.indexOf("CHARSET=", content); // NOI18N
        if (charset == -1) {
            return null;
        }

        int charend = txt.indexOf('"', charset);
        int charend2 = txt.indexOf('\'', charset);
        if (charend == -1 && charend2 == -1) {
            return null;
        }

        if (charend2 != -1) {
            if (charend == -1 || charend > charend2) {
                charend = charend2;
            }
        }

        return txt.substring(charset + "CHARSET=".length(), charend); // NOI18N
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        categoriesPanel = new CategoriesPanel ();
        jLabel3 = new javax.swing.JLabel();
        panelFilter = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        description = new javax.swing.JEditorPane();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(LoadDataVisualPanel.class, "LoadDataVisualPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(LoadDataVisualPanel.class, "LoadDataVisualPanel.jLabel3.text")); // NOI18N

        panelFilter.setLayout(new java.awt.BorderLayout());

        description.setEditable(false);
        description.setPreferredSize(new java.awt.Dimension(100, 66));
        jScrollPane1.setViewportView(description);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 570, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                        .addComponent(categoriesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(panelFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(12, 12, 12)
                    .addComponent(jLabel1)
                    .addGap(2, 2, 2)
                    .addComponent(categoriesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                    .addGap(8, 8, 8)
                    .addComponent(jLabel3)
                    .addGap(2, 2, 2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JEditorPane description;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelFilter;
    // End of variables declaration//GEN-END:variables

    private static final class ClickHyperlinks implements HyperlinkListener {

        public @Override
        void hyperlinkUpdate(HyperlinkEvent evt) {
            if (HyperlinkEvent.EventType.ACTIVATED == evt.getEventType() && evt.getURL() != null) {
                HtmlBrowser.URLDisplayer.getDefault().showURL(evt.getURL());
            }
        }
    }

    private URL getDescription(DataObject dobj) {
        //XXX: Some templates are using templateWizardURL others instantiatingWizardURL. What is correct?
        FileObject fo = dobj.getPrimaryFile();
        URL desc = (URL) fo.getAttribute(ATTR_INSTANTIATING_DESC);
        if (desc != null) {
            return desc;
        }
        desc = TemplateWizard.getDescription(dobj);
        return desc;
    }

    private static abstract class ExplorerProviderPanel extends JPanel implements ExplorerManager.Provider, PropertyChangeListener, VetoableChangeListener {

        private ExplorerManager manager;

        protected ExplorerProviderPanel() {
            this.manager = new ExplorerManager();
            this.manager.addPropertyChangeListener(this);
            this.manager.addVetoableChangeListener(this);
            this.initGUI();
        }

        public void setRootNode(Node node) {
            this.manager.setRootContext(node);
        }

        public Node getRootNode() {
            return this.manager.getRootContext();
        }

        public Node[] getSelectedNodes() {
            return this.manager.getSelectedNodes();
        }

        public void setSelectedNodes(Node[] nodes) throws PropertyVetoException {
            this.manager.setSelectedNodes(nodes);
        }

        public void setSelectedNode(String path) {
            if (path == null) {
                return;
            }
            StringTokenizer tk = new StringTokenizer(path, "/");    //NOI18N
            final String[] names = new String[tk.countTokens()];
            for (int i = 0; tk.hasMoreTokens(); i++) {
                names[i] = tk.nextToken();
            }
            RP.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Node node = NodeOp.findPath(manager.getRootContext(), names);
                        if (node != null) {
                            setSelectedNodes(new Node[]{node});
                        }
                    } catch (PropertyVetoException e) {
                        //Skip it, not important
                    } catch (NodeNotFoundException x) {
                        // OK, never mind
                    }
                }
            });
        }

        public String getSelectionPath() {
            Node[] selectedNodes = this.manager.getSelectedNodes();
            if (selectedNodes == null || selectedNodes.length != 1) {
                return null;
            }
            Node rootNode = this.manager.getRootContext();
            String[] path = NodeOp.createPath(selectedNodes[0], rootNode);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < path.length; i++) {
                builder.append('/');        //NOI18N
                builder.append(path[i]);
            }
            return builder.substring(1);
        }

        @Override
        public ExplorerManager getExplorerManager() {
            return this.manager;
        }

        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            // workaround of issue 43502, update of Help button set back the focus
            // to component which is active when this change starts
            //XXX: this workaround causes problems in the selection of templates
            // and should be removed, this workaround can be workarounded in the
            // setSelectedTemplateByName when template name is null
            // select the first template only if no template is already selected,
            // but nicer solution is to remove this workaround at all.
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    firePropertyChange(event.getPropertyName(),
                            event.getOldValue(), event.getNewValue());
                }
            });
        }

        @Override
        public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] newValue = (Node[]) evt.getNewValue();
                if (newValue == null || (newValue.length != 1 && newValue.length != 0)) {
                    throw new PropertyVetoException("Invalid length", evt);      //NOI18N
                }
            }
        }

        @Override
        public void requestFocus() {
            this.createComponent().requestFocus();
        }

        protected abstract JComponent createComponent();

        private void initGUI() {
            this.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = GridBagConstraints.RELATIVE;
            c.gridy = GridBagConstraints.RELATIVE;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.gridheight = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weightx = 1.0;
            c.weighty = 1.0;
            JComponent component = this.createComponent();
            ((GridBagLayout) this.getLayout()).setConstraints(component, c);
            this.add(component);
        }

        void addDefaultActionListener(ActionListener al) {
            //do nothing by default
        }

        public void selectFirst() {
            RP.post(new Runnable() {
                @Override
                public void run() {
                    final Children ch = getRootNode().getChildren();
                    // XXX what is the best way to wait for >0 node to appear without necessarily waiting for them all?
                    if (ch.getNodesCount(true) > 0) { // blocks
                        EventQueue.invokeLater(new Runnable() { // #210326
                            @Override
                            public void run() {
                                if (getSelectedNodes().length == 0) { // last minute
                                    try {
                                        getExplorerManager().setSelectedNodes(new Node[]{ch.getNodeAt(0)});
                                    } catch (PropertyVetoException x) {
                                        Logger.getLogger(LoadDataVisualPanel.class.getName()).log(Level.INFO, "race condition while selecting first of " + getRootNode(), x);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private static class CategoriesBeanTreeView extends BeanTreeView {

        CategoriesBeanTreeView() {
            this.tree.setEditable(false);
            //#219709 - workaround for JDK bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=8003400
            tree.setLargeModel(false);
        }
    }

    private static final class CategoriesPanel extends ExplorerProviderPanel {

        private CategoriesBeanTreeView btv;

        @NbBundle.Messages({
            "ACSN_CategoriesPanel=File Type",
            "ACSD_CategoriesPanel=List of file types which can be choosen"
        })
        @Override
        protected synchronized JComponent createComponent() {
            if (this.btv == null) {
                this.btv = new CategoriesBeanTreeView();
                this.btv.setRootVisible(false);
                this.btv.setPopupAllowed(false);
                this.btv.setFocusable(false);
                this.btv.setDefaultActionAllowed(false);
                this.btv.getAccessibleContext().setAccessibleName(Bundle.ACSN_CategoriesPanel());
                this.btv.getAccessibleContext().setAccessibleDescription(Bundle.ACSD_CategoriesPanel());
                Border b = (Border) UIManager.get("Nb.ScrollPane.border"); // NOI18N
                if (b != null) {
                    this.btv.setBorder(b);
                }
            }
            return this.btv;
        }
    }
}
