package com.portwatch.widget;
import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.function.IntConsumer;

public class MainActivity extends Activity {
 DynamicPalette q;LinearLayout root,body;ScrollView scroll;Button refresh;View panel;int preview=2;boolean busy=false;int scrollY=0;
 @Override public void onCreate(Bundle b){super.onCreate(b);if(b!=null)preview=b.getInt("preview",2);build();if(Tracker.read(this).updated==0)load();}
 @Override public void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b);b.putInt("preview",preview);}
 @Override protected void onResume(){super.onResume();if(panel!=null)panel.invalidate();PortWidget.all(this);}
 int dp(float n){return Math.round(n*getResources().getDisplayMetrics().density);}
 GradientDrawable bg(int color,float radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 TextView label(String s,int size,int color,boolean medium){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setFontFeatureSettings("kern");t.setTypeface(Typeface.create(medium?"sans-serif-medium":"sans-serif",Typeface.NORMAL));return t;}
 void add(LinearLayout l,View v,int height,int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,height<0?height:dp(height));p.topMargin=dp(top);l.addView(v,p);}
 LinearLayout column(int color,int radius,int pad){LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(dp(pad),dp(pad),dp(pad),dp(pad));l.setBackground(bg(color,radius));return l;}
 Button button(String s,int color,int ink){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(ink);b.setTypeface(Typeface.create("sans-serif-medium",0));b.setMinHeight(0);b.setMinimumHeight(0);b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(10),0,dp(10),0);b.setBackground(new android.graphics.drawable.RippleDrawable(ColorStateList.valueOf(0x22000000),bg(color,24),null));return b;}
 void choices(LinearLayout l,String[] names,int selected,IntConsumer click){
  LinearLayout row=new LinearLayout(this);row.setOrientation(0);for(int i=0;i<names.length;i++){final int j=i;Button b=button(names[i],i==selected?q.primary:q.surface,i==selected?q.onPrimary:q.text);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(44),1);if(i>0)p.leftMargin=dp(4);row.addView(b,p);b.setOnClickListener(v->click.accept(j));}add(l,row,44,8);
 }
 void rebuild(){scrollY=scroll.getScrollY();build();scroll.post(()->scroll.scrollTo(0,scrollY));PortWidget.all(this);}
 void build(){
  q=new DynamicPalette(this);root=column(q.bg,0,0);getWindow().setStatusBarColor(q.bg);getWindow().setNavigationBarColor(q.bg);getWindow().getDecorView().setSystemUiVisibility(q.dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
  root.setOnApplyWindowInsetsListener((v,i)->{if(android.os.Build.VERSION.SDK_INT>=30){android.graphics.Insets in=i.getInsets(WindowInsets.Type.systemBars());v.setPadding(in.left,in.top,in.right,in.bottom);}return i;});
  LinearLayout top=column(q.bg,0,18);TextView title=label("PortWatch VI",30,q.text,true);add(top,title,-2,0);add(top,label("Гонка за PC-портом",14,q.muted,false),-2,3);root.addView(top,new LinearLayout.LayoutParams(-1,-2));
  scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.setClipToPadding(false);body=column(q.bg,0,16);scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  add(body,label("Виджеты",23,q.text,true),-2,0);choices(body,new String[]{"2 × 2","4 × 2","Large"},preview,i->{preview=i;rebuild();});
  panel=new View(this){@Override protected void onDraw(Canvas c){super.onDraw(c);float den=getResources().getDisplayMetrics().density,w=getWidth()/den,h=getHeight()/den;float pw=preview==0?Math.min(w,200):Math.min(w,380);float ph=preview==0?pw:preview==1?pw*.49f:Math.min(h,pw*1.25f);c.save();c.scale(den,den);c.translate((w-pw)/2,(h-ph)/2);new Dashboard(c,new DynamicPalette(MainActivity.this)).draw(Tracker.filtered(MainActivity.this,Tracker.read(MainActivity.this)),pw,ph,Settings.news(MainActivity.this));c.restore();}};
  panel.setBackground(bg(q.surface,28));add(body,panel,preview==0?210:preview==1?185:440,10);
  Button pin=button("＋  Добавить на рабочий стол",q.primary,q.onPrimary);pin.setOnClickListener(v->{AppWidgetManager a=AppWidgetManager.getInstance(this);if(a.isRequestPinAppWidgetSupported())a.requestPinAppWidget(new ComponentName(this,PortWidget.class),null,null);else Toast.makeText(this,"Добавь виджет через меню рабочего стола",Toast.LENGTH_LONG).show();});add(body,pin,52,10);
  LinearLayout buttons=new LinearLayout(this);refresh=button(busy?"Обновление…":"↻  Обновить",q.secondary,q.onSecondary);refresh.setEnabled(!busy);refresh.setOnClickListener(v->load());Button site=button("Сайт ↗",q.tertiary,q.onTertiary);site.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Tracker.SITE))));buttons.addView(refresh,new LinearLayout.LayoutParams(0,dp(48),1));LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(0,dp(48),1);sp.leftMargin=dp(6);buttons.addView(site,sp);add(body,buttons,48,6);
  add(body,label("Оформление",23,q.text,true),-2,20);
  LinearLayout appearance=column(q.raised,26,14);add(appearance,label("Цвета из обоев · Monet",16,q.text,true),-2,0);add(appearance,label("Системная палитра Android 12+",12,q.muted,false),-2,3);String[] theme={"system","light","dark"};int selected=java.util.Arrays.asList(theme).indexOf(Settings.theme(this));choices(appearance,new String[]{"Система","Светлая","Тёмная"},selected,i->{Settings.put(this,"theme",theme[i]);rebuild();});add(body,appearance,-2,10);
  LinearLayout filters=column(q.raised,26,14);add(filters,label("Видеокарты",16,q.text,true),-2,0);String[] gpu={"all","amd","nvidia"};choices(filters,new String[]{"Все","AMD","NVIDIA"},java.util.Arrays.asList(gpu).indexOf(Settings.gpu(this)),i->{Settings.put(this,"gpu",gpu[i]);rebuild();});add(filters,label("Проекты без указанного GPU видны в режиме «Все».",12,q.muted,false),-2,6);add(body,filters,-2,8);
  LinearLayout update=column(q.raised,26,14);add(update,label("Автообновление",16,q.text,true),-2,0);int[] mins={15,30,60,0};String[] names={"15 мин","30 мин","1 час","Вручную"};int idx=1;for(int i=0;i<mins.length;i++)if(mins[i]==Settings.interval(this))idx=i;choices(update,names,idx,i->{Settings.interval(this,mins[i]);PortWidget.periodic(this);rebuild();});add(update,label("Android может отложить фоновое обновление.",12,q.muted,false),-2,6);
  Switch news=new Switch(this);news.setText("Новости в большом виджете");news.setTextColor(q.text);news.setTextSize(14);news.setChecked(Settings.news(this));news.setThumbTintList(new ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{q.primary,q.outline}));news.setOnCheckedChangeListener((v,x)->{Settings.news(this,x);panel.invalidate();PortWidget.all(this);});add(update,news,48,7);add(body,update,-2,8);add(body,label("PortWatch VI · неофициальный виджет. Балл трекера не равен готовности порта.",12,q.muted,false),-2,14);
  setContentView(root);root.requestApplyInsets();
 }
 void load(){if(busy)return;busy=true;refresh.setEnabled(false);refresh.setText("Обновление…");new Thread(()->{Tracker.refresh(getApplicationContext());PortWidget.all(getApplicationContext());runOnUiThread(()->{busy=false;if(!isFinishing()&&!isDestroyed()){panel.invalidate();refresh.setEnabled(true);refresh.setText("↻  Обновить");}});}).start();}
}
