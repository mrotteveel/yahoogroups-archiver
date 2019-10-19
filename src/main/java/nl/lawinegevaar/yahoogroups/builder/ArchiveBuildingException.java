package nl.lawinegevaar.yahoogroups.builder;

public class ArchiveBuildingException extends RuntimeException {

    public ArchiveBuildingException(String message) {
        super(message);
    }

    public ArchiveBuildingException(String message, Throwable cause) {
        super(message, cause);
    }
}
