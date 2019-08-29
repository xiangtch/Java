/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetaIndex
/*     */ {
/*     */   private static volatile Map<File, MetaIndex> jarMap;
/*     */   private String[] contents;
/*     */   private boolean isClassOnlyJar;
/*     */   
/*     */   public static MetaIndex forJar(File paramFile)
/*     */   {
/* 147 */     return (MetaIndex)getJarMap().get(paramFile);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void registerDirectory(File paramFile)
/*     */   {
/* 162 */     File localFile = new File(paramFile, "meta-index");
/* 163 */     if (localFile.exists()) {
/*     */       try {
/* 165 */         BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
/* 166 */         String str1 = null;
/* 167 */         String str2 = null;
/* 168 */         boolean bool = false;
/* 169 */         ArrayList localArrayList = new ArrayList();
/* 170 */         Map localMap = getJarMap();
/*     */         
/*     */ 
/* 173 */         paramFile = paramFile.getCanonicalFile();
/*     */         
/*     */ 
/*     */ 
/* 177 */         str1 = localBufferedReader.readLine();
/* 178 */         if ((str1 == null) || 
/* 179 */           (!str1.equals("% VERSION 2"))) {
/* 180 */           localBufferedReader.close();
/* 181 */           return;
/*     */         }
/* 183 */         while ((str1 = localBufferedReader.readLine()) != null) {
/* 184 */           switch (str1.charAt(0))
/*     */           {
/*     */           case '!': 
/*     */           case '#': 
/*     */           case '@': 
/* 189 */             if ((str2 != null) && (localArrayList.size() > 0)) {
/* 190 */               localMap.put(new File(paramFile, str2), new MetaIndex(localArrayList, bool));
/*     */               
/*     */ 
/*     */ 
/* 194 */               localArrayList.clear();
/*     */             }
/*     */             
/* 197 */             str2 = str1.substring(2);
/* 198 */             if (str1.charAt(0) == '!') {
/* 199 */               bool = true;
/* 200 */             } else if (bool) {
/* 201 */               bool = false;
/*     */             }
/*     */             
/*     */ 
/*     */             break;
/*     */           case '%': 
/*     */             break;
/*     */           default: 
/* 209 */             localArrayList.add(str1);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 214 */         if ((str2 != null) && (localArrayList.size() > 0)) {
/* 215 */           localMap.put(new File(paramFile, str2), new MetaIndex(localArrayList, bool));
/*     */         }
/*     */         
/*     */ 
/* 219 */         localBufferedReader.close();
/*     */       }
/*     */       catch (IOException localIOException) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean mayContain(String paramString)
/*     */   {
/* 236 */     if ((this.isClassOnlyJar) && (!paramString.endsWith(".class"))) {
/* 237 */       return false;
/*     */     }
/*     */     
/* 240 */     String[] arrayOfString = this.contents;
/* 241 */     for (int i = 0; i < arrayOfString.length; i++) {
/* 242 */       if (paramString.startsWith(arrayOfString[i])) {
/* 243 */         return true;
/*     */       }
/*     */     }
/* 246 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private MetaIndex(List<String> paramList, boolean paramBoolean)
/*     */     throws IllegalArgumentException
/*     */   {
/* 255 */     if (paramList == null) {
/* 256 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/* 259 */     this.contents = ((String[])paramList.toArray(new String[0]));
/* 260 */     this.isClassOnlyJar = paramBoolean;
/*     */   }
/*     */   
/*     */   private static Map<File, MetaIndex> getJarMap() {
/* 264 */     if (jarMap == null) {
/* 265 */       synchronized (MetaIndex.class) {
/* 266 */         if (jarMap == null) {
/* 267 */           jarMap = new HashMap();
/*     */         }
/*     */       }
/*     */     }
/* 271 */     assert (jarMap != null);
/* 272 */     return jarMap;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\MetaIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */