/*      */ package sun.font;
/*      */ 
/*      */ import java.awt.FontFormatException;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.geom.Point2D.Float;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.IntBuffer;
/*      */ import java.nio.ShortBuffer;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.java2d.Disposer;
/*      */ import sun.java2d.DisposerRecord;
/*      */ import sun.security.action.GetPropertyAction;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TrueTypeFont
/*      */   extends FileFont
/*      */ {
/*      */   public static final int cmapTag = 1668112752;
/*      */   public static final int glyfTag = 1735162214;
/*      */   public static final int headTag = 1751474532;
/*      */   public static final int hheaTag = 1751672161;
/*      */   public static final int hmtxTag = 1752003704;
/*      */   public static final int locaTag = 1819239265;
/*      */   public static final int maxpTag = 1835104368;
/*      */   public static final int nameTag = 1851878757;
/*      */   public static final int postTag = 1886352244;
/*      */   public static final int os_2Tag = 1330851634;
/*      */   public static final int GDEFTag = 1195656518;
/*      */   public static final int GPOSTag = 1196445523;
/*      */   public static final int GSUBTag = 1196643650;
/*      */   public static final int mortTag = 1836020340;
/*      */   public static final int fdscTag = 1717859171;
/*      */   public static final int fvarTag = 1719034226;
/*      */   public static final int featTag = 1717920116;
/*      */   public static final int EBLCTag = 1161972803;
/*      */   public static final int gaspTag = 1734439792;
/*      */   public static final int ttcfTag = 1953784678;
/*      */   public static final int v1ttTag = 65536;
/*      */   public static final int trueTag = 1953658213;
/*      */   public static final int ottoTag = 1330926671;
/*      */   public static final int MS_PLATFORM_ID = 3;
/*      */   public static final short ENGLISH_LOCALE_ID = 1033;
/*      */   public static final int FAMILY_NAME_ID = 1;
/*      */   public static final int FULL_NAME_ID = 4;
/*      */   public static final int POSTSCRIPT_NAME_ID = 6;
/*      */   private static final short US_LCID = 1033;
/*      */   private static Map<String, Short> lcidMap;
/*      */   
/*      */   static class DirectoryEntry
/*      */   {
/*      */     int tag;
/*      */     int offset;
/*      */     int length;
/*      */   }
/*      */   
/*      */   private static class TTDisposerRecord
/*      */     implements DisposerRecord
/*      */   {
/*  125 */     FileChannel channel = null;
/*      */     
/*      */     /* Error */
/*      */     public synchronized void dispose()
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 32	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
/*      */       //   4: ifnull +10 -> 14
/*      */       //   7: aload_0
/*      */       //   8: getfield 32	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
/*      */       //   11: invokevirtual 34	java/nio/channels/FileChannel:close	()V
/*      */       //   14: aload_0
/*      */       //   15: aconst_null
/*      */       //   16: putfield 32	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
/*      */       //   19: goto +20 -> 39
/*      */       //   22: astore_1
/*      */       //   23: aload_0
/*      */       //   24: aconst_null
/*      */       //   25: putfield 32	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
/*      */       //   28: goto +11 -> 39
/*      */       //   31: astore_2
/*      */       //   32: aload_0
/*      */       //   33: aconst_null
/*      */       //   34: putfield 32	sun/font/TrueTypeFont$TTDisposerRecord:channel	Ljava/nio/channels/FileChannel;
/*      */       //   37: aload_2
/*      */       //   38: athrow
/*      */       //   39: return
/*      */       // Line number table:
/*      */       //   Java source line #129	-> byte code offset #0
/*      */       //   Java source line #130	-> byte code offset #7
/*      */       //   Java source line #134	-> byte code offset #14
/*      */       //   Java source line #135	-> byte code offset #19
/*      */       //   Java source line #132	-> byte code offset #22
/*      */       //   Java source line #134	-> byte code offset #23
/*      */       //   Java source line #135	-> byte code offset #28
/*      */       //   Java source line #134	-> byte code offset #31
/*      */       //   Java source line #135	-> byte code offset #37
/*      */       //   Java source line #136	-> byte code offset #39
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	40	0	this	TTDisposerRecord
/*      */       //   22	1	1	localIOException	IOException
/*      */       //   31	7	2	localObject	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   0	14	22	java/io/IOException
/*      */       //   0	14	31	finally
/*      */     }
/*      */   }
/*      */   
/*  139 */   TTDisposerRecord disposerRecord = new TTDisposerRecord(null);
/*      */   
/*      */ 
/*  142 */   int fontIndex = 0;
/*      */   
/*      */ 
/*  145 */   int directoryCount = 1;
/*      */   
/*      */ 
/*      */   int directoryOffset;
/*      */   
/*      */ 
/*      */   int numTables;
/*      */   
/*      */ 
/*      */   DirectoryEntry[] tableDirectory;
/*      */   
/*      */ 
/*      */   private boolean supportsJA;
/*      */   
/*      */ 
/*      */   private boolean supportsCJK;
/*      */   
/*      */   private Locale nameLocale;
/*      */   
/*      */   private String localeFamilyName;
/*      */   
/*      */   private String localeFullName;
/*      */   
/*      */   private static final int TTCHEADERSIZE = 12;
/*      */   
/*      */   private static final int DIRECTORYHEADERSIZE = 12;
/*      */   
/*      */   private static final int DIRECTORYENTRYSIZE = 16;
/*      */   
/*      */ 
/*      */   public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean)
/*      */     throws FontFormatException
/*      */   {
/*  178 */     this(paramString, paramObject, paramInt, paramBoolean, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TrueTypeFont(String paramString, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
/*      */     throws FontFormatException
/*      */   {
/*  193 */     super(paramString, paramObject);
/*  194 */     this.useJavaRasterizer = paramBoolean1;
/*  195 */     this.fontRank = 3;
/*      */     try {
/*  197 */       verify(paramBoolean2);
/*  198 */       init(paramInt);
/*  199 */       if (!paramBoolean2) {
/*  200 */         close();
/*      */       }
/*      */     } catch (Throwable localThrowable) {
/*  203 */       close();
/*  204 */       if ((localThrowable instanceof FontFormatException)) {
/*  205 */         throw ((FontFormatException)localThrowable);
/*      */       }
/*  207 */       throw new FontFormatException("Unexpected runtime exception.");
/*      */     }
/*      */     
/*  210 */     Disposer.addObjectRecord(this, this.disposerRecord);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean checkUseNatives()
/*      */   {
/*  226 */     if (this.checkedNatives) {
/*  227 */       return this.useNatives;
/*      */     }
/*  229 */     if ((!FontUtilities.isSolaris) || (this.useJavaRasterizer) || (FontUtilities.useT2K) || (this.nativeNames == null) || 
/*      */     
/*  231 */       (getDirectoryEntry(1161972803) != null) || 
/*  232 */       (GraphicsEnvironment.isHeadless())) {
/*  233 */       this.checkedNatives = true;
/*  234 */       return false; }
/*  235 */     Object localObject; if ((this.nativeNames instanceof String)) {
/*  236 */       localObject = (String)this.nativeNames;
/*      */       
/*  238 */       if (((String)localObject).indexOf("8859") > 0) {
/*  239 */         this.checkedNatives = true;
/*  240 */         return false; }
/*  241 */       if (NativeFont.hasExternalBitmaps((String)localObject)) {
/*  242 */         this.nativeFonts = new NativeFont[1];
/*      */         try {
/*  244 */           this.nativeFonts[0] = new NativeFont((String)localObject, true);
/*      */           
/*      */ 
/*      */ 
/*  248 */           this.useNatives = true;
/*      */         } catch (FontFormatException localFontFormatException1) {
/*  250 */           this.nativeFonts = null;
/*      */         }
/*      */       }
/*  253 */     } else if ((this.nativeNames instanceof String[])) {
/*  254 */       localObject = (String[])this.nativeNames;
/*  255 */       int i = localObject.length;
/*  256 */       int j = 0;
/*  257 */       for (int k = 0; k < i; k++) {
/*  258 */         if (localObject[k].indexOf("8859") > 0) {
/*  259 */           this.checkedNatives = true;
/*  260 */           return false; }
/*  261 */         if (NativeFont.hasExternalBitmaps(localObject[k])) {
/*  262 */           j = 1;
/*      */         }
/*      */       }
/*  265 */       if (j == 0) {
/*  266 */         this.checkedNatives = true;
/*  267 */         return false;
/*      */       }
/*  269 */       this.useNatives = true;
/*  270 */       this.nativeFonts = new NativeFont[i];
/*  271 */       for (k = 0; k < i; k++) {
/*      */         try {
/*  273 */           this.nativeFonts[k] = new NativeFont(localObject[k], true);
/*      */         } catch (FontFormatException localFontFormatException2) {
/*  275 */           this.useNatives = false;
/*  276 */           this.nativeFonts = null;
/*      */         }
/*      */       }
/*      */     }
/*  280 */     if (this.useNatives) {
/*  281 */       this.glyphToCharMap = new char[getMapper().getNumGlyphs()];
/*      */     }
/*  283 */     this.checkedNatives = true;
/*  284 */     return this.useNatives;
/*      */   }
/*      */   
/*      */   private synchronized FileChannel open() throws FontFormatException
/*      */   {
/*  289 */     return open(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private synchronized FileChannel open(boolean paramBoolean)
/*      */     throws FontFormatException
/*      */   {
/*  301 */     if (this.disposerRecord.channel == null) {
/*  302 */       if (FontUtilities.isLogging()) {
/*  303 */         FontUtilities.getLogger().info("open TTF: " + this.platName);
/*      */       }
/*      */       try
/*      */       {
/*  307 */         RandomAccessFile localRandomAccessFile = (RandomAccessFile)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Object run() {
/*      */             try {
/*  311 */               return new RandomAccessFile(TrueTypeFont.this.platName, "r");
/*      */             }
/*      */             catch (FileNotFoundException localFileNotFoundException) {}
/*  314 */             return null;
/*      */           }
/*  316 */         });
/*  317 */         this.disposerRecord.channel = localRandomAccessFile.getChannel();
/*  318 */         this.fileSize = ((int)this.disposerRecord.channel.size());
/*  319 */         if (paramBoolean) {
/*  320 */           FontManager localFontManager = FontManagerFactory.getInstance();
/*  321 */           if ((localFontManager instanceof SunFontManager)) {
/*  322 */             ((SunFontManager)localFontManager).addToPool(this);
/*      */           }
/*      */         }
/*      */       } catch (NullPointerException localNullPointerException) {
/*  326 */         close();
/*  327 */         throw new FontFormatException(localNullPointerException.toString());
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (ClosedChannelException localClosedChannelException)
/*      */       {
/*      */ 
/*  334 */         Thread.interrupted();
/*  335 */         close();
/*  336 */         open();
/*      */       } catch (IOException localIOException) {
/*  338 */         close();
/*  339 */         throw new FontFormatException(localIOException.toString());
/*      */       }
/*      */     }
/*  342 */     return this.disposerRecord.channel;
/*      */   }
/*      */   
/*      */   protected synchronized void close() {
/*  346 */     this.disposerRecord.dispose();
/*      */   }
/*      */   
/*      */   int readBlock(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2)
/*      */   {
/*  351 */     int i = 0;
/*      */     try {
/*  353 */       synchronized (this) {
/*  354 */         if (this.disposerRecord.channel == null) {
/*  355 */           open();
/*      */         }
/*  357 */         if (paramInt1 + paramInt2 > this.fileSize) {
/*  358 */           if (paramInt1 >= this.fileSize)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  368 */             if (FontUtilities.isLogging()) {
/*  369 */               String str1 = "Read offset is " + paramInt1 + " file size is " + this.fileSize + " file is " + this.platName;
/*      */               
/*      */ 
/*  372 */               FontUtilities.getLogger().severe(str1);
/*      */             }
/*  374 */             return -1;
/*      */           }
/*  376 */           paramInt2 = this.fileSize - paramInt1;
/*      */         }
/*      */         
/*  379 */         paramByteBuffer.clear();
/*  380 */         this.disposerRecord.channel.position(paramInt1);
/*  381 */         while (i < paramInt2) {
/*  382 */           int j = this.disposerRecord.channel.read(paramByteBuffer);
/*  383 */           if (j == -1) {
/*  384 */             String str2 = "Unexpected EOF " + this;
/*  385 */             int k = (int)this.disposerRecord.channel.size();
/*  386 */             if (k != this.fileSize) {
/*  387 */               str2 = str2 + " File size was " + this.fileSize + " and now is " + k;
/*      */             }
/*      */             
/*  390 */             if (FontUtilities.isLogging()) {
/*  391 */               FontUtilities.getLogger().severe(str2);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  403 */             if ((i > paramInt2 / 2) || (i > 16384)) {
/*  404 */               paramByteBuffer.flip();
/*  405 */               if (FontUtilities.isLogging()) {
/*  406 */                 str2 = "Returning " + i + " bytes instead of " + paramInt2;
/*      */                 
/*  408 */                 FontUtilities.getLogger().severe(str2);
/*      */               }
/*      */             } else {
/*  411 */               i = -1;
/*      */             }
/*  413 */             throw new IOException(str2);
/*      */           }
/*  415 */           i += j;
/*      */         }
/*  417 */         paramByteBuffer.flip();
/*  418 */         if (i > paramInt2) {
/*  419 */           i = paramInt2;
/*      */         }
/*      */       }
/*      */     } catch (FontFormatException localFontFormatException) {
/*  423 */       if (FontUtilities.isLogging()) {
/*  424 */         FontUtilities.getLogger().severe("While reading " + this.platName, localFontFormatException);
/*      */       }
/*      */       
/*  427 */       i = -1;
/*  428 */       deregisterFontAndClearStrikeCache();
/*      */ 
/*      */     }
/*      */     catch (ClosedChannelException localClosedChannelException)
/*      */     {
/*  433 */       Thread.interrupted();
/*  434 */       close();
/*  435 */       return readBlock(paramByteBuffer, paramInt1, paramInt2);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */ 
/*      */ 
/*  444 */       if (FontUtilities.isLogging()) {
/*  445 */         FontUtilities.getLogger().severe("While reading " + this.platName, localIOException);
/*      */       }
/*      */       
/*  448 */       if (i == 0) {
/*  449 */         i = -1;
/*  450 */         deregisterFontAndClearStrikeCache();
/*      */       }
/*      */     }
/*  453 */     return i;
/*      */   }
/*      */   
/*      */   ByteBuffer readBlock(int paramInt1, int paramInt2)
/*      */   {
/*  458 */     ByteBuffer localByteBuffer = ByteBuffer.allocate(paramInt2);
/*      */     try {
/*  460 */       synchronized (this) {
/*  461 */         if (this.disposerRecord.channel == null) {
/*  462 */           open();
/*      */         }
/*  464 */         if (paramInt1 + paramInt2 > this.fileSize) {
/*  465 */           if (paramInt1 > this.fileSize) {
/*  466 */             return null;
/*      */           }
/*  468 */           localByteBuffer = ByteBuffer.allocate(this.fileSize - paramInt1);
/*      */         }
/*      */         
/*  471 */         this.disposerRecord.channel.position(paramInt1);
/*  472 */         this.disposerRecord.channel.read(localByteBuffer);
/*  473 */         localByteBuffer.flip();
/*      */       }
/*      */     } catch (FontFormatException localFontFormatException) {
/*  476 */       return null;
/*      */ 
/*      */     }
/*      */     catch (ClosedChannelException localClosedChannelException)
/*      */     {
/*  481 */       Thread.interrupted();
/*  482 */       close();
/*  483 */       readBlock(localByteBuffer, paramInt1, paramInt2);
/*      */     } catch (IOException localIOException) {
/*  485 */       return null;
/*      */     }
/*  487 */     return localByteBuffer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   byte[] readBytes(int paramInt1, int paramInt2)
/*      */   {
/*  497 */     ByteBuffer localByteBuffer = readBlock(paramInt1, paramInt2);
/*  498 */     if (localByteBuffer.hasArray()) {
/*  499 */       return localByteBuffer.array();
/*      */     }
/*  501 */     byte[] arrayOfByte = new byte[localByteBuffer.limit()];
/*  502 */     localByteBuffer.get(arrayOfByte);
/*  503 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */   private void verify(boolean paramBoolean) throws FontFormatException
/*      */   {
/*  508 */     open(paramBoolean);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void init(int paramInt)
/*      */     throws FontFormatException
/*      */   {
/*  517 */     int i = 0;
/*  518 */     ByteBuffer localByteBuffer1 = readBlock(0, 12);
/*      */     try {
/*  520 */       switch (localByteBuffer1.getInt())
/*      */       {
/*      */       case 1953784678: 
/*  523 */         localByteBuffer1.getInt();
/*  524 */         this.directoryCount = localByteBuffer1.getInt();
/*  525 */         if (paramInt >= this.directoryCount) {
/*  526 */           throw new FontFormatException("Bad collection index");
/*      */         }
/*  528 */         this.fontIndex = paramInt;
/*  529 */         localByteBuffer1 = readBlock(12 + 4 * paramInt, 4);
/*  530 */         i = localByteBuffer1.getInt();
/*  531 */         break;
/*      */       
/*      */       case 65536: 
/*      */       case 1330926671: 
/*      */       case 1953658213: 
/*      */         break;
/*      */       
/*      */ 
/*      */       default: 
/*  540 */         throw new FontFormatException("Unsupported sfnt " + getPublicFileName());
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  549 */       localByteBuffer1 = readBlock(i + 4, 2);
/*  550 */       this.numTables = localByteBuffer1.getShort();
/*  551 */       this.directoryOffset = (i + 12);
/*  552 */       ByteBuffer localByteBuffer2 = readBlock(this.directoryOffset, this.numTables * 16);
/*      */       
/*  554 */       IntBuffer localIntBuffer = localByteBuffer2.asIntBuffer();
/*      */       
/*  556 */       this.tableDirectory = new DirectoryEntry[this.numTables];
/*  557 */       for (int j = 0; j < this.numTables; j++) { DirectoryEntry localDirectoryEntry;
/*  558 */         this.tableDirectory[j] = (localDirectoryEntry = new DirectoryEntry());
/*  559 */         localDirectoryEntry.tag = localIntBuffer.get();
/*  560 */         localIntBuffer.get();
/*  561 */         localDirectoryEntry.offset = localIntBuffer.get();
/*  562 */         localDirectoryEntry.length = localIntBuffer.get();
/*  563 */         if (localDirectoryEntry.offset + localDirectoryEntry.length > this.fileSize) {
/*  564 */           throw new FontFormatException("bad table, tag=" + localDirectoryEntry.tag);
/*      */         }
/*      */       }
/*      */       
/*  568 */       if (getDirectoryEntry(1751474532) == null) {
/*  569 */         throw new FontFormatException("missing head table");
/*      */       }
/*  571 */       if (getDirectoryEntry(1835104368) == null) {
/*  572 */         throw new FontFormatException("missing maxp table");
/*      */       }
/*  574 */       if ((getDirectoryEntry(1752003704) != null) && 
/*  575 */         (getDirectoryEntry(1751672161) == null)) {
/*  576 */         throw new FontFormatException("missing hhea table");
/*      */       }
/*  578 */       initNames();
/*      */     } catch (Exception localException) {
/*  580 */       if (FontUtilities.isLogging()) {
/*  581 */         FontUtilities.getLogger().severe(localException.toString());
/*      */       }
/*  583 */       if ((localException instanceof FontFormatException)) {
/*  584 */         throw ((FontFormatException)localException);
/*      */       }
/*  586 */       throw new FontFormatException(localException.toString());
/*      */     }
/*      */     
/*  589 */     if ((this.familyName == null) || (this.fullName == null)) {
/*  590 */       throw new FontFormatException("Font name not found");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  596 */     ByteBuffer localByteBuffer3 = getTableBuffer(1330851634);
/*  597 */     setStyle(localByteBuffer3);
/*  598 */     setCJKSupport(localByteBuffer3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  608 */   static final String[] encoding_mapping = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361", "", "", "", "", "", "", "", "", "", "" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  656 */   private static final String[][] languages = { { "en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", "no", "pt", "sq", "sv" }, { "cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", "sq", "sr" }, { "bg", "mk", "ru", "sh", "uk" }, { "el" }, { "tr" }, { "he" }, { "ar" }, { "et", "lt", "lv" }, { "th" }, { "ja" }, { "zh", "zh_CN" }, { "ko" }, { "zh_HK", "zh_TW" }, { "ko" } };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  703 */   private static final String[] codePages = { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  720 */   private static String defaultCodePage = null;
/*      */   public static final int reserved_bits1 = Integer.MIN_VALUE;
/*      */   
/*  723 */   static String getCodePage() { if (defaultCodePage != null) {
/*  724 */       return defaultCodePage;
/*      */     }
/*      */     
/*  727 */     if (FontUtilities.isWindows)
/*      */     {
/*  729 */       defaultCodePage = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
/*      */     }
/*      */     else {
/*  732 */       if (languages.length != codePages.length) {
/*  733 */         throw new InternalError("wrong code pages array length");
/*      */       }
/*  735 */       Locale localLocale = SunToolkit.getStartupLocale();
/*      */       
/*  737 */       String str1 = localLocale.getLanguage();
/*  738 */       if (str1 != null) {
/*  739 */         if (str1.equals("zh")) {
/*  740 */           String str2 = localLocale.getCountry();
/*  741 */           if (str2 != null) {
/*  742 */             str1 = str1 + "_" + str2;
/*      */           }
/*      */         }
/*  745 */         for (int i = 0; i < languages.length; i++) {
/*  746 */           for (int j = 0; j < languages[i].length; j++) {
/*  747 */             if (str1.equals(languages[i][j])) {
/*  748 */               defaultCodePage = codePages[i];
/*  749 */               return defaultCodePage;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  755 */     if (defaultCodePage == null) {
/*  756 */       defaultCodePage = "";
/*      */     }
/*  758 */     return defaultCodePage;
/*      */   }
/*      */   
/*      */ 
/*      */   public static final int reserved_bits2 = 65535;
/*      */   
/*      */   boolean supportsEncoding(String paramString)
/*      */   {
/*  766 */     if (paramString == null) {
/*  767 */       paramString = getCodePage();
/*      */     }
/*  769 */     if ("".equals(paramString)) {
/*  770 */       return false;
/*      */     }
/*      */     
/*  773 */     paramString = paramString.toLowerCase();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  782 */     if (paramString.equals("gb18030")) {
/*  783 */       paramString = "gbk";
/*  784 */     } else if (paramString.equals("ms950_hkscs")) {
/*  785 */       paramString = "ms950";
/*      */     }
/*      */     
/*  788 */     ByteBuffer localByteBuffer = getTableBuffer(1330851634);
/*      */     
/*  790 */     if ((localByteBuffer == null) || (localByteBuffer.capacity() < 86)) {
/*  791 */       return false;
/*      */     }
/*      */     
/*  794 */     int i = localByteBuffer.getInt(78);
/*  795 */     int j = localByteBuffer.getInt(82);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  805 */     for (int k = 0; k < encoding_mapping.length; k++) {
/*  806 */       if ((encoding_mapping[k].equals(paramString)) && 
/*  807 */         ((1 << k & i) != 0)) {
/*  808 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  812 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setCJKSupport(ByteBuffer paramByteBuffer)
/*      */   {
/*  819 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 50)) {
/*  820 */       return;
/*      */     }
/*  822 */     int i = paramByteBuffer.getInt(46);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  829 */     this.supportsCJK = ((i & 0x29BF0000) != 0);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  838 */     this.supportsJA = ((i & 0x60000) != 0);
/*      */   }
/*      */   
/*      */   boolean supportsJA() {
/*  842 */     return this.supportsJA;
/*      */   }
/*      */   
/*      */   ByteBuffer getTableBuffer(int paramInt) {
/*  846 */     DirectoryEntry localDirectoryEntry = null;
/*      */     
/*  848 */     for (int i = 0; i < this.numTables; i++) {
/*  849 */       if (this.tableDirectory[i].tag == paramInt) {
/*  850 */         localDirectoryEntry = this.tableDirectory[i];
/*  851 */         break;
/*      */       }
/*      */     }
/*  854 */     if ((localDirectoryEntry == null) || (localDirectoryEntry.length == 0) || (localDirectoryEntry.offset + localDirectoryEntry.length > this.fileSize))
/*      */     {
/*  856 */       return null;
/*      */     }
/*      */     
/*  859 */     i = 0;
/*  860 */     ByteBuffer localByteBuffer = ByteBuffer.allocate(localDirectoryEntry.length);
/*  861 */     synchronized (this) {
/*      */       try {
/*  863 */         if (this.disposerRecord.channel == null) {
/*  864 */           open();
/*      */         }
/*  866 */         this.disposerRecord.channel.position(localDirectoryEntry.offset);
/*  867 */         i = this.disposerRecord.channel.read(localByteBuffer);
/*  868 */         localByteBuffer.flip();
/*      */ 
/*      */       }
/*      */       catch (ClosedChannelException localClosedChannelException)
/*      */       {
/*  873 */         Thread.interrupted();
/*  874 */         close();
/*  875 */         return getTableBuffer(paramInt);
/*      */       } catch (IOException localIOException) {
/*  877 */         return null;
/*      */       } catch (FontFormatException localFontFormatException) {
/*  879 */         return null;
/*      */       }
/*      */       
/*  882 */       if (i < localDirectoryEntry.length) {
/*  883 */         return null;
/*      */       }
/*  885 */       return localByteBuffer;
/*      */     }
/*      */   }
/*      */   
/*      */   protected long getLayoutTableCache()
/*      */   {
/*      */     try
/*      */     {
/*  893 */       return getScaler().getLayoutTableCache();
/*      */     } catch (FontScalerException localFontScalerException) {}
/*  895 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   protected byte[] getTableBytes(int paramInt)
/*      */   {
/*  901 */     ByteBuffer localByteBuffer = getTableBuffer(paramInt);
/*  902 */     if (localByteBuffer == null)
/*  903 */       return null;
/*  904 */     if (localByteBuffer.hasArray()) {
/*      */       try {
/*  906 */         return localByteBuffer.array();
/*      */       }
/*      */       catch (Exception localException) {}
/*      */     }
/*  910 */     byte[] arrayOfByte = new byte[getTableSize(paramInt)];
/*  911 */     localByteBuffer.get(arrayOfByte);
/*  912 */     return arrayOfByte;
/*      */   }
/*      */   
/*      */   int getTableSize(int paramInt) {
/*  916 */     for (int i = 0; i < this.numTables; i++) {
/*  917 */       if (this.tableDirectory[i].tag == paramInt) {
/*  918 */         return this.tableDirectory[i].length;
/*      */       }
/*      */     }
/*  921 */     return 0;
/*      */   }
/*      */   
/*      */   int getTableOffset(int paramInt) {
/*  925 */     for (int i = 0; i < this.numTables; i++) {
/*  926 */       if (this.tableDirectory[i].tag == paramInt) {
/*  927 */         return this.tableDirectory[i].offset;
/*      */       }
/*      */     }
/*  930 */     return 0;
/*      */   }
/*      */   
/*      */   DirectoryEntry getDirectoryEntry(int paramInt) {
/*  934 */     for (int i = 0; i < this.numTables; i++) {
/*  935 */       if (this.tableDirectory[i].tag == paramInt) {
/*  936 */         return this.tableDirectory[i];
/*      */       }
/*      */     }
/*  939 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   boolean useEmbeddedBitmapsForSize(int paramInt)
/*      */   {
/*  946 */     if (!this.supportsCJK) {
/*  947 */       return false;
/*      */     }
/*  949 */     if (getDirectoryEntry(1161972803) == null) {
/*  950 */       return false;
/*      */     }
/*  952 */     ByteBuffer localByteBuffer = getTableBuffer(1161972803);
/*  953 */     int i = localByteBuffer.getInt(4);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  958 */     for (int j = 0; j < i; j++) {
/*  959 */       int k = localByteBuffer.get(8 + j * 48 + 45) & 0xFF;
/*  960 */       if (k == paramInt) {
/*  961 */         return true;
/*      */       }
/*      */     }
/*  964 */     return false;
/*      */   }
/*      */   
/*      */   public String getFullName() {
/*  968 */     return this.fullName;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setStyle()
/*      */   {
/*  976 */     setStyle(getTableBuffer(1330851634));
/*      */   }
/*      */   
/*  979 */   private int fontWidth = 0;
/*      */   
/*      */   public int getWidth() {
/*  982 */     return this.fontWidth > 0 ? this.fontWidth : super.getWidth();
/*      */   }
/*      */   
/*  985 */   private int fontWeight = 0;
/*      */   private static final int fsSelectionItalicBit = 1;
/*      */   
/*  988 */   public int getWeight() { return this.fontWeight > 0 ? this.fontWeight : super.getWeight(); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int fsSelectionBoldBit = 32;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int fsSelectionRegularBit = 64;
/*      */   
/*      */ 
/*      */ 
/*      */   private float stSize;
/*      */   
/*      */ 
/*      */   private void setStyle(ByteBuffer paramByteBuffer)
/*      */   {
/* 1006 */     if (paramByteBuffer == null) {
/* 1007 */       return;
/*      */     }
/* 1009 */     if (paramByteBuffer.capacity() >= 8) {
/* 1010 */       this.fontWeight = (paramByteBuffer.getChar(4) & 0xFFFF);
/* 1011 */       this.fontWidth = (paramByteBuffer.getChar(6) & 0xFFFF);
/*      */     }
/*      */     
/* 1014 */     if (paramByteBuffer.capacity() < 64) {
/* 1015 */       super.setStyle();
/* 1016 */       return;
/*      */     }
/* 1018 */     int i = paramByteBuffer.getChar(62) & 0xFFFF;
/* 1019 */     int j = i & 0x1;
/* 1020 */     int k = i & 0x20;
/* 1021 */     int m = i & 0x40;
/*      */     
/*      */ 
/*      */ 
/* 1025 */     if ((m != 0) && ((j | k) != 0))
/*      */     {
/* 1027 */       super.setStyle();
/* 1028 */       return; }
/* 1029 */     if ((m | j | k) == 0)
/*      */     {
/* 1031 */       super.setStyle();
/* 1032 */       return;
/*      */     }
/* 1034 */     switch (k | j) {
/*      */     case 1: 
/* 1036 */       this.style = 2;
/* 1037 */       break;
/*      */     case 32: 
/* 1039 */       if ((FontUtilities.isSolaris) && (this.platName.endsWith("HG-GothicB.ttf")))
/*      */       {
/*      */ 
/*      */ 
/* 1043 */         this.style = 0;
/*      */       } else {
/* 1045 */         this.style = 1;
/*      */       }
/* 1047 */       break;
/*      */     case 33: 
/* 1049 */       this.style = 3;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */   private void setStrikethroughMetrics(ByteBuffer paramByteBuffer, int paramInt)
/*      */   {
/* 1056 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 30) || (paramInt < 0)) {
/* 1057 */       this.stSize = 0.05F;
/* 1058 */       this.stPos = -0.4F;
/* 1059 */       return;
/*      */     }
/* 1061 */     ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
/* 1062 */     this.stSize = (localShortBuffer.get(13) / paramInt);
/* 1063 */     this.stPos = (-localShortBuffer.get(14) / paramInt);
/*      */   }
/*      */   
/*      */   private void setUnderlineMetrics(ByteBuffer paramByteBuffer, int paramInt) {
/* 1067 */     if ((paramByteBuffer == null) || (paramByteBuffer.capacity() < 12) || (paramInt < 0)) {
/* 1068 */       this.ulSize = 0.05F;
/* 1069 */       this.ulPos = 0.1F;
/* 1070 */       return;
/*      */     }
/* 1072 */     ShortBuffer localShortBuffer = paramByteBuffer.asShortBuffer();
/* 1073 */     this.ulSize = (localShortBuffer.get(5) / paramInt);
/* 1074 */     this.ulPos = (-localShortBuffer.get(4) / paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */   public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt)
/*      */   {
/* 1080 */     if ((this.ulSize == 0.0F) && (this.ulPos == 0.0F))
/*      */     {
/* 1082 */       ByteBuffer localByteBuffer1 = getTableBuffer(1751474532);
/* 1083 */       int i = -1;
/* 1084 */       if ((localByteBuffer1 != null) && (localByteBuffer1.capacity() >= 18)) {
/* 1085 */         localObject = localByteBuffer1.asShortBuffer();
/* 1086 */         i = ((ShortBuffer)localObject).get(9) & 0xFFFF;
/* 1087 */         if ((i < 16) || (i > 16384)) {
/* 1088 */           i = 2048;
/*      */         }
/*      */       }
/*      */       
/* 1092 */       Object localObject = getTableBuffer(1330851634);
/* 1093 */       setStrikethroughMetrics((ByteBuffer)localObject, i);
/*      */       
/* 1095 */       ByteBuffer localByteBuffer2 = getTableBuffer(1886352244);
/* 1096 */       setUnderlineMetrics(localByteBuffer2, i);
/*      */     }
/*      */     
/* 1099 */     paramArrayOfFloat[paramInt] = (this.stPos * paramFloat);
/* 1100 */     paramArrayOfFloat[(paramInt + 1)] = (this.stSize * paramFloat);
/*      */     
/* 1102 */     paramArrayOfFloat[(paramInt + 2)] = (this.ulPos * paramFloat);
/* 1103 */     paramArrayOfFloat[(paramInt + 3)] = (this.ulSize * paramFloat);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String makeString(byte[] paramArrayOfByte, int paramInt, short paramShort)
/*      */   {
/*      */     Object localObject;
/*      */     
/*      */ 
/* 1114 */     if ((paramShort >= 2) && (paramShort <= 6)) {
/* 1115 */       localObject = paramArrayOfByte;
/* 1116 */       int i = paramInt;
/* 1117 */       paramArrayOfByte = new byte[i];
/* 1118 */       paramInt = 0;
/* 1119 */       for (int j = 0; j < i; j++) {
/* 1120 */         if (localObject[j] != 0) {
/* 1121 */           paramArrayOfByte[(paramInt++)] = localObject[j];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1127 */     switch (paramShort) {
/* 1128 */     case 1:  localObject = "UTF-16"; break;
/* 1129 */     case 0:  localObject = "UTF-16"; break;
/* 1130 */     case 2:  localObject = "SJIS"; break;
/* 1131 */     case 3:  localObject = "GBK"; break;
/* 1132 */     case 4:  localObject = "MS950"; break;
/* 1133 */     case 5:  localObject = "EUC_KR"; break;
/* 1134 */     case 6:  localObject = "Johab"; break;
/* 1135 */     default:  localObject = "UTF-16";
/*      */     }
/*      */     try
/*      */     {
/* 1139 */       return new String(paramArrayOfByte, 0, paramInt, (String)localObject);
/*      */     } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1141 */       if (FontUtilities.isLogging()) {
/* 1142 */         FontUtilities.getLogger().warning(localUnsupportedEncodingException + " EncodingID=" + paramShort);
/*      */       }
/* 1144 */       return new String(paramArrayOfByte, 0, paramInt);
/*      */     } catch (Throwable localThrowable) {}
/* 1146 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initNames()
/*      */   {
/* 1152 */     byte[] arrayOfByte = new byte['Ā'];
/* 1153 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/*      */     
/* 1155 */     if (localByteBuffer != null) {
/* 1156 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1157 */       localShortBuffer.get();
/* 1158 */       int i = localShortBuffer.get();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1164 */       int j = localShortBuffer.get() & 0xFFFF;
/*      */       
/* 1166 */       this.nameLocale = SunToolkit.getStartupLocale();
/* 1167 */       int k = getLCIDFromLocale(this.nameLocale);
/*      */       
/* 1169 */       for (int m = 0; m < i; m++) {
/* 1170 */         int n = localShortBuffer.get();
/* 1171 */         if (n != 3) {
/* 1172 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1175 */           short s = localShortBuffer.get();
/* 1176 */           int i1 = localShortBuffer.get();
/* 1177 */           int i2 = localShortBuffer.get();
/* 1178 */           int i3 = localShortBuffer.get() & 0xFFFF;
/* 1179 */           int i4 = (localShortBuffer.get() & 0xFFFF) + j;
/* 1180 */           String str = null;
/* 1181 */           switch (i2)
/*      */           {
/*      */ 
/*      */           case 1: 
/* 1185 */             if ((this.familyName == null) || (i1 == 1033) || (i1 == k))
/*      */             {
/*      */ 
/* 1188 */               localByteBuffer.position(i4);
/* 1189 */               localByteBuffer.get(arrayOfByte, 0, i3);
/* 1190 */               str = makeString(arrayOfByte, i3, s);
/*      */               
/* 1192 */               if ((this.familyName == null) || (i1 == 1033)) {
/* 1193 */                 this.familyName = str;
/*      */               }
/* 1195 */               if (i1 == k) {
/* 1196 */                 this.localeFamilyName = str;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             break;
/*      */           case 4: 
/* 1215 */             if ((this.fullName == null) || (i1 == 1033) || (i1 == k))
/*      */             {
/*      */ 
/* 1218 */               localByteBuffer.position(i4);
/* 1219 */               localByteBuffer.get(arrayOfByte, 0, i3);
/* 1220 */               str = makeString(arrayOfByte, i3, s);
/*      */               
/* 1222 */               if ((this.fullName == null) || (i1 == 1033)) {
/* 1223 */                 this.fullName = str;
/*      */               }
/* 1225 */               if (i1 == k)
/* 1226 */                 this.localeFullName = str;
/*      */             }
/*      */             break; }
/*      */           
/*      */         }
/*      */       }
/* 1232 */       if (this.localeFamilyName == null) {
/* 1233 */         this.localeFamilyName = this.familyName;
/*      */       }
/* 1235 */       if (this.localeFullName == null) {
/* 1236 */         this.localeFullName = this.fullName;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String lookupName(short paramShort, int paramInt)
/*      */   {
/* 1247 */     String str = null;
/* 1248 */     byte[] arrayOfByte = new byte['Ѐ'];
/*      */     
/* 1250 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/* 1251 */     if (localByteBuffer != null) {
/* 1252 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1253 */       localShortBuffer.get();
/* 1254 */       int i = localShortBuffer.get();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1261 */       int j = localShortBuffer.get() & 0xFFFF;
/*      */       
/* 1263 */       for (int k = 0; k < i; k++) {
/* 1264 */         int m = localShortBuffer.get();
/* 1265 */         if (m != 3) {
/* 1266 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1269 */           short s1 = localShortBuffer.get();
/* 1270 */           short s2 = localShortBuffer.get();
/* 1271 */           int n = localShortBuffer.get();
/* 1272 */           int i1 = localShortBuffer.get() & 0xFFFF;
/* 1273 */           int i2 = (localShortBuffer.get() & 0xFFFF) + j;
/* 1274 */           if ((n == paramInt) && (((str == null) && (s2 == 1033)) || (s2 == paramShort)))
/*      */           {
/*      */ 
/* 1277 */             localByteBuffer.position(i2);
/* 1278 */             localByteBuffer.get(arrayOfByte, 0, i1);
/* 1279 */             str = makeString(arrayOfByte, i1, s1);
/* 1280 */             if (s2 == paramShort)
/* 1281 */               return str;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1286 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getFontCount()
/*      */   {
/* 1293 */     return this.directoryCount;
/*      */   }
/*      */   
/*      */   protected synchronized FontScaler getScaler() {
/* 1297 */     if (this.scaler == null) {
/* 1298 */       this.scaler = FontScaler.getScaler(this, this.fontIndex, this.supportsCJK, this.fileSize);
/*      */     }
/*      */     
/* 1301 */     return this.scaler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getPostscriptName()
/*      */   {
/* 1310 */     String str = lookupName((short)1033, 6);
/* 1311 */     if (str == null) {
/* 1312 */       return this.fullName;
/*      */     }
/* 1314 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getFontName(Locale paramLocale)
/*      */   {
/* 1320 */     if (paramLocale == null)
/* 1321 */       return this.fullName;
/* 1322 */     if ((paramLocale.equals(this.nameLocale)) && (this.localeFullName != null)) {
/* 1323 */       return this.localeFullName;
/*      */     }
/* 1325 */     short s = getLCIDFromLocale(paramLocale);
/* 1326 */     String str = lookupName(s, 4);
/* 1327 */     if (str == null) {
/* 1328 */       return this.fullName;
/*      */     }
/* 1330 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void addLCIDMapEntry(Map<String, Short> paramMap, String paramString, short paramShort)
/*      */   {
/* 1340 */     paramMap.put(paramString, Short.valueOf(paramShort));
/*      */   }
/*      */   
/*      */   private static synchronized void createLCIDMap() {
/* 1344 */     if (lcidMap != null) {
/* 1345 */       return;
/*      */     }
/*      */     
/* 1348 */     HashMap localHashMap = new HashMap(200);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1376 */     addLCIDMapEntry(localHashMap, "ar", (short)1025);
/* 1377 */     addLCIDMapEntry(localHashMap, "bg", (short)1026);
/* 1378 */     addLCIDMapEntry(localHashMap, "ca", (short)1027);
/* 1379 */     addLCIDMapEntry(localHashMap, "zh", (short)1028);
/* 1380 */     addLCIDMapEntry(localHashMap, "cs", (short)1029);
/* 1381 */     addLCIDMapEntry(localHashMap, "da", (short)1030);
/* 1382 */     addLCIDMapEntry(localHashMap, "de", (short)1031);
/* 1383 */     addLCIDMapEntry(localHashMap, "el", (short)1032);
/* 1384 */     addLCIDMapEntry(localHashMap, "es", (short)1034);
/* 1385 */     addLCIDMapEntry(localHashMap, "fi", (short)1035);
/* 1386 */     addLCIDMapEntry(localHashMap, "fr", (short)1036);
/* 1387 */     addLCIDMapEntry(localHashMap, "iw", (short)1037);
/* 1388 */     addLCIDMapEntry(localHashMap, "hu", (short)1038);
/* 1389 */     addLCIDMapEntry(localHashMap, "is", (short)1039);
/* 1390 */     addLCIDMapEntry(localHashMap, "it", (short)1040);
/* 1391 */     addLCIDMapEntry(localHashMap, "ja", (short)1041);
/* 1392 */     addLCIDMapEntry(localHashMap, "ko", (short)1042);
/* 1393 */     addLCIDMapEntry(localHashMap, "nl", (short)1043);
/* 1394 */     addLCIDMapEntry(localHashMap, "no", (short)1044);
/* 1395 */     addLCIDMapEntry(localHashMap, "pl", (short)1045);
/* 1396 */     addLCIDMapEntry(localHashMap, "pt", (short)1046);
/* 1397 */     addLCIDMapEntry(localHashMap, "rm", (short)1047);
/* 1398 */     addLCIDMapEntry(localHashMap, "ro", (short)1048);
/* 1399 */     addLCIDMapEntry(localHashMap, "ru", (short)1049);
/* 1400 */     addLCIDMapEntry(localHashMap, "hr", (short)1050);
/* 1401 */     addLCIDMapEntry(localHashMap, "sk", (short)1051);
/* 1402 */     addLCIDMapEntry(localHashMap, "sq", (short)1052);
/* 1403 */     addLCIDMapEntry(localHashMap, "sv", (short)1053);
/* 1404 */     addLCIDMapEntry(localHashMap, "th", (short)1054);
/* 1405 */     addLCIDMapEntry(localHashMap, "tr", (short)1055);
/* 1406 */     addLCIDMapEntry(localHashMap, "ur", (short)1056);
/* 1407 */     addLCIDMapEntry(localHashMap, "in", (short)1057);
/* 1408 */     addLCIDMapEntry(localHashMap, "uk", (short)1058);
/* 1409 */     addLCIDMapEntry(localHashMap, "be", (short)1059);
/* 1410 */     addLCIDMapEntry(localHashMap, "sl", (short)1060);
/* 1411 */     addLCIDMapEntry(localHashMap, "et", (short)1061);
/* 1412 */     addLCIDMapEntry(localHashMap, "lv", (short)1062);
/* 1413 */     addLCIDMapEntry(localHashMap, "lt", (short)1063);
/* 1414 */     addLCIDMapEntry(localHashMap, "fa", (short)1065);
/* 1415 */     addLCIDMapEntry(localHashMap, "vi", (short)1066);
/* 1416 */     addLCIDMapEntry(localHashMap, "hy", (short)1067);
/* 1417 */     addLCIDMapEntry(localHashMap, "eu", (short)1069);
/* 1418 */     addLCIDMapEntry(localHashMap, "mk", (short)1071);
/* 1419 */     addLCIDMapEntry(localHashMap, "tn", (short)1074);
/* 1420 */     addLCIDMapEntry(localHashMap, "xh", (short)1076);
/* 1421 */     addLCIDMapEntry(localHashMap, "zu", (short)1077);
/* 1422 */     addLCIDMapEntry(localHashMap, "af", (short)1078);
/* 1423 */     addLCIDMapEntry(localHashMap, "ka", (short)1079);
/* 1424 */     addLCIDMapEntry(localHashMap, "fo", (short)1080);
/* 1425 */     addLCIDMapEntry(localHashMap, "hi", (short)1081);
/* 1426 */     addLCIDMapEntry(localHashMap, "mt", (short)1082);
/* 1427 */     addLCIDMapEntry(localHashMap, "se", (short)1083);
/* 1428 */     addLCIDMapEntry(localHashMap, "gd", (short)1084);
/* 1429 */     addLCIDMapEntry(localHashMap, "ms", (short)1086);
/* 1430 */     addLCIDMapEntry(localHashMap, "kk", (short)1087);
/* 1431 */     addLCIDMapEntry(localHashMap, "ky", (short)1088);
/* 1432 */     addLCIDMapEntry(localHashMap, "sw", (short)1089);
/* 1433 */     addLCIDMapEntry(localHashMap, "tt", (short)1092);
/* 1434 */     addLCIDMapEntry(localHashMap, "bn", (short)1093);
/* 1435 */     addLCIDMapEntry(localHashMap, "pa", (short)1094);
/* 1436 */     addLCIDMapEntry(localHashMap, "gu", (short)1095);
/* 1437 */     addLCIDMapEntry(localHashMap, "ta", (short)1097);
/* 1438 */     addLCIDMapEntry(localHashMap, "te", (short)1098);
/* 1439 */     addLCIDMapEntry(localHashMap, "kn", (short)1099);
/* 1440 */     addLCIDMapEntry(localHashMap, "ml", (short)1100);
/* 1441 */     addLCIDMapEntry(localHashMap, "mr", (short)1102);
/* 1442 */     addLCIDMapEntry(localHashMap, "sa", (short)1103);
/* 1443 */     addLCIDMapEntry(localHashMap, "mn", (short)1104);
/* 1444 */     addLCIDMapEntry(localHashMap, "cy", (short)1106);
/* 1445 */     addLCIDMapEntry(localHashMap, "gl", (short)1110);
/* 1446 */     addLCIDMapEntry(localHashMap, "dv", (short)1125);
/* 1447 */     addLCIDMapEntry(localHashMap, "qu", (short)1131);
/* 1448 */     addLCIDMapEntry(localHashMap, "mi", (short)1153);
/* 1449 */     addLCIDMapEntry(localHashMap, "ar_IQ", (short)2049);
/* 1450 */     addLCIDMapEntry(localHashMap, "zh_CN", (short)2052);
/* 1451 */     addLCIDMapEntry(localHashMap, "de_CH", (short)2055);
/* 1452 */     addLCIDMapEntry(localHashMap, "en_GB", (short)2057);
/* 1453 */     addLCIDMapEntry(localHashMap, "es_MX", (short)2058);
/* 1454 */     addLCIDMapEntry(localHashMap, "fr_BE", (short)2060);
/* 1455 */     addLCIDMapEntry(localHashMap, "it_CH", (short)2064);
/* 1456 */     addLCIDMapEntry(localHashMap, "nl_BE", (short)2067);
/* 1457 */     addLCIDMapEntry(localHashMap, "no_NO_NY", (short)2068);
/* 1458 */     addLCIDMapEntry(localHashMap, "pt_PT", (short)2070);
/* 1459 */     addLCIDMapEntry(localHashMap, "ro_MD", (short)2072);
/* 1460 */     addLCIDMapEntry(localHashMap, "ru_MD", (short)2073);
/* 1461 */     addLCIDMapEntry(localHashMap, "sr_CS", (short)2074);
/* 1462 */     addLCIDMapEntry(localHashMap, "sv_FI", (short)2077);
/* 1463 */     addLCIDMapEntry(localHashMap, "az_AZ", (short)2092);
/* 1464 */     addLCIDMapEntry(localHashMap, "se_SE", (short)2107);
/* 1465 */     addLCIDMapEntry(localHashMap, "ga_IE", (short)2108);
/* 1466 */     addLCIDMapEntry(localHashMap, "ms_BN", (short)2110);
/* 1467 */     addLCIDMapEntry(localHashMap, "uz_UZ", (short)2115);
/* 1468 */     addLCIDMapEntry(localHashMap, "qu_EC", (short)2155);
/* 1469 */     addLCIDMapEntry(localHashMap, "ar_EG", (short)3073);
/* 1470 */     addLCIDMapEntry(localHashMap, "zh_HK", (short)3076);
/* 1471 */     addLCIDMapEntry(localHashMap, "de_AT", (short)3079);
/* 1472 */     addLCIDMapEntry(localHashMap, "en_AU", (short)3081);
/* 1473 */     addLCIDMapEntry(localHashMap, "fr_CA", (short)3084);
/* 1474 */     addLCIDMapEntry(localHashMap, "sr_CS", (short)3098);
/* 1475 */     addLCIDMapEntry(localHashMap, "se_FI", (short)3131);
/* 1476 */     addLCIDMapEntry(localHashMap, "qu_PE", (short)3179);
/* 1477 */     addLCIDMapEntry(localHashMap, "ar_LY", (short)4097);
/* 1478 */     addLCIDMapEntry(localHashMap, "zh_SG", (short)4100);
/* 1479 */     addLCIDMapEntry(localHashMap, "de_LU", (short)4103);
/* 1480 */     addLCIDMapEntry(localHashMap, "en_CA", (short)4105);
/* 1481 */     addLCIDMapEntry(localHashMap, "es_GT", (short)4106);
/* 1482 */     addLCIDMapEntry(localHashMap, "fr_CH", (short)4108);
/* 1483 */     addLCIDMapEntry(localHashMap, "hr_BA", (short)4122);
/* 1484 */     addLCIDMapEntry(localHashMap, "ar_DZ", (short)5121);
/* 1485 */     addLCIDMapEntry(localHashMap, "zh_MO", (short)5124);
/* 1486 */     addLCIDMapEntry(localHashMap, "de_LI", (short)5127);
/* 1487 */     addLCIDMapEntry(localHashMap, "en_NZ", (short)5129);
/* 1488 */     addLCIDMapEntry(localHashMap, "es_CR", (short)5130);
/* 1489 */     addLCIDMapEntry(localHashMap, "fr_LU", (short)5132);
/* 1490 */     addLCIDMapEntry(localHashMap, "bs_BA", (short)5146);
/* 1491 */     addLCIDMapEntry(localHashMap, "ar_MA", (short)6145);
/* 1492 */     addLCIDMapEntry(localHashMap, "en_IE", (short)6153);
/* 1493 */     addLCIDMapEntry(localHashMap, "es_PA", (short)6154);
/* 1494 */     addLCIDMapEntry(localHashMap, "fr_MC", (short)6156);
/* 1495 */     addLCIDMapEntry(localHashMap, "sr_BA", (short)6170);
/* 1496 */     addLCIDMapEntry(localHashMap, "ar_TN", (short)7169);
/* 1497 */     addLCIDMapEntry(localHashMap, "en_ZA", (short)7177);
/* 1498 */     addLCIDMapEntry(localHashMap, "es_DO", (short)7178);
/* 1499 */     addLCIDMapEntry(localHashMap, "sr_BA", (short)7194);
/* 1500 */     addLCIDMapEntry(localHashMap, "ar_OM", (short)8193);
/* 1501 */     addLCIDMapEntry(localHashMap, "en_JM", (short)8201);
/* 1502 */     addLCIDMapEntry(localHashMap, "es_VE", (short)8202);
/* 1503 */     addLCIDMapEntry(localHashMap, "ar_YE", (short)9217);
/* 1504 */     addLCIDMapEntry(localHashMap, "es_CO", (short)9226);
/* 1505 */     addLCIDMapEntry(localHashMap, "ar_SY", (short)10241);
/* 1506 */     addLCIDMapEntry(localHashMap, "en_BZ", (short)10249);
/* 1507 */     addLCIDMapEntry(localHashMap, "es_PE", (short)10250);
/* 1508 */     addLCIDMapEntry(localHashMap, "ar_JO", (short)11265);
/* 1509 */     addLCIDMapEntry(localHashMap, "en_TT", (short)11273);
/* 1510 */     addLCIDMapEntry(localHashMap, "es_AR", (short)11274);
/* 1511 */     addLCIDMapEntry(localHashMap, "ar_LB", (short)12289);
/* 1512 */     addLCIDMapEntry(localHashMap, "en_ZW", (short)12297);
/* 1513 */     addLCIDMapEntry(localHashMap, "es_EC", (short)12298);
/* 1514 */     addLCIDMapEntry(localHashMap, "ar_KW", (short)13313);
/* 1515 */     addLCIDMapEntry(localHashMap, "en_PH", (short)13321);
/* 1516 */     addLCIDMapEntry(localHashMap, "es_CL", (short)13322);
/* 1517 */     addLCIDMapEntry(localHashMap, "ar_AE", (short)14337);
/* 1518 */     addLCIDMapEntry(localHashMap, "es_UY", (short)14346);
/* 1519 */     addLCIDMapEntry(localHashMap, "ar_BH", (short)15361);
/* 1520 */     addLCIDMapEntry(localHashMap, "es_PY", (short)15370);
/* 1521 */     addLCIDMapEntry(localHashMap, "ar_QA", (short)16385);
/* 1522 */     addLCIDMapEntry(localHashMap, "es_BO", (short)16394);
/* 1523 */     addLCIDMapEntry(localHashMap, "es_SV", (short)17418);
/* 1524 */     addLCIDMapEntry(localHashMap, "es_HN", (short)18442);
/* 1525 */     addLCIDMapEntry(localHashMap, "es_NI", (short)19466);
/* 1526 */     addLCIDMapEntry(localHashMap, "es_PR", (short)20490);
/*      */     
/* 1528 */     lcidMap = localHashMap;
/*      */   }
/*      */   
/*      */   private static short getLCIDFromLocale(Locale paramLocale)
/*      */   {
/* 1533 */     if (paramLocale.equals(Locale.US)) {
/* 1534 */       return 1033;
/*      */     }
/*      */     
/* 1537 */     if (lcidMap == null) {
/* 1538 */       createLCIDMap();
/*      */     }
/*      */     
/* 1541 */     String str = paramLocale.toString();
/* 1542 */     while (!"".equals(str)) {
/* 1543 */       Short localShort = (Short)lcidMap.get(str);
/* 1544 */       if (localShort != null) {
/* 1545 */         return localShort.shortValue();
/*      */       }
/* 1547 */       int i = str.lastIndexOf('_');
/* 1548 */       if (i < 1) {
/* 1549 */         return 1033;
/*      */       }
/* 1551 */       str = str.substring(0, i);
/*      */     }
/*      */     
/* 1554 */     return 1033;
/*      */   }
/*      */   
/*      */   public String getFamilyName(Locale paramLocale)
/*      */   {
/* 1559 */     if (paramLocale == null)
/* 1560 */       return this.familyName;
/* 1561 */     if ((paramLocale.equals(this.nameLocale)) && (this.localeFamilyName != null)) {
/* 1562 */       return this.localeFamilyName;
/*      */     }
/* 1564 */     short s = getLCIDFromLocale(paramLocale);
/* 1565 */     String str = lookupName(s, 1);
/* 1566 */     if (str == null) {
/* 1567 */       return this.familyName;
/*      */     }
/* 1569 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */   public CharToGlyphMapper getMapper()
/*      */   {
/* 1575 */     if (this.mapper == null) {
/* 1576 */       this.mapper = new TrueTypeGlyphMapper(this);
/*      */     }
/* 1578 */     return this.mapper;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initAllNames(int paramInt, HashSet paramHashSet)
/*      */   {
/* 1587 */     byte[] arrayOfByte = new byte['Ā'];
/* 1588 */     ByteBuffer localByteBuffer = getTableBuffer(1851878757);
/*      */     
/* 1590 */     if (localByteBuffer != null) {
/* 1591 */       ShortBuffer localShortBuffer = localByteBuffer.asShortBuffer();
/* 1592 */       localShortBuffer.get();
/* 1593 */       int i = localShortBuffer.get();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1600 */       int j = localShortBuffer.get() & 0xFFFF;
/* 1601 */       for (int k = 0; k < i; k++) {
/* 1602 */         int m = localShortBuffer.get();
/* 1603 */         if (m != 3) {
/* 1604 */           localShortBuffer.position(localShortBuffer.position() + 5);
/*      */         }
/*      */         else {
/* 1607 */           short s = localShortBuffer.get();
/* 1608 */           int n = localShortBuffer.get();
/* 1609 */           int i1 = localShortBuffer.get();
/* 1610 */           int i2 = localShortBuffer.get() & 0xFFFF;
/* 1611 */           int i3 = (localShortBuffer.get() & 0xFFFF) + j;
/*      */           
/* 1613 */           if (i1 == paramInt) {
/* 1614 */             localByteBuffer.position(i3);
/* 1615 */             localByteBuffer.get(arrayOfByte, 0, i2);
/* 1616 */             paramHashSet.add(makeString(arrayOfByte, i2, s));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/* 1623 */   String[] getAllFamilyNames() { HashSet localHashSet = new HashSet();
/*      */     try {
/* 1625 */       initAllNames(1, localHashSet);
/*      */     }
/*      */     catch (Exception localException) {}
/*      */     
/* 1629 */     return (String[])localHashSet.toArray(new String[0]);
/*      */   }
/*      */   
/*      */   String[] getAllFullNames() {
/* 1633 */     HashSet localHashSet = new HashSet();
/*      */     try {
/* 1635 */       initAllNames(4, localHashSet);
/*      */     }
/*      */     catch (Exception localException) {}
/*      */     
/* 1639 */     return (String[])localHashSet.toArray(new String[0]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2)
/*      */   {
/*      */     try
/*      */     {
/* 1648 */       return getScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
/*      */     }
/*      */     catch (FontScalerException localFontScalerException) {}
/* 1651 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private char[] getGaspTable()
/*      */   {
/* 1659 */     if (this.gaspTable != null) {
/* 1660 */       return this.gaspTable;
/*      */     }
/*      */     
/* 1663 */     ByteBuffer localByteBuffer = getTableBuffer(1734439792);
/* 1664 */     if (localByteBuffer == null) {
/* 1665 */       return this.gaspTable = new char[0];
/*      */     }
/*      */     
/* 1668 */     CharBuffer localCharBuffer = localByteBuffer.asCharBuffer();
/* 1669 */     int i = localCharBuffer.get();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1674 */     if (i > 1) {
/* 1675 */       return this.gaspTable = new char[0];
/*      */     }
/*      */     
/* 1678 */     int j = localCharBuffer.get();
/* 1679 */     if (4 + j * 4 > getTableSize(1734439792)) {
/* 1680 */       return this.gaspTable = new char[0];
/*      */     }
/* 1682 */     this.gaspTable = new char[2 * j];
/* 1683 */     localCharBuffer.get(this.gaspTable);
/* 1684 */     return this.gaspTable;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private float stPos;
/*      */   
/*      */ 
/*      */ 
/*      */   private float ulSize;
/*      */   
/*      */ 
/*      */ 
/*      */   private float ulPos;
/*      */   
/*      */ 
/*      */ 
/*      */   private char[] gaspTable;
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean useAAForPtSize(int paramInt)
/*      */   {
/* 1709 */     char[] arrayOfChar = getGaspTable();
/* 1710 */     if (arrayOfChar.length > 0) {
/* 1711 */       for (int i = 0; i < arrayOfChar.length; i += 2) {
/* 1712 */         if (paramInt <= arrayOfChar[i]) {
/* 1713 */           return (arrayOfChar[(i + 1)] & 0x2) != 0;
/*      */         }
/*      */       }
/* 1716 */       return true;
/*      */     }
/*      */     
/* 1719 */     if (this.style == 1) {
/* 1720 */       return true;
/*      */     }
/* 1722 */     return (paramInt <= 8) || (paramInt >= 18);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasSupplementaryChars()
/*      */   {
/* 1728 */     return ((TrueTypeGlyphMapper)getMapper()).hasSupplementaryChars();
/*      */   }
/*      */   
/*      */   public String toString()
/*      */   {
/* 1733 */     return 
/* 1734 */       "** TrueType Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + getPublicFileName();
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\font\TrueTypeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */