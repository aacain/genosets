/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager.api;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class ProgramParameterNode extends AbstractNode implements PropertyChangeListener {

    public ProgramParameterNode(ProgramParameter param) throws IntrospectionException{
        //super(param);
        super(Children.LEAF, Lookups.singleton(param));
        setName(param.getParentStep().getName() + param.getName());
        setDisplayName(param.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    protected Sheet createSheet() {
        final ProgramParameter param = getLookup().lookup(ProgramParameter.class);

        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("parameters");
        sheet.put(setProps);

        //unmodifiable properties
        Property<String> nameProp;
        Property<String> defaultProp;
        Property<String> descriptionProp;
        Property<String> valueProp;
        Property<String> isOptionalProp;
        Property<String> isUserRequiredProp;

        nameProp = new PropertySupport.ReadWrite<String>(ProgramParameter.PROP_NAME, String.class, "Description", "Description of parameter") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != param) ? param.getName() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

            }
        };

        PropertyEditorSupport sp = new PropertyEditorSupport(){

            @Override
            public String getAsText() {
                return super.getAsText();
            }

            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                super.setAsText(text);
            }

        };

        defaultProp = new PropertySupport.ReadOnly<String>(ProgramParameter.PROP_DEFAULTVALUE, String.class, "Default Value", "Default value of parameter") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != param) ? param.getDefaultParameterValue() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            }
        };
        descriptionProp = new PropertySupport.ReadOnly<String>(ProgramParameter.PROP_DESCRIPTION, String.class, "Description", "Description of parameter") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != param) ? param.getParameterDescription() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (null != param && (val instanceof String)) {
                    
                }
            }
        };
        
        valueProp = new PropertySupport.ReadWrite(ProgramParameter.PROP_VALUE, String.class, "Value", "Value") {

            private PropertyEditor editor;


            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                
//                if(editor == null){
//                    editor = getPropertyEditor();
//                }
//                String s = editor.getAsText();
//                if(s != null){
//                    return s;
//                }

                return (null != param) ? (param.getParameterValue() != null ? param.getParameterValue() : "") : "";
            }



            @Override
            public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (null != param && (val instanceof String)) {
                    param.setParameterValue((String) val);
                }

            }

        };
        valueProp.setName(ProgramParameter.PROP_VALUE);
        setProps.put(valueProp);

        
        //suppress stupid dots next to value
        nameProp.setValue("suppressCustomEditor", Boolean.TRUE);
        defaultProp.setValue("suppressCustomEditor", Boolean.TRUE);
        descriptionProp.setValue("suppressCustomEditor", Boolean.TRUE);

        //add properties to set
        setProps.put(nameProp);

        setProps.put(defaultProp);
        setProps.put(descriptionProp);

        return sheet;
    }

    public static class ValueEditor extends PropertyEditorSupport
        implements ExPropertyEditor, InplaceEditor.Factory{

        @Override
        public void attachEnv(PropertyEnv env) {
            env.registerInplaceEditorFactory(this);
        }

        @Override
        public InplaceEditor getInplaceEditor() {
            return new ValueInplaceEditor();
        }
        static class ValueInplaceEditor implements InplaceEditor{

            private PropertyEditor editor;
            private PropertyModel model;
            private JLabel label;

            public ValueInplaceEditor(){
                this.label = new JLabel();

            }

            @Override
            public void connect(PropertyEditor pe, PropertyEnv env) {
                this.editor = pe;
                this.reset();
                
            }

            @Override
            public JComponent getComponent() {
                return this.label;
            }

            @Override
            public void clear() {
                this.editor = null;
                this.model = null;
            }

            @Override
            public Object getValue() {
                return label.getText();
            }

            @Override
            public void setValue(Object o) {
                String v = (String)o;
                label.setText(v);
            }

            @Override
            public boolean supportsTextEntry() {
                return true;
            }

            @Override
            public void reset() {
                String v = (String)this.editor.getValue();
                this.setValue(v);
            }

            @Override
            public void addActionListener(ActionListener al) {

            }

            @Override
            public void removeActionListener(ActionListener al) {

            }

            @Override
            public KeyStroke[] getKeyStrokes() {
                return new KeyStroke[0];
            }

            @Override
            public PropertyEditor getPropertyEditor() {
                return this.editor;
            }

            @Override
            public PropertyModel getPropertyModel() {
                return this.model;
            }

            @Override
            public void setPropertyModel(PropertyModel pm) {
                this.model = pm;
            }

            @Override
            public boolean isKnownComponent(Component c) {
                return c == this.label || this.label.isAncestorOf(c);
            }

        }
    }

}
