/*     */ package sun.tools.jar;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
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
/*     */ import java.util.Vector;
/*     */ import sun.net.www.MessageHeader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Manifest
/*     */ {
/*  49 */   private Vector<MessageHeader> entries = new Vector();
/*  50 */   private byte[] tmpbuf = new byte['Ȁ'];
/*     */   
/*  52 */   private Hashtable<String, MessageHeader> tableEntries = new Hashtable();
/*     */   
/*  54 */   static final String[] hashes = { "SHA" };
/*  55 */   static final byte[] EOL = { 13, 10 };
/*     */   
/*     */   static final boolean debug = false;
/*     */   static final String VERSION = "1.0";
/*     */   
/*     */   static final void debug(String paramString) {}
/*     */   
/*     */   public Manifest() {}
/*     */   
/*     */   public Manifest(byte[] paramArrayOfByte)
/*     */     throws IOException
/*     */   {
/*  67 */     this(new ByteArrayInputStream(paramArrayOfByte), false);
/*     */   }
/*     */   
/*     */   public Manifest(InputStream paramInputStream) throws IOException {
/*  71 */     this(paramInputStream, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Manifest(InputStream paramInputStream, boolean paramBoolean)
/*     */     throws IOException
/*     */   {
/*  79 */     if (!paramInputStream.markSupported()) {
/*  80 */       paramInputStream = new BufferedInputStream(paramInputStream);
/*     */     }
/*     */     for (;;)
/*     */     {
/*  84 */       paramInputStream.mark(1);
/*  85 */       if (paramInputStream.read() == -1) {
/*     */         break;
/*     */       }
/*  88 */       paramInputStream.reset();
/*  89 */       MessageHeader localMessageHeader = new MessageHeader(paramInputStream);
/*  90 */       if (paramBoolean) {
/*  91 */         doHashes(localMessageHeader);
/*     */       }
/*  93 */       addEntry(localMessageHeader);
/*     */     }
/*     */   }
/*     */   
/*     */   public Manifest(String[] paramArrayOfString) throws IOException
/*     */   {
/*  99 */     MessageHeader localMessageHeader = new MessageHeader();
/* 100 */     localMessageHeader.add("Manifest-Version", "1.0");
/* 101 */     String str = System.getProperty("java.version");
/* 102 */     localMessageHeader.add("Created-By", "Manifest JDK " + str);
/* 103 */     addEntry(localMessageHeader);
/* 104 */     addFiles(null, paramArrayOfString);
/*     */   }
/*     */   
/*     */   public void addEntry(MessageHeader paramMessageHeader) {
/* 108 */     this.entries.addElement(paramMessageHeader);
/* 109 */     String str = paramMessageHeader.findValue("Name");
/* 110 */     debug("addEntry for name: " + str);
/* 111 */     if (str != null) {
/* 112 */       this.tableEntries.put(str, paramMessageHeader);
/*     */     }
/*     */   }
/*     */   
/*     */   public MessageHeader getEntry(String paramString) {
/* 117 */     return (MessageHeader)this.tableEntries.get(paramString);
/*     */   }
/*     */   
/*     */   public MessageHeader entryAt(int paramInt) {
/* 121 */     return (MessageHeader)this.entries.elementAt(paramInt);
/*     */   }
/*     */   
/*     */   public Enumeration<MessageHeader> entries() {
/* 125 */     return this.entries.elements();
/*     */   }
/*     */   
/*     */   public void addFiles(File paramFile, String[] paramArrayOfString) throws IOException {
/* 129 */     if (paramArrayOfString == null)
/* 130 */       return;
/* 131 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*     */       File localFile;
/* 133 */       if (paramFile == null) {
/* 134 */         localFile = new File(paramArrayOfString[i]);
/*     */       } else {
/* 136 */         localFile = new File(paramFile, paramArrayOfString[i]);
/*     */       }
/* 138 */       if (localFile.isDirectory()) {
/* 139 */         addFiles(localFile, localFile.list());
/*     */       } else {
/* 141 */         addFile(localFile);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final String stdToLocal(String paramString)
/*     */   {
/* 152 */     return paramString.replace('/', File.separatorChar);
/*     */   }
/*     */   
/*     */   private final String localToStd(String paramString) {
/* 156 */     paramString = paramString.replace(File.separatorChar, '/');
/* 157 */     if (paramString.startsWith("./")) {
/* 158 */       paramString = paramString.substring(2);
/* 159 */     } else if (paramString.startsWith("/"))
/* 160 */       paramString = paramString.substring(1);
/* 161 */     return paramString;
/*     */   }
/*     */   
/*     */   public void addFile(File paramFile) throws IOException {
/* 165 */     String str = localToStd(paramFile.getPath());
/* 166 */     if (this.tableEntries.get(str) == null) {
/* 167 */       MessageHeader localMessageHeader = new MessageHeader();
/* 168 */       localMessageHeader.add("Name", str);
/* 169 */       addEntry(localMessageHeader);
/*     */     }
/*     */   }
/*     */   
/*     */   public void doHashes(MessageHeader paramMessageHeader) throws IOException
/*     */   {
/* 175 */     String str = paramMessageHeader.findValue("Name");
/* 176 */     if ((str == null) || (str.endsWith("/"))) {
/* 177 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 182 */     for (int i = 0; i < hashes.length; i++) {
/* 183 */       FileInputStream localFileInputStream = new FileInputStream(stdToLocal(str));
/*     */       try {
/* 185 */         MessageDigest localMessageDigest = MessageDigest.getInstance(hashes[i]);
/*     */         
/*     */         int j;
/* 188 */         while ((j = localFileInputStream.read(this.tmpbuf, 0, this.tmpbuf.length)) != -1) {
/* 189 */           localMessageDigest.update(this.tmpbuf, 0, j);
/*     */         }
/* 191 */         paramMessageHeader.set(hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(localMessageDigest.digest()));
/*     */       } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 193 */         throw new JarException("Digest algorithm " + hashes[i] + " not available.");
/*     */       }
/*     */       finally {
/* 196 */         localFileInputStream.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void stream(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/*     */     PrintStream localPrintStream;
/* 206 */     if ((paramOutputStream instanceof PrintStream)) {
/* 207 */       localPrintStream = (PrintStream)paramOutputStream;
/*     */     } else {
/* 209 */       localPrintStream = new PrintStream(paramOutputStream);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 215 */     MessageHeader localMessageHeader1 = (MessageHeader)this.entries.elementAt(0);
/*     */     
/* 217 */     if (localMessageHeader1.findValue("Manifest-Version") == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 224 */       String str = System.getProperty("java.version");
/*     */       
/* 226 */       if (localMessageHeader1.findValue("Name") == null) {
/* 227 */         localMessageHeader1.prepend("Manifest-Version", "1.0");
/* 228 */         localMessageHeader1.add("Created-By", "Manifest JDK " + str);
/*     */       } else {
/* 230 */         localPrintStream.print("Manifest-Version: 1.0\r\nCreated-By: " + str + "\r\n\r\n");
/*     */       }
/*     */       
/* 233 */       localPrintStream.flush();
/*     */     }
/*     */     
/* 236 */     localMessageHeader1.print(localPrintStream);
/*     */     
/* 238 */     for (int i = 1; i < this.entries.size(); i++) {
/* 239 */       MessageHeader localMessageHeader2 = (MessageHeader)this.entries.elementAt(i);
/* 240 */       localMessageHeader2.print(localPrintStream);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isManifestName(String paramString)
/*     */   {
/* 247 */     if (paramString.charAt(0) == '/') {
/* 248 */       paramString = paramString.substring(1, paramString.length());
/*     */     }
/*     */     
/* 251 */     paramString = paramString.toUpperCase();
/*     */     
/* 253 */     if (paramString.equals("META-INF/MANIFEST.MF")) {
/* 254 */       return true;
/*     */     }
/* 256 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\tools\jar\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */