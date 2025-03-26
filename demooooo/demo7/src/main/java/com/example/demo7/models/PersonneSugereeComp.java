package com.example.demo7.models;

public class PersonneSugereeComp {
    private Personnel personnel;
    private String nomComp;

    public PersonneSugereeComp(Personnel personnel, String nomComp) {
        this.personnel = personnel;
        this.nomComp = nomComp;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    @Override
    public String toString() {
        return personnel + " " + nomComp;
    }
}