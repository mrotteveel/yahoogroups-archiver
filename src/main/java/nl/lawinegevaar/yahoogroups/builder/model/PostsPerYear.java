package nl.lawinegevaar.yahoogroups.builder.model;

import lombok.Getter;

@Getter
public final class PostsPerYear {

    private final int year;
    private final int postCount;

    public PostsPerYear(int year, int postCount) {
        this.year = year;
        this.postCount = postCount;
    }
}
