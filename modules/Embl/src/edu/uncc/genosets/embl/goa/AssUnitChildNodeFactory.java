/*
 * 
 * 
 */
package edu.uncc.genosets.embl.goa;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class AssUnitChildNodeFactory extends ChildFactory<AssembledUnit> {

    private Collection<AssembledUnit> assUnits;

    public AssUnitChildNodeFactory(Collection<AssembledUnit> assUnits) {
        this.assUnits = assUnits;
    }

    @Override
    protected boolean createKeys(List<AssembledUnit> toPopulate) {
        for (AssembledUnit assUnit : assUnits) {
            toPopulate.add(assUnit);
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(AssembledUnit key) {
        return new AssUnitNode[]{new AssUnitNode(key)};
    }

    public static class AssUnitNode extends AbstractNode {

        private AssembledUnit assUnit;
        private Date date;

        public AssUnitNode(AssembledUnit assUnit) {
            super(Children.LEAF, Lookups.singleton(assUnit));
            this.assUnit = assUnit;
            this.setName(assUnit.getAssembledUnitId().toString());
            this.setDisplayName(assUnit.getAssembledUnitName());
            date = (Date) assUnit.getValueOfCustomField(WizardQueryBuilder.PROP_DATE);
        }

        public AssembledUnit getAssUnit(){
            return this.assUnit;
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            Property<String> idProperty = new MyProperty("id", String.class, "ID", "unique id") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != assUnit) ? assUnit.getAssembledUnitId().toString() : "";
                }
            };
            

            Property<String> nameProperty = new MyProperty("name", String.class, "Name", "assembled unit name") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != assUnit) ? assUnit.getAssembledUnitName() : "";
                }
            };


            Property<String> orgProperty = new MyProperty("Organism", String.class, "Organism", "organism") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != assUnit) ? assUnit.getOrganism().getStrain() : "";
                }
            };

            Property<String> dateProperty = new MyProperty("Last Run", String.class, "Last Run", "last processed") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != date) ? new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(date) : "";
                }

            };



            set.put(idProperty);
            set.put(nameProperty);
            set.put(dateProperty);
            set.put(orgProperty);

            idProperty.setValue("supressCustomEditor", Boolean.TRUE);
            nameProperty.setValue("supressCustomEditor", Boolean.TRUE);
            dateProperty.setValue("supressCustomEditor", Boolean.TRUE);
            orgProperty.setValue("supressCustomEditor", Boolean.TRUE);

            sheet.put(set);
            return sheet;
        }
    }

    private static abstract class MyProperty extends PropertySupport.ReadOnly<String>{

        public MyProperty(String name, Class<String> type, String displayName, String shortDescription) {
            super(name, type, displayName, shortDescription);
            setValue("suppressCustomEditor",Boolean.TRUE);
        }
    }
}
