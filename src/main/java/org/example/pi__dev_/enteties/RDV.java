package org.example.pi__dev_.enteties;

import java.sql.Time;
import java.util.Date;


public class RDV {
    private int id;
    private Time heure;
    private Date date; // Correction : Utilisation de java.sql.Date
    private String priorite;
    public RDV(Time heure, Date date, String priorite) {
        this.heure = heure;
        this.date = date;
        this.priorite = priorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public java.sql.Date getDate() { // Retourne java.sql.Date
        return (java.sql.Date) date;
    }

    public void setDate(Date date) { // Correction du setter
        this.date = date;
    }

    public String getPriorite() {
        return priorite;
    }
    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }
    @Override
    public String toString() {
        return "RDV{" + "heure=" + heure + ", date=" + date + " priorite" + priorite + '}';
    }
}