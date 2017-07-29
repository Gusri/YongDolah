package net.gusri.yongdolah.fcm;


//import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.gusri.yongdolah.MainActivity;
import net.gusri.yongdolah.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by ghost on 4/8/17.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String image = remoteMessage.getNotification().getIcon();
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();

        int id = 0;
        Object obj = remoteMessage.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
        }

        this.sendNotification(new NotificationData(image, id, title, text, sound));


    }

    private void sendNotification(NotificationData notificationData) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

       /* int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_launcher:R.mipmap.ic_launcher;*/

        NotificationCompat.Builder notificationBuilder = null;
        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotifIcon())
                    .setContentTitle(URLDecoder.decode(notificationData.getTitle(), "UTF-8"))
                    .setContentText(URLDecoder.decode(notificationData.getTextMessage(), "UTF-8"))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "notificationBuilder");
        }
    }

    public int getNotifIcon() {
        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return whiteIcon ? R.drawable.ic_notif : R.drawable.ic_notif;
    }
}