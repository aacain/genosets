/*
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.studyset.actions;


import edu.uncc.genosets.studyset.listview.FocusChangeListener;
import edu.uncc.genosets.studyset.listview.ListViewProperties;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author aacain
 */
@ActionID(id = "edu.uncc.genosets.studyset.actions.FocusChangeAction", category = "Nodes")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_SETFOCUS", lazy = true)
@ActionReferences(value = {
    @ActionReference(path = "Actions/Nodes/Properties", position = 1100)})
@NbBundle.Messages("CTL_SETFOCUS=Set as focus")
public final class FocusChangedAction extends AbstractAction implements Presenter.Popup {

    private final ListViewProperties context;
    private final FocusChangeListener tc;

    public FocusChangedAction() {
        super(Bundle.CTL_SETFOCUS());
        this.context = null;
        this.tc = null;
    }

    public FocusChangedAction(FocusChangeListener tc, ListViewProperties context) {
        super(Bundle.CTL_SETFOCUS());
        this.context = context;
        this.tc = tc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.tc.focusChanged(new FocusChangeListener.FocusChangedEvent(context.getProperties()));
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
