package com.edis.eschool.utils;

public class Constante {
    public static final String SERVER = "https://www.uacosendai-edu.net/";
    public static final String DBNAME = "eschool/";

    public static String SERVER_PATH = SERVER + DBNAME;

    // The authority for the sync adapter's content provider
    /**
     * Confirm with authority in string.xml
     */
    public static final String AUTHORITY = "com.edis.eschool.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "edis.com";
    // The account name
    public static final String ACCOUNT = "edisaccount";

    public static final String MALE = "M";
    public static final String FEMALE = "F";

    public static final String SHARED_PREFERENCE_FILE = "shared_preferences";

    public static final int PHONE_NUMBER_LENGTH = 8;
}
