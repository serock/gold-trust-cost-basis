// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final List<String[]> costBasisFactorRows;
    private final List<String[]> taxLotsRows;

    private final State costBasisFactorsDataState = new CostBasisFactorsDataState();
    private final State searchState = new SearchState();
    private final State taxLotsState = new TaxLotsState();
    private final State undeterminedTermTransactionsHeaderState = new UndeterminedTermTransactionsHeaderState();

    private State state;
    private String taxYear;

    private static final Context DEFAULT_CONTEXT = new Context(false);
    private static final Context NULL_CONTEXT = new Context();

    public static Context DefaultContext() {
        return DEFAULT_CONTEXT;
    }

    public static Context NullContext() {
        return NULL_CONTEXT;
    }

    private Context() {
        this(true);
    }

    private Context(final boolean isNullContext) {
        if (isNullContext) {
            this.costBasisFactorRows = null;
            this.taxLotsRows = null;
        } else {
            this.costBasisFactorRows = new ArrayList<>();
            this.taxLotsRows = new ArrayList<>();
            transitionToTaxLotsState();
        }
    }

    public String[][] getCostBasisFactorsFormulas() {
        return getFormulas(costBasisFactorRows());
    }

    String[] getLastCostBasisFactorRow() {
        return costBasisFactorRows().get(costBasisFactorRows().size() - 1);
    }

    public String[][] getTaxLotsFormulas() {
        return getFormulas(taxLotsRows());
    }

    public State state() {
        return this.state;
    }

    void addCostBasisFactorRow(final String[] row) {
        costBasisFactorRows().add(row);
    }

    void addTaxLotsRow(final String[] row) {
        taxLotsRows().add(row);
    }

    public boolean hasCostBasisFactorHeader() {
        return !costBasisFactorRows().isEmpty();
    }

    void setTaxYear(final String year) {
        this.taxYear = year;
    }

    String taxYear() {
        return this.taxYear;
    }
    void transitionToCostBasisFactorsDataState() {
        setState(costBasisFactorsDataState());
    }

    void transitionToUndeterminedTermTransactionsHeaderState() {
        setState(undeterminedTermTransactionsHeaderState());
    }

    void transitionToSearchState() {
        setState(searchState());
    }

    void transitionToTaxLotsState() {
        setState(taxLotsState());
    }

    private State costBasisFactorsDataState() {
        return this.costBasisFactorsDataState;
    }

    private List<String[]> costBasisFactorRows() {
        return this.costBasisFactorRows;
    }

    private static String[][] getFormulas(final List<String[]> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (String[] row : rows) {
            formulas[rowIndex++] = row;
        }
        return formulas;
    }

    private State searchState() {
        return this.searchState;
    }

    private void setState(final State newState) {
        this.state = newState;
    }

    private List<String[]> taxLotsRows() {
        return this.taxLotsRows;
    }

    private State taxLotsState() {
        return this.taxLotsState;
    }

    private State undeterminedTermTransactionsHeaderState() {
        return this.undeterminedTermTransactionsHeaderState;
    }
}
