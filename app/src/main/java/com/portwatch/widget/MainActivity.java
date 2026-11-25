package com.portwatch.widget;
import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
public class MainActivity extends Activity {
 private View panel;private Button refresh;
 @Override public void onCreate(Bundle b){super.onCreate(b);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Dashboard.BG);root.setPadding(dp(12),dp(12),dp(12),dp(12));root.setOnApplyWindowInsetsListener((v,i)->{if(android.os.Build.VERSION.SDK_INT>=30){android.graphics.Insets safe=i.getInsets(WindowInsets.Type.systemBars());v.setPadding(dp(12)+safe.left,dp(12)+safe.top,dp(12)+safe.right,dp(12)+safe.bottom);}return i;});
 panel=new View(this){@Override protected void onDraw(Canvas c){float density=getResources().getDisplayMetrics().density;c.save();c.scale(density,density);new Dashboard(c).draw(Tracker.read(MainActivity.this),getWidth()/density,getHeight()/density);c.restore();}};root.addView(panel,new LinearLayout.LayoutParams(-1,0,1));
 refresh=button("Обновить");refresh.setOnClickListener(v->load());root.addView(refresh);
 Button pin=button("Добавить виджет на рабочий стол");pin.setOnClickListener(v->{AppWidgetManager a=AppWidgetManager.getInstance(this);if(a.isRequestPinAppWidgetSupported())a.requestPinAppWidget(new ComponentName(this,PortWidget.class),null,null);else Toast.makeText(this,"Добавь виджет через меню рабочего стола",Toast.LENGTH_LONG).show();});root.addView(pin);
 Button site=button("Открыть PortWatch VI ↗");site.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Tracker.SITE))));root.addView(site);setContentView(root);load();}
 int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
 Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Dashboard.ON_PRIMARY);android.graphics.drawable.GradientDrawable bg=new android.graphics.drawable.GradientDrawable();bg.setColor(Dashboard.PRIMARY);bg.setCornerRadius(dp(24));b.setBackground(bg);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(48));lp.topMargin=dp(7);b.setLayoutParams(lp);return b;}
 void load(){refresh.setEnabled(false);refresh.setText("Обновление…");new Thread(()->{Tracker.refresh(getApplicationContext());PortWidget.all(getApplicationContext());runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){panel.invalidate();refresh.setEnabled(true);refresh.setText("Обновить");}});}).start();}
}
