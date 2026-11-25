package com.portwatch.widget;
import android.graphics.*;
import android.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

final class Dashboard {
 static final int BG=0xff141218, PRIMARY=0xff4f378b, ON_PRIMARY=0xffeaddff, ACCENT=0xffd0bcff, SECONDARY=0xff4a4458, ON_SECONDARY=0xffe8def8, GREEN=0xff214f39, ON_GREEN=0xffc5f7d9, HIGH=0xff2b2930, SURFACE=0xff211f26, TEXT=0xffe6e1e5, MUTED=0xffcac4d0, DIM=0xffaaa4b0;
 final Canvas c; final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); final TextPaint tp=new TextPaint(Paint.ANTI_ALIAS_FLAG);
 Dashboard(Canvas c){this.c=c;}
 void rect(float x,float y,float w,float h,float r,int color){if(w<=0||h<=0)return;p.setColor(color);c.drawRoundRect(x,y,x+w,y+h,r,r,p);}
 void text(String s,float x,float y,float size,int color,boolean bold,float max,boolean right){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));tp.set(p);String v=TextUtils.ellipsize(s==null?"":s,tp,Math.max(1,max),TextUtils.TruncateAt.END).toString();p.setTextAlign(right?Paint.Align.RIGHT:Paint.Align.LEFT);c.drawText(v,x,y,p);p.setTextAlign(Paint.Align.LEFT);}
 void lines(String s,float x,float y,float w,float size,int color,boolean bold,int count){tp.setColor(color);tp.setTextSize(size);tp.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));StaticLayout l=StaticLayout.Builder.obtain(s,0,s.length(),tp,Math.max(1,(int)w)).setIncludePad(false).setMaxLines(count).setEllipsize(TextUtils.TruncateAt.END).setLineSpacing(1,1).build();c.save();c.translate(x,y);l.draw(c);c.restore();}
 static Bitmap bitmap(Tracker.Data d,int dpW,int dpH,float density){float scale=Math.min(density,Math.min(1200f/dpW,1200f/dpH));int w=Math.max(1,Math.round(dpW*scale)),h=Math.max(1,Math.round(dpH*scale));Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);c.scale(w/(float)dpW,h/(float)dpH);new Dashboard(c).draw(d,dpW,dpH);return b;}
 void draw(Tracker.Data d,float width,float height){
  if(width<=0||height<=0)return;
  float W=340,H=height*W/width,scale=width/W;
  if(H<155){W=width*155/height;H=155;scale=height/H;}
  c.save();c.scale(scale,scale);rect(0,0,W,H,28,BG);
  float m=10,inner=W-2*m;
  String time=d.updated==0?"—":Instant.ofEpochMilli(d.updated).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"));
  text("PORTWATCH VI",m+3,22,12,ACCENT,true,inner-100,false);
  rect(W-m-75,6,75,23,12,GREEN);text("↻ "+time,W-m-8,21,11,ON_GREEN,true,65,true);
  if(d.entries.isEmpty()){rect(m,34,inner,H-45,20,SURFACE);text("Нет данных",m+13,60,19,TEXT,true,inner-26,false);lines(d.error.isEmpty()?"Нажми ↻ для загрузки рейтинга":d.error,m+13,70,inner-26,13,MUTED,false,3);c.restore();return;}
  Tracker.Entry e=d.entries.get(0);float y=34;
  boolean tiny=H<220,compact=H<315,tall=H>=430;
  float hero=tiny?83:compact?92:104;
  rect(m,y,inner,hero,23,PRIMARY);
  float pad=13,left=m+pad,right=W-m-pad;
  text("ЛИДЕР ГОНКИ",left,y+19,10,ON_PRIMARY,true,inner-26,false);
  float valSize=compact?29:33;
  p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(valSize);float vw=p.measureText(Tracker.pct(e.score));
  text(e.name,left,y+43,20,ON_PRIMARY,true,Math.max(40,inner-26-vw-8),false);
  text(Tracker.pct(e.score),right,y+43,valSize,ON_PRIMARY,true,inner/2,true);
  text(Tracker.fps(e.fps)+" · "+Tracker.stage(e.score),left,y+59,11,ON_PRIMARY,false,inner-26,false);
  float barY=y+hero-13,bw=(inner-26-9*4)/10f;
  for(int i=0;i<10;i++){float x=left+i*(bw+4);rect(x,barY,bw,5,3,0xff8d76c0);float fill=(float)Math.max(0,Math.min(1,e.score/10-i));if(fill>0)rect(x,barY,bw*fill,5,3,ON_PRIMARY);}
  y+=hero+7;
  if(!tiny||H>=190){float sh=compact?45:55,half=(inner-7)/2;rect(m,y,half,sh,17,SECONDARY);rect(m+half+7,y,half,sh,17,GREEN);text(Tracker.pct(d.average),m+11,y+23,21,ON_SECONDARY,true,half-22,false);text("Среднее топ-3",m+11,y+sh-8,10,ON_SECONDARY,false,half-22,false);text(d.booted+" / "+d.entries.size(),m+half+18,y+23,21,ON_GREEN,true,half-22,false);text("Запускают EXE",m+half+18,y+sh-8,10,ON_GREEN,false,half-22,false);y+=sh+7;}
  float footer=17,available=H-y-footer;int n=Math.min(3,d.entries.size());
  float row=compact?19:22,rh=21+n*row+5;
  if(!tiny&&available>=rh){rect(m,y,inner,rh,18,HIGH);text("ТОП ПРОЕКТОВ",left,y+15,10,MUTED,true,inner-26,false);for(int i=0;i<n;i++){Tracker.Entry r=d.entries.get(i);float ry=y+30+i*row;text(String.format("%02d",i+1),left,ry,11,i==0?ACCENT:DIM,true,24,false);text(r.name,left+26,ry,13,TEXT,i==0,inner-106,false);text(Tracker.pct(r.score),right,ry,13,ACCENT,true,70,true);}y+=rh+7;}
  available=H-y-footer;
  if(!d.title.isEmpty()&&available>=46){rect(m,y,inner,available,18,SURFACE);rect(left,y+6,Math.min(100,Math.max(44,d.category.length()*7+12)),17,8,SECONDARY);text(d.category,left+6,y+18,10,ON_SECONDARY,true,88,false);text(d.date,right,y+18,10,DIM,false,116,true);int titleLines=available>=62?2:1;lines(d.title,left,y+27,inner-26,13,TEXT,true,titleLines);if(available>=100&&!d.dek.isEmpty())lines(d.dek,left,y+62,inner-26,12,MUTED,false,Math.max(1,Math.min(tall?5:3,(int)((available-68)/14))));}
  text(d.error.isEmpty()?"Балл трекера ≠ готовность порта":"Нет связи · показан кеш",m+3,H-5,9,d.error.isEmpty()?DIM:0xffffb4ab,false,inner-6,false);c.restore();
 }
}
