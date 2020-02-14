package nl.lawinegevaar.yahoogroups.builder.beans;

import lombok.Getter;

import java.nio.file.Path;

import static java.lang.String.format;

@Getter
public final class YearMonth {

    private final int year;
    private final int month;
    private Path path;

    public YearMonth(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public Path getPath() {
        if (path == null) {
            path = Path.of(String.valueOf(year), String.valueOf(month));
        }
        return path;
    }

    @Override
    public String toString() {
        return format("%d - %d", year, month);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearMonth yearMonth = (YearMonth) o;
        return year == yearMonth.year &&
                month == yearMonth.month;
    }

    @Override
    public int hashCode() {
        return 31 * month + year;
    }
}
