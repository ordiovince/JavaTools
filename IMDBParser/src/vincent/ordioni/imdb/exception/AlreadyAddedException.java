/**
 * 
 */
package vincent.ordioni.imdb.exception;

/**
 * @author VIORDION
 */
public class AlreadyAddedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6934370156095100315L;
    
    private String serieName;

    public AlreadyAddedException(String serieName) {
        super("There is already a serie: " + serieName);
        this.serieName = serieName;
    }

    public String getSerieName() {
        return serieName;
    }
}
