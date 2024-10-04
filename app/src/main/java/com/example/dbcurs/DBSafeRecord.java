package com.example.dbcurs;

public class DBSafeRecord {

    public static String escapeString(String str) { return str.replace("'", "''"); }
}
