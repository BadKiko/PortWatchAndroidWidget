package com.portwatch.widget;
import android.app.job.*;
import java.util.concurrent.*;
public class RefreshJob extends JobService {
 private static final ExecutorService POOL=Executors.newSingleThreadExecutor();
 @Override public boolean onStartJob(JobParameters p){POOL.execute(()->{try{Tracker.refresh(getApplicationContext());PortWidget.all(getApplicationContext());}finally{jobFinished(p,false);}});return true;}
 @Override public boolean onStopJob(JobParameters p){return true;}
}
