package com.gigaappz.vipani;

/**
 * Created by DELL on 21-Sep-18.
 */
public class PersonUtils {


    private String id,name,place,company;
    private String personName;
    private String disablereason;
    private String dor,doe,dop,remain;

    public PersonUtils(String personName,String disablereason,String dor,String doe,String dop,String remain,String id,String name,String place,String company) {
        this.personName = personName;
        this.disablereason = disablereason;
        this.dor = dor;
        this.doe = doe;
        this.dop = dop;

        this.remain = remain;
        this.id = id;
        this.name = name;
        this.place = place;
        this.company = company;
    }

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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getDisablereason() {
        return disablereason;
    }

    public void setDisablereason(String disablereason) {
        this.disablereason = disablereason;
    }

    public String getDor() {
        return dor;
    }

    public void setDor(String dor) {
        this.dor = dor;
    }

    public String getDoe() {
        return doe;
    }

    public void setDoe(String doe) {
        this.doe = doe;
    }

    public String getDop() {
        return dop;
    }

    public void setDop(String dop) {
        this.dop = dop;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }
}