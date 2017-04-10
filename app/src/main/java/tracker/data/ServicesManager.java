package tracker.data;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import tracker.data.local.DatabaseService;
import tracker.data.location.LocationService;
import tracker.data.remote.RemoteService;

public class ServicesManager {

  private final RemoteService mRemoteService;
  private final DatabaseService mDatabaseService;
  private final LocationService mLocationService;

  @Inject
  public ServicesManager(RemoteService remoteService, DatabaseService databaseService, LocationService locationService) {
    mRemoteService = remoteService;
    mDatabaseService = databaseService;
    mLocationService = locationService;
  }

  public void startApiPoller() {
    Observable.interval(30, TimeUnit.SECONDS, Schedulers.io())
        .map(tick -> {
          Timber.d("Sending location data to API...");
          return mRemoteService.sendDeviceData(mDatabaseService.getDeviceSnapshot());
        })
        .doOnError(err -> Timber.d("Error sending to API")).subscribe();
  }

  public void startDeviceTracking() {
    mLocationService.startLocationTracking();
  }
}
