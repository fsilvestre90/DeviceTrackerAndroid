package tracker.data.remote;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import tracker.data.model.DeviceSnapshot;

public class AutoValueGsonTypeAdapterFactory implements TypeAdapterFactory {

  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    Class<? super T> rawType = type.getRawType();

    if (rawType.equals(DeviceSnapshot.class)) {
      return (TypeAdapter<T>) DeviceSnapshot.typeAdapter(gson);
    }

    return null;
  }
}