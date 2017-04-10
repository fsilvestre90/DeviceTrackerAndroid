package tracker.ui.activity;

import android.os.Bundle;
import butterknife.ButterKnife;
import javax.inject.Inject;
import tracker.R;
import tracker.data.ServicesManager;

public class MainActivity extends BaseActivity {

  @Inject
  ServicesManager servicesManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityComponent().inject(this);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @Override
  protected void onLocationPermissionGranted() {
    servicesManager.startDeviceTracking();
    servicesManager.startApiPoller();
  }

}
