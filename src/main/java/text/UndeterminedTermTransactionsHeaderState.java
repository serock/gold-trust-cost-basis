// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UndeterminedTermTransactionsHeaderState implements State {

    private static final Pattern patternGoldTrust = Pattern.compile("^SPDR GOLD TRUST");
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
        matcher = patternGoldTrust.matcher(text);
        if (matcher.find()) {
            if (!context.hasCostBasisFactorHeader()) {
                context.addCostBasisFactorRow(new String[] {
                        "Date of Gold Sale",
                        "Cost Basis Factor"
                });
            }
            context.transitionToCostBasisFactorsDataState();
            return;
        }
    }
}
