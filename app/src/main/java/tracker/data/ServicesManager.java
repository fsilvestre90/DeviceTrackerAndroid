package tracker.data;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import tracker.data.local.DatabaseService;
import tracker.data.location.LocationService;
import tracker.data.model.DevicePayload;
import tracker.data.model.DeviceSnapshot;
import tracker.data.remote.RemoteService;

/**
 * ServicesManager orchestrates all things related to data
 */
public class ServicesManager {

  private final RemoteService mRemoteService;
  private final DatabaseService mDatabaseService;
  private final LocationService mLocationService;

  @Inject
  public ServicesManager(RemoteService remoteService, DatabaseService databaseService,
      LocationService locationService) {
    mRemoteService = remoteService;
    mDatabaseService = databaseService;
    mLocationService = locationService;
  }

  public void startApiPoller() {
    Observable.interval(30, TimeUnit.SECONDS, Schedulers.io())
        .map(tick -> {
          Timber.d("Sending location data to API...");
          DevicePayload data = new DevicePayload(mDatabaseService.getDeviceSnapshot());
          mRemoteService.sendDeviceData(data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
              Timber.d("Success");
              mDatabaseService.clearDb();
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

  public void startDeviceTracking() {
    mLocationService.startLocationTracking();
  }

  public List<DeviceSnapshot> get() {
    return mDatabaseService.getDeviceSnapshot();
  }
}
