package Model;

/**
 * Created by dell on 17/06/2015.
 */
public class Util_Contact {
    private String id_Util_Android;
    private String iduFrom;
    private String iduTo;

    public Util_Contact() { }
    public Util_Contact(String id) { id_Util_Android = id; }

    public String getId() { return id_Util_Android;}
    public void setId(String id) { this.id_Util_Android = id; }
    public String getIduFrom() { return iduFrom;}
    public void setIduFrom(String id) { this.iduFrom = id; }
    public String getIduTo() { return iduTo;}
    public void setIduTo(String id) { this.iduTo = id; }
}
