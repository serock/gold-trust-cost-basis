// SPDX-License-Identifier: MIT
package text;

public interface State {
    void accept(Context context, String text);
}
