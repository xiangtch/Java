/*     */ package sun.security.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.CodeSigner;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.Provider;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Base64;
/*     */ import java.util.Base64.Decoder;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarException;
/*     */ import java.util.jar.Manifest;
/*     */ import sun.security.jca.Providers;
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
/*     */ public class ManifestEntryVerifier
/*     */ {
/*  45 */   private static final Debug debug = Debug.getInstance("jar");
/*     */   
/*     */   HashMap<String, MessageDigest> createdDigests;
/*     */   
/*     */   ArrayList<MessageDigest> digests;
/*     */   ArrayList<byte[]> manifestHashes;
/*     */   
/*     */   private static class SunProviderHolder
/*     */   {
/*  54 */     private static final Provider instance = ;
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
/*  66 */   private String name = null;
/*     */   
/*     */   private Manifest man;
/*  69 */   private boolean skip = true;
/*     */   
/*     */   private JarEntry entry;
/*     */   
/*  73 */   private CodeSigner[] signers = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ManifestEntryVerifier(Manifest paramManifest)
/*     */   {
/*  80 */     this.createdDigests = new HashMap(11);
/*  81 */     this.digests = new ArrayList();
/*  82 */     this.manifestHashes = new ArrayList();
/*  83 */     this.man = paramManifest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setEntry(String paramString, JarEntry paramJarEntry)
/*     */     throws IOException
/*     */   {
/*  95 */     this.digests.clear();
/*  96 */     this.manifestHashes.clear();
/*  97 */     this.name = paramString;
/*  98 */     this.entry = paramJarEntry;
/*     */     
/* 100 */     this.skip = true;
/* 101 */     this.signers = null;
/*     */     
/* 103 */     if ((this.man == null) || (paramString == null)) {
/* 104 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 110 */     this.skip = false;
/*     */     
/* 112 */     Attributes localAttributes = this.man.getAttributes(paramString);
/* 113 */     if (localAttributes == null)
/*     */     {
/*     */ 
/*     */ 
/* 117 */       localAttributes = this.man.getAttributes("./" + paramString);
/* 118 */       if (localAttributes == null) {
/* 119 */         localAttributes = this.man.getAttributes("/" + paramString);
/* 120 */         if (localAttributes == null) {
/* 121 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 125 */     for (Map.Entry localEntry : localAttributes.entrySet()) {
/* 126 */       String str1 = localEntry.getKey().toString();
/*     */       
/* 128 */       if (str1.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST"))
/*     */       {
/* 130 */         String str2 = str1.substring(0, str1.length() - 7);
/*     */         
/* 132 */         MessageDigest localMessageDigest = (MessageDigest)this.createdDigests.get(str2);
/*     */         
/* 134 */         if (localMessageDigest == null)
/*     */         {
/*     */           try
/*     */           {
/* 138 */             localMessageDigest = MessageDigest.getInstance(str2, SunProviderHolder.instance);
/* 139 */             this.createdDigests.put(str2, localMessageDigest);
/*     */           }
/*     */           catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
/*     */         }
/*     */         
/*     */ 
/* 145 */         if (localMessageDigest != null) {
/* 146 */           localMessageDigest.reset();
/* 147 */           this.digests.add(localMessageDigest);
/* 148 */           this.manifestHashes.add(
/* 149 */             Base64.getMimeDecoder().decode((String)localEntry.getValue()));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void update(byte paramByte)
/*     */   {
/* 159 */     if (this.skip) { return;
/*     */     }
/* 161 */     for (int i = 0; i < this.digests.size(); i++) {
/* 162 */       ((MessageDigest)this.digests.get(i)).update(paramByte);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 170 */     if (this.skip) { return;
/*     */     }
/* 172 */     for (int i = 0; i < this.digests.size(); i++) {
/* 173 */       ((MessageDigest)this.digests.get(i)).update(paramArrayOfByte, paramInt1, paramInt2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JarEntry getEntry()
/*     */   {
/* 182 */     return this.entry;
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
/*     */   public CodeSigner[] verify(Hashtable<String, CodeSigner[]> paramHashtable1, Hashtable<String, CodeSigner[]> paramHashtable2)
/*     */     throws JarException
/*     */   {
/* 197 */     if (this.skip) {
/* 198 */       return null;
/*     */     }
/*     */     
/* 201 */     if (this.digests.isEmpty()) {
/* 202 */       throw new SecurityException("digest missing for " + this.name);
/*     */     }
/*     */     
/* 205 */     if (this.signers != null) {
/* 206 */       return this.signers;
/*     */     }
/* 208 */     for (int i = 0; i < this.digests.size(); i++)
/*     */     {
/* 210 */       MessageDigest localMessageDigest = (MessageDigest)this.digests.get(i);
/* 211 */       byte[] arrayOfByte1 = (byte[])this.manifestHashes.get(i);
/* 212 */       byte[] arrayOfByte2 = localMessageDigest.digest();
/*     */       
/* 214 */       if (debug != null) {
/* 215 */         debug.println("Manifest Entry: " + this.name + " digest=" + localMessageDigest
/* 216 */           .getAlgorithm());
/* 217 */         debug.println("  manifest " + toHex(arrayOfByte1));
/* 218 */         debug.println("  computed " + toHex(arrayOfByte2));
/* 219 */         debug.println();
/*     */       }
/*     */       
/* 222 */       if (!MessageDigest.isEqual(arrayOfByte2, arrayOfByte1)) {
/* 223 */         throw new SecurityException(localMessageDigest.getAlgorithm() + " digest error for " + this.name);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 228 */     this.signers = ((CodeSigner[])paramHashtable2.remove(this.name));
/* 229 */     if (this.signers != null) {
/* 230 */       paramHashtable1.put(this.name, this.signers);
/*     */     }
/* 232 */     return this.signers;
/*     */   }
/*     */   
/*     */ 
/* 236 */   private static final char[] hexc = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static String toHex(byte[] paramArrayOfByte)
/*     */   {
/* 246 */     StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
/*     */     
/* 248 */     for (int i = 0; i < paramArrayOfByte.length; i++) {
/* 249 */       localStringBuffer.append(hexc[(paramArrayOfByte[i] >> 4 & 0xF)]);
/* 250 */       localStringBuffer.append(hexc[(paramArrayOfByte[i] & 0xF)]);
/*     */     }
/* 252 */     return localStringBuffer.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ManifestEntryVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */