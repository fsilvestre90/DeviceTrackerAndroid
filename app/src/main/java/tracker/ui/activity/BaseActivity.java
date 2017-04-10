package tracker.ui.activity;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.tbruyelle.rxpermissions.RxPermissions;
import tracker.TrackerApplication;
import tracker.injection.component.ActivityComponent;
import tracker.injection.component.ApplicationComponent;
import tracker.injection.component.DaggerActivityComponent;
import tracker.injection.module.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {

  private ActivityComponent mActivityComponent;

  @Override
  protected void onStart() {
    super.onStart();
    RxPermissions
        .getInstance(this)
        .request(Manifest.permission.ACCESS_FINE_LOCATION)
        .subscribe(granted -> {
          if (granted) {
            onLocationPermissionGranted();
          } else {
            Toast.makeText(BaseActivity.this, "We need permissions for this to work",
                Toast.LENGTH_SHORT).show();
          }
        });
  }

  public ActivityComponent activityComponent() {
    if (mActivityComponent == null) {
      mActivityComponent = DaggerActivityComponent.builder()
          .activityModule(new ActivityModule(this))
          .applicationComponent(TrackerApplication.get(this).getComponent())
          .build();
    }
    return mActivityComponent;
  }

  protected ApplicationComponent applicationComponent() {
    return TrackerApplication.get(this).getComponent();
  }

  protected abstract void onLocationPermissionGranted();

}
