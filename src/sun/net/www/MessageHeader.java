/*     */ package sun.net.www;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.StringJoiner;
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
/*     */ public class MessageHeader
/*     */ {
/*     */   private String[] keys;
/*     */   private String[] values;
/*     */   private int nkeys;
/*     */   
/*     */   public MessageHeader()
/*     */   {
/*  50 */     grow();
/*     */   }
/*     */   
/*     */   public MessageHeader(InputStream paramInputStream) throws IOException {
/*  54 */     parseHeader(paramInputStream);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized String getHeaderNamesInList()
/*     */   {
/*  61 */     StringJoiner localStringJoiner = new StringJoiner(",");
/*  62 */     for (int i = 0; i < this.nkeys; i++) {
/*  63 */       localStringJoiner.add(this.keys[i]);
/*     */     }
/*  65 */     return localStringJoiner.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/*  72 */     this.keys = null;
/*  73 */     this.values = null;
/*  74 */     this.nkeys = 0;
/*  75 */     grow();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized String findValue(String paramString)
/*     */   {
/*     */     int i;
/*     */     
/*     */ 
/*  85 */     if (paramString == null) {
/*  86 */       i = this.nkeys; do { i--; if (i < 0) break;
/*  87 */       } while (this.keys[i] != null);
/*  88 */       return this.values[i];
/*     */     } else {
/*  90 */       i = this.nkeys; do { i--; if (i < 0) break;
/*  91 */       } while (!paramString.equalsIgnoreCase(this.keys[i]));
/*  92 */       return this.values[i];
/*     */     }
/*  94 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized int getKey(String paramString)
/*     */   {
/*  99 */     int i = this.nkeys; do { i--; if (i < 0) break;
/* 100 */     } while ((this.keys[i] != paramString) && ((paramString == null) || 
/* 101 */       (!paramString.equalsIgnoreCase(this.keys[i]))));
/* 102 */     return i;
/* 103 */     return -1;
/*     */   }
/*     */   
/*     */   public synchronized String getKey(int paramInt) {
/* 107 */     if ((paramInt < 0) || (paramInt >= this.nkeys)) return null;
/* 108 */     return this.keys[paramInt];
/*     */   }
/*     */   
/*     */   public synchronized String getValue(int paramInt) {
/* 112 */     if ((paramInt < 0) || (paramInt >= this.nkeys)) return null;
/* 113 */     return this.values[paramInt];
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
/*     */   public synchronized String findNextValue(String paramString1, String paramString2)
/*     */   {
/* 128 */     int i = 0;
/* 129 */     int j; if (paramString1 == null) {
/* 130 */       j = this.nkeys; for (;;) { j--; if (j < 0) break;
/* 131 */         if (this.keys[j] == null) {
/* 132 */           if (i != 0)
/* 133 */             return this.values[j];
/* 134 */           if (this.values[j] == paramString2)
/* 135 */             i = 1;
/*     */         }
/* 137 */       } } else { j = this.nkeys; for (;;) { j--; if (j < 0) break;
/* 138 */         if (paramString1.equalsIgnoreCase(this.keys[j])) {
/* 139 */           if (i != 0)
/* 140 */             return this.values[j];
/* 141 */           if (this.values[j] == paramString2)
/* 142 */             i = 1; } } }
/* 143 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean filterNTLMResponses(String paramString)
/*     */   {
/* 152 */     int i = 0;
/* 153 */     for (int j = 0; j < this.nkeys; j++) {
/* 154 */       if ((paramString.equalsIgnoreCase(this.keys[j])) && (this.values[j] != null) && 
/* 155 */         (this.values[j].length() > 5) && 
/* 156 */         (this.values[j].substring(0, 5).equalsIgnoreCase("NTLM "))) {
/* 157 */         i = 1;
/* 158 */         break;
/*     */       }
/*     */     }
/* 161 */     if (i != 0) {
/* 162 */       j = 0;
/* 163 */       for (int k = 0; k < this.nkeys; k++)
/* 164 */         if ((!paramString.equalsIgnoreCase(this.keys[k])) || (
/* 165 */           (!"Negotiate".equalsIgnoreCase(this.values[k])) && 
/* 166 */           (!"Kerberos".equalsIgnoreCase(this.values[k]))))
/*     */         {
/*     */ 
/* 169 */           if (k != j) {
/* 170 */             this.keys[j] = this.keys[k];
/* 171 */             this.values[j] = this.values[k];
/*     */           }
/* 173 */           j++;
/*     */         }
/* 175 */       if (j != this.nkeys) {
/* 176 */         this.nkeys = j;
/* 177 */         return true;
/*     */       }
/*     */     }
/* 180 */     return false;
/*     */   }
/*     */   
/*     */   class HeaderIterator implements Iterator<String> {
/* 184 */     int index = 0;
/* 185 */     int next = -1;
/*     */     String key;
/* 187 */     boolean haveNext = false;
/*     */     Object lock;
/*     */     
/*     */     public HeaderIterator(String paramString, Object paramObject) {
/* 191 */       this.key = paramString;
/* 192 */       this.lock = paramObject;
/*     */     }
/*     */     
/* 195 */     public boolean hasNext() { synchronized (this.lock) {
/* 196 */         if (this.haveNext) {
/* 197 */           return true;
/*     */         }
/* 199 */         while (this.index < MessageHeader.this.nkeys) {
/* 200 */           if (this.key.equalsIgnoreCase(MessageHeader.this.keys[this.index])) {
/* 201 */             this.haveNext = true;
/* 202 */             this.next = (this.index++);
/* 203 */             return true;
/*     */           }
/* 205 */           this.index += 1;
/*     */         }
/* 207 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 211 */     public String next() { synchronized (this.lock) {
/* 212 */         if (this.haveNext) {
/* 213 */           this.haveNext = false;
/* 214 */           return MessageHeader.this.values[this.next];
/*     */         }
/* 216 */         if (hasNext()) {
/* 217 */           return next();
/*     */         }
/* 219 */         throw new NoSuchElementException("No more elements");
/*     */       }
/*     */     }
/*     */     
/*     */     public void remove() {
/* 224 */       throw new UnsupportedOperationException("remove not allowed");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator<String> multiValueIterator(String paramString)
/*     */   {
/* 233 */     return new HeaderIterator(paramString, this);
/*     */   }
/*     */   
/*     */   public synchronized Map<String, List<String>> getHeaders() {
/* 237 */     return getHeaders(null);
/*     */   }
/*     */   
/*     */   public synchronized Map<String, List<String>> getHeaders(String[] paramArrayOfString) {
/* 241 */     return filterAndAddHeaders(paramArrayOfString, null);
/*     */   }
/*     */   
/*     */   public synchronized Map<String, List<String>> filterAndAddHeaders(String[] paramArrayOfString, Map<String, List<String>> paramMap)
/*     */   {
/* 246 */     int i = 0;
/* 247 */     HashMap localHashMap = new HashMap();
/* 248 */     int j = this.nkeys; Object localObject1; for (;;) { j--; if (j < 0) break;
/* 249 */       if (paramArrayOfString != null)
/*     */       {
/*     */ 
/* 252 */         for (int k = 0; k < paramArrayOfString.length; k++) {
/* 253 */           if ((paramArrayOfString[k] != null) && 
/* 254 */             (paramArrayOfString[k].equalsIgnoreCase(this.keys[j]))) {
/* 255 */             i = 1;
/* 256 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 260 */       if (i == 0) {
/* 261 */         localObject1 = (List)localHashMap.get(this.keys[j]);
/* 262 */         if (localObject1 == null) {
/* 263 */           localObject1 = new ArrayList();
/* 264 */           localHashMap.put(this.keys[j], localObject1);
/*     */         }
/* 266 */         ((List)localObject1).add(this.values[j]);
/*     */       }
/*     */       else {
/* 269 */         i = 0;
/*     */       }
/*     */     }
/*     */     
/* 273 */     if (paramMap != null) {
/* 274 */       for (localIterator = paramMap.entrySet().iterator(); localIterator.hasNext();) { localObject1 = (Entry)localIterator.next();
/* 275 */         Object localObject2 = (List)localHashMap.get(((Entry)localObject1).getKey());
/* 276 */         if (localObject2 == null) {
/* 277 */           localObject2 = new ArrayList();
/* 278 */           localHashMap.put(((Entry)localObject1).getKey(), localObject2);
/*     */         }
/* 280 */         ((List)localObject2).addAll((Collection)((Entry)localObject1).getValue());
/*     */       }
/*     */     }
/*     */     
/* 284 */     for (Iterator localIterator = localHashMap.keySet().iterator(); localIterator.hasNext();) { localObject1 = (String)localIterator.next();
/* 285 */       localHashMap.put(localObject1, Collections.unmodifiableList((List)localHashMap.get(localObject1)));
/*     */     }
/*     */     
/* 288 */     return Collections.unmodifiableMap(localHashMap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void print(PrintStream paramPrintStream)
/*     */   {
/* 295 */     for (int i = 0; i < this.nkeys; i++) {
/* 296 */       if (this.keys[i] != null) {
/* 297 */         paramPrintStream.print(this.keys[i] + (this.values[i] != null ? ": " + this.values[i] : "") + "\r\n");
/*     */       }
/*     */     }
/* 300 */     paramPrintStream.print("\r\n");
/* 301 */     paramPrintStream.flush();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void add(String paramString1, String paramString2)
/*     */   {
/* 307 */     grow();
/* 308 */     this.keys[this.nkeys] = paramString1;
/* 309 */     this.values[this.nkeys] = paramString2;
/* 310 */     this.nkeys += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void prepend(String paramString1, String paramString2)
/*     */   {
/* 316 */     grow();
/* 317 */     for (int i = this.nkeys; i > 0; i--) {
/* 318 */       this.keys[i] = this.keys[(i - 1)];
/* 319 */       this.values[i] = this.values[(i - 1)];
/*     */     }
/* 321 */     this.keys[0] = paramString1;
/* 322 */     this.values[0] = paramString2;
/* 323 */     this.nkeys += 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void set(int paramInt, String paramString1, String paramString2)
/*     */   {
/* 332 */     grow();
/* 333 */     if (paramInt < 0)
/* 334 */       return;
/* 335 */     if (paramInt >= this.nkeys) {
/* 336 */       add(paramString1, paramString2);
/*     */     } else {
/* 338 */       this.keys[paramInt] = paramString1;
/* 339 */       this.values[paramInt] = paramString2;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void grow()
/*     */   {
/* 347 */     if ((this.keys == null) || (this.nkeys >= this.keys.length)) {
/* 348 */       String[] arrayOfString1 = new String[this.nkeys + 4];
/* 349 */       String[] arrayOfString2 = new String[this.nkeys + 4];
/* 350 */       if (this.keys != null)
/* 351 */         System.arraycopy(this.keys, 0, arrayOfString1, 0, this.nkeys);
/* 352 */       if (this.values != null)
/* 353 */         System.arraycopy(this.values, 0, arrayOfString2, 0, this.nkeys);
/* 354 */       this.keys = arrayOfString1;
/* 355 */       this.values = arrayOfString2;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void remove(String paramString)
/*     */   {
/*     */     int i;
/*     */     
/*     */     int j;
/*     */     
/* 367 */     if (paramString == null) {
/* 368 */       for (i = 0; i < this.nkeys; i++) {
/* 369 */         while ((this.keys[i] == null) && (i < this.nkeys)) {
/* 370 */           for (j = i; j < this.nkeys - 1; j++) {
/* 371 */             this.keys[j] = this.keys[(j + 1)];
/* 372 */             this.values[j] = this.values[(j + 1)];
/*     */           }
/* 374 */           this.nkeys -= 1;
/*     */         }
/*     */       }
/*     */     } else {
/* 378 */       for (i = 0; i < this.nkeys; i++) {
/* 379 */         while ((paramString.equalsIgnoreCase(this.keys[i])) && (i < this.nkeys)) {
/* 380 */           for (j = i; j < this.nkeys - 1; j++) {
/* 381 */             this.keys[j] = this.keys[(j + 1)];
/* 382 */             this.values[j] = this.values[(j + 1)];
/*     */           }
/* 384 */           this.nkeys -= 1;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void set(String paramString1, String paramString2)
/*     */   {
/* 395 */     int i = this.nkeys; do { i--; if (i < 0) break;
/* 396 */     } while (!paramString1.equalsIgnoreCase(this.keys[i]));
/* 397 */     this.values[i] = paramString2;
/* 398 */     return;
/*     */     
/* 400 */     add(paramString1, paramString2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setIfNotSet(String paramString1, String paramString2)
/*     */   {
/* 408 */     if (findValue(paramString1) == null) {
/* 409 */       add(paramString1, paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static String canonicalID(String paramString)
/*     */   {
/* 416 */     if (paramString == null)
/* 417 */       return "";
/* 418 */     int i = 0;
/* 419 */     int j = paramString.length();
/* 420 */     int k = 0;
/*     */     int m;
/* 422 */     while ((i < j) && (((m = paramString.charAt(i)) == '<') || (m <= 32)))
/*     */     {
/* 424 */       i++;
/* 425 */       k = 1;
/*     */     }
/* 427 */     while ((i < j) && (((m = paramString.charAt(j - 1)) == '>') || (m <= 32)))
/*     */     {
/* 429 */       j--;
/* 430 */       k = 1;
/*     */     }
/* 432 */     return k != 0 ? paramString.substring(i, j) : paramString;
/*     */   }
/*     */   
/*     */   public void parseHeader(InputStream paramInputStream) throws IOException
/*     */   {
/* 437 */     synchronized (this) {
/* 438 */       this.nkeys = 0;
/*     */     }
/* 440 */     mergeHeader(paramInputStream);
/*     */   }
/*     */   
/*     */   public void mergeHeader(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 446 */     if (paramInputStream == null)
/* 447 */       return;
/* 448 */     Object localObject1 = new char[10];
/* 449 */     int i = paramInputStream.read();
/* 450 */     while ((i != 10) && (i != 13) && (i >= 0)) {
/* 451 */       int j = 0;
/* 452 */       int k = -1;
/*     */       
/* 454 */       int n = i > 32 ? 1 : 0;
/* 455 */       localObject1[(j++)] = ((char)i);
/*     */       int m;
/* 457 */       Object localObject2; while ((m = paramInputStream.read()) >= 0) {
/* 458 */         switch (m) {
/*     */         case 58: 
/* 460 */           if ((n != 0) && (j > 0))
/* 461 */             k = j;
/* 462 */           n = 0;
/* 463 */           break;
/*     */         case 9: 
/* 465 */           m = 32;
/*     */         
/*     */         case 32: 
/* 468 */           n = 0;
/* 469 */           break;
/*     */         case 10: 
/*     */         case 13: 
/* 472 */           i = paramInputStream.read();
/* 473 */           if ((m == 13) && (i == 10)) {
/* 474 */             i = paramInputStream.read();
/* 475 */             if (i == 13)
/* 476 */               i = paramInputStream.read();
/*     */           }
/* 478 */           if ((i == 10) || (i == 13) || (i > 32)) {
/*     */             break label252;
/*     */           }
/* 481 */           m = 32;
/*     */         }
/*     */         
/* 484 */         if (j >= localObject1.length) {
/* 485 */           localObject2 = new char[localObject1.length * 2];
/* 486 */           System.arraycopy(localObject1, 0, localObject2, 0, j);
/* 487 */           localObject1 = localObject2;
/*     */         }
/* 489 */         localObject1[(j++)] = ((char)m);
/*     */       }
/* 491 */       i = -1;
/*     */       label252:
/* 493 */       while ((j > 0) && (localObject1[(j - 1)] <= ' ')) {
/* 494 */         j--;
/*     */       }
/* 496 */       if (k <= 0) {
/* 497 */         localObject2 = null;
/* 498 */         k = 0;
/*     */       } else {
/* 500 */         localObject2 = String.copyValueOf((char[])localObject1, 0, k);
/* 501 */         if ((k < j) && (localObject1[k] == ':'))
/* 502 */           k++;
/* 503 */         while ((k < j) && (localObject1[k] <= ' '))
/* 504 */           k++;
/*     */       }
/*     */       String str;
/* 507 */       if (k >= j) {
/* 508 */         str = new String();
/*     */       } else
/* 510 */         str = String.copyValueOf((char[])localObject1, k, j - k);
/* 511 */       add((String)localObject2, str);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized String toString() {
/* 516 */     String str = super.toString() + this.nkeys + " pairs: ";
/* 517 */     for (int i = 0; (i < this.keys.length) && (i < this.nkeys); i++) {
/* 518 */       str = str + "{" + this.keys[i] + ": " + this.values[i] + "}";
/*     */     }
/* 520 */     return str;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\MessageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */