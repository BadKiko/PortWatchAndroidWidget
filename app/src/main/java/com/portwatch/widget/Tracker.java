package com.portwatch.widget;
import android.content.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

final class Tracker {
 static final String SITE="https://portwatch-vi.xyz/";
 static final String URL=SITE+"data/tracker.json";
 static SharedPreferences prefs(Context c){return c.getSharedPreferences("tracker",Context.MODE_PRIVATE);}
 static class Entry {
  String name,gpu; double score,fps; boolean booted;
  Entry(JSONObject e){JSONObject g=e.optJSONObject("gta");if(g==null)g=new JSONObject();name=e.optString("name","Без названия");score=score(e);fps=Math.max(0,num(g.opt("fps")));booted=unit(g.opt("loads"))==1;gpu=gpu(e,g);}
 }
 static String gpu(JSONObject e,JSONObject g){
  StringBuilder s=new StringBuilder();
  for(String k:new String[]{"gpu","gpus","gpuVendor","gpuVendors","vendor","vendors","hardware","platform","platforms","tags","testedOn","testGpu","gpuSupport"}){s.append(" ").append(e.optString(k)).append(" ").append(g.optString(k));}
  String v=s.toString().toLowerCase(Locale.ROOT);
  boolean amd=v.contains("amd")||v.contains("radeon")||v.matches(".*\\brx\\s*\\d{4}.*"),nv=v.contains("nvidia")||v.contains("geforce")||v.contains("rtx")||v.contains("gtx");
  return amd&&nv?"AMD / NVIDIA":amd?"AMD":nv?"NVIDIA":"";
 }
 static Data filtered(Context c,Data d){
  String f=Settings.gpu(c);if(f.equals("all"))return d;
  Data x=new Data();x.title=d.title;x.dek=d.dek;x.date=d.date;x.category=d.category;x.error=d.error;x.updated=d.updated;
  for(Entry e:d.entries)if(e.gpu.toLowerCase(Locale.ROOT).contains(f)){x.entries.add(e);if(e.booted)x.booted++;}
  int n=Math.min(3,x.entries.size());for(int i=0;i<n;i++)x.average+=x.entries.get(i).score/n;return x;
 }
 static class Data {List<Entry> entries=new ArrayList<>();int booted;double average;String title="",dek="",category="НОВОСТИ",date="",error="";long updated;}
 static double num(Object v){if(v instanceof Boolean)return (Boolean)v?1:0;try{double n=Double.parseDouble(String.valueOf(v));return Double.isFinite(n)?n:0;}catch(Exception e){return 0;}}
 static double unit(Object v){return Math.max(0,Math.min(1,num(v)));}
 static double score(JSONObject e){
  if(e.opt("manualScore") instanceof Number)return Math.max(0,Math.min(100,num(e.opt("manualScore"))));
  JSONObject g=e.optJSONObject("gta"),w=e.optJSONObject("groundwork");if(g==null)g=new JSONObject();
  double s=w==null?0:10*(.2*unit(w.opt("loader"))+.28*unit(w.opt("shaders"))+.32*unit(w.opt("graphics"))+.2*unit(w.opt("proven")));
  String[] keys={"loads","boots","menu","world","playable"};boolean hit=false;
  for(int i=0;i<keys.length;i++){double v=unit(g.opt(keys[i]));double tick=10*(i+1);if(v==1){s=tick;hit=i==4;}else{if(v>0)s+=v*(tick-s);break;}}
  if(hit){Object st=g.opt("stability");double stability=st instanceof Number?unit(st):switch(String.valueOf(st)){case "stable"->1;case "rarely"->.8;case "sometimes"->.55;case "often"->.25;default->0;};s=50+17.5*unit(g.opt("story"))+12.5*unit(g.opt("render"))+10*unit(num(g.opt("fps"))/30)+10*stability;}
  return Math.round(Math.max(0,Math.min(100,s))*10)/10.0;
 }
 static String stage(double s){return s>=100?"Завершено":s>=50?"Играбельность":s>=40?"Игровой мир":s>=30?"Главное меню":s>=20?"Первые кадры":s>=10?"Запуск EXE":"Подготовка";}
 static String pct(double n){return String.format(Locale.forLanguageTag("ru-RU"),"%.1f%%",n);}
 static String fps(double n){return n>0?String.format(Locale.forLanguageTag("ru-RU"),n==Math.rint(n)?"%.0f FPS":"%.1f FPS",n):"FPS —";}
 static long newsTime(JSONObject n){for(String k:new String[]{"publishedAt","date"}){String v=n.optString(k);try{return Instant.parse(v).toEpochMilli();}catch(Exception ignored){}try{return OffsetDateTime.parse(v).toInstant().toEpochMilli();}catch(Exception ignored){}try{return LocalDate.parse(v).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();}catch(Exception ignored){}}return 0;}
 static JSONObject latest(JSONObject j){JSONArray a=j.optJSONArray("news");JSONObject best=null;if(a!=null)for(int i=0;i<a.length();i++){JSONObject n=a.optJSONObject(i);if(n!=null&&(best==null||newsTime(n)>newsTime(best)))best=n;}return best;}
 static String category(String k){return switch(k.toLowerCase(Locale.ROOT)){case "port"->"ПОРТ";case "takedown"->"DMCA";case "leak"->"УТЕЧКА";case "legal"->"ПРАВО";case "scam"->"БЕЗОПАСНОСТЬ";default->"НОВОСТИ";};}
 static Data read(Context c){SharedPreferences p=prefs(c);Data d=new Data();d.updated=p.getLong("updated",0);d.error=p.getString("error","");try{
  JSONObject j=new JSONObject(p.getString("json","{}"));JSONArray a=j.optJSONArray("efforts");if(a!=null)for(int i=0;i<a.length();i++){JSONObject e=a.optJSONObject(i);if(e!=null&&!e.optBoolean("nuked",false)){Entry v=new Entry(e);d.entries.add(v);if(v.booted)d.booted++;}}
  d.entries.sort((a1,b)->Double.compare(b.score,a1.score));int count=Math.min(3,d.entries.size());for(int i=0;i<count;i++)d.average+=d.entries.get(i).score/count;
  JSONObject n=latest(j);if(n!=null){d.title=n.optString("title");d.dek=n.optString("dek");d.category=category(n.optString("category"));long t=newsTime(n);if(t>0)d.date=Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM · HH:mm"));if(d.title.equals(p.getString("translationSource",""))){d.title=p.getString("translationTitle",d.title);d.dek=p.getString("translationDek",d.dek);}}
 }catch(Exception ignored){}return d;}
 static String get(String url,int timeout)throws Exception{HttpURLConnection h=(HttpURLConnection)new URL(url).openConnection();h.setConnectTimeout(timeout);h.setReadTimeout(timeout);h.setRequestProperty("User-Agent","Mozilla/5.0 (Android) PortWatchWidget/1.0");try{int status=h.getResponseCode();if(status!=200)throw new IOException("HTTP "+status);try(InputStream in=h.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1){out.write(b,0,n);if(out.size()>8*1024*1024)throw new IOException("Ответ слишком большой");}return out.toString(StandardCharsets.UTF_8.name());}}finally{h.disconnect();}}
 static String translate(String s)throws Exception{if(s.isEmpty())return s;String url="https://clients5.google.com/translate_a/t?client=dict-chrome-ex&sl=en&tl=ru&q="+URLEncoder.encode(s,"UTF-8");JSONArray a=new JSONArray(get(url,2500));Object first=a.opt(0);if(first instanceof String)return (String)first;if(first instanceof JSONArray){StringBuilder b=new StringBuilder();JSONArray rows=(JSONArray)first;for(int i=0;i<rows.length();i++){JSONArray row=rows.optJSONArray(i);if(row!=null)b.append(row.optString(0));}if(b.length()>0)return b.toString();}return s;}
 static synchronized void refresh(Context c){SharedPreferences p=prefs(c);try{String raw=get(URL,6000);JSONObject j=new JSONObject(raw);if(j.optJSONArray("efforts")==null)throw new IOException("Неожиданный формат данных");p.edit().putString("json",raw).putLong("updated",System.currentTimeMillis()).putString("error","").apply();JSONObject n=latest(j);if(n!=null){String title=n.optString("title");if(!title.equals(p.getString("translationSource",""))){try{String t=translate(title),dek=translate(n.optString("dek"));p.edit().putString("translationSource",title).putString("translationTitle",t).putString("translationDek",dek).apply();}catch(Exception ignored){}}}}catch(Exception e){p.edit().putString("error",e.getMessage()==null?"Нет связи":e.getMessage()).apply();}}
}
