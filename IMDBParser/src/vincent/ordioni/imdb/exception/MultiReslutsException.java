/**
 * 
 */
package vincent.ordioni.imdb.exception;

import java.util.ArrayList;

/**
 * @author VIORDION
 */
public class MultiReslutsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3968087001701014968L;

    private String serieName;

    private ArrayList<Integer> years;

    public MultiReslutsException(String serieName, int n) {
        super("There is "+ n + " series for the name: " + serieName);
        this.serieName = serieName;
        this.years = new ArrayList<Integer>();
    }

    public String getSerieName() {
        return serieName;
    }

    public ArrayList<Integer> getYears() {
        return years;
    }
    
    public void addYear(Integer year) {
        years.add(year);
    }
}
