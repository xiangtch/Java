/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.cert.CRLException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.security.util.DerInputStream;
/*     */ import sun.security.util.DerOutputStream;
/*     */ import sun.security.util.DerValue;
/*     */ import sun.security.util.ObjectIdentifier;
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
/*     */ public class CRLExtensions
/*     */ {
/*  66 */   private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
/*     */   
/*  68 */   private boolean unsupportedCritExt = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLExtensions() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CRLExtensions(DerInputStream paramDerInputStream)
/*     */     throws CRLException
/*     */   {
/*  83 */     init(paramDerInputStream);
/*     */   }
/*     */   
/*     */   private void init(DerInputStream paramDerInputStream) throws CRLException
/*     */   {
/*     */     try {
/*  89 */       DerInputStream localDerInputStream = paramDerInputStream;
/*     */       
/*  91 */       int i = (byte)paramDerInputStream.peekByte();
/*     */       
/*  93 */       if (((i & 0xC0) == 128) && ((i & 0x1F) == 0))
/*     */       {
/*  95 */         localObject = localDerInputStream.getDerValue();
/*  96 */         localDerInputStream = ((DerValue)localObject).data;
/*     */       }
/*     */       
/*  99 */       Object localObject = localDerInputStream.getSequence(5);
/* 100 */       for (int j = 0; j < localObject.length; j++) {
/* 101 */         Extension localExtension = new Extension(localObject[j]);
/* 102 */         parseExtension(localExtension);
/*     */       }
/*     */     } catch (IOException localIOException) {
/* 105 */       throw new CRLException("Parsing error: " + localIOException.toString());
/*     */     }
/*     */   }
/*     */   
/* 109 */   private static final Class[] PARAMS = { Boolean.class, Object.class };
/*     */   
/*     */   private void parseExtension(Extension paramExtension) throws CRLException
/*     */   {
/*     */     try {
/* 114 */       Class localClass = OIDMap.getClass(paramExtension.getExtensionId());
/* 115 */       if (localClass == null) {
/* 116 */         if (paramExtension.isCritical())
/* 117 */           this.unsupportedCritExt = true;
/* 118 */         if (this.map.put(paramExtension.getExtensionId().toString(), paramExtension) != null)
/* 119 */           throw new CRLException("Duplicate extensions not allowed");
/* 120 */         return;
/*     */       }
/* 122 */       Constructor localConstructor = localClass.getConstructor(PARAMS);
/*     */       
/* 124 */       Object[] arrayOfObject = { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
/* 125 */       CertAttrSet localCertAttrSet = (CertAttrSet)localConstructor.newInstance(arrayOfObject);
/* 126 */       if (this.map.put(localCertAttrSet.getName(), (Extension)localCertAttrSet) != null) {
/* 127 */         throw new CRLException("Duplicate extensions not allowed");
/*     */       }
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 130 */       throw new CRLException(localInvocationTargetException.getTargetException().getMessage());
/*     */     } catch (Exception localException) {
/* 132 */       throw new CRLException(localException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream, boolean paramBoolean)
/*     */     throws CRLException
/*     */   {
/*     */     try
/*     */     {
/* 147 */       DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 148 */       Collection localCollection = this.map.values();
/* 149 */       Object[] arrayOfObject = localCollection.toArray();
/*     */       
/* 151 */       for (int i = 0; i < arrayOfObject.length; i++) {
/* 152 */         if ((arrayOfObject[i] instanceof CertAttrSet)) {
/* 153 */           ((CertAttrSet)arrayOfObject[i]).encode(localDerOutputStream1);
/* 154 */         } else if ((arrayOfObject[i] instanceof Extension)) {
/* 155 */           ((Extension)arrayOfObject[i]).encode(localDerOutputStream1);
/*     */         } else {
/* 157 */           throw new CRLException("Illegal extension object");
/*     */         }
/*     */       }
/* 160 */       DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 161 */       localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*     */       
/* 163 */       DerOutputStream localDerOutputStream3 = new DerOutputStream();
/* 164 */       if (paramBoolean) {
/* 165 */         localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), localDerOutputStream2);
/*     */       }
/*     */       else {
/* 168 */         localDerOutputStream3 = localDerOutputStream2;
/*     */       }
/* 170 */       paramOutputStream.write(localDerOutputStream3.toByteArray());
/*     */     } catch (IOException localIOException) {
/* 172 */       throw new CRLException("Encoding error: " + localIOException.toString());
/*     */     } catch (CertificateException localCertificateException) {
/* 174 */       throw new CRLException("Encoding error: " + localCertificateException.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Extension get(String paramString)
/*     */   {
/* 184 */     X509AttributeName localX509AttributeName = new X509AttributeName(paramString);
/*     */     
/* 186 */     String str2 = localX509AttributeName.getPrefix();
/* 187 */     String str1; if (str2.equalsIgnoreCase("x509")) {
/* 188 */       int i = paramString.lastIndexOf(".");
/* 189 */       str1 = paramString.substring(i + 1);
/*     */     } else {
/* 191 */       str1 = paramString; }
/* 192 */     return (Extension)this.map.get(str1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */   {
/* 203 */     this.map.put(paramString, (Extension)paramObject);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */   {
/* 212 */     this.map.remove(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<Extension> getElements()
/*     */   {
/* 220 */     return Collections.enumeration(this.map.values());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<Extension> getAllExtensions()
/*     */   {
/* 228 */     return this.map.values();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/* 236 */     return this.unsupportedCritExt;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 250 */     if (this == paramObject)
/* 251 */       return true;
/* 252 */     if (!(paramObject instanceof CRLExtensions)) {
/* 253 */       return false;
/*     */     }
/* 255 */     Collection localCollection = ((CRLExtensions)paramObject).getAllExtensions();
/* 256 */     Object[] arrayOfObject = localCollection.toArray();
/*     */     
/* 258 */     int i = arrayOfObject.length;
/* 259 */     if (i != this.map.size()) {
/* 260 */       return false;
/*     */     }
/*     */     
/* 263 */     String str = null;
/* 264 */     for (int j = 0; j < i; j++) {
/* 265 */       if ((arrayOfObject[j] instanceof CertAttrSet))
/* 266 */         str = ((CertAttrSet)arrayOfObject[j]).getName();
/* 267 */       Extension localExtension1 = (Extension)arrayOfObject[j];
/* 268 */       if (str == null)
/* 269 */         str = localExtension1.getExtensionId().toString();
/* 270 */       Extension localExtension2 = (Extension)this.map.get(str);
/* 271 */       if (localExtension2 == null)
/* 272 */         return false;
/* 273 */       if (!localExtension2.equals(localExtension1))
/* 274 */         return false;
/*     */     }
/* 276 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 285 */     return this.map.hashCode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 297 */     return this.map.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CRLExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */