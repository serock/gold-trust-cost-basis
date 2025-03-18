// SPDX-License-Identifier: MIT
package text;

public class TaxLotsState implements State {

    @Override
    public void accept(final Context context, final String text) {
        String[] row = new String[] {
                "Shares",
                "Date Acquired",
                "Date Sold",
                "Proceeds",
                "Cost"
        };
        context.addTaxLotsRow(row);
        context.transitionToSearchState();
        context.state().accept(context, text);
    }
}
