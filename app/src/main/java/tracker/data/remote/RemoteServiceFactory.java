package tracker.data.remote;

import static java.util.concurrent.TimeUnit.MINUTES;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import tracker.BuildConfig;

public class RemoteServiceFactory {

  public static RemoteService makeRemoteService() {
    OkHttpClient okHttpClient = makeOkHttpClient(makeLoggingInterceptor());
    return makeRemoteService(okHttpClient);
  }

  public static RemoteService makeRemoteService(OkHttpClient okHttpClient) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.ENDPOINT_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(makeGson()))
        .build();
    return retrofit.create(RemoteService.class);
  }

  public static OkHttpClient makeOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor) {
    return new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
        .addInterceptor(chain -> {
          Request original = chain.request();
          Request.Builder requestBuilder = original.newBuilder()
              .header("Accept", "application/json")
              .method(original.method(), original.body());

          Request request = requestBuilder.build();
          return chain.proceed(request);
        })
        .connectTimeout(5, MINUTES)
        .readTimeout(5, MINUTES)
        .build();
  }

  public static Gson makeGson() {
    return new GsonBuilder()
        .registerTypeAdapterFactory(new AutoValueGsonTypeAdapterFactory())
        .create();
  }

  public static HttpLoggingInterceptor makeLoggingInterceptor() {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(
        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    return logging;
  }
}
