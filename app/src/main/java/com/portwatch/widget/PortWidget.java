package com.portwatch.widget;
import android.app.*;
import android.app.job.*;
import android.appwidget.*;
import android.content.*;
import android.os.Bundle;
import android.widget.RemoteViews;
public class PortWidget extends AppWidgetProvider {
 static final String REFRESH="com.portwatch.widget.REFRESH";static final int JOB_ID=7046;
 static void schedule(Context c){JobScheduler j=c.getSystemService(JobScheduler.class);if(j!=null)j.schedule(new JobInfo.Builder(JOB_ID,new ComponentName(c,RefreshJob.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());}
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)draw(c,m,id);schedule(c);}
 @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager m,int id,Bundle opts){draw(c,m,id);}
 @Override public void onReceive(Context c,Intent i){super.onReceive(c,i);if(REFRESH.equals(i.getAction()))schedule(c);}
 static void all(Context c){AppWidgetManager m=AppWidgetManager.getInstance(c);for(int id:m.getAppWidgetIds(new ComponentName(c,PortWidget.class)))draw(c,m,id);}
 static void draw(Context c,AppWidgetManager m,int id){Bundle o=m.getAppWidgetOptions(id);int w=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,320),h=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,340);boolean landscape=c.getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_LANDSCAPE;if(landscape){w=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,w);h=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,h);}w=Math.max(150,Math.min(w,720));h=Math.max(100,Math.min(h,900));Tracker.Data d=Tracker.read(c);RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);v.setImageViewBitmap(R.id.dashboard,Dashboard.bitmap(d,w,h,c.getResources().getDisplayMetrics().density));v.setContentDescription(R.id.dashboard,d.entries.isEmpty()?"PortWatch VI, нет данных":"PortWatch VI, "+d.entries.get(0).name+", "+Tracker.pct(d.entries.get(0).score));v.setOnClickPendingIntent(R.id.dashboard,PendingIntent.getActivity(c,id,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));v.setOnClickPendingIntent(R.id.refresh,PendingIntent.getBroadcast(c,id,new Intent(c,PortWidget.class).setAction(REFRESH),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));m.updateAppWidget(id,v);}
}
