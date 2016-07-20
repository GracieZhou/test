
package com.android.settings.datetimecity;
/**
 * Package class of timezone,include the detailed attribute of timezone
 *
 */
public class Timezone {

    private String id;

    private String name;

    private String gmt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

}
