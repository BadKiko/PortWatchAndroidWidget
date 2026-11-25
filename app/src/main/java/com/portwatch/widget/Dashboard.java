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
 static int mode(float w,float h){if(w<230&&h/w>=.65f)return SQUARE;return h/w<.83f?COMPACT:LARGE;}
 static Bitmap bitmap(Context ctx,Tracker.Data d,int w,int h,float density){
  // Cap the bitmap to stay safely within the AppWidgetService memory budget.
  float scale=Math.min(density,Math.min(1000f/w,1000f/h));int bw=Math.max(1,Math.round(w*scale)),bh=Math.max(1,Math.round(h*scale));
  Bitmap b=Bitmap.createBitmap(bw,bh,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);c.scale(bw/(float)w,bh/(float)h);new Dashboard(c,new DynamicPalette(ctx)).draw(d,w,h,Settings.news(ctx));return b;
 }
 void draw(Tracker.Data d,float width,float height,boolean showNews){
  if(width<1||height<1)return;int m=mode(width,height);float w=m==SQUARE?180:340;
  float h=m==SQUARE?190:m==COMPACT?166:Math.max(300,Math.min(600,height*w/width));
  float sc=Math.min(width/w,height/h);c.save();c.translate((width-w*sc)/2,(height-h*sc)/2);c.scale(sc,sc);rect(0,0,w,h,28,q.bg);
  if(d.entries.isEmpty()){header(d,w);text("Нет данных",w/2,h/2,18,q.text,true,w-24,1);lines(d.error.isEmpty()?"Нет проектов для выбранного фильтра":d.error,14,h/2+12,w-28,11,q.muted,false,3);c.restore();return;}
  if(m==SQUARE)square(d,w,h);else if(m==COMPACT)compact(d,w,h);else large(d,w,h,showNews);c.restore();
 }
 void square(Tracker.Data d,float w,float h){
  Tracker.Entry e=d.entries.get(0);text("PortWatch VI",w/2,19,12,q.text,true,w-20,1);meter(w/2,86,57,e,true);text(e.name,w/2,155,15,q.text,true,w-18,1);icon("refresh",w/2-23,173,q.muted);text(time(d),w/2+9,177,10,q.muted,false,48,1);
 }
 void compact(Tracker.Data d,float w,float h){
  header(d,w);Tracker.Entry e=d.entries.get(0);meter(76,101,58,e,false);
  float x=145; text("Лидер гонки",x,54,11,q.muted,false,180,0);text(e.name,x,77,21,q.text,true,180,0);
  rect(x,86,86,25,13,q.tertiary);text(Tracker.fps(e.fps),x+43,103,13,q.onTertiary,true,78,1);
  String gpu=e.gpu.isEmpty()?"":e.gpu;
  if(!gpu.isEmpty()){rect(x+91,86,90,25,13,q.secondary);text(gpu,x+136,103,11,q.onSecondary,true,80,1);}
  rect(x,117,181,27,14,q.surface);text(d.booted+" / "+d.entries.size()+" · Запускают EXE",x+90.5f,135,11,q.text,true,169,1);
 }
 void large(Tracker.Data d,float w,float h,boolean news){
  header(d,w);Tracker.Entry e=d.entries.get(0);float y=40,inner=w-20;
  float hero=h>=390?124:104;rect(10,y,inner,hero,27,q.primary);
  float radius=hero*.44f;float cx=w-10-radius-9,cy=y+hero/2;
  text("Лидер гонки",25,y+25,12,q.onPrimarySoft,false,cx-radius-31,0);
  text(e.name,25,y+50,22,q.onPrimary,true,cx-radius-31,0);
  String tag=e.gpu.isEmpty()?Tracker.fps(e.fps):e.gpu+" · "+Tracker.fps(e.fps);
  float chip=Math.min(cx-radius-30,Math.max(76,tag.length()*6.5f+14));rect(25,y+60,chip,24,12,q.primarySoft);text(tag,25+chip/2,y+76,11,q.onPrimary,true,chip-10,1);
  meter(cx,cy,radius,e,false);
  y+=hero+7;float half=(inner-7)/2, mh=h>=390?68:52;
  rect(10,y,half,mh,23,q.secondary);rect(17+half,y,half,mh,23,q.tertiary);
  if(mh>=60){float iy=y+18;float ix=10+half-20,jx=w-30;circle(ix,iy,12,DynamicPalette.blend(q.secondary,q.onSecondary,.12f));circle(jx,iy,12,DynamicPalette.blend(q.tertiary,q.onTertiary,.12f));icon("trend",ix,iy,q.onSecondary);icon("play",jx,iy,q.onTertiary);}
  float numY=y+(mh>=60?34:25);float numW=half-(mh>=60?47:26);text(Tracker.pct(d.average),23,numY,25,q.onSecondary,true,numW,0);text(d.booted+" / "+d.entries.size(),30+half,numY,25,q.onTertiary,true,numW,0);
  text("Среднее топ-3",23,y+mh-10,11,q.onSecondary,false,half-26,0);text("Запускают EXE",30+half,y+mh-10,11,q.onTertiary,false,half-26,0);
  y+=mh+7;float footer=16, avail=h-y-footer;int n=Math.min(3,d.entries.size());
  float rowH=h>=390?27:22, rankH=22+n*rowH+6;
  if(avail>=rankH){rect(10,y,inner,rankH,23,q.surface);text("Лидеры",23,y+17,12,q.text,true,inner-26,0);
   for(int i=0;i<n;i++){Tracker.Entry v=d.entries.get(i);float ry=y+22+i*rowH;int accent=i==1?q.tertiary:q.primarySoft;int ink=i==1?q.onTertiary:q.onPrimary;
    if(i==0)flower(33,ry+rowH/2,10,accent);else if(i==1)rect(24,ry+rowH/2-9,18,18,6,accent);else circle(33,ry+rowH/2,9,accent);
    text(String.valueOf(i+1),33,ry+rowH/2+4,11,ink,true,15,1);
    text(v.name,49,ry+10,12,q.text,true,205,0);text(Tracker.pct(v.score),w-23,ry+10,12,q.text,true,70,2);
    rect(49,ry+15,205,4,2,q.track);rect(49,ry+15,(float)(205*v.score/100),4,2,accent);
   }y+=rankH+7;
  }
  avail=h-y-footer;
  if(news&&!d.title.isEmpty()&&avail>=43){float nh=Math.min(avail,86);rect(10,y,inner,nh,23,q.raised);circle(34,y+20,12,q.primarySoft);icon("news",34,y+20,q.onPrimary);text("Последняя новость",53,y+15,10,q.text,true,170,0);text(d.date,w-23,y+15,10,q.muted,false,94,2);
   if(nh>=61)lines(d.title,23,y+35,inner-26,13,q.text,true,nh>=76?3:2);else text(d.title,53,y+32,11,q.text,true,inner-66,0);
  }
  text(d.error.isEmpty()?"Балл трекера ≠ готовность порта":"Нет связи · показан кеш",14,h-7,9,d.error.isEmpty()?q.muted:q.error,false,w-28,0);
 }
}
