package com.example.dbcurs;

public enum DBTables {

    CLIENTS(0, "Clients", R.string.tab_text_1),
    MOVIES(1, "Movies", R.string.tab_text_2),
    RENTS(2, "MovieRents", R.string.tab_text_3),
    STUDIOS(3, "Studios", 0);

    public final int pageIndex;
    public final String table;
    public final int stringResource;

    DBTables(int pageIndex, String table, int stringResource) {
        this.pageIndex = pageIndex;
        this.table = table;
        this.stringResource = stringResource;
    }
}
