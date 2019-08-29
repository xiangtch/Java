/*     */ package sun.misc;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.security.AccessController;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import sun.security.action.GetPropertyAction;
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
/*     */ public class JarIndex
/*     */ {
/*     */   private HashMap<String, LinkedList<String>> indexMap;
/*     */   private HashMap<String, LinkedList<String>> jarMap;
/*     */   private String[] jarFiles;
/*     */   public static final String INDEX_NAME = "META-INF/INDEX.LIST";
/*  76 */   private static final boolean metaInfFilenames = "true"
/*  77 */     .equals(AccessController.doPrivileged(new GetPropertyAction("sun.misc.JarIndex.metaInfFilenames")));
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JarIndex()
/*     */   {
/*  84 */     this.indexMap = new HashMap();
/*  85 */     this.jarMap = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JarIndex(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/*  94 */     this();
/*  95 */     read(paramInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JarIndex(String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/* 104 */     this();
/* 105 */     this.jarFiles = paramArrayOfString;
/* 106 */     parseJars(paramArrayOfString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JarIndex getJarIndex(JarFile paramJarFile)
/*     */     throws IOException
/*     */   {
/* 119 */     return getJarIndex(paramJarFile, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JarIndex getJarIndex(JarFile paramJarFile, MetaIndex paramMetaIndex)
/*     */     throws IOException
/*     */   {
/* 129 */     JarIndex localJarIndex = null;
/*     */     
/*     */ 
/*     */ 
/* 133 */     if ((paramMetaIndex != null) && 
/* 134 */       (!paramMetaIndex.mayContain("META-INF/INDEX.LIST"))) {
/* 135 */       return null;
/*     */     }
/* 137 */     JarEntry localJarEntry = paramJarFile.getJarEntry("META-INF/INDEX.LIST");
/*     */     
/* 139 */     if (localJarEntry != null) {
/* 140 */       localJarIndex = new JarIndex(paramJarFile.getInputStream(localJarEntry));
/*     */     }
/* 142 */     return localJarIndex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getJarFiles()
/*     */   {
/* 149 */     return this.jarFiles;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addToList(String paramString1, String paramString2, HashMap<String, LinkedList<String>> paramHashMap)
/*     */   {
/* 158 */     LinkedList localLinkedList = (LinkedList)paramHashMap.get(paramString1);
/* 159 */     if (localLinkedList == null) {
/* 160 */       localLinkedList = new LinkedList();
/* 161 */       localLinkedList.add(paramString2);
/* 162 */       paramHashMap.put(paramString1, localLinkedList);
/* 163 */     } else if (!localLinkedList.contains(paramString2)) {
/* 164 */       localLinkedList.add(paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LinkedList<String> get(String paramString)
/*     */   {
/* 174 */     LinkedList localLinkedList = null;
/* 175 */     if ((localLinkedList = (LinkedList)this.indexMap.get(paramString)) == null)
/*     */     {
/*     */       int i;
/* 178 */       if ((i = paramString.lastIndexOf("/")) != -1) {
/* 179 */         localLinkedList = (LinkedList)this.indexMap.get(paramString.substring(0, i));
/*     */       }
/*     */     }
/* 182 */     return localLinkedList;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void add(String paramString1, String paramString2)
/*     */   {
/*     */     int i;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     String str;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 201 */     if ((i = paramString1.lastIndexOf("/")) != -1) {
/* 202 */       str = paramString1.substring(0, i);
/*     */     } else {
/* 204 */       str = paramString1;
/*     */     }
/*     */     
/* 207 */     addMapping(str, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addMapping(String paramString1, String paramString2)
/*     */   {
/* 217 */     addToList(paramString1, paramString2, this.indexMap);
/*     */     
/*     */ 
/* 220 */     addToList(paramString2, paramString1, this.jarMap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void parseJars(String[] paramArrayOfString)
/*     */     throws IOException
/*     */   {
/* 228 */     if (paramArrayOfString == null) {
/* 229 */       return;
/*     */     }
/*     */     
/* 232 */     String str1 = null;
/*     */     
/* 234 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 235 */       str1 = paramArrayOfString[i];
/*     */       
/* 237 */       ZipFile localZipFile = new ZipFile(str1.replace('/', File.separatorChar));
/*     */       
/* 239 */       Enumeration localEnumeration = localZipFile.entries();
/* 240 */       while (localEnumeration.hasMoreElements()) {
/* 241 */         ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 242 */         String str2 = localZipEntry.getName();
/*     */         
/*     */ 
/*     */ 
/* 246 */         if ((!str2.equals("META-INF/")) && 
/* 247 */           (!str2.equals("META-INF/INDEX.LIST")) && 
/* 248 */           (!str2.equals("META-INF/MANIFEST.MF")))
/*     */         {
/*     */ 
/* 251 */           if ((!metaInfFilenames) || (!str2.startsWith("META-INF/"))) {
/* 252 */             add(str2, str1);
/* 253 */           } else if (!localZipEntry.isDirectory())
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 258 */             addMapping(str2, str1);
/*     */           }
/*     */         }
/*     */       }
/* 262 */       localZipFile.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 273 */     BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF8"));
/*     */     
/* 275 */     localBufferedWriter.write("JarIndex-Version: 1.0\n\n");
/*     */     
/* 277 */     if (this.jarFiles != null) {
/* 278 */       for (int i = 0; i < this.jarFiles.length; i++)
/*     */       {
/* 280 */         String str = this.jarFiles[i];
/* 281 */         localBufferedWriter.write(str + "\n");
/* 282 */         LinkedList localLinkedList = (LinkedList)this.jarMap.get(str);
/* 283 */         if (localLinkedList != null) {
/* 284 */           Iterator localIterator = localLinkedList.iterator();
/* 285 */           while (localIterator.hasNext()) {
/* 286 */             localBufferedWriter.write((String)localIterator.next() + "\n");
/*     */           }
/*     */         }
/* 289 */         localBufferedWriter.write("\n");
/*     */       }
/* 291 */       localBufferedWriter.flush();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void read(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 303 */     BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "UTF8"));
/*     */     
/* 305 */     String str1 = null;
/* 306 */     String str2 = null;
/*     */     
/*     */ 
/* 309 */     Vector localVector = new Vector();
/*     */     
/*     */ 
/* 312 */     while (((str1 = localBufferedReader.readLine()) != null) && (!str1.endsWith(".jar"))) {}
/* 314 */     for (; 
/* 314 */         str1 != null; str1 = localBufferedReader.readLine()) {
/* 315 */       if (str1.length() != 0)
/*     */       {
/*     */ 
/* 318 */         if (str1.endsWith(".jar")) {
/* 319 */           str2 = str1;
/* 320 */           localVector.add(str2);
/*     */         } else {
/* 322 */           String str3 = str1;
/* 323 */           addMapping(str3, str2);
/*     */         }
/*     */       }
/*     */     }
/* 327 */     this.jarFiles = ((String[])localVector.toArray(new String[localVector.size()]));
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
/*     */   public void merge(JarIndex paramJarIndex, String paramString)
/*     */   {
/* 341 */     Iterator localIterator1 = this.indexMap.entrySet().iterator();
/* 342 */     while (localIterator1.hasNext()) {
/* 343 */       Map.Entry localEntry = (Map.Entry)localIterator1.next();
/* 344 */       String str1 = (String)localEntry.getKey();
/* 345 */       LinkedList localLinkedList = (LinkedList)localEntry.getValue();
/* 346 */       Iterator localIterator2 = localLinkedList.iterator();
/* 347 */       while (localIterator2.hasNext()) {
/* 348 */         String str2 = (String)localIterator2.next();
/* 349 */         if (paramString != null) {
/* 350 */           str2 = paramString.concat(str2);
/*     */         }
/* 352 */         paramJarIndex.addMapping(str1, str2);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\misc\JarIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */