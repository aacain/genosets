/*
 * 
 * 
 */
package edu.uncc.genosets.connections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author aacain
 */
@XmlRootElement
public class Connection implements Serializable {
    private String connectionId;
    private String connectionName;
    private String url;
    private String userName;
    private String password;
    private String canRemove;
    private boolean isDefault = false;
    private boolean savePassword = false;
    private String connectionType;
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**
     * static variable *
     */
    public final static String TYPE_WEB_SERVICE = "Web";
    public final static String TYPE_DIRECT_DB = "JDBC";
    public final static String[] ALL_TYPES = new String[]{TYPE_WEB_SERVICE, TYPE_DIRECT_DB};
    public static final String CONNECTION__DETAILS_CHANGED = "CONNECTION_CHANGED";

    public Connection() {
    }

    public Connection(String connectionId, String connectionName, String url, String userName, String password, boolean isDefault, String connectionType, boolean savePassword) {
        this.connectionId = connectionId;
        this.connectionName = connectionName;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.isDefault = isDefault;
        this.connectionType = connectionType;
        this.savePassword = savePassword;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        String old = this.connectionName;
        this.connectionName = connectionName;
        this.pcs.firePropertyChange(CONNECTION__DETAILS_CHANGED, old, this.connectionName);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String old = this.password;
        this.password = password;
        this.pcs.firePropertyChange(CONNECTION__DETAILS_CHANGED, old, this.password);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        String old = this.userName;
        this.userName = userName;
        this.pcs.firePropertyChange(CONNECTION__DETAILS_CHANGED, old, this.userName);
    }

    public String getCanRemove() {
        return canRemove;
    }

    public void setCanRemove(String canRemove) {
        String old = this.canRemove;
        this.canRemove = canRemove;
        this.pcs.firePropertyChange(CONNECTION__DETAILS_CHANGED, old, this.canRemove);
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setIsDefault(boolean isDefault) {
        boolean old = this.isDefault;
        this.isDefault = isDefault;
        this.pcs.firePropertyChange(new PropertyChangeEvent(this, CONNECTION__DETAILS_CHANGED, old, isDefault));
    }

    public boolean isSavePassword() {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Connection other = (Connection) obj;
        if ((this.getConnectionId() == null) ? (other.getConnectionId() != null) : !this.getConnectionId().equals(other.getConnectionId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.getConnectionId() != null ? this.getConnectionId().hashCode() : 0);
        return hash;
    }
}
