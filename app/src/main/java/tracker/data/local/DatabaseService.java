package tracker.data.local;

import android.content.Context;
import android.location.Location;
import android.provider.Settings.Secure;
import android.util.Log;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import javax.inject.Inject;
import tracker.data.model.DeviceSnapshot;
import tracker.injection.ApplicationContext;

public class DatabaseService {

  private static String DEVICE_ID;

  private final Bus mBus;
  private final StorIOSQLite mStoreSqlLite;

  @Inject
  public DatabaseService(@ApplicationContext Context context, StorIOSQLite storIOSQLite, Bus bus) {
    mBus = bus;
    mStoreSqlLite = storIOSQLite;
    bus.register(this);
    DEVICE_ID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
  }

  @Subscribe
  public void saveLocationUpdate(Location location) {
    Log.d("DatabaseService",
        String.format("Saving location to db...", location.getLatitude(), location.getLongitude()));
    mStoreSqlLite
        .put()
        .object(DeviceSnapshot.create(DEVICE_ID, location.getLatitude(), location.getLongitude(),
            System.currentTimeMillis()))
        .prepare()
        .executeAsBlocking();
  }

  public List<DeviceSnapshot> getDeviceSnapshot() {
    return mStoreSqlLite
        .get()
        .listOfObjects(DeviceSnapshot.class)
        .withQuery(Query.builder()
            .table("device_snapshot")
            .build())
        .prepare()
        .executeAsBlocking();
  }

  public void clearDb() {
    mStoreSqlLite
        .delete()
        .byQuery(DeleteQuery.builder()
            .table("device_snapshot")
            .where("timestamp <= ?")
            .whereArgs(System.currentTimeMillis() - 86400)
            .build())
        .prepare()
        .executeAsBlocking();
  }

}