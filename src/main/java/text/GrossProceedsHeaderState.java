// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GrossProceedsHeaderState implements State {

    private static final Pattern patternHeader = Pattern.compile("^[A-Z]");
    private static final Pattern patternData = Pattern.compile("^\\d");

    @Override
    public void accept(final Context context, final String text) {
        Matcher matcher;
        matcher = patternHeader.matcher(text);
        if (matcher.find()) {
            if (!context.hasGoldOuncesHeader()) {
                context.addGoldOuncesRow(new String[] {
                        "Date",
                        "Gold Ounces Per Share"
                });
            }
            if (!context.hasGrossProceedsHeader()) {
                context.addGrossProceedsRow(new String[] {
                        "Date",
                        "Gold Ounces Sold Per Share",
                        "Proceeds Per Share"
                });
            }
            return;
        }
        matcher = patternData.matcher(text);
        if (matcher.find()) {
            context.transitionToGrossProceedsDataState();
            context.state().accept(context, text);
            return;
        }
    }
}
