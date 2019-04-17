package com.edis.eschool.utils;

public class Contante {
    public static final String SERVER = "http://localhost:5000/";
    public static final String DBNAME = "mobil_bd/";

    public static String SERVER_PATH = SERVER + DBNAME;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.edis.eschool.android.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "edis.com";
    // The account name
    public static final String ACCOUNT = "edisaccount";
}
