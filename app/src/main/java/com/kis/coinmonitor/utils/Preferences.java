package com.kis.coinmonitor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Preferences {

    private static SharedPreferences sSharedPref;
    private static Preferences sInstance;

    private static final String KEY_SETTING_FAVOURITE_COINS = "Settings.FavouriteCoins";
    public static String getKeySettingFavouriteCoins() {
        return KEY_SETTING_FAVOURITE_COINS;
    }

    private Preferences(Context context) {
        sSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static Preferences getInstance(@NonNull Context context) {
        Preferences instance = sInstance;
        if (instance == null) {
            synchronized (Preferences.class) {
                if (sInstance == null) {
                    instance = new Preferences(context);
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sSharedPref.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sSharedPref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void writeSet(String key, Set<String> values) {
        SharedPreferences.Editor prefsEditor = sSharedPref.edit();
        prefsEditor.putStringSet(key, values);
        prefsEditor.apply();
    }

    public Set<String> readSet(String key) {
        return new HashSet(sSharedPref.getStringSet(key, new HashSet()));
    }

    public void removeSetting(String key) {
        SharedPreferences.Editor prefsEditor = sSharedPref.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }
}
