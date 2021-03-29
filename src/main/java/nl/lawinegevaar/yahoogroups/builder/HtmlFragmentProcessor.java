package nl.lawinegevaar.yahoogroups.builder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

/**
 * Processes an HTML fragment.
 * <p>
 * Processing done:
 * <ul>
 *     <li>Remove {@code rel="nofollow"} from anchors with an {@code href} with a specified prefix</li>
 * </ul>
 * </p>
 */
class HtmlFragmentProcessor implements UnaryOperator<String> {

    private final String nofollowRemovalSelector;

    private HtmlFragmentProcessor(String nofollowRemovalSelector) {
        this.nofollowRemovalSelector = nofollowRemovalSelector;
    }

    @Override
    public String apply(String htmlFragment) {
        if (htmlFragment == null || htmlFragment.isBlank()) {
            return htmlFragment;
        }
        Document document = Jsoup.parseBodyFragment(htmlFragment);
        document.outputSettings().prettyPrint(false);
        document.select(nofollowRemovalSelector).removeAttr("rel");
        return document.body().html();
    }

    /**
     * Creates a processor to process an HTML fragment.
     *
     * @param whiteListedUrlPrefixes URL prefixes that will have their {@code rel="nofollow"} attribute removed
     * @return Function to process html to html
     */
    public static Function<String, String> htmlFragmentProcessor(Collection<String> whiteListedUrlPrefixes) {
        if (whiteListedUrlPrefixes.isEmpty()) {
            return Function.identity();
        }
        return new HtmlFragmentProcessor(createNofollowRemovalSelector(whiteListedUrlPrefixes));
    }

    private static String createNofollowRemovalSelector(Collection<String> urlPrefixes) {
        return urlPrefixes.stream()
                .map(urlPrefix -> format("a[href^='%s'][rel='nofollow']", urlPrefix))
                .collect(joining(","));
    }

}
