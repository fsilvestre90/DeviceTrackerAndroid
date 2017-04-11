package tracker.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import timber.log.Timber;
import tracker.TrackerApplication;
import tracker.util.Constants;

public class BackgroundService extends Service {

  PeriodicTaskReceiver mPeriodicTaskReceiver = new PeriodicTaskReceiver();

  /**
   * Start a background thread which continues to save the user location
   * when the app is not in the foreground.
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    TrackerApplication myApplication = (TrackerApplication) getApplicationContext();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(myApplication);
    IntentFilter batteryStatusIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    Intent batteryStatusIntent = registerReceiver(null, batteryStatusIntentFilter);

    // We want to check the battery level before restarting the heartbeat.
    // The service won't continue if juice is too low.
    if (batteryStatusIntent != null) {
      int level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
      int scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
      float batteryPercentage = level / (float) scale;
      float lowBatteryPercentageLevel = 0.14f;

      try {
        int lowBatteryLevel = Resources.getSystem()
            .getInteger(Resources.getSystem()
                .getIdentifier("config_lowBatteryWarningLevel", "integer", "android"));
        lowBatteryPercentageLevel = lowBatteryLevel / (float) scale;
      } catch (Resources.NotFoundException e) {
        Timber.e("Missing low battery threshold resource");
      }

      sharedPreferences.edit()
          .putBoolean(Constants.BACKGROUND_SERVICE_BATTERY_CONTROL,
              batteryPercentage >= lowBatteryPercentageLevel)
          .apply();
    } else {
      sharedPreferences.edit()
          .putBoolean(Constants.BACKGROUND_SERVICE_BATTERY_CONTROL, true)
          .apply();
    }

    mPeriodicTaskReceiver.restartPeriodicTaskHeartBeat(BackgroundService.this);
    return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
