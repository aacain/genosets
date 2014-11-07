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
package edu.uncc.genosets.propertieseditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.LARGE_ICON_KEY;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.UndoRedo;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class GlobalUndoManager {

    private static final Mgr manager = new Mgr();

    static {
        manager.setLimit(20);
    }

    private GlobalUndoManager() {
    }

    public static UndoRedo.Manager get() {
        return manager;
    }

    static void fireEditsChanged() {
        manager.fire();
    }

    //Use the actions below if you want your own undo actions over a global
    //undo manager
    public static Action createUndoAction() {
        return new UndoAction();
    }

    public static Action createRedoAction() {
        return new RedoAction();
    }

    static class Mgr extends UndoRedo.Manager {
        //Subclass which can fire changes if an edit stops being valid

        private final ChangeSupport supp = new ChangeSupport(this);

        @Override
        public void addChangeListener(ChangeListener l) {
            super.addChangeListener(l);
            supp.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            super.removeChangeListener(l);
            supp.removeChangeListener(l);
        }

        void fire() {
            supp.fireChange();
        }
    }

    @ActionID(id = "edu.uncc.genosets.propertieseditor.UndoAction", category = "Edit")
    @ActionRegistration(lazy = true, iconInMenu = true, displayName = "#CTL_UndoAction", iconBase = "edu/uncc/genosets/icons/undo.gif")
    @ActionReferences(value = {
        @ActionReference(path = "Actions/UndoManager", position = 10)})
    @NbBundle.Messages({"CTL_UndoAction=Undo", "CTL_UNDO_STANDALONE=Undo"})
    public static final class UndoAction extends AbstractAction implements ChangeListener, ContextAwareAction {

        @SuppressWarnings("LeakingThisInConstructor") //NOI18N
        UndoAction() {
            super(Bundle.CTL_UndoAction()); //NOI18N
            manager.addChangeListener(WeakListeners.change(this, manager));
            putValue(SMALL_ICON, ImageUtilities.loadImage("edu/uncc/genosets/icons/undo.gif"));
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImage("edu/uncc/genosets/icons/undo24.gif"));
            stateChanged(null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manager.undo();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled();
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new UndoAction();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setEnabled(manager.canUndo());
            putValue(NAME, isEnabled() ? manager.getUndoPresentationName()
                    : Bundle.CTL_UNDO_STANDALONE()); //NOI18N
        }
    }

    @ActionID(id = "edu.uncc.genosets.propertieseditor.RedoAction", category = "Edit")
    @ActionRegistration(lazy = true, iconInMenu = true, displayName = "#CTL_RedoAction", iconBase = "edu/uncc/genosets/icons/redo.gif")
    @ActionReferences(value = {
        @ActionReference(path = "Actions/UndoManager", position = 20)})
    @NbBundle.Messages({"CTL_RedoAction=Redo", "REDO_STANDALONE=Redo", "UNDO=Undo Set {0}", "REDO=Redo Set {0}"})
    public static final class RedoAction extends AbstractAction implements ChangeListener, ContextAwareAction {

        @SuppressWarnings("LeakingThisInConstructor")
        RedoAction() {
            super(Bundle.CTL_RedoAction()); //NOI18N
            manager.addChangeListener(WeakListeners.change(this, manager));
            putValue(SMALL_ICON, ImageUtilities.loadImage("edu/uncc/genosets/icons/redo.gif"));
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImage("edu/uncc/genosets/icons/redo24.gif"));
            stateChanged(null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manager.redo();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            setEnabled(manager.canRedo());
            putValue(NAME, isEnabled() ? manager.getRedoPresentationName()
                    : Bundle.REDO_STANDALONE()); //NOI18N
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new RedoAction();
        }
    }

    @ActionID(id = "edu.uncc.genosets.propertieseditor.SaveAction", category = "Edit")
    @ActionRegistration(lazy = true, iconInMenu = true, displayName = "#CTL_SaveAction")
    @NbBundle.Messages({"CTL_SaveAction=Save", "CTL_SAVE_STANDALONE=Save"})
    public static final class SaveAction extends AbstractAction implements ChangeListener, ContextAwareAction {

        @SuppressWarnings("LeakingThisInConstructor")
        SaveAction() {
            super(Bundle.CTL_SaveAction()); //NOI18N
            manager.addChangeListener(WeakListeners.change(this, manager));
            putValue(SMALL_ICON, ImageUtilities.loadImage("edu/uncc/genosets/icons/redo.gif"));
            putValue(LARGE_ICON_KEY, ImageUtilities.loadImage("edu/uncc/genosets/icons/redo24.gif"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            manager.discardAllEdits();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new SaveAction();
        }
    }
}
