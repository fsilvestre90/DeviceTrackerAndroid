package tracker.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DevicePayload {

  @SerializedName("data")
  @Expose
  private List<DeviceSnapshot> payload;

  public DevicePayload(List<DeviceSnapshot> payload) {
    this.payload = payload;
  }

}
