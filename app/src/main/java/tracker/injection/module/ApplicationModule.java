package tracker.injection.module;

import android.app.Application;
import android.content.Context;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;
import javax.inject.Scope;
import javax.inject.Singleton;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import tracker.data.remote.RemoteService;
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
    return RemoteService.Factory.makeRemoteService(mApplication);
  }

  @Provides
  @Singleton
  Bus provideBus(){
    return new Bus(ThreadEnforcer.ANY);
  }

  @Provides
  @Singleton
  ReactiveLocationProvider provideReactiveLocation() {
    return new ReactiveLocationProvider(mApplication);
  }

}