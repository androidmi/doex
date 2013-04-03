/*
 * Copyright (C) 2010 The Android Open Source Project
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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Accumulates information for an entire transaction. {@link ContactsProvider2}
 * consumes it at commit time.
 */
public class TransactionContext {

    private final boolean mForProfile;
    private HashSet<Long> mUpdatedRawContacts = Sets.newHashSet();
    private HashSet<Long> mDirtyRawContacts = Sets.newHashSet();
    private HashSet<Long> mStaleSearchIndexRawContacts = Sets.newHashSet();
    private HashSet<Long> mStaleSearchIndexContacts = Sets.newHashSet();
    private HashMap<Long, Object> mUpdatedSyncStates = Maps.newHashMap();

    public TransactionContext(boolean forProfile) {
        mForProfile = forProfile;
    }

    public boolean isForProfile() {
        return mForProfile;
    }

    public void rawContactUpdated(long rawContactId) {
        mUpdatedRawContacts.add(rawContactId);
    }

    public void markRawContactDirty(long rawContactId) {
        mDirtyRawContacts.add(rawContactId);
    }

    public void syncStateUpdated(long rowId, Object data) {
        mUpdatedSyncStates.put(rowId, data);
    }

    public void invalidateSearchIndexForRawContact(long rawContactId) {
        mStaleSearchIndexRawContacts.add(rawContactId);
    }

    public void invalidateSearchIndexForContact(long contactId) {
        mStaleSearchIndexContacts.add(contactId);
    }

    public Set<Long> getUpdatedRawContactIds() {
        return mUpdatedRawContacts;
    }

    public Set<Long> getDirtyRawContactIds() {
        return mDirtyRawContacts;
    }

    public Set<Long> getStaleSearchIndexRawContactIds() {
        return mStaleSearchIndexRawContacts;
    }

    public Set<Long> getStaleSearchIndexContactIds() {
        return mStaleSearchIndexContacts;
    }

    public Set<Entry<Long, Object>> getUpdatedSyncStates() {
        return mUpdatedSyncStates.entrySet();
    }

    public void clear() {
        mUpdatedRawContacts.clear();
        mUpdatedSyncStates.clear();
        mDirtyRawContacts.clear();
    }

    public void clearSearchIndexUpdates() {
        mStaleSearchIndexRawContacts.clear();
        mStaleSearchIndexContacts.clear();
    }
}
