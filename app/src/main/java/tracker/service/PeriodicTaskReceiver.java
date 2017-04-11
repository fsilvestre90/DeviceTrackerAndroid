package tracker.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import java.util.concurrent.TimeUnit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import tracker.TrackerApplication;
import tracker.data.model.DevicePayload;
import tracker.util.Constants;

public class PeriodicTaskReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Timber.d("Alarm received: %s", intent.toString());
    if (intent.getAction() != null && !intent.getAction().isEmpty()) {
      TrackerApplication myApplication = (TrackerApplication) context.getApplicationContext();
      SharedPreferences sharedPreferences =
          PreferenceManager.getDefaultSharedPreferences(myApplication);

      if (intent.getAction().equals("android.intent.action.BATTERY_LOW")) {
        // Battery is low. Stopping Heartbeat.
        sharedPreferences.edit()
            .putBoolean(Constants.BACKGROUND_SERVICE_BATTERY_CONTROL, false)
            .apply();
        stopPeriodicTaskHeartBeat(context);
      } else if (intent.getAction().equals("android.intent.action.BATTERY_OKAY")) {
        // Restarting heartbeat
        sharedPreferences.edit()
            .putBoolean(Constants.BACKGROUND_SERVICE_BATTERY_CONTROL, true)
            .apply();
        restartPeriodicTaskHeartBeat(context);
      } else if (intent.getAction().equals(Constants.INTENT_ACTION)) {
        // Begin periodic tasks
        doPeriodicTask(myApplication);
      }
    }
  }

  private void doPeriodicTask(TrackerApplication myApplication) {
    // Do your work in here
    TrackerApplication.get(myApplication).getComponent().locationService().startLocationTracking();
    Observable.interval(30, TimeUnit.SECONDS, Schedulers.io())
        .map(tick -> {
          Timber.d("Sending location data to API...");
          DevicePayload data = new DevicePayload(TrackerApplication.get(myApplication).getComponent().databaseHelper().getDeviceSnapshot());
          TrackerApplication.get(myApplication).getComponent().remoteService().sendDeviceData(data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
              Timber.d("Success");
              TrackerApplication.get(myApplication).getComponent().databaseHelper().clearDb();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
              Timber.d("Failed");
            }
          });
          return Observable.empty();
        })
        .doOnError(err -> Timber.d("Error sending to API")).subscribe();
  }

  public void restartPeriodicTaskHeartBeat(Context context) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    boolean isBatteryOk =
        sharedPreferences.getBoolean(Constants.BACKGROUND_SERVICE_BATTERY_CONTROL, true);
    Intent alarmIntent = new Intent(context, PeriodicTaskReceiver.class);
    boolean isAlarmUp =
        PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null;

    // Check the battery levels and if our alarm is up to restart heartbeat
    if (isBatteryOk && !isAlarmUp) {
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      alarmIntent.setAction(Constants.INTENT_ACTION);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
      alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
          SystemClock.elapsedRealtime(), Constants.UPDATE_INTERVAL_IN_MILLISECONDS, pendingIntent);
    }
  }

  public void stopPeriodicTaskHeartBeat(Context context) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent alarmIntent = new Intent(context, PeriodicTaskReceiver.class);
    alarmIntent.setAction(Constants.INTENT_ACTION);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
    alarmManager.cancel(pendingIntent);
  }
}
