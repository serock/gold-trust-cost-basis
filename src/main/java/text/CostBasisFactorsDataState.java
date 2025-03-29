// SPDX-License-Identifier: MIT
package text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CostBasisFactorsDataState implements State {

    private static final Pattern patternDate = Pattern.compile("^(\\d{2}/\\d{2}/\\d{2})");
    private static final Pattern patternCostBasisFactor = Pattern.compile("^Cost Basis Factor: (\\d\\.\\d{9})$");

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
        matcher = patternDate.matcher(text);
        if (matcher.find()) {
            context.addCostBasisFactorRow(new String[] {
                    matcher.group(1),
                    ""
            });
            return;
        }
        matcher = patternCostBasisFactor.matcher(text);
        if (matcher.find()) {
            context.getLastCostBasisFactorRow()[1] = matcher.group(1);
            return;
        }
    }
}
