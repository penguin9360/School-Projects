package edu.ncsu.csc.itrust.beans;

public class PatientAllergyBean {

    private long MID = 0;
    private String firstName = "";
    private String lastName = "";
    private String zip = "";
    private String allergy = "";

    public long getMID() {
        return MID;
    }

    public void setMID(long MID) {
        this.MID = MID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }
}
