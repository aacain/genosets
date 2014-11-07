package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.api.DataLoadException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author aacain
 */
public class AssembledUnitMapping {

    HashMap<String, AssembledUnit> mapping;

    public void readMappings(String mappingString) throws DataLoadException {
        String[] lines = mappingString.split("[\n\r]");
        List<String> ids = new ArrayList<String>(lines.length);
        HashMap<String, String> tempMap = new HashMap<String, String>();
        for (String line : lines) {
            if (!line.startsWith("#") && !line.equals("")) {
                String[] ss = line.split("\\t");
                if (ss.length == 2) {
                    tempMap.put(ss[0], ss[1]);
                    ids.add(ss[0].toString());
                } else {
                    throw new DataLoadException("Error reading assembled unit mapping file");
                }
            }
        }
        mapping = new HashMap<String, AssembledUnit>();
        if (!ids.isEmpty()) {
            List<? extends AssembledUnit> query = AssUnitQuery.query(ids);
            for (AssembledUnit assembledUnit : query) {
                String newId = tempMap.get(assembledUnit.getAssembledUnitId().toString());
                mapping.put(newId, assembledUnit);
            }
        }
    }

    public AssembledUnit lookup(String seqId) {
        return this.mapping.get(seqId);
    }

    private static class AssUnitQuery implements QueryCreator {

        static AssembledUnit query(Integer assUnitId) {
            return (AssembledUnit) DataManager.getDefault().get(AssembledUnit.DEFAULT_NAME, assUnitId);
        }

        static List<? extends AssembledUnit> query(List<String> ids) {
            StringBuilder bldr = new StringBuilder("(");
            for (String string : ids) {
                bldr.append(string).append(",");
            }
            bldr.setCharAt(bldr.length() - 1, ')');
            return DataManager.getDefault().createQuery("SELECT ass FROM AssembledUnit as ass where ass.assembledUnitId in " + bldr.toString(), AssembledUnit.class);
        }
    }
}
