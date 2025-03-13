// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GrossProceedsDataState implements State {

    private static final Pattern patternProceeds = Pattern.compile("^(\\d{1,2}/\\d{1,2}/\\d{4})\\s+(0\\.\\d{8})\\s+(0\\.\\d{8})\\s+(0\\.\\d{6,8})$");
    private static final Pattern patternOunces = Pattern.compile("^(\\d{1,2}/\\d{1,2}/\\d{4})\\s+(0\\.\\d{8})");

    @Override
    public void accept(final Context context, final String text) {
        if (text.isEmpty()) {
            return;
        }
        if (text.startsWith("\f")) {
            context.transitionToSearchState();
            context.state().accept(context, text.substring(1));
            return;
        }
        Matcher matcher;
        matcher = patternProceeds.matcher(text);
        if (matcher.find()) {
            final String[] row = createEmptyRow();
            row[Constants.GP_FIELD_DATE] = matcher.group(1);
            row[Constants.GP_FIELD_GOLD_OUNCES_PER_SHARE] = matcher.group(2);
            row[Constants.GP_FIELD_GOLD_OUNCES_SOLD_PER_SHARE] = matcher.group(3);
            row[Constants.GP_FIELD_PROCEEDS_PER_SHARE] = matcher.group(4);
            context.addGrossProceedsRow(row);
            return;
        }
        matcher = patternOunces.matcher(text);
        if (matcher.find()) {
            final String[] row = createEmptyRow();
            row[Constants.GP_FIELD_DATE] = matcher.group(1);
            row[Constants.GP_FIELD_GOLD_OUNCES_PER_SHARE] = matcher.group(2);
            context.addGrossProceedsRow(row);
            return;
        }
    }

    private static String[] createEmptyRow() {
        final int capacity = 4;
        final String[] row = new String[capacity];
        for (int i = capacity - 1; i >= 0; i--) {
            row[i] = "";
        }
        return row;
    }
}
