/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.embl;

/**
 *
 * @author aacain
 */
public class EmblProject {
    private String accession;
    private String entryVersion;
    private String versionData;
    private String taxId;
    private String description;
    

    public EmblProject(String accession, String entryVersion, String versionData, String taxId, String description) {
        this.accession = accession;
        this.entryVersion = entryVersion;
        this.versionData = versionData;
        this.taxId = taxId;
        this.description = description;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryVersion() {
        return entryVersion;
    }

    public void setEntryVersion(String entryVersion) {
        this.entryVersion = entryVersion;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getVersionData() {
        return versionData;
    }

    public void setVersionData(String versionData) {
        this.versionData = versionData;
    }
}
