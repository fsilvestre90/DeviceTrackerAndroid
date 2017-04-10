package tracker;

import android.app.Application;
import android.content.Context;
import timber.log.Timber;
import tracker.injection.component.ApplicationComponent;
import tracker.injection.component.DaggerApplicationComponent;
import tracker.injection.module.ApplicationModule;

public class TrackerApplication extends Application {

  ApplicationComponent mApplicationComponent;

  public static TrackerApplication get(Context context) {
    return (TrackerApplication) context.getApplicationContext();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    mApplicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
    mApplicationComponent.inject(this);
  }

  public ApplicationComponent getComponent() {
    return mApplicationComponent;
  }

  // Needed to replace the component with a test specific one
  public void setComponent(ApplicationComponent applicationComponent) {
    mApplicationComponent = applicationComponent;
  }
}
