package tracker.data.local;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.queries.Query;

class DeviceTable {

  @NonNull
  public static final String TABLE = "device_snapshot";

  @NonNull
  public static final String COLUMN_DEVICE_ID = "device_id";

  @NonNull
  public static final String COLUMN_LATITUDE = "latitude";

  @NonNull
  public static final String COLUMN_LONGITUDE = "longitude";

  @NonNull
  public static final String COLUMN_TIMESTAMP = "timestamp";

  public static final String COLUMN_DEVICE_ID_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_DEVICE_ID;
  public static final String COLUMN_LATITUDE_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_LATITUDE;
  public static final String COLUMN_LONGITUDE_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_LONGITUDE;
  public static final String COLUMN_TIMESTAMP_WITH_TABLE_PREFIX = TABLE + "." + COLUMN_TIMESTAMP;

  @NonNull
  public static final Query QUERY_ALL = Query.builder()
      .table(TABLE)
      .build();

  private DeviceTable() {
    throw new IllegalStateException("No instances please");
  }

  @NonNull
  public static String getCreateTableQuery() {
    return "CREATE TABLE " + TABLE + "("
        + COLUMN_TIMESTAMP + " LONG NOT NULL PRIMARY KEY, "
        + COLUMN_DEVICE_ID + " TEXT NOT NULL, "
        + COLUMN_LATITUDE + " DOUBLE NOT NULL,"
        + COLUMN_LONGITUDE + " DOUBLE NOT NULL"
        + ");";
  }
}
