// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final List<String[]> goldOuncesRows;
    private final List<String[]> grossProceedsRows;
    private final List<String[]> taxLotsRows;

    private final State grossProceedsDataState = new GrossProceedsDataState();
    private final State grossProceedsHeaderState = new GrossProceedsHeaderState();
    private final State searchState = new SearchState();
    private final State taxLotsState = new TaxLotsState();

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
            this.goldOuncesRows = null;
            this.grossProceedsRows = null;
            this.taxLotsRows = null;
        } else {
            this.goldOuncesRows = new ArrayList<>();
            this.grossProceedsRows = new ArrayList<>();
            this.taxLotsRows = new ArrayList<>();
            transitionToTaxLotsState();
        }
    }

    public String[][] getGoldOuncesFormulas() {
        return getFormulas(goldOuncesRows());
    }

    public String[][] getGrossProceedsFormulas() {
        return getFormulas(grossProceedsRows());
    }

    public String[][] getTaxLotsFormulas() {
        return getFormulas(taxLotsRows());
    }

    public State state() {
        return this.state;
    }

    void addGoldOuncesRow(final String[] row) {
        goldOuncesRows().add(row);
    }

    void addGrossProceedsRow(final String[] row) {
        grossProceedsRows().add(row);
    }

    void addTaxLotsRow(final String[] row) {
        taxLotsRows().add(row);
    }

    public boolean hasGoldOuncesHeader() {
        return !goldOuncesRows().isEmpty();
    }

    public boolean hasGrossProceedsHeader() {
        return !grossProceedsRows().isEmpty();
    }

    void setTaxYear(final String year) {
        this.taxYear = year;
    }

    String taxYear() {
        return this.taxYear;
    }

    void transitionToGrossProceedsDataState() {
        setState(grossProceedsDataState());
    }

    void transitionToGrossProceedsHeaderState() {
        setState(grossProceedsHeaderState());
    }

    void transitionToSearchState() {
        setState(searchState());
    }

    void transitionToTaxLotsState() {
        setState(taxLotsState());
    }

    private static String[][] getFormulas(final List<String[]> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (String[] row : rows) {
            formulas[rowIndex++] = row;
        }
        return formulas;
    }

    private List<String[]> goldOuncesRows() {
        return this.goldOuncesRows;
    }

    private State grossProceedsDataState() {
        return this.grossProceedsDataState;
    }

    private State grossProceedsHeaderState() {
        return this.grossProceedsHeaderState;
    }

    private List<String[]> grossProceedsRows() {
        return this.grossProceedsRows;
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
}
