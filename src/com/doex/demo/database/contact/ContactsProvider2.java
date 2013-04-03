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

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Contacts content provider. The contract between this provider and
 * applications is defined in {@link ContactsContract}.
 */
public class ContactsProvider2 extends AbstractContactsProvider
        implements OnAccountsUpdateListener {
    static String TAG = "ContactsProvider2";
    private static final boolean VERBOSE_LOGGING = Log.isLoggable(TAG, Log.VERBOSE);
    private ContactAggregator mContactAggregator;
    // private ContactAggregator mProfileAggregator;
    private TransactionContext mContactTransactionContext = new TransactionContext(false);
    private TransactionContext mProfileTransactionContext = new TransactionContext(true);

    // The database tag to use for representing the contacts DB in contacts
    // transactions.
    /* package */static final String CONTACTS_DB_TAG = "contacts";

    // The database tag to use for representing the profile DB in contacts
    // transactions.
    /* package */static final String PROFILE_DB_TAG = "profile";

    // This variable keeps track of whether the current operation is intended
    // for the profile DB.
    private final ThreadLocal<Boolean> mInProfileMode = new ThreadLocal<Boolean>();

    /**
     * The active (thread-local) database. This will be switched between a
     * contacts-specific database and a profile-specific database, depending on
     * what the current operation is targeted to.
     */
    private final ThreadLocal<SQLiteDatabase> mActiveDb = new ThreadLocal<SQLiteDatabase>();

    private final ThreadLocal<TransactionContext> mTransactionContext =
            new ThreadLocal<TransactionContext>();
    // Depending on whether the action being performed is for the profile or
    // not, we will use one of
    // two aggregator instances.
    private final ThreadLocal<ContactAggregator> mAggregator = new ThreadLocal<ContactAggregator>();
    private ContactAggregator mProfileAggregator;

    // Depending on whether the action being performed is for the profile, we
    // will use one of two
    // database helper instances.
    private final ThreadLocal<ContactsDatabaseHelper> mDbHelper =
            new ThreadLocal<ContactsDatabaseHelper>();

    /**
     * The thread-local holder of the active transaction. Shared between this
     * and the profile provider, to keep transactions on both databases
     * synchronized.
     */
    private final ThreadLocal<ContactsTransaction> mTransactionHolder =
            new ThreadLocal<ContactsTransaction>();

    private StringBuilder mSb = new StringBuilder();

    private boolean mVisibleTouched = false;

    private boolean mProviderStatusUpdateNeeded;

    private int mProviderStatus;

    private boolean mSyncToNetwork;

    private ContactsDatabaseHelper mContactsHelper;

    private static final UriMatcher sUriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    private ContentValues mValues = new ContentValues();

    private volatile CountDownLatch mReadAccessLatch;
    private volatile CountDownLatch mWriteAccessLatch;

    private ProfileProvider mProfileProvider;

    private ProfileDatabaseHelper mProfileHelper;

    @Override
    public boolean onCreate() {
        super.onCreate();
        try {
            return initialize();
        } catch (RuntimeException e) {
            Log.e(TAG, "Cannot start provider", e);
            return false;
        } finally {

        }
    }

    private boolean initialize() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        mDbHelper.set(mContactsHelper);

        // Set up the DB helper for keeping transactions serialized.
        setDbHelperToSerializeOn(mContactsHelper, CONTACTS_DB_TAG);

        // The provider is closed for business until fully initialized
        mReadAccessLatch = new CountDownLatch(1);
        mWriteAccessLatch = new CountDownLatch(1);

        // Set up the sub-provider for handling profiles.
        mProfileProvider = getProfileProvider();
        mProfileProvider.setDbHelperToSerializeOn(mContactsHelper, CONTACTS_DB_TAG);
        ProviderInfo profileInfo = new ProviderInfo();
        profileInfo.readPermission = "android.permission.READ_PROFILE";
        profileInfo.writePermission = "android.permission.WRITE_PROFILE";
        mProfileProvider.attachInfo(getContext(), profileInfo);
        mProfileHelper = mProfileProvider.getDatabaseHelper(getContext());

        return true;
    }

    public ProfileProvider getProfileProvider() {
        return new ProfileProvider(this);
    }

    @Override
    public void onBegin() {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "onBeginTransaction");
        }
        if (inProfileMode()) {
            // mProfileAggregator.clearPendingAggregations();
            mProfileTransactionContext.clear();
        } else {
            // mContactAggregator.clearPendingAggregations();
            mContactTransactionContext.clear();
        }
    }

    private boolean inProfileMode() {
        Boolean profileMode = mInProfileMode.get();
        return profileMode != null && profileMode;
    }

    @Override
    public void onCommit() {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "beforeTransactionCommit");
        }
        flushTransactionalChanges();
        mAggregator.get().aggregateInTransaction(mTransactionContext.get(), mActiveDb.get());
        if (mVisibleTouched) {
            mVisibleTouched = false;
            mDbHelper.get().updateAllVisible();
        }

        updateSearchIndexInTransaction();

        if (mProviderStatusUpdateNeeded) {
            updateProviderStatus();
            mProviderStatusUpdateNeeded = false;
        }
    }

    private void updateProviderStatus() {
        if (mProviderStatus != ProviderStatus.STATUS_NORMAL
                && mProviderStatus != ProviderStatus.STATUS_NO_ACCOUNTS_NO_CONTACTS) {
            return;
        }

        // No accounts/no contacts status is true if there are no account and
        // there are no contacts or one profile contact
        setProviderStatus(ProviderStatus.STATUS_NO_ACCOUNTS_NO_CONTACTS);
        setProviderStatus(ProviderStatus.STATUS_NORMAL);
    }

    protected void setProviderStatus(int status) {
        if (mProviderStatus != status) {
            mProviderStatus = status;
            getContext().getContentResolver().notifyChange(ProviderStatus.CONTENT_URI, null, false);
        }
    }

    /**
     * Private API for inquiring about the general status of the provider.
     * 
     * @hide
     */
    public static final class ProviderStatus {

        /**
         * Not instantiable.
         */
        private ProviderStatus() {
        }

        /**
         * The content:// style URI for this table. Requests to this URI can be
         * performed on the UI thread because they are always unblocking.
         * 
         * @hide
         */
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "provider_status");

        /**
         * The MIME-type of {@link #CONTENT_URI} providing a directory of
         * settings.
         * 
         * @hide
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/provider_status";

        /**
         * An integer representing the current status of the provider.
         * 
         * @hide
         */
        public static final String STATUS = "status";

        /**
         * Default status of the provider.
         * 
         * @hide
         */
        public static final int STATUS_NORMAL = 0;

        /**
         * The status used when the provider is in the process of upgrading.
         * Contacts are temporarily unaccessible.
         * 
         * @hide
         */
        public static final int STATUS_UPGRADING = 1;

        /**
         * The status used if the provider was in the process of upgrading but
         * ran out of storage. The DATA1 column will contain the estimated
         * amount of storage required (in bytes). Update status to STATUS_NORMAL
         * to force the provider to retry the upgrade.
         * 
         * @hide
         */
        public static final int STATUS_UPGRADE_OUT_OF_MEMORY = 2;

        /**
         * The status used during a locale change.
         * 
         * @hide
         */
        public static final int STATUS_CHANGING_LOCALE = 3;

        /**
         * The status that indicates that there are no accounts and no contacts
         * on the device.
         * 
         * @hide
         */
        public static final int STATUS_NO_ACCOUNTS_NO_CONTACTS = 4;

        /**
         * Additional data associated with the status.
         * 
         * @hide
         */
        public static final String DATA1 = "data1";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        waitForAccess(mWriteAccessLatch);

        // Enforce stream items access check if applicable.

        if (doTrue) {
            switchToProfileMode();
            return mProfileProvider.insert(uri, values);
        } else {
            switchToContactMode();
            return super.insert(uri, values);
        }
    }

    boolean doTrue = true;

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (mWriteAccessLatch != null) {
            // We are stuck trying to upgrade contacts db. The only update
            // request
            // allowed in this case is an update of provider status, which will
            // trigger
            // an attempt to upgrade contacts again.
            Integer newStatus = values.getAsInteger(ProviderStatus.STATUS);
            if (newStatus != null && newStatus == ProviderStatus.STATUS_UPGRADING) {
                return 1;
            } else {
                return 0;
            }
        }

        if (doTrue) {
            switchToProfileMode();
            return mProfileProvider.update(uri, values, selection, selectionArgs);
        } else {
            switchToContactMode();
            return super.update(uri, values, selection, selectionArgs);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        waitForAccess(mWriteAccessLatch);

        if (mapsToProfileDb(uri)) {
            switchToProfileMode();
            return mProfileProvider.delete(uri, selection, selectionArgs);
        } else {
            switchToContactMode();
            return super.delete(uri, selection, selectionArgs);
        }
    }

    private void updateSearchIndexInTransaction() {
        Set<Long> staleContacts = mTransactionContext.get().getStaleSearchIndexContactIds();
        Set<Long> staleRawContacts = mTransactionContext.get().getStaleSearchIndexRawContactIds();
        if (!staleContacts.isEmpty() || !staleRawContacts.isEmpty()) {
            mTransactionContext.get().clearSearchIndexUpdates();
        }
    }

    private void flushTransactionalChanges() {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "flushTransactionChanges");
        }
        mSb.setLength(0);
        mSb.append(")");
        mActiveDb.get().execSQL(mSb.toString());

        mTransactionContext.get().clear();
    }

    /**
     * During intialization, this content provider will block all attempts to
     * change contacts data. In particular, it will hold up all contact syncs.
     * As soon as the import process is complete, all processes waiting to write
     * to the provider are unblocked and can proceed to compete for the database
     * transaction monitor.
     */
    private void waitForAccess(CountDownLatch latch) {
        if (latch == null) {
            return;
        }

        while (true) {
            try {
                latch.await();
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onRollback() {
        // TODO Not used.
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        // TODO not used

    }

    @Override
    protected SQLiteOpenHelper getDatabaseHelper(Context context) {
        return ContactsDatabaseHelper.getInstance(context);
    }

    @Override
    protected ThreadLocal<ContactsTransaction> getTransactionHolder() {
        return mTransactionHolder;
    }

    @Override
    protected Uri insertInTransaction(Uri uri, ContentValues values) {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "insertInTransaction: " + uri + " " + values);
        }

        // Default active DB to the contacts DB if none has been set.
        if (mActiveDb.get() == null) {
            mActiveDb.set(mContactsHelper.getWritableDatabase());
        }

        final int match = sUriMatcher.match(uri);
        long id = 0;
        // to insert data
        switch (match) {
            case 1:
                id = insertStreamItem(uri, values);
                break;
            default:
        }

        if (id < 0) {
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Inserts an item in the stream_item_photos table. The account is checked
     * against the account in the raw contact that owns the stream item being
     * modified.
     * 
     * @param uri the insertion URI
     * @param values the values for the new row
     * @return the stream item photo _ID of the newly created row, or 0 if there
     *         was an issue with processing the photo or creating the row
     */
    private long insertStreamItem(Uri uri, ContentValues values) {
        long id = 0;
        mValues.putAll(values);

        // Insert the stream item photo.
        id = mActiveDb.get().insert("photo", null, mValues);
        return id;
    }

    @Override
    protected int deleteInTransaction(Uri uri, String selection, String[] selectionArgs) {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "deleteInTransaction: " + uri);
        }

        // Default active DB to the contacts DB if none has been set.
        if (mActiveDb.get() == null) {
            mActiveDb.set(mContactsHelper.getWritableDatabase());
        }

        flushTransactionalChanges();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case 1:
            case 2:
            case 3:
                return deleteContact(1, true);
        }
        return 0;
    }

    private int deleteContact(long contactId, boolean callerIsSyncAdapter) {
        Cursor c = mActiveDb.get().query("contact", new String[] {
                RawContacts._ID
        },
                RawContacts.CONTACT_ID + "=?", new String[] {},
                null, null, null);
        try {
            while (c.moveToNext()) {
                long rawContactId = c.getLong(0);
                // markRawContactAsDeleted(rawContactId, callerIsSyncAdapter);
            }
        } finally {
            c.close();
        }

        mProviderStatusUpdateNeeded = true;

        return mActiveDb.get().delete("contact", Contacts._ID + "=" + contactId, null);
    }

    @Override
    protected int updateInTransaction(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        if (VERBOSE_LOGGING) {
            Log.v(TAG, "updateInTransaction: " + uri);
        }

        // Default active DB to the contacts DB if none has been set.
        if (mActiveDb.get() == null) {
            mActiveDb.set(mContactsHelper.getWritableDatabase());
        }

        final int match = sUriMatcher.match(uri);
        if (selection == null) {
            long rowId = ContentUris.parseId(uri);
            Object data = values.get(ContactsContract.SyncState.DATA);
            mTransactionContext.get().syncStateUpdated(rowId, data);
            return 1;
        }
        flushTransactionalChanges();
        switch (match) {
            case 1:
            case 2:
                return updateStreamItemPhotos(uri, values, selection, selectionArgs);
        }
        return 0;
    }

    private int updateStreamItemPhotos(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // Stream item photos can't be moved to a new stream item.

        // Don't attempt to update accounts params - they don't exist in the
        // stream item
        // photos table.
        values.remove(RawContacts.ACCOUNT_NAME);
        values.remove(RawContacts.ACCOUNT_TYPE);

        // Process the photo (since we're updating, it's valid for the photo to
        // not be present).
        // If there's been no exception, the update should be fine.
        return mActiveDb.get().update("photo", values, selection,
                selectionArgs);
    }

    @Override
    protected boolean yield(ContactsTransaction transaction) {
        // If there's a profile transaction in progress, and we're yielding, we
        // need to
        // end it. Unlike the Contacts DB yield (which re-starts a transaction
        // at its
        // conclusion), we can just go back into a state in which we have no
        // active
        // profile transaction, and let it be re-created as needed. We can't
        // hold onto
        // the transaction without risking a deadlock.
        SQLiteDatabase profileDb = transaction.removeDbForTag(PROFILE_DB_TAG);
        if (profileDb != null) {
            profileDb.setTransactionSuccessful();
            profileDb.endTransaction();
        }

        // Now proceed with the Contacts DB yield.
        SQLiteDatabase contactsDb = transaction.getDbForTag(CONTACTS_DB_TAG);
        return contactsDb != null && contactsDb.yieldIfContendedSafely(SLEEP_AFTER_YIELD_DELAY);
    }

    protected void notifyChange(boolean syncToNetwork) {
        getContext().getContentResolver().notifyChange(ContactsContract.AUTHORITY_URI, null,
                syncToNetwork);

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        waitForAccess(mReadAccessLatch);

        // Enforce stream items access check if applicable.
        enforceSocialStreamReadPermission(uri);

        // Query the profile DB if appropriate.
        if (mapsToProfileDb(uri)) {
            switchToProfileMode();
            return mProfileProvider.query(uri, projection, selection, selectionArgs, sortOrder);
        }

        // Otherwise proceed with a normal query against the contacts DB.
        switchToContactMode();
        mActiveDb.set(mContactsHelper.getReadableDatabase());

        Builder builder = new Uri.Builder();
        builder.scheme(ContentResolver.SCHEME_CONTENT);

        Uri directoryUri = builder.build();

        Cursor cursor = getContext().getContentResolver().query(directoryUri, projection,
                selection,
                selectionArgs, sortOrder);

        if (cursor == null) {
            return null;
        }

        return matrixCursorFromCursor(cursor);

    }

    public MatrixCursor matrixCursorFromCursor(Cursor cursor) {
        MatrixCursor newCursor = new MatrixCursor(cursor.getColumnNames());
        int numColumns = cursor.getColumnCount();
        String data[] = new String[numColumns];
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            for (int i = 0; i < numColumns; i++) {
                data[i] = cursor.getString(i);
            }
            newCursor.addRow(data);
        }
        return newCursor;
    }

    /**
     * A fast re-implementation of {@link Uri#getQueryParameter}
     */
    /* package */static String getQueryParameter(Uri uri, String parameter) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }

        int queryLength = query.length();
        int parameterLength = parameter.length();

        String value;
        int index = 0;
        while (true) {
            index = query.indexOf(parameter, index);
            if (index == -1) {
                return null;
            }

            // Should match against the whole parameter instead of its suffix.
            // e.g. The parameter "param" must not be found in "some_param=val".
            if (index > 0) {
                char prevChar = query.charAt(index - 1);
                if (prevChar != '?' && prevChar != '&') {
                    // With "some_param=val1&param=val2", we should find second
                    // "param" occurrence.
                    index += parameterLength;
                    continue;
                }
            }

            index += parameterLength;

            if (queryLength == index) {
                return null;
            }

            if (query.charAt(index) == '=') {
                index++;
                break;
            }
        }

        int ampIndex = query.indexOf('&', index);
        if (ampIndex == -1) {
            value = query.substring(index);
        } else {
            value = query.substring(index, ampIndex);
        }

        return Uri.decode(value);
    }

    /**
     * Switches the provider's thread-local context variables to prepare for
     * performing a contacts operation.
     */
    protected void switchToContactMode() {
        mDbHelper.set(mContactsHelper);
        mTransactionContext.set(mContactTransactionContext);
        mAggregator.set(mContactAggregator);
        mInProfileMode.set(false);

        // Clear out the active database; modification operations will set this
        // to the contacts DB.
        mActiveDb.set(null);
    }

    private boolean mapsToProfileDb(Uri uri) {
        return false;
    }

    private void enforceSocialStreamReadPermission(Uri uri) {
        getContext().enforceCallingOrSelfPermission(
                "android.permission.READ_SOCIAL_STREAM", null);
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isValidPreAuthorizedUri(Uri uri) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Replaces the current (thread-local) database to use for the operation
     * with the given one.
     * 
     * @param db The database to use.
     */
    /* package */void substituteDb(SQLiteDatabase db) {
        mActiveDb.set(db);
    }

    public Cursor queryLocal(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, int i) {
        // TODO Auto-generated method stub
        return null;
    }

    public AssetFileDescriptor openAssetFileLocal(Uri uri, String mode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void notifyChange() {
        notifyChange(mSyncToNetwork);
        mSyncToNetwork = false;

    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * Switches the provider's thread-local context variables to prepare for
     * performing a profile operation.
     */
    protected void switchToProfileMode() {
        mDbHelper.set(mProfileHelper);
        mTransactionContext.set(mProfileTransactionContext);
        mAggregator.set(mProfileAggregator);
        mInProfileMode.set(true);
    }
}
