package Model;

/**
 * Created by dell on 17/06/2015.
 */
public class Reservation {
    private String id_Reservation;
    private String iduEntreprise;
    private String iduEmploye;
    private String debut;
    private String fin;
    private String id_EtatReservation;

    public Reservation() { }
    public Reservation(String id_Reservation1,String iduEntreprise1,String iduEmploye1,String debut1, String fin1, String id_EtatReservation1) {
        id_Reservation = id_Reservation1;
        iduEntreprise = iduEntreprise1;
        iduEmploye = iduEmploye1;
        debut = debut1;
        fin = fin1;
        id_EtatReservation = id_EtatReservation1;
    }

    public String getId() { return id_Reservation;}
    public void setId(String id) { this.id_Reservation = id; }
    public String getIduEntreprise() { return iduEntreprise;}
    public void setIduEntreprise(String id) { this.iduEntreprise = id; }
    public String getIduEmploye() { return iduEmploye;}
    public void setIduEmploye(String id) { this.iduEmploye = id; }
    public String getDebut() { return debut.split("T")[0];}
    public void setDebut(String id) { this.debut = id; }
    public String getFin() { return fin.split("T")[0];}
    public void setFin(String id) { this.fin = id; }
    public String getIdEtatReservation() { return id_EtatReservation;}
    public void setIdEtatReservation(String id) { this.id_EtatReservation = id; }
}
