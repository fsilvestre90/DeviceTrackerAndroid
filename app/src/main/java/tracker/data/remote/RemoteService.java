package tracker.data.remote;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.POST;
import rx.Observable;
import tracker.BuildConfig;
import tracker.data.model.DeviceSnapshot;

public interface RemoteService {

  String ENDPOINT = "http://swapi.co/api/";

  @POST("location/")
  Observable<DeviceSnapshot> sendDeviceData(List<DeviceSnapshot> snapshot);

  /********
   * Factory class that sets up a new remote service
   *******/
  class Factory {

    public static Gson makeGson() {
      return new GsonBuilder().registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
          .create();
    }

    public static RemoteService makeRemoteService(Context context) {
      OkHttpClient okHttpClient = new OkHttpClient();
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
          : HttpLoggingInterceptor.Level.NONE);
//            okHttpClient.interceptors().add(logging);

      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(RemoteService.ENDPOINT)
          .client(okHttpClient)
          .addConverterFactory(GsonConverterFactory.create(makeGson()))
          .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .build();
      return retrofit.create(RemoteService.class);
    }

  }
}
