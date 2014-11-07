/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.api;

/**
 *
 * @author aacain
 */
public class AbstractDimension {
    private String name;
    private String handle;
    private String dataType;

    public final static String DATA_TYPE_CATEGORICAL = "DATA_TYPE_CATEGORICAL";
    public final static String DATA_TYPE_NUMERICAL = "DATA_TYPE_NUMERICAL";
    public final static String DATA_TYPE_TEXTUAL = "DATA_TYPE_TEXTUAL";

    public AbstractDimension(String name, String handle, String dataType) {
        this.name = name;
        this.handle = handle;
        this.dataType = dataType;
    }

    
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
