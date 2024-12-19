package use;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Document;

public class NumericDocumentFilter extends DocumentFilter {

    private final int filterType;
    private final int filterLength;

    // Constructor
    public NumericDocumentFilter(int filterType, int maxLength) {
        this.filterType = filterType;
        this.filterLength = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && isValidInput(fb.getDocument(), offset, string) && isValidLength(fb.getDocument(), string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && isValidInput(fb.getDocument(), offset, string) && isValidLength(fb.getDocument(), string)) {
            super.replace(fb, offset, length, string, attr);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }

    private boolean isValidInput(Document doc, int offset, String string) throws BadLocationException {
        String currentText = doc.getText(0, doc.getLength());
        StringBuilder prospectiveText = new StringBuilder(currentText);
        prospectiveText.insert(offset, string);

        switch (filterType) {
            case Constants.filter.FILTER_NULL:
                // No filtering, all input is valid
                return true;

            case Constants.filter.FILTER_INTEGER:
                // Integer only
                return prospectiveText.toString().matches("\\d*");

            case Constants.filter.FILTER_DOUBLE:
                // Double numbers
                return prospectiveText.toString().matches("\\d*|\\d+\\.\\d*|\\.\\d+");

            case Constants.filter.FILTER_DATE:
                // Matches "  .  .    ", "DD.MM.YYYY" with optional spaces
                return prospectiveText.toString().matches("\\s*\\d{0,2}\\.?\\d{0,2}\\.?\\d{0,4}\\s*");

            default:
                // Invalid filter type
                return false;
        }
    }

    private boolean isValidLength(Document doc, String string) {
        int maxLength;
        if (filterType == Constants.filter.FILTER_DATE) {
            if (filterLength > -1) {
                if (filterLength > 10) maxLength = 10;
                else maxLength = filterLength;
            } else maxLength = 10;
        } else if (filterType == Constants.filter.FILTER_INTEGER || filterType == Constants.filter.FILTER_DOUBLE) {
            if (filterLength > -1) {
                if (filterLength > 9) maxLength = 9;
                else maxLength = filterLength;
            } else maxLength = 9;
        } else {
            if (filterLength > -1) maxLength = filterLength;
            else maxLength = 24000;
        }

        int currentLength = doc.getLength();
        int newLength = currentLength + string.length();
        return newLength <= maxLength;
    }

}