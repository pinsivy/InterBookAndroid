package Model;

/**
 * Created by dell on 11/06/2015.
 */
public class Departement {
    private String id_Departement;
    private String description;

    public Departement(String productId, String name) { id_Departement = productId; description = name; }

    public String getId() { return id_Departement;}
    public void setId(String productId) { this.id_Departement = productId; }
    public String getName() {
        return description;
    }
    public void setName(String name) {
        this.description = name;
    }
}
