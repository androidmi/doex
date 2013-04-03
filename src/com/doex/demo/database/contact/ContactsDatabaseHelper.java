/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.doex.demo.database.contact;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;


import java.util.HashMap;

/**
 * Database helper for contacts. Designed as a singleton to make sure that all
 * {@link android.content.ContentProvider} users get the same reference.
 * Provides handy methods for maintaining package and mime-type lookup tables.
 */
/* package */class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContactsDatabaseHelper";

    /**
     * Contacts DB version ranges:
     * 
     * <pre>
     *   0-98    Cupcake/Donut
     *   100-199 Eclair
     *   200-299 Eclair-MR1
     *   300-349 Froyo
     *   350-399 Gingerbread
     *   400-499 Honeycomb
     *   500-549 Honeycomb-MR1
     *   550-599 Honeycomb-MR2
     *   600-699 Ice Cream Sandwich
     * </pre>
     */
    static final int DATABASE_VERSION = 623;

    private static final String DATABASE_NAME = "contacts2.db";
    private static final String DATABASE_PRESENCE = "presence_db";

    /** In-memory cache of previously found MIME-type mappings */
    private final HashMap<String, Long> mMimetypeCache = new HashMap<String, Long>();
    /** In-memory cache of previously found package name mappings */
    private final HashMap<String, Long> mPackageCache = new HashMap<String, Long>();

    private long mMimeTypeIdEmail;
    private long mMimeTypeIdIm;
    private long mMimeTypeIdNickname;
    private long mMimeTypeIdOrganization;
    private long mMimeTypeIdPhone;
    private long mMimeTypeIdSip;
    private long mMimeTypeIdStructuredName;
    private long mMimeTypeIdStructuredPostal;

    /** Compiled statements for querying and inserting mappings */
    private SQLiteStatement mContactIdQuery;
    private SQLiteStatement mAggregationModeQuery;
    private SQLiteStatement mDataMimetypeQuery;
    private SQLiteStatement mActivitiesMimetypeQuery;

    /** Precompiled sql statement for setting a data record to the primary. */
    private SQLiteStatement mSetPrimaryStatement;
    /**
     * Precompiled sql statement for setting a data record to the super primary.
     */
    private SQLiteStatement mSetSuperPrimaryStatement;
    /** Precompiled sql statement for clearing super primary of a single record. */
    private SQLiteStatement mClearSuperPrimaryStatement;
    /** Precompiled sql statement for updating a contact display name */
    private SQLiteStatement mRawContactDisplayNameUpdate;

    private SQLiteStatement mNameLookupInsert;
    private SQLiteStatement mNameLookupDelete;
    private SQLiteStatement mStatusUpdateAutoTimestamp;
    private SQLiteStatement mStatusUpdateInsert;
    private SQLiteStatement mStatusUpdateReplace;
    private SQLiteStatement mStatusAttributionUpdate;
    private SQLiteStatement mStatusUpdateDelete;
    private SQLiteStatement mResetNameVerifiedForOtherRawContacts;
    private SQLiteStatement mContactInDefaultDirectoryQuery;

    public interface Tables {
        public static final String CONTACTS = "contacts";
        public static final String RAW_CONTACTS = "raw_contacts";
        public static final String STREAM_ITEMS = "stream_items";
        public static final String STREAM_ITEM_PHOTOS = "stream_item_photos";
        public static final String PHOTO_FILES = "photo_files";
        public static final String PACKAGES = "packages";
        public static final String MIMETYPES = "mimetypes";
        public static final String PHONE_LOOKUP = "phone_lookup";
        public static final String NAME_LOOKUP = "name_lookup";
        public static final String AGGREGATION_EXCEPTIONS = "agg_exceptions";
        public static final String SETTINGS = "settings";
        public static final String DATA = "data";
        public static final String GROUPS = "groups";
        public static final String PRESENCE = "presence";
        public static final String AGGREGATED_PRESENCE = "agg_presence";
        public static final String NICKNAME_LOOKUP = "nickname_lookup";
        public static final String CALLS = "calls";
        public static final String STATUS_UPDATES = "status_updates";
        public static final String PROPERTIES = "properties";
        public static final String ACCOUNTS = "accounts";
        public static final String VISIBLE_CONTACTS = "visible_contacts";
        public static final String DIRECTORIES = "directories";
        public static final String DEFAULT_DIRECTORY = "default_directory";
        public static final String SEARCH_INDEX = "search_index";
        public static final String VOICEMAIL_STATUS = "voicemail_status";

        // This list of tables contains auto-incremented sequences.
        public static final String[] SEQUENCE_TABLES = new String[] {
                CONTACTS,
                RAW_CONTACTS,
                STREAM_ITEMS,
                STREAM_ITEM_PHOTOS,
                PHOTO_FILES,
                DATA,
                GROUPS,
                CALLS,
                DIRECTORIES
        };
    }

    private final Context mContext;
    private final boolean mDatabaseOptimizationEnabled;
    private StringBuilder mSb = new StringBuilder();

    private boolean mReopenDatabase = false;

    private static ContactsDatabaseHelper sSingleton = null;

    private boolean mUseStrictPhoneNumberComparison;

    public static synchronized ContactsDatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new ContactsDatabaseHelper(context, DATABASE_NAME, true);
        }
        return sSingleton;
    }

    /**
     * Private constructor, callers except unit tests should obtain an instance
     * through {@link #getInstance(android.content.Context)} instead.
     */
    ContactsDatabaseHelper(Context context) {
        this(context, null, false);
    }

    protected ContactsDatabaseHelper(
            Context context, String databaseName, boolean optimizationEnabled) {
        super(context, databaseName, null, DATABASE_VERSION);
        mDatabaseOptimizationEnabled = optimizationEnabled;
        Resources resources = context.getResources();

        mContext = context;
    }

    private void refreshDatabaseCaches(SQLiteDatabase db) {
        mStatusUpdateDelete = null;
        mStatusUpdateReplace = null;
        mStatusUpdateInsert = null;
        mStatusUpdateAutoTimestamp = null;
        mStatusAttributionUpdate = null;
        mResetNameVerifiedForOtherRawContacts = null;
        mRawContactDisplayNameUpdate = null;
        mSetPrimaryStatement = null;
        mClearSuperPrimaryStatement = null;
        mSetSuperPrimaryStatement = null;
        mNameLookupInsert = null;
        mNameLookupDelete = null;
        mDataMimetypeQuery = null;
        mActivitiesMimetypeQuery = null;
        mContactIdQuery = null;
        mAggregationModeQuery = null;
        mContactInDefaultDirectoryQuery = null;

        populateMimeTypeCache(db);
    }

    private void populateMimeTypeCache(SQLiteDatabase db) {
        mMimetypeCache.clear();
        mPackageCache.clear();

        // TODO: This could be optimized into one query instead of 7
        // Also: We shouldn't have those fields in the first place. This should
        // just be
        // in the cache
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        refreshDatabaseCaches(db);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Bootstrapping database version: " + DATABASE_VERSION);

        // Add the legacy API support views, etc

        if (mDatabaseOptimizationEnabled) {
            // This will create a sqlite_stat1 table that is used for query
            // optimization
            db.execSQL("ANALYZE;");

            updateSqliteStats(db);

            // We need to close and reopen the database connection so that the
            // stats are
            // taken into account. Make a note of it and do the actual reopening
            // in the
            // getWritableDatabase method.
            mReopenDatabase = true;
        }

        ContentResolver.requestSync(null /* all accounts */,
                ContactsContract.AUTHORITY, new Bundle());
    }

    protected void initializeAutoIncrementSequences(SQLiteDatabase db) {
        // Default implementation does nothing.
    }

    /**
     * Returns the value to be returned when querying the column indicating that
     * the contact or raw contact belongs to the user's personal profile.
     * Overridden in the profile DB helper subclass.
     */
    protected int dbForProfile() {
        return 0;
    }

    /**
     * Update {@link Contacts#IN_VISIBLE_GROUP} for all contacts.
     */
    public void updateAllVisible() {
    }

    public String extractHandleFromEmailAddress(String email) {
        Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(email);
        if (tokens.length == 0) {
            return null;
        }

        String address = tokens[0].getAddress();
        int at = address.indexOf('@');
        if (at != -1) {
            return address.substring(0, at);
        }
        return null;
    }

    public String extractAddressFromEmailAddress(String email) {
        Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(email);
        if (tokens.length == 0) {
            return null;
        }

        return tokens[0].getAddress().trim();
    }

    private void bindString(SQLiteStatement stmt, int index, String value) {
        if (value == null) {
            stmt.bindNull(index);
        } else {
            stmt.bindString(index, value);
        }
    }

    private void bindLong(SQLiteStatement stmt, int index, Number value) {
        if (value == null) {
            stmt.bindNull(index);
        } else {
            stmt.bindLong(index, value.longValue());
        }
    }

    /**
     * Adds index stats into the SQLite database to force it to always use the
     * lookup indexes.
     */
    private void updateSqliteStats(SQLiteDatabase db) {

        // Specific stats strings are based on an actual large database after
        // running ANALYZE
        try {
            updateIndexStats(db, Tables.CONTACTS,
                    "contacts_has_phone_index", "10000 500");

            updateIndexStats(db, Tables.RAW_CONTACTS,
                    "raw_contacts_source_id_index", "10000 1 1 1");
            updateIndexStats(db, Tables.RAW_CONTACTS,
                    "raw_contacts_contact_id_index", "10000 2");

            updateIndexStats(db, Tables.NAME_LOOKUP,
                    "name_lookup_raw_contact_id_index", "10000 3");
            updateIndexStats(db, Tables.NAME_LOOKUP,
                    "name_lookup_index", "10000 3 2 2 1");
            updateIndexStats(db, Tables.NAME_LOOKUP,
                    "sqlite_autoindex_name_lookup_1", "10000 3 2 1");

            updateIndexStats(db, Tables.PHONE_LOOKUP,
                    "phone_lookup_index", "10000 2 2 1");
            updateIndexStats(db, Tables.PHONE_LOOKUP,
                    "phone_lookup_min_match_index", "10000 2 2 1");

            updateIndexStats(db, Tables.DATA,
                    "data_mimetype_data1_index", "60000 5000 2");
            updateIndexStats(db, Tables.DATA,
                    "data_raw_contact_id", "60000 10");

            updateIndexStats(db, Tables.GROUPS,
                    "groups_source_id_index", "50 1 1 1");

            updateIndexStats(db, Tables.NICKNAME_LOOKUP,
                    "sqlite_autoindex_name_lookup_1", "500 2 1");

        } catch (SQLException e) {
            Log.e(TAG, "Could not update index stats", e);
        }
    }

    /**
     * Stores statistics for a given index.
     * 
     * @param stats has the following structure: the first index is the expected
     *            size of the table. The following integer(s) are the expected
     *            number of records selected with the index. There should be one
     *            integer per indexed column.
     */
    private void updateIndexStats(SQLiteDatabase db, String table, String index,
            String stats) {
        db.execSQL("DELETE FROM sqlite_stat1 WHERE tbl='" + table + "' AND idx='" + index + "';");
        db.execSQL("INSERT INTO sqlite_stat1 (tbl,idx,stat)"
                + " VALUES ('" + table + "','" + index + "','" + stats + "');");
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = super.getWritableDatabase();
        if (mReopenDatabase) {
            mReopenDatabase = false;
            close();
            db = super.getWritableDatabase();
        }
        return db;
    }

    /**
     * Return the {@link ApplicationInfo#uid} for the given package name.
     */
    public static int getUidForPackageName(PackageManager pm, String packageName) {
        try {
            ApplicationInfo clientInfo = pm.getApplicationInfo(packageName, 0 /*
                                                                               * no
                                                                               * flags
                                                                               */);
            return clientInfo.uid;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Perform an internal string-to-integer lookup using the compiled
     * {@link SQLiteStatement} provided. If a mapping isn't found in database,
     * it will be created. All new, uncached answers are added to the cache
     * automatically.
     * 
     * @param query Compiled statement used to query for the mapping.
     * @param insert Compiled statement used to insert a new mapping when no
     *            existing one is found in cache or from query.
     * @param value Value to find mapping for.
     * @param cache In-memory cache of previous answers.
     * @return An unique integer mapping for the given value.
     */
    private long lookupAndCacheId(SQLiteStatement query, SQLiteStatement insert,
            String value, HashMap<String, Long> cache) {
        long id = -1;
        try {
            // Try searching database for mapping
            DatabaseUtils.bindObjectToProgram(query, 1, value);
            id = query.simpleQueryForLong();
        } catch (SQLiteDoneException e) {
            // Nothing found, so try inserting new mapping
            DatabaseUtils.bindObjectToProgram(insert, 1, value);
            id = insert.executeInsert();
        }
        if (id != -1) {
            // Cache and return the new answer
            cache.put(value, id);
            return id;
        } else {
            // Otherwise throw if no mapping found or created
            throw new IllegalStateException("Couldn't find or create internal "
                    + "lookup table entry for value " + value);
        }
    }

    public long getMimeTypeIdForStructuredName() {
        return mMimeTypeIdStructuredName;
    }

    public long getMimeTypeIdForStructuredPostal() {
        return mMimeTypeIdStructuredPostal;
    }

    public long getMimeTypeIdForOrganization() {
        return mMimeTypeIdOrganization;
    }

    public long getMimeTypeIdForIm() {
        return mMimeTypeIdIm;
    }

    public long getMimeTypeIdForEmail() {
        return mMimeTypeIdEmail;
    }

    public long getMimeTypeIdForPhone() {
        return mMimeTypeIdPhone;
    }

    public long getMimeTypeIdForSip() {
        return mMimeTypeIdSip;
    }

    /**
     * Update {@link Contacts#IN_VISIBLE_GROUP} and
     * {@link Tables#DEFAULT_DIRECTORY} for a specific contact.
     */
    public void updateContactVisible(TransactionContext txContext, long contactId) {
    }

    private void appendPhoneLookupSelection(StringBuilder sb, String number, String numberE164) {
        sb.append("lookup.data_id=data._id AND data.raw_contact_id=raw_contacts._id");
        boolean hasNumberE164 = !TextUtils.isEmpty(numberE164);
        boolean hasNumber = !TextUtils.isEmpty(number);
        if (hasNumberE164 || hasNumber) {
            sb.append(" AND ( ");
            if (hasNumberE164) {
                sb.append(" lookup.normalized_number = ");
                DatabaseUtils.appendEscapedSQLString(sb, numberE164);
            }
            if (hasNumberE164 && hasNumber) {
                sb.append(" OR ");
            }
            if (hasNumber) {
                int numberLen = number.length();
                sb.append(" lookup.len <= ");
                sb.append(numberLen);
                sb.append(" AND substr(");
                DatabaseUtils.appendEscapedSQLString(sb, number);
                sb.append(',');
                sb.append(numberLen);
                sb.append(" - lookup.len + 1) = lookup.normalized_number");
                // Some countries (e.g. Brazil) can have incoming calls which
                // contain only the local
                // number (no country calling code and no area code). This case
                // is handled below.
                // Details see b/5197612.
                if (!hasNumberE164) {
                    sb.append(" OR (");
                    sb.append(" lookup.len > ");
                    sb.append(numberLen);
                    sb.append(" AND substr(lookup.normalized_number,");
                    sb.append("lookup.len + 1 - ");
                    sb.append(numberLen);
                    sb.append(") = ");
                    DatabaseUtils.appendEscapedSQLString(sb, number);
                    sb.append(")");
                }
            }
            sb.append(')');
        }
    }

    public String getUseStrictPhoneNumberComparisonParameter() {
        return mUseStrictPhoneNumberComparison ? "1" : "0";
    }

    public static void copyStringValue(ContentValues toValues, String toKey,
            ContentValues fromValues, String fromKey) {
        if (fromValues.containsKey(fromKey)) {
            toValues.put(toKey, fromValues.getAsString(fromKey));
        }
    }

    public static void copyLongValue(ContentValues toValues, String toKey,
            ContentValues fromValues, String fromKey) {
        if (fromValues.containsKey(fromKey)) {
            long longValue;
            Object value = fromValues.get(fromKey);
            if (value instanceof Boolean) {
                if ((Boolean) value) {
                    longValue = 1;
                } else {
                    longValue = 0;
                }
            } else if (value instanceof String) {
                longValue = Long.parseLong((String) value);
            } else {
                longValue = ((Number) value).longValue();
            }
            toValues.put(toKey, longValue);
        }
    }

    /**
     * Test if any of the columns appear in the given projection.
     */
    public boolean isInProjection(String[] projection, String... columns) {
        if (projection == null) {
            return true;
        }

        // Optimized for a single-column test
        if (columns.length == 1) {
            String column = columns[0];
            for (String test : projection) {
                if (column.equals(test)) {
                    return true;
                }
            }
        } else {
            for (String test : projection) {
                for (String column : columns) {
                    if (column.equals(test)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns a detailed exception message for the supplied URI. It includes
     * the calling user and calling package(s).
     */
    public String exceptionMessage(Uri uri) {
        return exceptionMessage(null, uri);
    }

    /**
     * Returns a detailed exception message for the supplied URI. It includes
     * the calling user and calling package(s).
     */
    public String exceptionMessage(String message, Uri uri) {
        StringBuilder sb = new StringBuilder();
        if (message != null) {
            sb.append(message).append("; ");
        }
        sb.append("URI: ").append(uri);
        final PackageManager pm = mContext.getPackageManager();
        int callingUid = Binder.getCallingUid();
        sb.append(", calling user: ");
        String userName = pm.getNameForUid(callingUid);
        if (userName != null) {
            sb.append(userName);
        } else {
            sb.append(callingUid);
        }

        final String[] callerPackages = pm.getPackagesForUid(callingUid);
        if (callerPackages != null && callerPackages.length > 0) {
            if (callerPackages.length == 1) {
                sb.append(", calling package:");
                sb.append(callerPackages[0]);
            } else {
                sb.append(", calling package is one of: [");
                for (int i = 0; i < callerPackages.length; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(callerPackages[i]);
                }
                sb.append("]");
            }
        }

        return sb.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
