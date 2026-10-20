package com.gradesystem.io;

/**
 * One data row from a CSV file, plus the row number (used in error messages
 * like "Row 7: Invalid student ID").
 */
public class CsvRecord {

    private final int rowNumber;
    private final String[] fields;

    /**
     * Creates a new CsvRecord.
     *
     * @param rowNumber the row number, counting data rows from 1 (the header is not counted)
     * @param fields    the values in the row, already trimmed
     */
    public CsvRecord(int rowNumber, String[] fields) {
        this.rowNumber = rowNumber;
        this.fields = fields.clone();
    }

    /** {@return the row number (1 = first row after the header)} */
    public int getRowNumber() { return rowNumber; }

    /** {@return how many values the row has} */
    public int getFieldCount() { return fields.length; }

    /**
     * Returns the value in one column.
     *
     * @param index the column, starting at 0
     * @return the value in that column
     */
    public String getField(int index) { return fields[index]; }

    /** {@return the row as it would look in the file} */
    public String toLine() { return String.join(",", fields); }
}
