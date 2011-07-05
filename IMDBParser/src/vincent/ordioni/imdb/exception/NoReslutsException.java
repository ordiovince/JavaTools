/**
 * 
 */
package vincent.ordioni.imdb.exception;

/**
 * @author VIORDION
 */
public class NoReslutsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 8226535493383135431L;

    private String serieName;

    public NoReslutsException(String serieName) {
        super("There is no series for the name: " + serieName);
        this.serieName = serieName;
    }

    public String getSerieName() {
        return serieName;
    }
}
