package tracker.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class DbOpenHelper extends SQLiteOpenHelper {

  public DbOpenHelper(@NonNull Context context) {
    super(context, "device_db", null, 1);
  }

  @Override
  public void onCreate(@NonNull SQLiteDatabase db) {
    db.execSQL(DeviceTable.getCreateTableQuery());
  }

  @Override
  public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
    // no impl
  }
}