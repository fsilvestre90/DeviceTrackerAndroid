package tracker.data.location;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.squareup.otto.Bus;
import javax.inject.Inject;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class LocationService {

  private final ReactiveLocationProvider mLocationService;
  private final Bus mBus;

  private Observable<Location> locationUpdatesObservable;
  private Subscription locationSubscription;

  @Inject
  public LocationService(ReactiveLocationProvider reactiveLocationProvider, Bus bus) {
    mLocationService = reactiveLocationProvider;
    mBus = bus;
    setupLocationObservable();
  }

  private void setupLocationObservable() {
    final LocationRequest locationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(50);

    locationUpdatesObservable = mLocationService
        .checkLocationSettings(
            new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)
                .build()
        )
        .flatMap(new Func1<LocationSettingsResult, Observable<Location>>() {
          @Override
          public Observable<Location> call(LocationSettingsResult locationSettingsResult) {
            return mLocationService.getUpdatedLocation(locationRequest);
          }
        });
  }

  public void startLocationTracking() {
    locationSubscription = locationUpdatesObservable
        .doOnNext(location -> {
          Log.d("LocationService", "Location Event received");
          mBus.post(location);
        })
        .subscribe();
  }
}
