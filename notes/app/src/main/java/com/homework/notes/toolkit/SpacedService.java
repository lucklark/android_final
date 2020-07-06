package com.homework.notes.toolkit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.homework.notes.R;
import com.homework.notes.persistence.datastructure.NoteItems;
import com.homework.notes.persistence.NotesDataSource;
import com.homework.notes.presentation.main.tabpage.notespage.AnswerCard;

import java.util.List;



public class SpacedService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                NotesDataSource nds = new NotesDataSource(SpacedService.this);
                List<NoteItems> items = nds.getAllNotesForNotification();
                for(NoteItems item : items)
                {
                    SpacedService.this.notification(item.id,item.title,item.content,item.note_class);
                    nds.incrementTotalReviews(item.id);
                }
                handler.postDelayed(this, 60000);//每分钟检测一次
            }
        }, 60000);



    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void notification(long _id,String title, String message, String class_name)
    {
        int random = 1 + (int)(Math.random() * ((100 - 1) + 1));
        int num = message.hashCode();

        String id = "channel";
        String name = "记录 " + title;
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, AnswerCard.class);
        intent.putExtra("id",String.valueOf(_id));
        intent.putExtra("title",title);
        intent.putExtra("class_name", class_name);
        intent.putExtra("content",message);
        intent.putExtra("from","notification");
        PendingIntent i=PendingIntent.getActivity(this, random,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//判断API
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentTitle("有内容需要记忆！")
                    .setContentText("题目为 " + title)
                    .setContentIntent(i)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_round).build();
        }else{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("有内容需要记忆！")
                    .setContentText("题目为 " + title)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(i)
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true)
                    .setChannelId(id);//无效
            notification = notificationBuilder.build();
        }
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(num,notification);


    }
}
