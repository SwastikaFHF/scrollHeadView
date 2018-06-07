package com.aitangba.testproject.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by fhf11991 on 2018/6/7
 */
public class LocationUtil {

    private static final String PREFERENCES_KEY_LOCATION_CACHED = "PreferencesKeyLocationCached";
    private static final String SPLIT_REGEX = ",";

    private static Location sLocation;

    public static Location getCachedLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(PREFERENCES_KEY_LOCATION_CACHED, null);
        if(value == null) {
            return null;
        }

        String[] arr = value.split(SPLIT_REGEX);

        if(arr.length != 2) {
            preferences.edit().remove(PREFERENCES_KEY_LOCATION_CACHED).apply();
            return null;
        }

        try {
            Location location = new Location();
            location.latitude = Double.parseDouble(arr[0]);
            location.longitude = Double.parseDouble(arr[1]);
            return location;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            preferences.edit().remove(PREFERENCES_KEY_LOCATION_CACHED).apply();
            return null;
        }
    }

    public static void setCachedLocation(Context context,@NonNull Location location) {
        setCachedLocation(context, location.latitude, location.longitude);
    }

    public static void setCachedLocation(Context context, double latitude, double longitude) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(PREFERENCES_KEY_LOCATION_CACHED, latitude + SPLIT_REGEX + longitude).apply();
    }


    public static Location getLocation() {
        return sLocation;
    }

    public static void setLocation(double latitude, double longitude) {
        if(sLocation == null) {
            sLocation = new Location();
        }
        sLocation.latitude = latitude;
        sLocation.longitude = longitude;
    }

    public static class Location {
        public double latitude;
        public double longitude;

        public boolean isAvailable() {
            return latitude != 0 && longitude != 0;
        }
    }
}
