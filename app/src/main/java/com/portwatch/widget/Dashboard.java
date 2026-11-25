package com.portwatch.widget;
import android.content.Context;
import android.graphics.*;
import android.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class Dashboard {
 static final int SQUARE=0,COMPACT=1,LARGE=2;
 final Canvas c; final DynamicPalette q; final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final TextPaint tp=new TextPaint(Paint.ANTI_ALIAS_FLAG);
 final Typeface medium=Typeface.create("sans-serif-medium",Typeface.NORMAL), regular=Typeface.create("sans-serif",Typeface.NORMAL);
 Dashboard(Canvas c,DynamicPalette q){this.c=c;this.q=q;}
 void rect(float x,float y,float w,float h,float r,int col){if(w<=0||h<=0)return;p.setStyle(Paint.Style.FILL);p.setColor(col);c.drawRoundRect(x,y,x+w,y+h,Math.min(r,h/2),Math.min(r,h/2),p);}
 void circle(float x,float y,float r,int col){p.setStyle(Paint.Style.FILL);p.setColor(col);c.drawCircle(x,y,r,p);}
 void font(float size,int color,boolean bold){p.setStyle(Paint.Style.FILL);p.setTextSize(size);p.setColor(color);p.setTypeface(bold?medium:regular);}
 void text(String s,float x,float y,float size,int col,boolean bold,float max,int align){
  if(max<=1)return;font(size,col,bold);tp.set(p);String v=TextUtils.ellipsize(s==null?"":s,tp,max,TextUtils.TruncateAt.END).toString();p.setTextAlign(align==1?Paint.Align.CENTER:align==2?Paint.Align.RIGHT:Paint.Align.LEFT);c.drawText(v,x,y,p);p.setTextAlign(Paint.Align.LEFT);
 }
 void lines(String s,float x,float y,float w,float size,int col,boolean bold,int count){
  if(s==null||s.isEmpty()||count<1||w<1)return;tp.setColor(col);tp.setTextSize(size);tp.setTypeface(bold?medium:regular);
  StaticLayout l=StaticLayout.Builder.obtain(s,0,s.length(),tp,Math.max(1,(int)w)).setIncludePad(false).setMaxLines(count).setEllipsize(TextUtils.TruncateAt.END).setLineSpacing(1,1).build();c.save();c.translate(x,y);l.draw(c);c.restore();
 }
 void flower(float x,float y,float radius,int col){
  Path a=new Path();for(int i=0;i<=240;i++){double t=Math.PI*2*i/240-Math.PI/2;double r=radius*(.94+.06*Math.cos(12*(t+Math.PI/2)));float xx=x+(float)(r*Math.cos(t)),yy=y+(float)(r*Math.sin(t));if(i==0)a.moveTo(xx,yy);else a.lineTo(xx,yy);}a.close();p.setStyle(Paint.Style.FILL);p.setColor(col);c.drawPath(a,p);
 }
 void stroke(float x,float y,float r,float angle,int col,float sw){p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(sw);p.setStrokeCap(Paint.Cap.ROUND);p.setColor(col);c.drawArc(x-r,y-r,x+r,y+r,-90,angle,false,p);p.setStyle(Paint.Style.FILL);}
 void meter(float x,float y,float r,Tracker.Entry e,boolean fps){
  flower(x,y,r,q.primarySoft);stroke(x,y,r*.75f,360,DynamicPalette.blend(q.primarySoft,q.onPrimarySoft,.20f),r*.095f);if(e.score>0)stroke(x,y,r*.75f,(float)(e.score*3.6),q.onPrimarySoft,r*.095f);
  String val=Tracker.pct(e.score);text(val,x,y+1,r*.45f,q.onPrimary,true,r*1.40f,1);
  text(fps?Tracker.fps(e.fps):"БАЛЛ",x,y+r*.29f,r*.17f,q.onPrimarySoft,true,r*1.4f,1);
 }
 // Material-style 24-unit glyphs drawn in an isolated paint/canvas state.
 void icon(String kind,float x,float y,int col){
  c.save();c.translate(x-9,y-9);c.scale(.75f,.75f);
  Paint a=new Paint(Paint.ANTI_ALIAS_FLAG);a.setColor(col);a.setStyle(Paint.Style.FILL);
  Path z=new Path();
  switch(kind){
   case "refresh":
    z.moveTo(17.65f,6.35f);z.cubicTo(16.2f,4.9f,14.21f,4,12,4);z.cubicTo(7.58f,4,4,7.58f,4,12);z.cubicTo(4,16.42f,7.58f,20,12,20);z.cubicTo(15.73f,20,18.84f,17.45f,19.73f,14);z.lineTo(17.65f,14);z.cubicTo(16.83f,16.33f,14.61f,18,12,18);z.cubicTo(8.69f,18,6,15.31f,6,12);z.cubicTo(6,8.69f,8.69f,6,12,6);z.cubicTo(13.66f,6,15.14f,6.69f,16.22f,7.78f);z.lineTo(13,11);z.lineTo(20,11);z.lineTo(20,4);z.close();break;
   case "trend":
    z.moveTo(3.41f,18.59f);z.lineTo(10,12);z.lineTo(14,16);z.lineTo(21,9);z.lineTo(21,13);z.lineTo(23,13);z.lineTo(23,5);z.lineTo(15,5);z.lineTo(15,7);z.lineTo(19.59f,7);z.lineTo(14,12.59f);z.lineTo(10,8.59f);z.lineTo(2,17.17f);z.close();break;
   case "news":
    z.setFillType(Path.FillType.EVEN_ODD);z.addRoundRect(4,3,20,21,2,2,Path.Direction.CW);z.addRoundRect(6,5,18,19,1,1,Path.Direction.CW);c.drawPath(z,a);c.drawRoundRect(8,7,16,9,1,1,a);c.drawRoundRect(8,11,16,13,1,1,a);c.drawRoundRect(8,15,14,17,1,1,a);c.restore();return;
   default:
    z.moveTo(8,5);z.lineTo(19,12);z.lineTo(8,19);z.close();break;
  }
  c.drawPath(z,a);c.restore();
 }
 String time(Tracker.Data d){return d.updated==0?"—":Instant.ofEpochMilli(d.updated).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"));}
 void header(Tracker.Data d,float w){text("PortWatch VI",14,25,14,q.text,true,w-105,0);rect(w-80,8,70,25,13,q.raised);icon("refresh",w-65,20.5f,d.error.isEmpty()?q.text:q.error);text(time(d),w-17,24,11,q.text,true,42,2);}

 // Four independent layout families; all geometry is measured in logical dp.
 static int mode(float w,float h){if(w<230&&h/w>=.65f)return SQUARE;return h/w<.83f?COMPACT:LARGE;}
 static Bitmap bitmap(Context ctx,Tracker.Data d,int w,int h,float density){
  float scale=Math.min(density,Math.min(950f/w,950f/h));
  int bw=Math.max(1,Math.round(w*scale)),bh=Math.max(1,Math.round(h*scale));
  Bitmap b=Bitmap.createBitmap(bw,bh,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);c.scale(bw/(float)w,bh/(float)h);
  new Dashboard(c,new DynamicPalette(ctx)).draw(d,w,h,Settings.news(ctx),Settings.design(ctx));return b;
 }
 void draw(Tracker.Data d,float width,float height,boolean showNews,int design){
  if(width<1||height<1)return;
  int m=mode(width,height);float w=m==SQUARE?180:340;
  // Use the actual aspect ratio where possible, never stretch typography.
  float h=m==SQUARE?190:m==COMPACT?166:390;
  if(m==LARGE&&width>0&&height*w/width>=440)h=Math.min(490,height*w/width);
  float sc=Math.min(width/w,height/h);
  c.save();rect(0,0,width,height,Math.min(28,Math.min(width,height)*.15f),q.bg);
  c.translate((width-w*sc)/2,(height-h*sc)/2);c.scale(sc,sc);
  if(d.entries.isEmpty()){
   header(d,w);text("Нет данных",w/2,h/2,18,q.text,true,w-24,1);
   lines(d.error.isEmpty()?"Нет проектов для выбранного фильтра":d.error,14,h/2+12,w-28,11,q.muted,false,3);c.restore();return;
  }
  if(m==SQUARE)squareDesign(d,design);else if(m==COMPACT)compactDesign(d,design);else {
   switch(design){case 0:liquid(d,showNews,h);break;case 1:organic(d,h);break;case 2:racing(d,h);break;default:editorial(d,h);break;}
   if(design!=0&&showNews&&!d.title.isEmpty()&&h>=440)newsCard(d,14,397,312,h-407);
   if(!d.error.isEmpty()){circle(237,20,3,q.error);}
  }
  c.restore();
 }
 void fill(Path a,int color){p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawPath(a,p);}
 void scoreText(double val,float x,float y,float size,int col,float max,int align){
  String v=Tracker.pct(val);font(size,col,true);
  float measured=p.measureText(v);if(measured>max)size*=max/measured;
  text(v,x,y,size,col,true,max+1,align);
 }
 void fit(String s,float x,float y,float size,int col,boolean bold,float max,int align){
  font(size,col,bold);float mw=p.measureText(s);if(mw>max&&mw>0)size=Math.max(size*.68f,size*max/mw);
  text(s,x,y,size,col,bold,max,align);
 }
 void bar(float x,float y,float w,float h,double score,int track,int fill){
  rect(x,y,w,h,h/2,track);float f=(float)(w*Math.max(0,Math.min(100,score))/100);
  if(f>0)rect(x,y,f,h,h/2,fill);
 }
 String gpuFps(Tracker.Entry e){return e.gpu.isEmpty()?Tracker.fps(e.fps):e.gpu+" · "+Tracker.fps(e.fps);}
 String fpsValue(Tracker.Entry e){return e.fps>0?Tracker.fps(e.fps).replace(" FPS",""):"—";}
 String count(Tracker.Data d){return d.booted+" / "+d.entries.size();}
 void chip(String s,float x,float y,float w,float h,int bg,int fg){rect(x,y,w,h,h/2,bg);fit(s,x+w/2,y+h/2+4,11,fg,true,w-12,1);}
 void wave(float x,float y,float w,int col){
  p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setStrokeCap(Paint.Cap.ROUND);p.setColor(col);
  Path z=new Path();z.moveTo(x,y);for(int i=0;i<8;i++){float a=x+i*w/8;z.quadTo(a+w/16,y+(i%2==0?-6:6),a+w/8,y);}c.drawPath(z,p);p.setStyle(Paint.Style.FILL);
 }
 void newsCard(Tracker.Data d,float x,float y,float w,float h){
  if(h<29)return;rect(x,y,w,h,20,q.raised);circle(x+17,y+15,10,q.primarySoft);icon("news",x+17,y+15,q.onPrimary);
  text(d.category,x+33,y+18,10,q.text,true,w-100,0);
  text(d.date,x+w-12,y+18,10,q.muted,false,100,2);
  if(h>=44)lines(d.title,x+13,y+27,w-26,12,q.text,true,h>=67?2:1);
 }
 void rankPill(Tracker.Data d,int i,float x,float y,float w,float h,int bg,int fg){
  if(i>=d.entries.size())return;Tracker.Entry e=d.entries.get(i);
  rect(x,y,w,h,h/2,bg);
  text(String.format(Locale.ROOT,"%02d",i+1),x+15,y+h/2+4,11,fg,true,22,0);
  fit(e.name,x+43,y+h/2+5,15,fg,true,w-130,0);
  scoreText(e.score,x+w-16,y+h/2+6,19,fg,78,2);
 }
 void liquid(Tracker.Data d,boolean news,float h){
  Tracker.Entry e=d.entries.get(0);header(d,340);
  Path z=new Path();z.moveTo(14,73);z.quadTo(14,48,40,48);z.lineTo(298,48);z.quadTo(326,48,326,76);
  z.lineTo(326,158);z.quadTo(326,183,298,183);z.lineTo(218,183);z.cubicTo(180,183,178,212,135,212);
  z.lineTo(40,212);z.quadTo(14,212,14,186);z.close();fill(z,q.primary);
  text("Балл трекера",29,77,12,q.onPrimarySoft,false,180,0);
  scoreText(e.score,25,151,73,q.onPrimary,286,0);
  fit(e.name,29,178,19,q.onPrimary,true,280,0);
  bar(29,191,112,5,e.score,DynamicPalette.blend(q.primary,q.onPrimarySoft,.20f),q.onPrimarySoft);
  rect(190,194,136,58,29,q.tertiary);fit(fpsValue(e),206,229,32,q.onTertiary,true,67,0);
  text("FPS",311,216,12,q.onTertiary,true,48,2);text(e.gpu.isEmpty()?"":e.gpu,311,233,10,q.onTertiary,false,57,2);
  fit(count(d),23,247,28,q.text,true,151,0);text("Запускают EXE",25,264,11,q.muted,false,145,0);
  rect(14,277,312,64,27,q.surface);text("Следом за лидером",29,295,11,q.muted,false,180,0);
  for(int i=1;i<Math.min(3,d.entries.size());i++){Tracker.Entry r=d.entries.get(i);float x=i==1?29:177;
   fit(r.name,x,313,13,q.text,true,133,0);scoreText(r.score,x,331,15,i==1?q.text:q.muted,130,0);
  }
  if(d.entries.size()>2){rect(166,301,1,29,0,q.track);}
  if(news&&!d.title.isEmpty()){
   if(h>=440)newsCard(d,14,350,312,h-360);
   else {circle(28,365,12,q.raised);icon("news",28,365,q.text);text(d.title,48,369,11,q.text,false,261,0);}
  }else {text("Среднее топ-3",24,362,11,q.muted,false,160,0);scoreText(d.average,316,373,24,q.text,120,2);}
 }
 void organic(Tracker.Data d,float h){
  Tracker.Entry e=d.entries.get(0);header(d,340);
  flower(120,151,104,q.primary);
  text("Балл трекера",120,109,12,q.onPrimarySoft,false,150,1);
  scoreText(e.score,120,159,52,q.onPrimary,182,1);
  fit(e.name,120,183,17,q.onPrimary,true,166,1);
  if(!e.gpu.isEmpty())chip(e.gpu,68,196,104,24,q.primarySoft,q.onPrimary);
  rect(235,49,90,103,45,q.tertiary);
  fit(fpsValue(e),280,99,39,q.onTertiary,true,75,1);text("FPS",280,120,13,q.onTertiary,true,72,1);
  flower(280,205,46,q.secondary);fit(String.valueOf(d.booted),280,207,28,q.onSecondary,true,64,1);
  text("из "+d.entries.size(),280,224,11,q.onSecondary,false,62,1);text("запусков",280,237,9,q.onSecondary,false,62,1);
  rankPill(d,1,15,269,310,43,q.primarySoft,q.onPrimary);
  rankPill(d,2,15,319,310,43,q.surface,q.text);
  text("Среднее топ-3 · "+Tracker.pct(d.average),170,380,11,q.muted,false,300,1);
 }
 void racing(Tracker.Data d,float h){
  header(d,340);text("Гонка за PC",19,68,27,q.text,true,301,0);
  float[] yy={82,177,248},hh={88,64,56};
  int[] colors={q.primary,q.tertiary,q.primarySoft},inks={q.onPrimary,q.onTertiary,q.onPrimary};
  for(int i=0;i<Math.min(3,d.entries.size());i++){
   Tracker.Entry r=d.entries.get(i);float y=yy[i],rh=hh[i],fw=(float)(312*r.score/100);int color=colors[i],ink=inks[i];
   rect(14,y,312,rh,rh/2,q.surface);
   // Bar represents the exact score; text stays readable even for a short bar.
   rect(14,y,fw,rh,rh/2,color);c.save();
   float cx=14+rh/2,cy=y+rh/2;
   circle(cx,cy,rh/2-8,DynamicPalette.blend(q.surface,q.text,.10f));text("0"+(i+1),cx,cy+6,i==0?25:18,q.text,true,rh-20,1);
   float tx=24+rh,baseline=y+(i==0?32:22),scoreY=y+(i==0?65:45);
   fit(r.name,tx,baseline,i==0?17:13,q.text,true,314-tx,0);scoreText(r.score,tx,scoreY,i==0?32:21,q.text,314-tx,0);
   Path clip=new Path();if(fw>0){clip.addRoundRect(14,y,14+fw,y+rh,Math.min(fw/2,rh/2),rh/2,Path.Direction.CW);c.clipPath(clip);
    circle(cx,cy,rh/2-8,DynamicPalette.blend(color,ink,.12f));text("0"+(i+1),cx,cy+6,i==0?25:18,ink,true,rh-20,1);
    fit(r.name,tx,baseline,i==0?17:13,ink,true,314-tx,0);scoreText(r.score,tx,scoreY,i==0?32:21,ink,314-tx,0);}
   c.restore();
  }
  rect(14,319,96,56,23,q.secondary);fit(Tracker.fps(d.entries.get(0).fps),62,345,20,q.onSecondary,true,82,1);text("Лидер",62,362,10,q.onSecondary,false,80,1);
  rect(116,319,108,56,23,q.surface);fit(count(d),170,345,21,q.text,true,96,1);text("Запускают EXE",170,362,10,q.muted,false,98,1);
  rect(230,319,96,56,23,q.tertiary);scoreText(d.average,278,345,22,q.onTertiary,84,1);text("Топ-3",278,362,10,q.onTertiary,false,80,1);
 }
 void editorial(Tracker.Data d,float h){
  Tracker.Entry e=d.entries.get(0);header(d,340);
  String val=Tracker.pct(e.score);int comma=val.indexOf(',');String big=comma>0?val.substring(0,comma):val.replace("%","");String rest=comma>0?val.substring(comma):"%";
  int bigInk=q.dark?q.primary:q.onPrimary,smallInk=q.dark?q.tertiary:q.onTertiary;
  font(113,bigInk,true);float bigSize=Math.min(113,165f/Math.max(1,p.measureText(big))*113);font(bigSize,bigInk,true);
  float end=17+p.measureText(big);text(big,17,150,bigSize,bigInk,true,168,0);
  text(rest,end+2,120,43,smallInk,true,Math.min(118,326-end-5),0);
  text("Балл трекера",end+5,144,11,q.muted,false,Math.min(145,326-end-5),0);
  flower(285,76,32,q.tertiary);fit(fpsValue(e),285,78,24,q.onTertiary,true,50,1);text("FPS",285,94,10,q.onTertiary,true,42,1);
  bar(20,168,300,13,e.score,q.track,q.dark?q.primary:q.onPrimarySoft);
  circle(20+(float)(300*e.score/100),174.5f,8,q.dark?q.tertiary:q.onTertiary);
  fit(e.name,21,213,24,q.text,true,219,0);
  if(!e.gpu.isEmpty())chip(e.gpu,250,192,70,27,q.secondary,q.onSecondary);
  rect(20,234,129,80,23,q.primary);fit(count(d),33,276,29,q.onPrimary,true,103,0);text("Запускают EXE",33,296,11,q.onPrimarySoft,false,105,0);
  for(int i=1;i<Math.min(3,d.entries.size());i++){Tracker.Entry v=d.entries.get(i);float y=234+(i-1)*43;rect(157,y,163,37,19,q.surface);
   fit(v.name,169,y+23,12,q.text,true,86,0);scoreText(v.score,308,y+24,14,q.text,55,2);}
  rect(23,336,294,1,0,q.track);text("Среднее топ-3",23,356,11,q.muted,false,150,0);scoreText(d.average,23,375,18,bigInk,130,0);
  text("Обновлено "+time(d),317,375,11,q.muted,false,150,2);
 }
 void squareDesign(Tracker.Data d,int design){
  Tracker.Entry e=d.entries.get(0);text("PortWatch VI",90,19,12,q.text,true,160,1);
  switch(design){
   case 0:{
    Path a=new Path();a.addRoundRect(8,30,172,136,new float[]{28,28,28,28,14,14,36,36},Path.Direction.CW);fill(a,q.primary);
    text("Балл трекера",20,51,10,q.onPrimarySoft,false,140,0);scoreText(e.score,20,96,42,q.onPrimary,144,0);fit(e.name,20,118,15,q.onPrimary,true,140,0);
    chip(Tracker.fps(e.fps),9,145,88,27,q.tertiary,q.onTertiary);text(time(d),165,162,11,q.muted,false,58,2);break;}
   case 1:
    flower(90,87,57,q.primary);scoreText(e.score,90,87,33,q.onPrimary,100,1);text(Tracker.fps(e.fps),90,108,12,q.onPrimarySoft,true,90,1);fit(e.name,90,156,15,q.text,true,160,1);text(time(d),90,177,11,q.muted,false,140,1);break;
   case 2:
    for(int i=0;i<Math.min(3,d.entries.size());i++){Tracker.Entry r=d.entries.get(i);float y=31+i*43;int color=i==1?q.tertiary:q.primary,ink=i==1?q.onTertiary:q.onPrimary;rect(8,y,164,38,19,q.surface);rect(8,y,(float)(164*r.score/100),38,19,color);
     // Use a tonal overlay so labels stay legible at any progress value.
     rect(15,y+5,150,28,14,q.surface);fit(r.name,21,y+23,12,q.text,true,88,0);scoreText(r.score,158,y+24,14,q.text,54,2);}
    text(Tracker.fps(e.fps)+" · "+time(d),90,177,11,q.muted,false,160,1);break;
   default:
    int ink=q.dark?q.primary:q.onPrimary;scoreText(e.score,90,91,48,ink,164,1);bar(14,102,152,8,e.score,q.track,ink);
    fit(e.name,90,136,16,q.text,true,160,1);chip(Tracker.fps(e.fps),14,147,86,27,q.tertiary,q.onTertiary);text(time(d),165,165,10,q.muted,false,60,2);break;
  }
 }
 void compactDesign(Tracker.Data d,int design){
  Tracker.Entry e=d.entries.get(0);header(d,340);
  switch(design){
   case 0:{
    Path a=new Path();a.addRoundRect(10,40,330,112,new float[]{28,28,28,28,28,28,13,13},Path.Direction.CW);fill(a,q.primary);
    scoreText(e.score,23,92,44,q.onPrimary,157,0);fit(e.name,185,75,18,q.onPrimary,true,128,0);text(gpuFps(e),185,93,11,q.onPrimarySoft,true,128,0);
    bar(14,130,150,8,e.score,q.track,q.dark?q.primary:q.onPrimary);text(count(d)+" · EXE",325,139,14,q.text,true,150,2);break;}
   case 1:
    flower(76,101,58,q.primary);scoreText(e.score,76,100,34,q.onPrimary,108,1);text("БАЛЛ",76,118,10,q.onPrimarySoft,true,100,1);
    text("Лидер гонки",145,54,11,q.muted,false,180,0);fit(e.name,145,77,21,q.text,true,180,0);chip(Tracker.fps(e.fps),145,86,86,25,q.tertiary,q.onTertiary);
    if(!e.gpu.isEmpty())chip(e.gpu,236,86,90,25,q.secondary,q.onSecondary);chip(count(d)+" · Запускают EXE",145,117,181,27,q.surface,q.text);break;
   case 2:
    for(int i=0;i<Math.min(3,d.entries.size());i++){Tracker.Entry r=d.entries.get(i);float y=40+i*38;int color=i==1?q.tertiary:q.primary,ink=i==1?q.onTertiary:q.onPrimary;rect(10,y,320,33,17,q.surface);rect(10,y,(float)(320*r.score/100),33,17,color);
     rect(19,y+4,302,25,13,q.surface);text("0"+(i+1),29,y+21,12,q.text,true,24,0);fit(r.name,57,y+21,14,q.text,true,168,0);scoreText(r.score,311,y+22,17,q.text,75,2);}
    break;
   default:{
    int ink=q.dark?q.primary:q.onPrimary;scoreText(e.score,15,101,61,ink,190,0);bar(16,114,180,9,e.score,q.track,ink);
    flower(267,76,34,q.tertiary);fit(fpsValue(e),267,78,26,q.onTertiary,true,54,1);text("FPS",267,95,10,q.onTertiary,true,45,1);
    fit(e.name,207,130,15,q.text,true,118,0);text(count(d)+" · EXE",16,146,11,q.muted,false,180,0);break;}
  }
 }
}
