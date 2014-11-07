/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class GroupingWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>{

    private final ChangeSupport cs = new ChangeSupport(this);
    final static String PROP_FILE_PER_ORGANISM = "PROP_FILE_PER_ORGANISM";
    final static String PROP_FILE_PER_METHOD = "PROP_FILE_PER_METHOD";
    final static String PROP_PREFIX_FOUR_LETTER_ORGANISM = "PROP_PREFIX_FOUR_LETTER_ORGANISM";
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GroupingVisualPanel2 component;
    private WizardDescriptor wiz;
    private Pattern prefixPattern;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GroupingVisualPanel2 getComponent() {
        if (component == null) {
            component = new GroupingVisualPanel2();
            prefixPattern = Pattern.compile("[0-9]{0,4}");
            ((AbstractDocument) component.getPrefixStartField().getDocument()).setDocumentFilter(new DocumentFilter(){

                @Override
                public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    Matcher matcher = prefixPattern.matcher(text);
                    if(!matcher.matches()){
                        return;
                    }
                    if(fb.getDocument().getLength() + text.length() - length > 4){
                        return;
                    }
                    super.replace(fb, offset, length, text, attrs);
                }
                
            });            
        }
        return component;
    }
    
    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        OrthoMclFormat format = (OrthoMclFormat) wiz.getProperty(OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT);
        format.setFilePerOrganism(true);
        format.setPrefixOrganism(getComponent().getPrefixCheck().isSelected());
        format.setFilePerMethod(getComponent().getFilePerMethodRadio().isSelected());
        int start = 0;
        if(!getComponent().getPrefixStartField().getText().isEmpty()){
            start = Integer.parseInt(getComponent().getPrefixStartField().getText());
        }
        format.setStartingPrefix(start);
    }
}
