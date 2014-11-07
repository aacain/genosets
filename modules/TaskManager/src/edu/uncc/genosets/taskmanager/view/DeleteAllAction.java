/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.taskmanager.view;


import edu.uncc.genosets.taskmanager.TaskLog.Message;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(
category = "Actions",
id = "edu.uncc.genosets.taskmanager.view.DeleteAllAction")
@ActionRegistration(
displayName = "#CTL_DeleteAllAction")
@ActionReferences({})
@NbBundle.Messages("CTL_DeleteAllAction=Delete All")
public final class DeleteAllAction implements ActionListener {

    private final List<Message> context;

    public DeleteAllAction(List<Message> context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        for (Message message : context) {
            // TODO use message
        }
    }
}
