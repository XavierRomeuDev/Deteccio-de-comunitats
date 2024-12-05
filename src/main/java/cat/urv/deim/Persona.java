package cat.urv.deim;

public class Persona implements Comparable<Persona> {
    private int id_persona;
    private String nom;
    private String cognom;
    private int edat;
    private int pes;
    private int alsada;

    public Persona(int id_persona, int edat, String nom, String cognom, int alsada, int pes) {
        this.id_persona = id_persona;
        this.nom = nom;
        this.cognom = cognom;
        this.edat = edat;
        this.pes = pes;
        this.alsada = alsada;
    }

    public int getId_persona() {
        return id_persona;
    }

    public String getNom() {
        return nom;
    }

    public String getCognom() {
        return cognom;
    }

    public int getEdat() {
        return edat;
    }

    public int getPes() {
        return pes;
    }

    public int getAlsada() {
        return alsada;
    }

    ///////// NOVETAT!! /////////

    // Falta implementar correctament aquest metode per a fer la comparacio de
    // persones. La comparacio
    // es fara per l'ID de les persones (no pel nom i congnom com es feia abans)
    @Override
    public int compareTo(Persona p) {
        return Integer.compare(this.getId_persona(), p.getId_persona());
    }

    // Dues persones son iguals si tenen el mateix ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) obj;
        return this.getId_persona() == other.getId_persona();
    }
}
