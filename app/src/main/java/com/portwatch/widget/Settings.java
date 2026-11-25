package com.portwatch.widget;
import android.content.*;
final class Settings {
 static SharedPreferences prefs(Context c){return c.getSharedPreferences("appearance",0);}
 static int design(Context c){return Math.max(0,Math.min(3,prefs(c).getInt("design",1)));}
 static void design(Context c,int v){prefs(c).edit().putInt("design",Math.max(0,Math.min(3,v))).apply();}
 static String theme(Context c){return prefs(c).getString("theme","system");}
 static String gpu(Context c){return prefs(c).getString("gpu","all");}
 static int interval(Context c){return prefs(c).getInt("interval",30);}
 static boolean news(Context c){return prefs(c).getBoolean("news",true);}
 static void put(Context c,String k,String v){prefs(c).edit().putString(k,v).apply();}
 static void interval(Context c,int v){prefs(c).edit().putInt("interval",v).apply();}
 static void news(Context c,boolean v){prefs(c).edit().putBoolean("news",v).apply();}
}
