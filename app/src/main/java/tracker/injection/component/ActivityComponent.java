package tracker.injection.component;

import android.app.LauncherActivity;
import dagger.Component;
import tracker.injection.PerActivity;
import tracker.injection.module.ActivityModule;
import tracker.ui.activity.MainActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

  void inject(LauncherActivity launcherActivity);

  void inject(MainActivity mainActivity);

}

