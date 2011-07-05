/**
 * 
 */
package vincent.ordioni.imdb.parser.model;

import java.util.Collection;
import java.util.TreeMap;

import vincent.ordioni.imdb.util.Utils;

/**
 * @author VIORDION
 */
public class Serie implements Comparable<Serie> {

    private String id;

    private String name;

    private String dispName;

    private Integer year;

    private TreeMap<Integer, Saison> saisons;

    public Serie() {
    }

    /**
     * @param name
     * @param year
     */
    public Serie(String name, Integer year) {
        id = name.trim();
        id = id.replace(" ", "");
        id = id.replace(".", "");
        id = id.replace(":", "");
        id = id.replace(",", "");
        id = id.replace("'", "");
        id = id.replace("_", "");
        id = Utils.unAccent(id);

        id = id.toLowerCase();
        id = "data" + id + year;

        this.name = name;
        this.dispName = name;
        this.year = year;
    }

    /**
     * @param name
     * @param dispName
     * @param year
     */
    public Serie(String name, String dispName, Integer year) {
        id = name.trim();
        id = id.replace(" ", "");
        id = id.replace(".", "");
        id = id.replace(":", "");
        id = id.replace(",", "");
        id = id.replace("'", "");
        id = id.replace("_", "");
        id = id.replace("-", "");
        id = id.toLowerCase();
        id = "data" + id + year;

        this.name = name;
        this.dispName = dispName;
        this.year = year;
    }

    /**
     * @param id
     * @param name
     * @param dispName
     * @param year
     */
    public Serie(String id, String name, String dispName, Integer year) {
        this.id = id;
        this.name = name;
        this.dispName = dispName;
        this.year = year;
        this.saisons = new TreeMap<Integer, Saison>();
    }

    /**
     * @param saison
     */
    public void addSaison(Saison saison) {
        saisons.put(saison.getNumber(), saison);
    }

    /**
     * @param saisons
     */
    public void addAllSaisons(TreeMap<Integer, Saison> saisons) {
        saisons.putAll(saisons);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dispName
     */
    public String getDispName() {
        return dispName;
    }

    /**
     * @param dispName the dispName to set
     */
    public void setDispName(String dispName) {
        this.dispName = dispName;
    }

    /**
     * @return the saisons
     */
    public TreeMap<Integer, Saison> getMapSaisons() {
        return saisons;
    }

    /**
     * @param saisons the saisons to set
     */
    public void setMapSaisons(TreeMap<Integer, Saison> saisons) {
        this.saisons = saisons;
    }

    /**
     * @return the saisons
     */
    public Collection<Saison> getSaisons() {
        return saisons.values();
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return name + " - " + year;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Serie) {
            Serie serie = (Serie) obj;
            return id.equals(serie.id);
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public int compareTo(Serie o) {
        return id.compareTo(o.id);
    }
}
