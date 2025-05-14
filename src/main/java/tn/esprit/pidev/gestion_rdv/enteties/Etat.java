package tn.esprit.pidev.gestion_rdv.enteties;

public enum Etat {
    EN_ATTENTE,
    VALIDEE,
    ANNULEE;
    private String displayName = null;

    Etat() {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Méthode pour retrouver un Etat à partir de son displayName
    public static Etat fromDisplayName(String displayName) {
        for (Etat etat : values()) {
            if (etat.displayName.equals(displayName)) {
                return etat;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
