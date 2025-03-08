// SPDX-License-Identifier: MIT
package text;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final List<String[]> grossProceedsRows;

    private final State grossProceedsDataState = new GrossProceedsDataState();
    private final State grossProceedsHeaderState = new GrossProceedsHeaderState();
    private final State searchState = new SearchState();

    private State state;
    private String taxYear;

    public Context() {
        this.grossProceedsRows = new ArrayList<>();
        transitionToSearchState();
    }

    public String[][] getGrossProceedsFormulas() {
        return getFormulas(grossProceedsRows());
    }

    public State state() {
        return this.state;
    }

    void addGrossProceedsRow(final String[] row) {
        grossProceedsRows().add(row);
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

    private State grossProceedsDataState() {
        return this.grossProceedsDataState;
    }

    private State grossProceedsHeaderState() {
        return this.grossProceedsHeaderState;
    }

    private static String[][] getFormulas(final List<String[]> rows) {
        final String[][] formulas = new String[rows.size()][];
        int rowIndex = 0;
        for (String[] row : rows) {
            formulas[rowIndex++] = row;
        }
        return formulas;
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
}
