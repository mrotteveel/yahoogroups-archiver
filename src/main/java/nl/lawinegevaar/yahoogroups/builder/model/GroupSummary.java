package nl.lawinegevaar.yahoogroups.builder.model;

import lombok.Getter;
import nl.lawinegevaar.yahoogroups.builder.json.YgMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

/**
 * Provides various summaries on a group.
 * <p>
 * This class is only thread-safe after calling {@link #doneAddingMessages()}
 * </p>
 */
public final class GroupSummary {

    @Getter
    private final String groupName;
    private final Map<YearMonth, List<PostSummary>> postSummariesPerMonth = new HashMap<>();
    private volatile boolean doneAddingMessages;
    private List<YearMonth> yearMonths;
    private List<Integer> years;

    public GroupSummary(String groupName) {
        this.groupName = groupName;
    }

    public void addToSummary(YearMonth yearMonth, YgMessage ygMessage) {
        requireNotDone();
        postSummariesPerMonth.computeIfAbsent(yearMonth, ignore -> new ArrayList<>())
                .add(PostSummary.of(ygMessage));
    }

    public void doneAddingMessages() {
        years = postSummariesPerMonth.keySet().stream()
                .mapToInt(YearMonth::getYear)
                .distinct()
                .sorted()
                .boxed()
                .collect(toList());
        yearMonths = postSummariesPerMonth.keySet().stream()
                .sorted(comparingInt(YearMonth::getYear).thenComparingInt(YearMonth::getMonth))
                .collect(toList());
        doneAddingMessages = true;
    }

    public void forEachMonth(BiConsumer<YearMonth, List<PostSummary>> perMonthPostSummaryConsumer) {
        postSummariesPerMonth.forEach(perMonthPostSummaryConsumer);
    }

    /**
     * Determines the previous YearMonth. This method only works correctly after all messages have been processed.
     *
     * @param current current YearMonth
     * @return Previous YearMonth, or {@code null}
     */
    public YearMonth previous(YearMonth current) {
        requireDone();
        int currentIndex = yearMonths.indexOf(current);
        int previousIndex = currentIndex - 1;
        if (previousIndex < 0) {
            return null;
        }
        return yearMonths.get(previousIndex);
    }

    /**
     * Determines the previous year. This method only works correctly after all messages have been processed.
     *
     * @param currentYear current year
     * @return Previous year, or {@code null}
     */
    public Integer previous(Integer currentYear) {
        requireDone();
        int currentIndex = years.indexOf(currentYear);
        int previousIndex = currentIndex - 1;
        if (previousIndex < 0) {
            return null;
        }
        return years.get(previousIndex);
    }

    /**
     * Determines the next YearMonth. This method only works correctly after all messages have been processed.
     *
     * @param current current YearMonth
     * @return Next YearMonth, or {@code null}
     */
    public YearMonth next(YearMonth current) {
        requireDone();
        int currentIndex = yearMonths.indexOf(current);
        int nextIndex = currentIndex + 1;
        if (currentIndex == -1 || nextIndex >= yearMonths.size()) {
            return null;
        }
        return yearMonths.get(nextIndex);
    }

    /**
     * Determines the next year. This method only works correctly after all messages have been processed.
     *
     * @param currentYear current year
     * @return Next year, or {@code null}
     */
    public Integer next(Integer currentYear) {
        requireDone();
        int currentIndex = years.indexOf(currentYear);
        int nextIndex = currentIndex + 1;
        if (currentIndex == -1 || nextIndex >= years.size()) {
            return null;
        }
        return years.get(nextIndex);
    }

    public List<PostsPerYear> getPostsPerYear() {
        requireDone();
        return postSummariesPerMonth.entrySet().stream()
                .collect(groupingBy(entry -> entry.getKey().getYear(), summingInt(entry -> entry.getValue().size())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new PostsPerYear(entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    public Map<Integer, List<PostsPerMonth>> getPostsPerMonthPerYear() {
        requireDone();
        return postSummariesPerMonth.entrySet().stream()
                .map(entry -> new PostsPerMonth(entry.getKey(), entry.getValue().size()))
                .collect(groupingBy(posts -> posts.getYearMonth().getYear()));
    }

    private void requireNotDone() {
        if (doneAddingMessages) {
            throw new IllegalStateException("addToSummary called after done adding messages reported");
        }
    }

    private void requireDone() {
        if (!doneAddingMessages) {
            throw new IllegalStateException("Method cannot be called while still adding messages");
        }
    }

}
