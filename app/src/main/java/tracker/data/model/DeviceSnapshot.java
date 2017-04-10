package tracker.data.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteCreator;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

@AutoValue
@StorIOSQLiteType(table = "device_snapshot")
public abstract class DeviceSnapshot implements Parcelable {

  public static Builder builder() {
    return new AutoValue_DeviceSnapshot.Builder();
  }

  public static TypeAdapter<DeviceSnapshot> typeAdapter(Gson gson) {
    return new AutoValue_DeviceSnapshot.GsonTypeAdapter(gson);
  }

  @StorIOSQLiteCreator
  public static DeviceSnapshot create(String deviceId, double lat, double lng, long timestamp) {
    return new AutoValue_DeviceSnapshot(deviceId, lat, lng, timestamp);
  }

  @StorIOSQLiteColumn(name = "device_id")
  @Nullable
  @SerializedName("device_id")
  public abstract String deviceId();

  @StorIOSQLiteColumn(name = "latitude")
  @SerializedName("latitude")
  public abstract double lat();

  @StorIOSQLiteColumn(name = "longitude")
  @SerializedName("longitude")
  public abstract double lng();

  @StorIOSQLiteColumn(name = "timestamp", key = true)
  @SerializedName("timestamp")
  public abstract long timestamp();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setLat(double lat);

    public abstract Builder setLng(double lng);

    public abstract Builder setDeviceId(String deviceId);

    public abstract Builder setTimestamp(long timestamp);

    public abstract DeviceSnapshot build();
  }
}
