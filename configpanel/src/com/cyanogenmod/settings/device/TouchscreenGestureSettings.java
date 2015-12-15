/*
 * Copyright (C) 2015 The CyanogenMod Project
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

package com.cyanogenmod.settings.device;

import com.cyanogenmod.settings.device.utils.NodePreferenceActivity;

import org.cyanogenmod.internal.util.ScreenType;

import android.os.Bundle;
import android.provider.Settings;
import android.preference.Preference;
import android.preference.SwitchPreference;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.preference.ListPreference;

import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import java.util.List;
import java.util.ArrayList;
import android.util.Log;
import com.cyanogenmod.settings.device.utils.FileUtils;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

public class TouchscreenGestureSettings extends NodePreferenceActivity {
	
	private static final String KEY_HAPTIC_FEEDBACK = "touchscreen_gesture_haptic_feedback";
    
    private static final String KEY_CAMERA_LAUNCH_INTENT = 
			"touchscreen_gesture_camera_launch_intent";
    private static final String PROP_CAMERA_LAUNCH_INTENT = "persist.gestures.camera";
    
    private static final String KEY_TORCH_LAUNCH_INTENT = "touchscreen_gesture_torch_launch_intent";
    private static final String PROP_TORCH_LAUNCH_INTENT = "persist.gestures.torch";
    
    private static final String KEY_PLAY_PAUSE_LAUNCH_INTENT = 
			"touchscreen_gesture_play_pause_launch_intent";
    private static final String PROP_PLAY_PAUSE_LAUNCH_INTENT = "persist.gestures.play";
    
    private static final String KEY_PREVIOUS_LAUNCH_INTENT = 
			"touchscreen_gesture_previous_launch_intent";
    private static final String PROP_PREVIOUS_LAUNCH_INTENT = "persist.gestures.previous";
    
    private static final String KEY_NEXT_LAUNCH_INTENT = "touchscreen_gesture_next_launch_intent";
    private static final String PROP_NEXT_LAUNCH_INTENT = "persist.gestures.next";
    
    private static final String GESTURE_FILE_LOCATION = 
			"/data/data/com.cyanogenmod.settings.device/";
			
    private SwitchPreference mHapticFeedback;
    private ListPreference mCameraLaunchIntent;
    private ListPreference mTorchLaunchIntent;
    private ListPreference mPlayPauseLaunchIntent;
    private ListPreference mPreviousLaunchIntent;
    private ListPreference mNextLaunchIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.touchscreen_panel);
        
        List<String> listPackageNames = getPackageNames();        
        final CharSequence[] packageNames = 
				listPackageNames.toArray(new CharSequence[listPackageNames.size()]);
		final CharSequence[] hrblPackageNames = new CharSequence[listPackageNames.size()];
		hrblPackageNames[0] = "Default action";
		
		for(int i = 1; i < listPackageNames.size(); i++){
			 hrblPackageNames[i] = getAppnameFromPackagename(listPackageNames.get(i));
		}		
		
		mHapticFeedback = (SwitchPreference) findPreference(KEY_HAPTIC_FEEDBACK);
        mHapticFeedback.setOnPreferenceChangeListener(this);                
        
        mCameraLaunchIntent = (ListPreference) findPreference(KEY_CAMERA_LAUNCH_INTENT);
        mCameraLaunchIntent.setEntries(hrblPackageNames);
        mCameraLaunchIntent.setEntryValues(packageNames);
        mCameraLaunchIntent.setSummary(getAppnameFromPackagename(FileUtils.readOneLine(
				GESTURE_FILE_LOCATION + PROP_CAMERA_LAUNCH_INTENT)));  
        mCameraLaunchIntent.setOnPreferenceChangeListener(this);
        
        mTorchLaunchIntent = (ListPreference) findPreference(KEY_TORCH_LAUNCH_INTENT);
        mTorchLaunchIntent.setEntries(hrblPackageNames);
        mTorchLaunchIntent.setEntryValues(packageNames);
        mTorchLaunchIntent.setSummary(getAppnameFromPackagename(FileUtils.readOneLine(
				GESTURE_FILE_LOCATION + PROP_TORCH_LAUNCH_INTENT)));     
        mTorchLaunchIntent.setOnPreferenceChangeListener(this);
        
        mPlayPauseLaunchIntent = (ListPreference) findPreference(KEY_PLAY_PAUSE_LAUNCH_INTENT);
        mPlayPauseLaunchIntent.setEntries(hrblPackageNames);
        mPlayPauseLaunchIntent.setEntryValues(packageNames);
        mPlayPauseLaunchIntent.setSummary(getAppnameFromPackagename(FileUtils.readOneLine(
				GESTURE_FILE_LOCATION + PROP_PLAY_PAUSE_LAUNCH_INTENT)));     
        mPlayPauseLaunchIntent.setOnPreferenceChangeListener(this);
        
        mPreviousLaunchIntent = (ListPreference) findPreference(KEY_PREVIOUS_LAUNCH_INTENT);
        mPreviousLaunchIntent.setEntries(hrblPackageNames);
        mPreviousLaunchIntent.setEntryValues(packageNames);
        mPreviousLaunchIntent.setSummary(getAppnameFromPackagename(FileUtils.readOneLine(
			GESTURE_FILE_LOCATION + PROP_PREVIOUS_LAUNCH_INTENT)));     
        mPreviousLaunchIntent.setOnPreferenceChangeListener(this);
        
        mNextLaunchIntent = (ListPreference) findPreference(KEY_NEXT_LAUNCH_INTENT);
        mNextLaunchIntent.setEntries(hrblPackageNames);
        mNextLaunchIntent.setEntryValues(packageNames);
        mNextLaunchIntent.setSummary(getAppnameFromPackagename(FileUtils.readOneLine(
				GESTURE_FILE_LOCATION + PROP_NEXT_LAUNCH_INTENT)));     
        mNextLaunchIntent.setOnPreferenceChangeListener(this);
	}
          
	@Override 
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		final String key = preference.getKey();
        if (KEY_HAPTIC_FEEDBACK.equals(key)) {
            final boolean value = (Boolean) newValue;
            Settings.System.putInt(getContentResolver(), KEY_HAPTIC_FEEDBACK, value ? 1 : 0);
            return true;
        }
		if(KEY_CAMERA_LAUNCH_INTENT.equals(key)){
			final String value = (String) newValue;
			findPreference(KEY_CAMERA_LAUNCH_INTENT).setSummary(getAppnameFromPackagename(value));
			FileUtils.writeLine(GESTURE_FILE_LOCATION + PROP_CAMERA_LAUNCH_INTENT, value);
			return true;
		}
		if(KEY_TORCH_LAUNCH_INTENT.equals(key)){
			final String value = (String) newValue;
			findPreference(KEY_TORCH_LAUNCH_INTENT).setSummary(getAppnameFromPackagename(value));
			FileUtils.writeLine(GESTURE_FILE_LOCATION + PROP_TORCH_LAUNCH_INTENT, value);
			return true;
		}
		if(KEY_PLAY_PAUSE_LAUNCH_INTENT.equals(key)){
			final String value = (String) newValue;
			findPreference(KEY_PLAY_PAUSE_LAUNCH_INTENT).setSummary(
					getAppnameFromPackagename(value));
			FileUtils.writeLine(GESTURE_FILE_LOCATION + PROP_PLAY_PAUSE_LAUNCH_INTENT, value);
			return true;
		}
		if(KEY_PREVIOUS_LAUNCH_INTENT.equals(key)){
			final String value = (String) newValue;
			findPreference(KEY_PREVIOUS_LAUNCH_INTENT).setSummary(getAppnameFromPackagename(value));
			FileUtils.writeLine(GESTURE_FILE_LOCATION + PROP_PREVIOUS_LAUNCH_INTENT, value);
			return true;
		}
		if(KEY_NEXT_LAUNCH_INTENT.equals(key)){
			final String value = (String) newValue;
			findPreference(KEY_NEXT_LAUNCH_INTENT).setSummary(getAppnameFromPackagename(value));
			FileUtils.writeLine(GESTURE_FILE_LOCATION + PROP_NEXT_LAUNCH_INTENT, value);
			return true;
		}
		return super.onPreferenceChange(preference, newValue);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If running on a phone, remove padding around the listview
        if (!ScreenType.isTablet(this)) {
            getListView().setPadding(0, 0, 0, 0);
        }
                
        mHapticFeedback.setChecked(
                Settings.System.getInt(getContentResolver(), KEY_HAPTIC_FEEDBACK, 1) != 0);
    }
    
    private List<String> getPackageNames(){
		List<String> packageNameList = new ArrayList<String>();
		List<PackageInfo> packs = 
			getApplicationContext().getPackageManager().getInstalledPackages(0);
		packageNameList.add("");
		for(int i = 0; i < packs.size(); i++){
			String packageName = packs.get(i).packageName;
			Intent launchIntent = getApplicationContext().getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if(launchIntent != null){
				packageNameList.add(packageName);
			}
		}
		return packageNameList;
	}
	
	private String getAppnameFromPackagename(String packagename){
		if(packagename == null || "".equals(packagename)){
			 return getResources().getString(R.string.touchscreen_action_default);
		}
		final PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo( packagename, 0);
		} catch (final Exception e) {
			ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai) : 
				getResources().getString(R.string.touchscreen_action_unkownappforpackagename));
	}
}
