package org.unidue.ub.libintel.elisaconnector.model;

public class ElisaData implements Comparable<ElisaData> {

    private String description;

    private String elisaUserId;

    private String elisaName;

    private int priority;

    public ElisaData() {
        this.priority = 99;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElisaUserId() {
        return elisaUserId;
    }

    public void setElisaUserId(String elisaUserId) {
        this.elisaUserId = elisaUserId;
    }

    public String getElisaName() {
        return elisaName;
    }

    public void setElisaName(String elisaName) {
        this.elisaName = elisaName;
    }

    @Override
    public int compareTo(ElisaData other){
        //returns -1 if "this" object is less than "that" object
        //returns 0 if they are equal
        //returns 1 if "this" object is greater than "that" object
        return this.priority - other.priority;
    }
}
