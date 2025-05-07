package tn.esprit.pidev.gestion_rdv.enteties;

public class Patient {
    private int id;
    private String dossier_medical;
    private String firstName;
    private String lastName;
    private String email;

    // Constructors
    public Patient() {}

    public Patient(int id, String dossier_medical, String firstName, String lastName) {
        this.id = id;
        this.dossier_medical = dossier_medical;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Patient(int id, String dossier_medical, String firstName, String lastName, String email) {
        this.id = id;
        this.dossier_medical = dossier_medical;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Patient(int id) {
        this.id = id;
    }

    public Patient(int patientId, String dossierMedical) {
        this.id = patientId;
        this.dossier_medical = dossierMedical;

    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDossier_medical() { return dossier_medical; }
    public void setDossier_medical(String dossier_medical) {
        this.dossier_medical = dossier_medical;
    }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }
}
