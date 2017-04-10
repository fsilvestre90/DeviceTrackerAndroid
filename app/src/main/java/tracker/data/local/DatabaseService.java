package tracker.data.local;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.Settings.Secure;
import android.util.Log;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import nl.qbusict.cupboard.QueryResultIterable;
import tracker.data.model.DeviceSnapshot;
import tracker.injection.ApplicationContext;

public class DatabaseService extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "device_location.db";
  private static final int DATABASE_VERSION = 1;
  private static String DEVICE_ID;

  private final Bus mBus;

  static {
    // register our models
    cupboard().register(DeviceSnapshot.class);
  }


  @Inject
  public DatabaseService(@ApplicationContext Context context, Bus bus) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    mBus = bus;
    bus.register(this);
    DEVICE_ID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // this will ensure that all tables are created
    cupboard().withDatabase(db).createTables();
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // this will upgrade tables, adding columns and new tables.
    cupboard().withDatabase(db).upgradeTables();
  }

  @Subscribe
  public void saveLocationUpdate(Location location) {
    Log.d("DatabaseService", String.format("Saving location to db...", location.getLatitude(), location.getLongitude()));
    cupboard().withDatabase(getWritableDatabase()).put(DeviceSnapshot.create(DEVICE_ID, location.getLatitude(), location.getLongitude(),
        Calendar.getInstance().getTimeInMillis()));
  }

  public List<DeviceSnapshot> getDeviceSnapshot() {
    final QueryResultIterable<DeviceSnapshot> iter = cupboard().withDatabase(getReadableDatabase())
        .query(DeviceSnapshot.class).query();
    final List<DeviceSnapshot> deviceHistory = new ArrayList<>();
    for (DeviceSnapshot snapshot : iter) {
      deviceHistory.add(snapshot);
    }
    iter.close();
    // since we are uploading device information over a given time, clear the past snapshots
    cupboard().withDatabase(getWritableDatabase()).delete(DeviceSnapshot.class);
    return deviceHistory;
  }

}