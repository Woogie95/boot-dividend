package com.example.dividend.model.constants;

public enum Month {
    JAN("Jan", 1),
    FEB("Feb", 2),
    MAR("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12);

    private final String month;
    private final int numberMonth;

    Month(String month, int numberMonth) {
        this.month = month;
        this.numberMonth = numberMonth;
    }

    public static int StringToNumber(String string) {
        for (Month month : Month.values()) {
            if (month.month.equals(string)) {
                return month.numberMonth;
            }
        }
        return -1;
    }

}
