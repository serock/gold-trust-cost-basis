// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SearchState implements State {

    private static final Pattern patternUndeterminedTermTransactions = Pattern.compile("^UNDETERMINED TERM TRANSACTIONS FOR NONCOVERED TAX LOTS");

    @Override
    public void accept(final Context context, final String text) {
        if (text.isEmpty()) {
            return;
        }
        Matcher m;
        m = patternUndeterminedTermTransactions.matcher(text);
        if (m.find()) {
            context.transitionToUndeterminedTermTransactionsHeaderState();
            return;
        }
    }
}
