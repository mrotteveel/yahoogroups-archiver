package nl.lawinegevaar.yahoogroups.builder.model;

import lombok.Getter;

@Getter
public final class YearNavigation {

    private final Integer previous;
    private final Integer next;

    public YearNavigation(Integer previous, Integer next) {
        this.previous = previous;
        this.next = next;
    }
}
