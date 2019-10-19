package nl.lawinegevaar.yahoogroups.archiver;

public class ScrapingFailureException extends RuntimeException {

    public ScrapingFailureException(String message) {
        super(message);
    }

    public ScrapingFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
