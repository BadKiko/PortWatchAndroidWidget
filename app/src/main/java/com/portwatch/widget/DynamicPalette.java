package com.portwatch.widget;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;

final class DynamicPalette {
 final boolean dark;
 final int bg,surface,raised,text,muted,primary,onPrimary,primarySoft,onPrimarySoft,secondary,onSecondary,tertiary,onTertiary,track,outline,error;
 DynamicPalette(Context c){
  String mode=Settings.theme(c);
  dark=mode.equals("dark")||(!mode.equals("light")&&(c.getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES);
  bg=tone(c,"neutral1",dark?900:10,dark?0xff1d1a29:0xfffcf7ff);
  surface=tone(c,"neutral2",dark?800:100,dark?0xff2c2739:0xffeee7f5);
  raised=tone(c,"neutral2",dark?700:50,dark?0xff393248:0xfff5eefb);
  text=tone(c,"neutral1",dark?50:900,dark?0xffe8e0f1:0xff211a2c);
  muted=tone(c,"neutral2",dark?200:700,dark?0xffc8bdd9:0xff534a62);
  primary=tone(c,"accent1",dark?100:100,0xffd5c5f1);
  onPrimary=tone(c,"accent1",900,0xff241934);
  primarySoft=tone(c,"accent1",200,0xffb9a8d8);
  onPrimarySoft=tone(c,"accent1",800,0xff49365f);
  secondary=tone(c,"accent2",dark?700:100,dark?0xff393248:0xffe8dff3);
  onSecondary=tone(c,"accent2",dark?100:900,dark?0xffe8def8:0xff302438);
  tertiary=tone(c,"accent3",dark?100:100,0xffe5b9cf);
  onTertiary=tone(c,"accent3",900,0xff382232);
  track=tone(c,"neutral2",dark?600:200,dark?0xff484053:0xffd7cfe1);
  outline=tone(c,"neutral2",dark?500:400,dark?0xff8d849d:0xffa39aaf);
  error=dark?0xffffb4ab:0xffa52620;
 }
 static int tone(Context c,String family,int level,int fallback){
  if(Build.VERSION.SDK_INT<31)return fallback;
  int id=c.getResources().getIdentifier("system_"+family+"_"+level,"color","android");
  if(id==0)return fallback;
  try{return c.getColor(id);}catch(Exception ignored){return fallback;}
 }
 static int blend(int a,int b,float t){return Color.rgb(Math.round(Color.red(a)*(1-t)+Color.red(b)*t),Math.round(Color.green(a)*(1-t)+Color.green(b)*t),Math.round(Color.blue(a)*(1-t)+Color.blue(b)*t));}
}
