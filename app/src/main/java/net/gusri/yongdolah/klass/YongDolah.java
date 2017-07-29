package net.gusri.yongdolah.klass;

import android.app.Application;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

/**
 * Created by ghostonk on 23/12/16.
 */

public class YongDolah extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        GlideBuilder gb = new GlideBuilder(this);
        DiskCache dlw = DiskLruCacheWrapper.get(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myCatch/"), 250 * 1024 * 1024);
        gb.setDiskCache(dlw);
        Glide.setup(gb);
    }
}
