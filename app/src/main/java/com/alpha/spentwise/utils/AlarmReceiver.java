package com.alpha.spentwise.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alpha.spentwise.R;
import com.alpha.spentwise.activities.LoginPageActivity;
import com.alpha.spentwise.customDataObjects.ProjectDetails;
import com.alpha.spentwise.customDataObjects.UserDetails;
import com.alpha.spentwise.dataManager.Constants;
import com.alpha.spentwise.dataManager.DatabaseHandler;

import java.util.Calendar;
import java.util.List;

import static com.alpha.spentwise.utils.CustomFunctions.getDateIntCustom;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent newIntent = new Intent(context, LoginPageActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "SPENTWISE_PROJECT_REMINDER")
                .setSmallIcon(R.drawable.ic_applogo_cropped)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        DatabaseHandler dbHandler = new DatabaseHandler(context);

        UserDetails userDetails = dbHandler.getLoggedInUserDetails();
        int currentDate = getDateIntCustom(Calendar.getInstance().getTimeInMillis(), Constants.dateFormat);

        if(userDetails != null){
            if(userDetails.getUserID() > 0){
                List<ProjectDetails> userProjectList = dbHandler.getProjectForUser(userDetails);
                if(userProjectList != null){
                    for(int i = 0; i < userProjectList.size(); i++){
                        int entriesAdded = dbHandler.getDailyNewEntryCount(currentDate,userProjectList.get(i));
                        if(entriesAdded == 0){
                            bigText.bigText("You have not added any transactions today");
                            bigText.setBigContentTitle(userProjectList.get(i).getProjectName());
                            bigText.setSummaryText("Reminder");

                            mBuilder.setContentIntent(pendingIntent);
                            mBuilder.setSmallIcon(R.drawable.ic_applogo_cropped);
                            mBuilder.setContentTitle(userProjectList.get(i).getProjectName());
                            mBuilder.setContentText("Reminder");
                            mBuilder.setStyle(bigText);
                            notificationManagerCompat.notify(i, mBuilder.build());
                        }


                    }
                }

            }
        }

    }
}
