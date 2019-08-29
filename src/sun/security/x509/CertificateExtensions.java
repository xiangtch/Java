/*     */ package sun.security.x509;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import sun.misc.HexDumpEncoder;
/*     */ import sun.security.util.Debug;
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
/*     */ public class CertificateExtensions
/*     */   implements CertAttrSet<Extension>
/*     */ {
/*     */   public static final String IDENT = "x509.info.extensions";
/*     */   public static final String NAME = "extensions";
/*  58 */   private static final Debug debug = Debug.getInstance("x509");
/*     */   
/*  60 */   private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
/*     */   
/*  62 */   private boolean unsupportedCritExt = false;
/*     */   
/*     */ 
/*     */ 
/*     */   private Map<String, Extension> unparseableExtensions;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificateExtensions() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public CertificateExtensions(DerInputStream paramDerInputStream)
/*     */     throws IOException
/*     */   {
/*  78 */     init(paramDerInputStream);
/*     */   }
/*     */   
/*     */   private void init(DerInputStream paramDerInputStream)
/*     */     throws IOException
/*     */   {
/*  84 */     DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(5);
/*     */     
/*  86 */     for (int i = 0; i < arrayOfDerValue.length; i++) {
/*  87 */       Extension localExtension = new Extension(arrayOfDerValue[i]);
/*  88 */       parseExtension(localExtension);
/*     */     }
/*     */   }
/*     */   
/*  92 */   private static Class[] PARAMS = { Boolean.class, Object.class };
/*     */   
/*     */   private void parseExtension(Extension paramExtension) throws IOException
/*     */   {
/*     */     try {
/*  97 */       Class localClass = OIDMap.getClass(paramExtension.getExtensionId());
/*  98 */       if (localClass == null) {
/*  99 */         if (paramExtension.isCritical()) {
/* 100 */           this.unsupportedCritExt = true;
/*     */         }
/* 102 */         if (this.map.put(paramExtension.getExtensionId().toString(), paramExtension) == null) {
/* 103 */           return;
/*     */         }
/* 105 */         throw new IOException("Duplicate extensions not allowed");
/*     */       }
/*     */       
/* 108 */       localObject1 = localClass.getConstructor(PARAMS);
/*     */       
/*     */ 
/* 111 */       localObject2 = new Object[] { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
/*     */       
/* 113 */       CertAttrSet localCertAttrSet = (CertAttrSet)((Constructor)localObject1).newInstance((Object[])localObject2);
/* 114 */       if (this.map.put(localCertAttrSet.getName(), (Extension)localCertAttrSet) != null)
/* 115 */         throw new IOException("Duplicate extensions not allowed");
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/*     */       Object localObject2;
/* 118 */       Object localObject1 = localInvocationTargetException.getTargetException();
/* 119 */       if (!paramExtension.isCritical())
/*     */       {
/* 121 */         if (this.unparseableExtensions == null) {
/* 122 */           this.unparseableExtensions = new TreeMap();
/*     */         }
/* 124 */         this.unparseableExtensions.put(paramExtension.getExtensionId().toString(), new UnparseableExtension(paramExtension, (Throwable)localObject1));
/*     */         
/* 126 */         if (debug != null) {
/* 127 */           debug.println("Error parsing extension: " + paramExtension);
/* 128 */           ((Throwable)localObject1).printStackTrace();
/* 129 */           localObject2 = new HexDumpEncoder();
/* 130 */           System.err.println(((HexDumpEncoder)localObject2).encodeBuffer(paramExtension.getExtensionValue()));
/*     */         }
/* 132 */         return;
/*     */       }
/* 134 */       if ((localObject1 instanceof IOException)) {
/* 135 */         throw ((IOException)localObject1);
/*     */       }
/* 137 */       throw new IOException((Throwable)localObject1);
/*     */     }
/*     */     catch (IOException localIOException) {
/* 140 */       throw localIOException;
/*     */     } catch (Exception localException) {
/* 142 */       throw new IOException(localException);
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
/*     */   public void encode(OutputStream paramOutputStream)
/*     */     throws CertificateException, IOException
/*     */   {
/* 156 */     encode(paramOutputStream, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void encode(OutputStream paramOutputStream, boolean paramBoolean)
/*     */     throws CertificateException, IOException
/*     */   {
/* 169 */     DerOutputStream localDerOutputStream1 = new DerOutputStream();
/* 170 */     Collection localCollection = this.map.values();
/* 171 */     Object[] arrayOfObject = localCollection.toArray();
/*     */     
/* 173 */     for (int i = 0; i < arrayOfObject.length; i++) {
/* 174 */       if ((arrayOfObject[i] instanceof CertAttrSet)) {
/* 175 */         ((CertAttrSet)arrayOfObject[i]).encode(localDerOutputStream1);
/* 176 */       } else if ((arrayOfObject[i] instanceof Extension)) {
/* 177 */         ((Extension)arrayOfObject[i]).encode(localDerOutputStream1);
/*     */       } else {
/* 179 */         throw new CertificateException("Illegal extension object");
/*     */       }
/*     */     }
/* 182 */     DerOutputStream localDerOutputStream2 = new DerOutputStream();
/* 183 */     localDerOutputStream2.write((byte)48, localDerOutputStream1);
/*     */     
/*     */     DerOutputStream localDerOutputStream3;
/* 186 */     if (!paramBoolean) {
/* 187 */       localDerOutputStream3 = new DerOutputStream();
/* 188 */       localDerOutputStream3.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)3), localDerOutputStream2);
/*     */     }
/*     */     else {
/* 191 */       localDerOutputStream3 = localDerOutputStream2;
/*     */     }
/* 193 */     paramOutputStream.write(localDerOutputStream3.toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set(String paramString, Object paramObject)
/*     */     throws IOException
/*     */   {
/* 203 */     if ((paramObject instanceof Extension)) {
/* 204 */       this.map.put(paramString, (Extension)paramObject);
/*     */     } else {
/* 206 */       throw new IOException("Unknown extension type.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Extension get(String paramString)
/*     */     throws IOException
/*     */   {
/* 216 */     Extension localExtension = (Extension)this.map.get(paramString);
/* 217 */     if (localExtension == null) {
/* 218 */       throw new IOException("No extension found with name " + paramString);
/*     */     }
/* 220 */     return localExtension;
/*     */   }
/*     */   
/*     */ 
/*     */   Extension getExtension(String paramString)
/*     */   {
/* 226 */     return (Extension)this.map.get(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(String paramString)
/*     */     throws IOException
/*     */   {
/* 235 */     Object localObject = this.map.get(paramString);
/* 236 */     if (localObject == null) {
/* 237 */       throw new IOException("No extension found with name " + paramString);
/*     */     }
/* 239 */     this.map.remove(paramString);
/*     */   }
/*     */   
/*     */   public String getNameByOid(ObjectIdentifier paramObjectIdentifier) throws IOException {
/* 243 */     for (String str : this.map.keySet()) {
/* 244 */       if (((Extension)this.map.get(str)).getExtensionId().equals(paramObjectIdentifier)) {
/* 245 */         return str;
/*     */       }
/*     */     }
/* 248 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Enumeration<Extension> getElements()
/*     */   {
/* 256 */     return Collections.enumeration(this.map.values());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Collection<Extension> getAllExtensions()
/*     */   {
/* 264 */     return this.map.values();
/*     */   }
/*     */   
/*     */   public Map<String, Extension> getUnparseableExtensions() {
/* 268 */     if (this.unparseableExtensions == null) {
/* 269 */       return Collections.emptyMap();
/*     */     }
/* 271 */     return this.unparseableExtensions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 279 */     return "extensions";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasUnsupportedCriticalExtension()
/*     */   {
/* 287 */     return this.unsupportedCritExt;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 302 */     if (this == paramObject)
/* 303 */       return true;
/* 304 */     if (!(paramObject instanceof CertificateExtensions)) {
/* 305 */       return false;
/*     */     }
/* 307 */     Collection localCollection = ((CertificateExtensions)paramObject).getAllExtensions();
/* 308 */     Object[] arrayOfObject = localCollection.toArray();
/*     */     
/* 310 */     int i = arrayOfObject.length;
/* 311 */     if (i != this.map.size()) {
/* 312 */       return false;
/*     */     }
/*     */     
/* 315 */     String str = null;
/* 316 */     for (int j = 0; j < i; j++) {
/* 317 */       if ((arrayOfObject[j] instanceof CertAttrSet))
/* 318 */         str = ((CertAttrSet)arrayOfObject[j]).getName();
/* 319 */       Extension localExtension1 = (Extension)arrayOfObject[j];
/* 320 */       if (str == null)
/* 321 */         str = localExtension1.getExtensionId().toString();
/* 322 */       Extension localExtension2 = (Extension)this.map.get(str);
/* 323 */       if (localExtension2 == null)
/* 324 */         return false;
/* 325 */       if (!localExtension2.equals(localExtension1))
/* 326 */         return false;
/*     */     }
/* 328 */     return getUnparseableExtensions().equals(((CertificateExtensions)paramObject)
/* 329 */       .getUnparseableExtensions());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 338 */     return this.map.hashCode() + getUnparseableExtensions().hashCode();
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
/* 350 */     return this.map.toString();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\x509\CertificateExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */