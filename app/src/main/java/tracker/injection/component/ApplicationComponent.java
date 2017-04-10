package tracker.injection.component;

import android.app.Application;
import android.content.Context;
import com.squareup.otto.Bus;
import dagger.Component;
import javax.inject.Singleton;
import tracker.TrackerApplication;
import tracker.data.ServicesManager;
import tracker.data.local.DatabaseService;
import tracker.data.local.PreferencesHelper;
import tracker.data.location.LocationService;
import tracker.data.remote.RemoteService;
import tracker.injection.ApplicationContext;
import tracker.injection.module.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

  void inject(TrackerApplication trackerApplication);

  @ApplicationContext
  Context context();

  Application application();

  RemoteService remoteService();

  PreferencesHelper preferencesHelper();

  DatabaseService databaseHelper();

  LocationService locationService();

  Bus bus();

  ServicesManager dataManager();

}
