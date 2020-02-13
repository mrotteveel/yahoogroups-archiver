package nl.lawinegevaar.yahoogroups.builder.beans;

import lombok.Getter;

@Getter
public class PostsPerMonth {

    private final YearMonth yearMonth;
    private final int postCount;

    public PostsPerMonth(YearMonth yearMonth, int postCount) {
        this.yearMonth = yearMonth;
        this.postCount = postCount;
    }
    
}
