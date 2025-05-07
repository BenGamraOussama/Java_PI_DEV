package tn.esprit.pidev.gestion_rdv.enteties;

public class Psychiatre {
private int id;
private String firstName;
private String lastName;
private String specialite;
public Psychiatre(int id, String firstName, String lastName, String specialite) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.specialite = specialite;


}

    public Psychiatre() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
