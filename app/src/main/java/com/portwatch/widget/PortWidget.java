package com.portwatch.widget;
import android.app.*;
import android.app.job.*;
import android.appwidget.*;
import android.content.*;
import android.os.Bundle;
import android.util.SizeF;
import android.widget.RemoteViews;
import java.util.*;
public class PortWidget extends AppWidgetProvider {
 static final String REFRESH="com.portwatch.widget.REFRESH";static final int JOB_ID=7046,PERIODIC_ID=7047;
 static void schedule(Context c){JobScheduler j=c.getSystemService(JobScheduler.class);if(j!=null)j.schedule(new JobInfo.Builder(JOB_ID,new ComponentName(c,RefreshJob.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());}
 static void periodic(Context c){JobScheduler j=c.getSystemService(JobScheduler.class);if(j==null)return;int n=Settings.interval(c);if(n==0){j.cancel(PERIODIC_ID);return;}j.schedule(new JobInfo.Builder(PERIODIC_ID,new ComponentName(c,RefreshJob.class)).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPeriodic(Math.max(15,n)*60000L).build());}
 @Override public void onEnabled(Context c){periodic(c);}
 @Override public void onDisabled(Context c){JobScheduler j=c.getSystemService(JobScheduler.class);if(j!=null){j.cancel(JOB_ID);j.cancel(PERIODIC_ID);}}
 @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)draw(c,m,id);periodic(c);if(Settings.interval(c)!=0)schedule(c);}
 @Override public void onAppWidgetOptionsChanged(Context c,AppWidgetManager m,int id,Bundle opts){draw(c,m,id);}
 @Override public void onReceive(Context c,Intent i){super.onReceive(c,i);String a=i.getAction();if(REFRESH.equals(a))schedule(c);else if(Intent.ACTION_CONFIGURATION_CHANGED.equals(a)||Intent.ACTION_WALLPAPER_CHANGED.equals(a)||Intent.ACTION_MY_PACKAGE_REPLACED.equals(a)){all(c);if(AppWidgetManager.getInstance(c).getAppWidgetIds(new ComponentName(c,PortWidget.class)).length>0)periodic(c);}}
 static void all(Context c){AppWidgetManager m=AppWidgetManager.getInstance(c);for(int id:m.getAppWidgetIds(new ComponentName(c,PortWidget.class)))draw(c,m,id);}
 static RemoteViews view(Context c,int id,Tracker.Data d,int w,int h){
  w=Math.max(100,Math.min(w,720));h=Math.max(85,Math.min(h,900));RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
  v.setImageViewBitmap(R.id.dashboard,Dashboard.bitmap(c,d,w,h,c.getResources().getDisplayMetrics().density));
  v.setContentDescription(R.id.dashboard,d.entries.isEmpty()?"PortWatch VI, нет данных":"PortWatch VI, "+d.entries.get(0).name+", "+Tracker.pct(d.entries.get(0).score));
  v.setOnClickPendingIntent(R.id.dashboard,PendingIntent.getActivity(c,id,new Intent(c,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
  v.setOnClickPendingIntent(R.id.refresh,PendingIntent.getBroadcast(c,id,new Intent(c,PortWidget.class).setAction(REFRESH),PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));
  v.setViewVisibility(R.id.refresh,Dashboard.mode(w,h)==Dashboard.SQUARE?android.view.View.GONE:android.view.View.VISIBLE);return v;
 }
 static void draw(Context c,AppWidgetManager m,int id){
  Bundle o=m.getAppWidgetOptions(id);Tracker.Data d=Tracker.filtered(c,Tracker.read(c));
  if(android.os.Build.VERSION.SDK_INT>=31){ArrayList<SizeF> sizes=o.getParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES);if(sizes!=null&&!sizes.isEmpty()){Map<SizeF,RemoteViews> map=new LinkedHashMap<>();for(SizeF s:sizes){if(s.getWidth()>0&&s.getHeight()>0)map.put(s,view(c,id,d,Math.round(s.getWidth()),Math.round(s.getHeight())));if(map.size()>=4)break;}if(!map.isEmpty()){m.updateAppWidget(id,new RemoteViews(map));return;}}}
  int w=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,320),h=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,340);if(c.getResources().getConfiguration().orientation==android.content.res.Configuration.ORIENTATION_LANDSCAPE){w=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,w);h=o.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,h);}m.updateAppWidget(id,view(c,id,d,w,h));
 }
}
