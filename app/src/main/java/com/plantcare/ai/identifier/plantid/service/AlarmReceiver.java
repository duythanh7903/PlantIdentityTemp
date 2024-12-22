package com.plantcare.ai.identifier.plantid.service;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static com.plantcare.ai.identifier.plantid.app.AppConstants.KEY_DATA_TO_RING_SCREEN;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.plantcare.ai.identifier.plantid.BuildConfig;
import com.plantcare.ai.identifier.plantid.R;
import com.plantcare.ai.identifier.plantid.app.GlobalApp;
import com.plantcare.ai.identifier.plantid.data.database.AppDatabase;
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity;
import com.plantcare.ai.identifier.plantid.service.utils.AlarmUtils;
import com.plantcare.ai.identifier.plantid.ui.component.ring.RingActivity;
import com.plantcare.ai.identifier.plantid.ui.component.splash.SplashActivity;
import com.plantcare.ai.identifier.plantid.utils.SystemUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static final String CHANNEL_ID = "alarm_channel";

    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";

    @SuppressLint("LogNotTimber")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundleExtra = intent.getBundleExtra(BUNDLE_EXTRA);
        if(bundleExtra != null){
            final AlarmEntity alarm = (AlarmEntity) bundleExtra.getSerializable(ALARM_KEY);
            if(alarm == null) {
                Log.e(TAG, "Alarm is null", new NullPointerException());
                return;
            }
            final int id = alarm.notificationId();

            final NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            createNotificationChannel(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_alarm_white);
            builder.setColor(ContextCompat.getColor(context, R.color.primary));
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setContentText(alarm.getLabel());
            builder.setTicker(alarm.getLabel());
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            builder.setContentIntent(launchAlarmLandingPage(context, alarm));
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_MAX);
            manager.notify(id, builder.build());

            long idAlarmReceiver = alarm.getId();
            AlarmEntity alarmUpdate = AppDatabase.Companion.getInstance(context).alarmDao().searchAlarmById(idAlarmReceiver);
        /*alarmUpdate.setIsEnabled(false);
        AppDatabase.Companion.getInstance(context).alarmDao().updateRecord(alarmUpdate);*/
            Log.d("duylt", "Check Alarm Receiver:\n" + (alarmUpdate == null));

            //Reset Alarm manually
            setReminderAlarm(context, alarm);

            if (SystemUtil.INSTANCE.isAppRunning(context, BuildConfig.APPLICATION_ID) && GlobalApp.Companion.getActivityVisible()) {
                Intent intentRing = new Intent(context, RingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_DATA_TO_RING_SCREEN, alarm);
                intentRing.putExtras(bundle);
                intentRing.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentRing);
                Log.d("duylt", "Start Activity When App Is Running");
            } else {
                Intent intentRing = new Intent(context, RingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_DATA_TO_RING_SCREEN, alarm);
                intentRing.putExtras(bundle);
                intentRing.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intentRing);
                Log.d("duylt", "Start Activity When App Is Not Running");
            }
        }



    }

    //Convenience method for setting a notification
    @SuppressLint("LogNotTimber")
    public static void setReminderAlarm(Context context, AlarmEntity alarm) {

        //Check whether the alarm is set to run on any days
        if(!AlarmUtils.isAlarmActive(alarm) || !alarm.isEnabled()) {
            //If alarm not set to run on any days, cancel any existing notifications for this alarm
            cancelReminderAlarm(context, alarm);
            return;
        } else Log.d("duylt", "Enable Remind");

        final Calendar nextAlarmTime = getTimeForNextAlarm(alarm);
        alarm.setTime(roundedToMinuteTimestamp(nextAlarmTime.getTimeInMillis()));

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_KEY, alarm);
        intent.putExtra(BUNDLE_EXTRA, bundle);

        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Log.d("duylt", "Alarm In Set Reminder Alarm Func: " + alarm);

        ScheduleAlarm.with(context).schedule(alarm, pIntent);
    }

    private static long roundedToMinuteTimestamp(long time) {
        // Làm tròn về đầu phút (bỏ phần giây và mili giây)
        return (time / (60 * 1000)) * (60 * 1000);
    }

    public static String convertTimeToDateFormat(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timeInMillis));
    }

    public static void setReminderAlarms(Context context, List<AlarmEntity> alarms) {
        for(AlarmEntity alarm : alarms) {
            setReminderAlarm(context, alarm);
        }
    }

    /**
     * Calculates the actual time of the next alarm/notification based on the user-set time the
     * alarm should sound each day, the days the alarm is set to run, and the current time.
     * @param alarm Alarm containing the daily time the alarm is set to run and days the alarm
     *              should run
     * @return A Calendar with the actual time of the next alarm.
     */
    private static Calendar getTimeForNextAlarm(AlarmEntity alarm) {

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarm.getTime());

        final long currentTime = System.currentTimeMillis();
        final int startIndex = getStartIndexFromTime(calendar);

        int count = 0;
        boolean isAlarmSetForDay;

        final SparseBooleanArray daysArray = alarm.getDays();

        do {
            final int index = (startIndex + count) % 7;
            isAlarmSetForDay =
                    daysArray.valueAt(index) && (calendar.getTimeInMillis() > currentTime);
            if(!isAlarmSetForDay) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                count++;
            }
        } while(!isAlarmSetForDay && count < 7);

        return calendar;

    }

    public static void cancelReminderAlarm(Context context, AlarmEntity alarm) {

        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                alarm.notificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pIntent);
    }

    private static int getStartIndexFromTime(Calendar c) {

        final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        int startIndex = 0;
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                break;
            case Calendar.TUESDAY:
                startIndex = 1;
                break;
            case Calendar.WEDNESDAY:
                startIndex = 2;
                break;
            case Calendar.THURSDAY:
                startIndex = 3;
                break;
            case Calendar.FRIDAY:
                startIndex = 4;
                break;
            case Calendar.SATURDAY:
                startIndex = 5;
                break;
            case Calendar.SUNDAY:
                startIndex = 6;
                break;
        }

        return startIndex;

    }

    private static void createNotificationChannel(Context ctx) {
        if(SDK_INT < O) return;

        final NotificationManager mgr = ctx.getSystemService(NotificationManager.class);
        if(mgr == null) return;

        final String name = ctx.getString(R.string.channel_name);
        if(mgr.getNotificationChannel(name) == null) {
            final NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {1000,500,1000,500,1000,500});
            channel.setBypassDnd(true);
            mgr.createNotificationChannel(channel);
        }
    }

    private static PendingIntent launchAlarmLandingPage(Context ctx, AlarmEntity alarm) {
        Intent intent = new Intent(ctx, SplashActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DATA_TO_RING_SCREEN, alarm);
        intent.putExtras(bundle);
        return PendingIntent.getActivity(
                ctx, alarm.notificationId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static class ScheduleAlarm {

        @NonNull
        private final Context ctx;
        @NonNull private final AlarmManager am;

        private ScheduleAlarm(@NonNull AlarmManager am, @NonNull Context ctx) {
            this.am = am;
            this.ctx = ctx;
        }

        static ScheduleAlarm with(Context context) {
            final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(am == null) {
                throw new IllegalStateException("AlarmManager is null");
            }
            return new ScheduleAlarm(am, context);
        }

        void schedule(AlarmEntity alarm, PendingIntent pi) {
            am.setAlarmClock(new AlarmClockInfo(alarm.getTime(), launchAlarmLandingPage(ctx, alarm)), pi);
        }

    }

}