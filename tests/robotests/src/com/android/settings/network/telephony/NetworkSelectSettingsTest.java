/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * limitations under the License.
 */

package com.android.settings.network.telephony;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class NetworkSelectSettingsTest {
    private static final int SUB_ID = 2;

    @Mock
    private TelephonyManager mTelephonyManager;
    @Mock
    private SubscriptionManager mSubscriptionManager;
    @Mock
    private CellInfo mCellInfo1;
    @Mock
    private CellInfo mCellInfo2;
    @Mock
    private PreferenceManager mPreferenceManager;
    private Context mContext;

    private PreferenceCategory mConnectedPreferenceCategory;
    private PreferenceCategory mPreferenceCategory;

    private NetworkSelectSettings mNetworkSelectSettings;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mContext = spy(RuntimeEnvironment.application);
        when(mContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mTelephonyManager);
        when(mContext.getSystemService(SubscriptionManager.class)).thenReturn(mSubscriptionManager);
        when(mTelephonyManager.createForSubscriptionId(SUB_ID)).thenReturn(mTelephonyManager);

        when(mCellInfo1.isRegistered()).thenReturn(true);
        when(mCellInfo2.isRegistered()).thenReturn(false);

        mConnectedPreferenceCategory = spy(new PreferenceCategory(mContext));
        doReturn(mPreferenceManager).when(mConnectedPreferenceCategory).getPreferenceManager();
        mPreferenceCategory = spy(new PreferenceCategory(mContext));
        doReturn(mPreferenceManager).when(mPreferenceCategory).getPreferenceManager();

        mNetworkSelectSettings = spy(new NetworkSelectSettings());
        doReturn(mContext).when(mNetworkSelectSettings).getContext();
        doReturn(mPreferenceManager).when(mNetworkSelectSettings).getPreferenceManager();
        doReturn(mContext).when(mPreferenceManager).getContext();

        mNetworkSelectSettings.mTelephonyManager = mTelephonyManager;
        mNetworkSelectSettings.mConnectedPreferenceCategory = mConnectedPreferenceCategory;
        mNetworkSelectSettings.mPreferenceCategory = mPreferenceCategory;
        mNetworkSelectSettings.mCellInfoList = Arrays.asList(mCellInfo1, mCellInfo2);
    }

    @Test
    public void updateAllPreferenceCategory_containCorrectPreference() {
        mNetworkSelectSettings.updateAllPreferenceCategory();

        assertThat(mConnectedPreferenceCategory.getPreferenceCount()).isEqualTo(1);
        final NetworkOperatorPreference connectedPreference =
                (NetworkOperatorPreference) mConnectedPreferenceCategory.getPreference(0);
        assertThat(connectedPreference.getCellInfo()).isEqualTo(mCellInfo1);
        assertThat(mPreferenceCategory.getPreferenceCount()).isEqualTo(1);
        final NetworkOperatorPreference preference =
                (NetworkOperatorPreference) mPreferenceCategory.getPreference(0);
        assertThat(preference.getCellInfo()).isEqualTo(mCellInfo2);
    }

    @Test
    public void updateForbiddenPlmns_forbiddenPlmnsNull_shouldNotCrash() {
        when(mTelephonyManager.getForbiddenPlmns()).thenReturn(null);

        // Should not Crash
        mNetworkSelectSettings.updateForbiddenPlmns();
    }
}
