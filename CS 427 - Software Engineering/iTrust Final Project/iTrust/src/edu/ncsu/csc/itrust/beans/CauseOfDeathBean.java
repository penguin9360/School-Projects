package edu.ncsu.csc.itrust.beans;

import java.io.Serializable;

public class CauseOfDeathBean implements Serializable {

    private String code;
    private String causeOfDeath;
    private int count;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }
}
