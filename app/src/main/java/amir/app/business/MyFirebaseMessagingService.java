package amir.app.business;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import amir.app.business.models.db.Notification;

/**
 * Created by amin on 05/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        String message = remoteMessage.getData().get("message");
        sendNotification(this, message, "پیام جدید دریافت شد");


        //save received notification to database
        Notification notification = new Notification();
        notification.message = message;
        notification.date = Calendar.getInstance().getTimeInMillis();
        notification.save();
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(final Context context, final String message, final String messageBody) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://amir.app.business/" + R.raw.message);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.business_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.business_logo))
                .setOngoing(false)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setContentTitle("پیام جدید")
                .setContentText(messageBody)
                .setVibrate(new long[]{1000, 500, 1000, 500, 1000})
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setGroup("message")
                .setContentIntent(pendingIntent);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, notificationBuilder.build());

//        Intent popupintent = new Intent(context, popup.class);
//        popupintent.putExtra("order_id", order_id);
//        popupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(popupintent);
    }
}
