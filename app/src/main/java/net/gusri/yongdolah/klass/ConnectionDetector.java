package net.gusri.yongdolah.klass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ghost on 2/28/17.
 */

public class ConnectionDetector {
    Context mContext;

    public boolean isConnect(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        if(networkInfo !=null && networkInfo.isConnectedOrConnecting()){
            android.net.NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile !=null && mobile.isConnectedOrConnecting()) || (wifi !=null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;


    }
}
