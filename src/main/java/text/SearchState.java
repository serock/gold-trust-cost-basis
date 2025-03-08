// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchState implements State {

    private static final Pattern patternGrossProceedsFile = Pattern.compile("^SPDR® GOLD TRUST (\\d{4}) GROSS PROCEEDS FILE$");

    @Override
    public void accept(final Context context, final String text) {
        if (text.isEmpty()) {
            return;
        }
        Matcher m;
        m = patternGrossProceedsFile.matcher(text);
        if (m.find()) {
            context.setTaxYear(m.group(1));
            context.transitionToGrossProceedsHeaderState();
            return;
        }
    }
}
