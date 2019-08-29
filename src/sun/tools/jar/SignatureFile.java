/*     */ package sun.tools.jar;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Base64;
/*     */ import java.util.Base64.Encoder;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Locale;
/*     */ import java.util.Vector;
/*     */ import sun.net.www.MessageHeader;
/*     */ import sun.security.pkcs.PKCS7;
/*     */ import sun.security.pkcs.SignerInfo;
/*     */ import sun.security.x509.AlgorithmId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignatureFile
/*     */ {
/*     */   static final boolean debug = false;
/*  69 */   private Vector<MessageHeader> entries = new Vector();
/*     */   
/*     */ 
/*  72 */   static final String[] hashes = { "SHA" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Manifest manifest;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String rawName;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private PKCS7 signatureBlock;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static final void debug(String paramString) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private SignatureFile(String paramString)
/*     */     throws JarException
/*     */   {
/* 101 */     this.entries = new Vector();
/*     */     
/* 103 */     if (paramString != null) {
/* 104 */       if ((paramString.length() > 8) || (paramString.indexOf('.') != -1)) {
/* 105 */         throw new JarException("invalid file name");
/*     */       }
/* 107 */       this.rawName = paramString.toUpperCase(Locale.ENGLISH);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SignatureFile(String paramString, boolean paramBoolean)
/*     */     throws JarException
/*     */   {
/* 118 */     this(paramString);
/*     */     
/* 120 */     if (paramBoolean) {
/* 121 */       MessageHeader localMessageHeader = new MessageHeader();
/* 122 */       localMessageHeader.set("Signature-Version", "1.0");
/* 123 */       this.entries.addElement(localMessageHeader);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignatureFile(Manifest paramManifest, String paramString)
/*     */     throws JarException
/*     */   {
/* 142 */     this(paramString, true);
/*     */     
/* 144 */     this.manifest = paramManifest;
/* 145 */     Enumeration localEnumeration = paramManifest.entries();
/* 146 */     while (localEnumeration.hasMoreElements()) {
/* 147 */       MessageHeader localMessageHeader = (MessageHeader)localEnumeration.nextElement();
/* 148 */       String str = localMessageHeader.findValue("Name");
/* 149 */       if (str != null) {
/* 150 */         add(str);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignatureFile(Manifest paramManifest, String[] paramArrayOfString, String paramString)
/*     */     throws JarException
/*     */   {
/* 172 */     this(paramString, true);
/* 173 */     this.manifest = paramManifest;
/* 174 */     add(paramArrayOfString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignatureFile(InputStream paramInputStream, String paramString)
/*     */     throws IOException
/*     */   {
/* 185 */     this(paramString);
/* 186 */     while (paramInputStream.available() > 0) {
/* 187 */       MessageHeader localMessageHeader = new MessageHeader(paramInputStream);
/* 188 */       this.entries.addElement(localMessageHeader);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignatureFile(InputStream paramInputStream)
/*     */     throws IOException
/*     */   {
/* 199 */     this(paramInputStream, null);
/*     */   }
/*     */   
/*     */   public SignatureFile(byte[] paramArrayOfByte) throws IOException {
/* 203 */     this(new ByteArrayInputStream(paramArrayOfByte));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 210 */     return "META-INF/" + this.rawName + ".SF";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getBlockName()
/*     */   {
/* 217 */     Object localObject = "DSA";
/* 218 */     if (this.signatureBlock != null) {
/* 219 */       SignerInfo localSignerInfo = this.signatureBlock.getSignerInfos()[0];
/* 220 */       localObject = localSignerInfo.getDigestEncryptionAlgorithmId().getName();
/* 221 */       String str = AlgorithmId.getEncAlgFromSigAlg((String)localObject);
/* 222 */       if (str != null) localObject = str;
/*     */     }
/* 224 */     return "META-INF/" + this.rawName + "." + (String)localObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PKCS7 getBlock()
/*     */   {
/* 231 */     return this.signatureBlock;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBlock(PKCS7 paramPKCS7)
/*     */   {
/* 238 */     this.signatureBlock = paramPKCS7;
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(String[] paramArrayOfString)
/*     */     throws JarException
/*     */   {
/* 245 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 246 */       add(paramArrayOfString[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(String paramString)
/*     */     throws JarException
/*     */   {
/* 254 */     MessageHeader localMessageHeader1 = this.manifest.getEntry(paramString);
/* 255 */     if (localMessageHeader1 == null) {
/* 256 */       throw new JarException("entry " + paramString + " not in manifest");
/*     */     }
/*     */     MessageHeader localMessageHeader2;
/*     */     try {
/* 260 */       localMessageHeader2 = computeEntry(localMessageHeader1);
/*     */     } catch (IOException localIOException) {
/* 262 */       throw new JarException(localIOException.getMessage());
/*     */     }
/* 264 */     this.entries.addElement(localMessageHeader2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MessageHeader getEntry(String paramString)
/*     */   {
/* 272 */     Enumeration localEnumeration = entries();
/* 273 */     while (localEnumeration.hasMoreElements()) {
/* 274 */       MessageHeader localMessageHeader = (MessageHeader)localEnumeration.nextElement();
/* 275 */       if (paramString.equals(localMessageHeader.findValue("Name"))) {
/* 276 */         return localMessageHeader;
/*     */       }
/*     */     }
/* 279 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public MessageHeader entryAt(int paramInt)
/*     */   {
/* 285 */     return (MessageHeader)this.entries.elementAt(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Enumeration<MessageHeader> entries()
/*     */   {
/* 292 */     return this.entries.elements();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private MessageHeader computeEntry(MessageHeader paramMessageHeader)
/*     */     throws IOException
/*     */   {
/* 300 */     MessageHeader localMessageHeader = new MessageHeader();
/*     */     
/* 302 */     String str = paramMessageHeader.findValue("Name");
/* 303 */     if (str == null) {
/* 304 */       return null;
/*     */     }
/* 306 */     localMessageHeader.set("Name", str);
/*     */     try
/*     */     {
/* 309 */       for (int i = 0; i < hashes.length; i++) {
/* 310 */         MessageDigest localMessageDigest = getDigest(hashes[i]);
/* 311 */         ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 312 */         PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
/* 313 */         paramMessageHeader.print(localPrintStream);
/* 314 */         byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();
/* 315 */         byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
/* 316 */         localMessageHeader.set(hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(arrayOfByte2));
/*     */       }
/* 318 */       return localMessageHeader;
/*     */     } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 320 */       throw new JarException(localNoSuchAlgorithmException.getMessage());
/*     */     }
/*     */   }
/*     */   
/* 324 */   private Hashtable<String, MessageDigest> digests = new Hashtable();
/*     */   
/*     */   private MessageDigest getDigest(String paramString) throws NoSuchAlgorithmException
/*     */   {
/* 328 */     MessageDigest localMessageDigest = (MessageDigest)this.digests.get(paramString);
/* 329 */     if (localMessageDigest == null) {
/* 330 */       localMessageDigest = MessageDigest.getInstance(paramString);
/* 331 */       this.digests.put(paramString, localMessageDigest);
/*     */     }
/* 333 */     localMessageDigest.reset();
/* 334 */     return localMessageDigest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stream(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/* 346 */     MessageHeader localMessageHeader1 = (MessageHeader)this.entries.elementAt(0);
/* 347 */     if (localMessageHeader1.findValue("Signature-Version") == null) {
/* 348 */       throw new JarException("Signature file requires Signature-Version: 1.0 in 1st header");
/*     */     }
/*     */     
/*     */ 
/* 352 */     PrintStream localPrintStream = new PrintStream(paramOutputStream);
/* 353 */     localMessageHeader1.print(localPrintStream);
/*     */     
/* 355 */     for (int i = 1; i < this.entries.size(); i++) {
/* 356 */       MessageHeader localMessageHeader2 = (MessageHeader)this.entries.elementAt(i);
/* 357 */       localMessageHeader2.print(localPrintStream);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tools\jar\SignatureFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */