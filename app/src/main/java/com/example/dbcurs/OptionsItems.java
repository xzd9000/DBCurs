package com.example.dbcurs;

public enum OptionsItems {

    ADD(0, R.string.optionsAdd),
    DELETE(1, R.string.optionsDelete),
    ALTER(2, R.string.optionsAlter);

    public final int index;
    public final int stringResource;

    OptionsItems(int index, int stringResource) {
        this.index = index;
        this.stringResource = stringResource;
    }
}
