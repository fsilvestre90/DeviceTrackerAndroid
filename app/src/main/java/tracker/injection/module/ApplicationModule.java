package tracker.injection.module;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import tracker.data.local.DbOpenHelper;
import tracker.data.model.DeviceSnapshot;
import tracker.data.model.DeviceSnapshotSQLiteTypeMapping;
import tracker.data.remote.RemoteService;
import tracker.data.remote.RemoteServiceFactory;
import tracker.injection.ApplicationContext;

/**
 * Provide application-level dependencies. Mainly singleton object that can be injected from
 * anywhere in the app.
 */
@Module
public class ApplicationModule {

  protected final Application mApplication;

  public ApplicationModule(Application application) {
    mApplication = application;
  }

  @Provides
  Application provideApplication() {
    return mApplication;
  }

  @Provides
  @ApplicationContext
  Context provideContext() {
    return mApplication;
  }

  @Provides
  @Singleton
  RemoteService provideRemoteService() {
    return RemoteServiceFactory.makeRemoteService();
  }

  @Provides
  @Singleton
  Bus provideBus() {
    return new Bus(ThreadEnforcer.ANY);
  }

  @Provides
  @Singleton
  ReactiveLocationProvider provideReactiveLocation() {
    return new ReactiveLocationProvider(mApplication);
  }

  @Provides
  @NonNull
  @Singleton
  public StorIOSQLite provideStorIOSQLite(SQLiteOpenHelper sqLiteOpenHelper) {
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(sqLiteOpenHelper)
        .addTypeMapping(DeviceSnapshot.class, new DeviceSnapshotSQLiteTypeMapping())
        .build();
  }

  @Provides
  @NonNull
  @Singleton
  public SQLiteOpenHelper provideSQLiteOpenHelper() {
    return new DbOpenHelper(mApplication);
  }

}