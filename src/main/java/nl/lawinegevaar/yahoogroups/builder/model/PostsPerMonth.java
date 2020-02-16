package nl.lawinegevaar.yahoogroups.builder.model;

import lombok.Getter;

@Getter
public final class PostsPerMonth {

    private final YearMonth yearMonth;
    private final int postCount;

    public PostsPerMonth(YearMonth yearMonth, int postCount) {
        this.yearMonth = yearMonth;
        this.postCount = postCount;
    }
    
}
