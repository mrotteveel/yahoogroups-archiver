package nl.lawinegevaar.yahoogroups.builder;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
class RawMessageDateParser {

    private static final Duration ALLOWED_DATE_DIFFERENCE = Duration.ofHours(6);

    private RawMessageDateParser() {
        // no instances
    }

    static Optional<OffsetDateTime> findDateTime(String rawMessage) {
        // Look for Received:, X-Received: and Date: headers
        OffsetDateTime firstReceived = null;
        OffsetDateTime firstDate = null;
        var scanner = new Scanner(rawMessage);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                // end of header section
                break;
            } else if (firstReceived == null && (line.startsWith("Received:") || line.startsWith("X-Received:"))) {
                // Headers could be folded, but the first header found is not folded, so we don't try to unfold
                Optional<OffsetDateTime> dateTime = parseReceivedHeader(line);
                if (dateTime.isPresent()) {
                    firstReceived = dateTime.get();
                }
            } else if (firstDate == null && line.startsWith("Date:")) {
                Optional<OffsetDateTime> dateTime = parseDateHeader(line);
                if (dateTime.isPresent()) {
                    firstDate = dateTime.get();
                }
            }

            if (firstReceived != null && firstDate != null) {
                // With a small difference, use original date, otherwise the received date
                OffsetDateTime returnDateTime = Duration.between(firstDate, firstReceived).abs()
                        .compareTo(ALLOWED_DATE_DIFFERENCE) <= 0
                        ? firstDate
                        : firstReceived;
                return Optional.of(returnDateTime);
            }
        }

        if (firstReceived != null) {
            return Optional.of(firstReceived);
        }
        return Optional.ofNullable(firstDate);
    }

    private static Optional<OffsetDateTime> parseReceivedHeader(String receivedHeader) {
        int dateTimeStart = receivedHeader.lastIndexOf(';');
        if (dateTimeStart < 0) {
            return Optional.empty();
        }
        String dateTimeString = receivedHeader.substring(dateTimeStart + 1).trim();
        return parseRfc1123DateTime(dateTimeString, receivedHeader);
    }

    private static Optional<OffsetDateTime> parseDateHeader(String dateHeader) {
        int dateTimeStart = dateHeader.indexOf(':');
        if (dateTimeStart < 0) {
            return Optional.empty();
        }
        String dateTimeString = dateHeader.substring(dateTimeStart + 1).trim();
        return parseRfc1123DateTime(dateTimeString, dateHeader);
    }

    private static Optional<OffsetDateTime> parseRfc1123DateTime(String dateTimeString, String fullHeader) {
        try {
            return Optional.of(OffsetDateTime.parse(
                    stripZoneName(dateTimeString),
                    DateTimeFormatter.RFC_1123_DATE_TIME));
        } catch (DateTimeParseException e) {
            log.debug("Could not parse header: [{}],\n{}", fullHeader, e.getMessage());
            return Optional.empty();
        }
    }

    private static String stripZoneName(String dateTimeString) {
        // Strips the ' (CET)' part from dates like Thu, 6 Mar 2008 11:57:39 +0100 (CET)
        int zoneIndex = dateTimeString.indexOf('(');
        if (zoneIndex < 0) {
            return dateTimeString;
        }
        if (dateTimeString.charAt(zoneIndex - 1) == ' ') {
            zoneIndex--;
        }
        return dateTimeString.substring(0, zoneIndex);
    }

}
