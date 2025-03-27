// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GrossProceedsDataState implements State {

    private static final Pattern patternOunces = Pattern.compile("^(\\d{1,2}/\\d{1,2}/\\d{4})\\s+(0\\.\\d{8})");
    private static final Pattern patternProceeds = Pattern.compile("^(\\d{1,2}/\\d{1,2}/\\d{4})\\s+(0\\.\\d{8})\\s+(0\\.\\d{8})\\s+(0\\.\\d{6,8})$");

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
            context.addGoldOuncesRow(new String[] {
                    matcher.group(1),
                    matcher.group(2)
            });
            context.addGrossProceedsRow(new String[] {
                    matcher.group(1),
                    matcher.group(3),
                    matcher.group(4)
            });
            return;
        }
        matcher = patternOunces.matcher(text);
        if (matcher.find()) {
            context.addGoldOuncesRow(new String[] {
                    matcher.group(1),
                    matcher.group(2)
            });
            return;
        }
    }
}
