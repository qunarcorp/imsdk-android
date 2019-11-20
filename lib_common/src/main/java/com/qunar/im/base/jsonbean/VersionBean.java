package com.qunar.im.base.jsonbean;

public class VersionBean {

    /**
     * version : 9
     */

    private int version;
    private String resource;
    private boolean force;
    private boolean forcequickreply;
    private boolean forceOldSearch;

    public boolean isForceOldSearch() {
        return forceOldSearch;
    }

    public void setForceOldSearch(boolean forceOldSearch) {
        this.forceOldSearch = forceOldSearch;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isForcequickreply() {
        return forcequickreply;
    }

    public void setForcequickreply(boolean forcequickreply) {
        this.forcequickreply = forcequickreply;
    }
}
