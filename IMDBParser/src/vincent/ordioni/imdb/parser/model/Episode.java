/**
 * 
 */
package vincent.ordioni.imdb.parser.model;

/**
 * @author VIORDION
 */
public class Episode {

    private Integer number;

    private String name;

    private String date;

    public Episode() {
    }

    /**
     * @param number
     * @param name
     * @param date
     */
    public Episode(Integer number, String name, String date) {
        this.number = number;
        this.name = name;
        this.date = date;
    }

    /**
     * @return the number
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(Integer number) {
        this.number = number;
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
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    
    @Override
    public String toString() {
        return "Episode " + number + " : " + name;
    }
}
