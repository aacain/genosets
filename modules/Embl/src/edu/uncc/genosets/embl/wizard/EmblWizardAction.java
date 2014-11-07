/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

/**
 *
 * @author aacain
 */
public class EmblWizardAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // To invoke this wizard, copy-paste and run the following code, e.g. from
        // SomeAction.performAction():

        WizardDescriptor.Iterator iterator = new EmblWizardIterator();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator);
        // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
        // {1} will be replaced by WizardDescriptor.Iterator.name()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
        wizardDescriptor.setTitle("Your wizard dialog title here");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            // do something
        }
    }
}
