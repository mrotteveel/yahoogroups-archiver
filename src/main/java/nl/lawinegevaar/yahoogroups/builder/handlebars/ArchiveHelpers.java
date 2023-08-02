package nl.lawinegevaar.yahoogroups.builder.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Locale;

import static com.github.jknack.handlebars.internal.lang3.Validate.notNull;

public enum ArchiveHelpers implements Helper<Object> {
    monthName {

        final String[] monthNames;
        {
            var dfSymbols = new DateFormatSymbols(Locale.ENGLISH);
            monthNames = dfSymbols.getMonths();
        }

        @Override
        protected CharSequence safeApply(Object context, Options options) {
            int month;
            if (context instanceof Integer || context instanceof Short) {
                month = ((Number) context).intValue();
            } else if (context instanceof String) {
                month = Integer.parseInt((String) context);
            } else {
                throw new IllegalArgumentException("Expected an int or a string convertible to int");
            }
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("month out of range [1, 12], was: " + month);
            }
            return monthNames[month - 1];
        }
    };

    @Override
    public Object apply(final Object context, final Options options) throws IOException {
        if (options.isFalsy(context)) {
            Object param = options.param(0, null);
            return param == null ? null : param.toString();
        }
        return safeApply(context, options);
    }

    /**
     * Apply the helper to the context.
     *
     * @param context The context object (param=0).
     * @param options The options object.
     * @return A string result.
     */
    protected abstract CharSequence safeApply(final Object context, final Options options);

    /**
     * Register the helper in a handlebars instance.
     *
     * @param handlebars A handlebars object. Required.
     */
    public void registerHelper(final Handlebars handlebars) {
        notNull(handlebars, "The handlebars is required.");
        handlebars.registerHelper(name(), this);
    }

    /**
     * Register all the text helpers.
     *
     * @param handlebars The helper's owner. Required.
     */
    public static void register(final Handlebars handlebars) {
        notNull(handlebars, "A handlebars object is required.");
        ArchiveHelpers[] helpers = values();
        for (ArchiveHelpers helper : helpers) {
            helper.registerHelper(handlebars);
        }
    }
}
