package com.plcoding.backgroundlocationtracking;

import android.location.Location;
import android.util.Log;

public class LocationServiceUtils {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final String TAG = LocationServiceUtils.class.getSimpleName();

    static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        Log.d(TAG, " new Location.getTime() :" + location.getTime());
        Log.d(TAG, " currentBestLocation.getTime() :" + currentBestLocation.getTime());
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
//        Log.d(TAG, " timeDelta :" + timeDelta);
//        Log.d(TAG, " TWO_MINUTES :" + TWO_MINUTES);
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());

        Log.d(TAG, " location.getAccuracy() :" + location.getAccuracy());
        Log.d(TAG, " currentBestLocation.getAccuracy() :" + currentBestLocation.getAccuracy());
        Log.d(TAG, " accuracyDelta :" + accuracyDelta);

        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Log.d(TAG, " isBetterLocation true more accurate");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.d(TAG, " isBetterLocation true is newer");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.d(TAG, " isBetterLocation true isSignificantlyLessAccurate");
            return true;
        }
        Log.d(TAG, " isBetterLocation false");
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

   
    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher;
    }


}
