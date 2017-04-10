package tracker.data.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import tracker.data.model.DevicePayload;

public interface RemoteService {

  @POST("/location")
  Call<ResponseBody> sendDeviceData(@Body DevicePayload payload);

}
