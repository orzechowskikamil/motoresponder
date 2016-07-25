package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract public class SettingsBase {

    private SharedPreferencesUtility sharedPreferencesUtility;
    private HashMap<String, List<Predicate<Boolean>>> callbacksMap;
    private boolean isListeningToSharedPreferencesChanges = false;


    public SettingsBase(SharedPreferencesUtility sharedPreferencesUtility) {
        this.sharedPreferencesUtility = sharedPreferencesUtility;
        this.startListening();
        this.callbacksMap = new HashMap<>();
    }

    public void listenToSettingChange(String settingName, Predicate<Boolean> callback) {
        if (!this.isListeningToSharedPreferencesChanges) {
            throw new RuntimeException("Not listening to shared prefs, no sense of listening to properties");
        }
        List<Predicate<Boolean>> callbackList = this.getCallbacksListForSetting(settingName);
        callbackList.add(callback);
    }

    public void stopListeningToSetting(String settingName, Predicate<Boolean> callback) {
        List<Predicate<Boolean>> callbackList = this.getCallbacksListForSetting(settingName);
        callbackList.remove(callback);
    }

    public String getStringFromRes(int resID) {
        return this.sharedPreferencesUtility.getStringFromRes(resID);
    }

    protected List<Predicate<Boolean>> getCallbacksListForSetting(String settingName) {
        if (!this.callbacksMap.containsKey(settingName)) {
            this.callbacksMap.put(settingName, new ArrayList<Predicate<Boolean>>());
        }

        return this.callbacksMap.get(settingName);
    }

    protected void onSharedPreferenceChanged(String changedKey) {
        try {
            for (Predicate<Boolean> callback : this.getCallbacksListForSetting(changedKey)) {
                if (callback != null) {
                    callback.apply(true);
                }
            }
        } catch (Exception e) {
        }
    }

    protected String getStringValue(int resID) {
        return this.getStringValue(this.getStringFromRes(resID));
    }

    protected String getStringValue(String name) {
        return this.sharedPreferencesUtility.getStringValue(name, this.getDefaultStringValue(name));
    }

    protected void setStringValue(int resID, String value) {
        this.sharedPreferencesUtility.setStringValue(this.getStringFromRes(resID), value);
    }

    protected boolean getBooleanValue(int resID) {
        return this.getBooleanValue(this.getStringFromRes(resID));
    }

    protected boolean getBooleanValue(String name) {
        return this.sharedPreferencesUtility.getBooleanValue(name, this.getDefaultBooleanValue(name));
    }

    protected void setBooleanValue(int resID, boolean value) {
        this.sharedPreferencesUtility.setBooleanValue(this.getStringFromRes(resID), value);
    }

    protected int getIntValue(int resID) {
        return this.getIntValue(this.getStringFromRes(resID));
    }

    protected void setIntValue(int resID, int value) {
        this.sharedPreferencesUtility.setIntValue(this.getStringFromRes(resID), value);
    }

    protected int getIntValue(String name) {
        return this.sharedPreferencesUtility.getIntValue(name, this.getDefaultIntValue(name));
    }

    protected String getDefaultStringValue(String name) {
        return this.sharedPreferencesUtility.getStringFromRes(this.getDefaultValueName(name));
    }

    protected int getDefaultIntValue(String name) {
        return this.sharedPreferencesUtility.getIntegerFromRes(this.getDefaultValueName(name));
    }

    protected boolean getDefaultBooleanValue(String name) {
        return this.sharedPreferencesUtility.getBooleanFromRes(this.getDefaultValueName(name));
    }

    protected String getDefaultValueName(String name) {
        return name + "_default_value";
    }

    private void stopListening() {
        this.sharedPreferencesUtility.stopListeningToSharedPreferenceChanged();
        this.isListeningToSharedPreferencesChanges = false;
    }

    private void startListening() {
        this.sharedPreferencesUtility.listenToSharedPreferenceChanged(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String changedKey) {
                SettingsBase.this.onSharedPreferenceChanged(changedKey);
                return false;
            }
        });
        this.isListeningToSharedPreferencesChanges = true;
    }

}
