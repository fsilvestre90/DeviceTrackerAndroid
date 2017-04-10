package tracker.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import javax.inject.Inject;
import tracker.injection.ApplicationContext;

public class PreferencesHelper {

  public static final String PREF_FILE_NAME = "device_pref_file";

  private final SharedPreferences mPref;

  @Inject
  public PreferencesHelper(@ApplicationContext Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void clear() {
    mPref.edit().clear().apply();
  }

}
