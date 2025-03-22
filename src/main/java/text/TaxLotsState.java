// SPDX-License-Identifier: MIT
package text;

public class TaxLotsState implements State {

    @Override
    public void accept(final Context context, final String text) {
        String[] row = new String[] {
                "Tax Lot ID",
                "Number of Shares",
                "Date Acquired",
                "Date Sold",
                "Proceeds",
                "Cost Basis"
        };
        context.addTaxLotsRow(row);
        context.transitionToSearchState();
        context.state().accept(context, text);
    }
}
