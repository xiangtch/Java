/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class ManifestDigester
/*     */ {
/*     */   public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
/*     */   private byte[] rawBytes;
/*     */   private HashMap<String, Entry> entries;
/*     */   
/*     */   private boolean findSection(int paramInt, Position paramPosition)
/*     */   {
/*  70 */     int i = paramInt;int j = this.rawBytes.length;
/*  71 */     int k = paramInt;
/*     */     
/*  73 */     int m = 1;
/*     */     
/*  75 */     paramPosition.endOfFirstLine = -1;
/*     */     
/*  77 */     while (i < j) {
/*  78 */       int n = this.rawBytes[i];
/*  79 */       switch (n) {
/*     */       case 13: 
/*  81 */         if (paramPosition.endOfFirstLine == -1)
/*  82 */           paramPosition.endOfFirstLine = (i - 1);
/*  83 */         if ((i < j) && (this.rawBytes[(i + 1)] == 10)) {
/*  84 */           i++;
/*     */         }
/*     */       case 10: 
/*  87 */         if (paramPosition.endOfFirstLine == -1)
/*  88 */           paramPosition.endOfFirstLine = (i - 1);
/*  89 */         if ((m != 0) || (i == j - 1)) {
/*  90 */           if (i == j - 1) {
/*  91 */             paramPosition.endOfSection = i;
/*     */           } else
/*  93 */             paramPosition.endOfSection = k;
/*  94 */           paramPosition.startOfNext = (i + 1);
/*  95 */           return true;
/*     */         }
/*     */         
/*     */ 
/*  99 */         k = i;
/* 100 */         m = 1;
/*     */         
/* 102 */         break;
/*     */       }
/* 104 */       m = 0;
/*     */       
/*     */ 
/* 107 */       i++;
/*     */     }
/* 109 */     return false;
/*     */   }
/*     */   
/*     */   public ManifestDigester(byte[] paramArrayOfByte)
/*     */   {
/* 114 */     this.rawBytes = paramArrayOfByte;
/* 115 */     this.entries = new HashMap();
/*     */     
/* 117 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/*     */     
/* 119 */     Position localPosition = new Position();
/*     */     
/* 121 */     if (!findSection(0, localPosition)) {
/* 122 */       return;
/*     */     }
/*     */     
/* 125 */     this.entries.put("Manifest-Main-Attributes", new Entry().addSection(new Section(0, localPosition.endOfSection + 1, localPosition.startOfNext, this.rawBytes)));
/*     */     
/*     */ 
/* 128 */     int i = localPosition.startOfNext;
/* 129 */     while (findSection(i, localPosition)) {
/* 130 */       int j = localPosition.endOfFirstLine - i + 1;
/* 131 */       int k = localPosition.endOfSection - i + 1;
/* 132 */       int m = localPosition.startOfNext - i;
/*     */       
/* 134 */       if ((j > 6) && 
/* 135 */         (isNameAttr(paramArrayOfByte, i))) {
/* 136 */         StringBuilder localStringBuilder = new StringBuilder(k);
/*     */         try
/*     */         {
/* 139 */           localStringBuilder.append(new String(paramArrayOfByte, i + 6, j - 6, "UTF8"));
/*     */           
/*     */ 
/* 142 */           int n = i + j;
/* 143 */           if (n - i < k) {
/* 144 */             if (paramArrayOfByte[n] == 13) {
/* 145 */               n += 2;
/*     */             } else {
/* 147 */               n++;
/*     */             }
/*     */           }
/*     */           
/* 151 */           while ((n - i < k) && 
/* 152 */             (paramArrayOfByte[(n++)] == 32))
/*     */           {
/* 154 */             int i1 = n;
/* 155 */             while ((n - i < k) && (paramArrayOfByte[(n++)] != 10)) {}
/*     */             
/* 157 */             if (paramArrayOfByte[(n - 1)] != 10)
/*     */               return;
/*     */             int i2;
/* 160 */             if (paramArrayOfByte[(n - 2)] == 13) {
/* 161 */               i2 = n - i1 - 2;
/*     */             } else {
/* 163 */               i2 = n - i1 - 1;
/*     */             }
/* 165 */             localStringBuilder.append(new String(paramArrayOfByte, i1, i2, "UTF8"));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */           Entry localEntry = (Entry)this.entries.get(localStringBuilder.toString());
/* 173 */           if (localEntry == null) {
/* 174 */             this.entries.put(localStringBuilder.toString(), new Entry()
/* 175 */               .addSection(new Section(i, k, m, this.rawBytes)));
/*     */           }
/*     */           else {
/* 178 */             localEntry.addSection(new Section(i, k, m, this.rawBytes));
/*     */           }
/*     */         }
/*     */         catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*     */         {
/* 183 */           throw new IllegalStateException("UTF8 not available on platform");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 188 */       i = localPosition.startOfNext;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isNameAttr(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 194 */     return ((paramArrayOfByte[paramInt] == 78) || (paramArrayOfByte[paramInt] == 110)) && ((paramArrayOfByte[(paramInt + 1)] == 97) || (paramArrayOfByte[(paramInt + 1)] == 65)) && ((paramArrayOfByte[(paramInt + 2)] == 109) || (paramArrayOfByte[(paramInt + 2)] == 77)) && ((paramArrayOfByte[(paramInt + 3)] == 101) || (paramArrayOfByte[(paramInt + 3)] == 69)) && (paramArrayOfByte[(paramInt + 4)] == 58) && (paramArrayOfByte[(paramInt + 5)] == 32);
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
/*     */   public static class Entry
/*     */   {
/* 208 */     private List<Section> sections = new ArrayList();
/*     */     boolean oldStyle;
/*     */     
/*     */     private Entry addSection(Section paramSection)
/*     */     {
/* 213 */       this.sections.add(paramSection);
/* 214 */       return this;
/*     */     }
/*     */     
/*     */     public byte[] digest(MessageDigest paramMessageDigest)
/*     */     {
/* 219 */       paramMessageDigest.reset();
/* 220 */       for (Section localSection : this.sections) {
/* 221 */         if (this.oldStyle) {
/* 222 */           Section.access$100(paramMessageDigest, localSection.rawBytes, localSection.offset, localSection.lengthWithBlankLine);
/*     */         } else {
/* 224 */           paramMessageDigest.update(localSection.rawBytes, localSection.offset, localSection.lengthWithBlankLine);
/*     */         }
/*     */       }
/* 227 */       return paramMessageDigest.digest();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public byte[] digestWorkaround(MessageDigest paramMessageDigest)
/*     */     {
/* 234 */       paramMessageDigest.reset();
/* 235 */       for (Section localSection : this.sections) {
/* 236 */         paramMessageDigest.update(localSection.rawBytes, localSection.offset, localSection.length);
/*     */       }
/* 238 */       return paramMessageDigest.digest();
/*     */     }
/*     */   }
/*     */   
/*     */   static class Position { int endOfFirstLine;
/*     */     int endOfSection;
/*     */     int startOfNext; }
/*     */   
/*     */   private static class Section { int offset;
/*     */     int length;
/*     */     int lengthWithBlankLine;
/*     */     byte[] rawBytes;
/*     */     
/* 251 */     public Section(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte) { this.offset = paramInt1;
/* 252 */       this.length = paramInt2;
/* 253 */       this.lengthWithBlankLine = paramInt3;
/* 254 */       this.rawBytes = paramArrayOfByte;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private static void doOldStyle(MessageDigest paramMessageDigest, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     {
/* 267 */       int i = paramInt1;
/* 268 */       int j = paramInt1;
/* 269 */       int k = paramInt1 + paramInt2;
/* 270 */       int m = -1;
/* 271 */       while (i < k) {
/* 272 */         if ((paramArrayOfByte[i] == 13) && (m == 32)) {
/* 273 */           paramMessageDigest.update(paramArrayOfByte, j, i - j - 1);
/* 274 */           j = i;
/*     */         }
/* 276 */         m = paramArrayOfByte[i];
/* 277 */         i++;
/*     */       }
/* 279 */       paramMessageDigest.update(paramArrayOfByte, j, i - j);
/*     */     }
/*     */   }
/*     */   
/*     */   public Entry get(String paramString, boolean paramBoolean) {
/* 284 */     Entry localEntry = (Entry)this.entries.get(paramString);
/* 285 */     if (localEntry != null)
/* 286 */       localEntry.oldStyle = paramBoolean;
/* 287 */     return localEntry;
/*     */   }
/*     */   
/*     */   public byte[] manifestDigest(MessageDigest paramMessageDigest)
/*     */   {
/* 292 */     paramMessageDigest.reset();
/* 293 */     paramMessageDigest.update(this.rawBytes, 0, this.rawBytes.length);
/* 294 */     return paramMessageDigest.digest();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ManifestDigester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */