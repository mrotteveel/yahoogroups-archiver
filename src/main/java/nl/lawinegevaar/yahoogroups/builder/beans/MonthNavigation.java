package nl.lawinegevaar.yahoogroups.builder.beans;

import lombok.Getter;

@Getter
public final class MonthNavigation {

    private final YearMonth previous;
    private final YearMonth next;

    public MonthNavigation(YearMonth previous, YearMonth next) {
        this.previous = previous;
        this.next = next;
    }
}
