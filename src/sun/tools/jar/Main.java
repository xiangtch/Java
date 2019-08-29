/*      */ package sun.tools.jar;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileDescriptor;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.OpenOption;
/*      */ import java.nio.file.Path;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.jar.Attributes;
/*      */ import java.util.jar.Attributes.Name;
/*      */ import java.util.jar.JarFile;
/*      */ import java.util.jar.JarOutputStream;
/*      */ import java.util.jar.Manifest;
/*      */ import java.util.jar.Pack200;
/*      */ import java.util.jar.Pack200.Packer;
/*      */ import java.util.jar.Pack200.Unpacker;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import java.util.zip.ZipOutputStream;
/*      */ import sun.misc.JarIndex;
/*      */ 
/*      */ public class Main
/*      */ {
/*      */   String program;
/*      */   PrintStream out;
/*      */   PrintStream err;
/*      */   String fname;
/*      */   String mname;
/*      */   String ename;
/*   52 */   String zname = "";
/*      */   String[] files;
/*   54 */   String rootjar = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   59 */   Map<String, File> entryMap = new java.util.HashMap();
/*      */   
/*      */ 
/*   62 */   Set<File> entries = new java.util.LinkedHashSet();
/*      */   
/*      */ 
/*   65 */   Set<String> paths = new HashSet();
/*      */   
/*      */   boolean cflag;
/*      */   
/*      */   boolean uflag;
/*      */   
/*      */   boolean xflag;
/*      */   
/*      */   boolean tflag;
/*      */   
/*      */   boolean vflag;
/*      */   
/*      */   boolean flag0;
/*      */   
/*      */   boolean Mflag;
/*      */   
/*      */   boolean iflag;
/*      */   
/*      */   boolean nflag;
/*      */   boolean pflag;
/*      */   static final String MANIFEST_DIR = "META-INF/";
/*      */   static final String VERSION = "1.0";
/*      */   private static ResourceBundle rsrc;
/*      */   private static final boolean useExtractionTime;
/*      */   private boolean ok;
/*      */   
/*      */   static
/*      */   {
/*   93 */     useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  100 */       rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
/*      */     } catch (MissingResourceException localMissingResourceException) {
/*  102 */       throw new Error("Fatal: Resource for jar is missing");
/*      */     }
/*      */   }
/*      */   
/*      */   private String getMsg(String paramString) {
/*      */     try {
/*  108 */       return rsrc.getString(paramString);
/*      */     } catch (MissingResourceException localMissingResourceException) {
/*  110 */       throw new Error("Error in message file");
/*      */     }
/*      */   }
/*      */   
/*      */   private String formatMsg(String paramString1, String paramString2) {
/*  115 */     String str = getMsg(paramString1);
/*  116 */     String[] arrayOfString = new String[1];
/*  117 */     arrayOfString[0] = paramString2;
/*  118 */     return MessageFormat.format(str, (Object[])arrayOfString);
/*      */   }
/*      */   
/*      */   private String formatMsg2(String paramString1, String paramString2, String paramString3) {
/*  122 */     String str = getMsg(paramString1);
/*  123 */     String[] arrayOfString = new String[2];
/*  124 */     arrayOfString[0] = paramString2;
/*  125 */     arrayOfString[1] = paramString3;
/*  126 */     return MessageFormat.format(str, (Object[])arrayOfString);
/*      */   }
/*      */   
/*      */   public Main(PrintStream paramPrintStream1, PrintStream paramPrintStream2, String paramString) {
/*  130 */     this.out = paramPrintStream1;
/*  131 */     this.err = paramPrintStream2;
/*  132 */     this.program = paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static File createTempFileInSameDirectoryAs(File paramFile)
/*      */     throws IOException
/*      */   {
/*  141 */     File localFile = paramFile.getParentFile();
/*  142 */     if (localFile == null)
/*  143 */       localFile = new File(".");
/*  144 */     return File.createTempFile("jartmp", null, localFile);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized boolean run(String[] paramArrayOfString)
/*      */   {
/*  153 */     this.ok = true;
/*  154 */     if (!parseArgs(paramArrayOfString)) {
/*  155 */       return false;
/*      */     }
/*      */     try {
/*  158 */       if (((this.cflag) || (this.uflag)) && 
/*  159 */         (this.fname != null))
/*      */       {
/*      */ 
/*      */ 
/*  163 */         this.zname = this.fname.replace(File.separatorChar, '/');
/*  164 */         if (this.zname.startsWith("./"))
/*  165 */           this.zname = this.zname.substring(2); }
/*      */       Object localObject1;
/*      */       Object localObject2;
/*      */       Object localObject3;
/*  169 */       Object localObject4; Object localObject5; if (this.cflag) {
/*  170 */         localObject1 = null;
/*  171 */         localObject2 = null;
/*      */         
/*  173 */         if (!this.Mflag) {
/*  174 */           if (this.mname != null) {
/*  175 */             localObject2 = new FileInputStream(this.mname);
/*  176 */             localObject1 = new Manifest(new BufferedInputStream((InputStream)localObject2));
/*      */           } else {
/*  178 */             localObject1 = new Manifest();
/*      */           }
/*  180 */           addVersion((Manifest)localObject1);
/*  181 */           addCreatedBy((Manifest)localObject1);
/*  182 */           if (isAmbiguousMainClass((Manifest)localObject1)) {
/*  183 */             if (localObject2 != null) {
/*  184 */               ((InputStream)localObject2).close();
/*      */             }
/*  186 */             return false;
/*      */           }
/*  188 */           if (this.ename != null) {
/*  189 */             addMainClass((Manifest)localObject1, this.ename);
/*      */           }
/*      */         }
/*  192 */         expand(null, this.files, false);
/*      */         
/*  194 */         if (this.fname != null) {
/*  195 */           localObject3 = new FileOutputStream(this.fname);
/*      */         } else {
/*  197 */           localObject3 = new FileOutputStream(FileDescriptor.out);
/*  198 */           if (this.vflag)
/*      */           {
/*      */ 
/*      */ 
/*  202 */             this.vflag = false;
/*      */           }
/*      */         }
/*  205 */         localObject4 = null;
/*  206 */         localObject5 = localObject3;
/*      */         
/*      */ 
/*  209 */         String str = this.fname == null ? "tmpjar" : this.fname.substring(this.fname.indexOf(File.separatorChar) + 1);
/*  210 */         if (this.nflag) {
/*  211 */           localObject4 = createTemporaryFile(str, ".jar");
/*  212 */           localObject3 = new FileOutputStream((File)localObject4);
/*      */         }
/*  214 */         create(new BufferedOutputStream((OutputStream)localObject3, 4096), (Manifest)localObject1);
/*  215 */         if (localObject2 != null) {
/*  216 */           ((InputStream)localObject2).close();
/*      */         }
/*  218 */         ((OutputStream)localObject3).close();
/*  219 */         if (this.nflag) {
/*  220 */           JarFile localJarFile = null;
/*  221 */           File localFile = null;
/*  222 */           JarOutputStream localJarOutputStream = null;
/*      */           try {
/*  224 */             Packer localPacker = Pack200.newPacker();
/*  225 */             java.util.SortedMap localSortedMap = localPacker.properties();
/*  226 */             localSortedMap.put("pack.effort", "1");
/*  227 */             localJarFile = new JarFile(((File)localObject4).getCanonicalPath());
/*  228 */             localFile = createTemporaryFile(str, ".pack");
/*  229 */             localObject3 = new FileOutputStream(localFile);
/*  230 */             localPacker.pack(localJarFile, (OutputStream)localObject3);
/*  231 */             localJarOutputStream = new JarOutputStream((OutputStream)localObject5);
/*  232 */             Unpacker localUnpacker = Pack200.newUnpacker();
/*  233 */             localUnpacker.unpack(localFile, localJarOutputStream);
/*      */           } catch (IOException localIOException2) {
/*  235 */             fatalError(localIOException2);
/*      */           } finally {
/*  237 */             if (localJarFile != null) {
/*  238 */               localJarFile.close();
/*      */             }
/*  240 */             if (localObject3 != null) {
/*  241 */               ((OutputStream)localObject3).close();
/*      */             }
/*  243 */             if (localJarOutputStream != null) {
/*  244 */               localJarOutputStream.close();
/*      */             }
/*  246 */             if ((localObject4 != null) && (((File)localObject4).exists())) {
/*  247 */               ((File)localObject4).delete();
/*      */             }
/*  249 */             if ((localFile != null) && (localFile.exists())) {
/*  250 */               localFile.delete();
/*      */             }
/*      */           }
/*      */         }
/*  254 */       } else if (this.uflag) {
/*  255 */         localObject1 = null;localObject2 = null;
/*      */         
/*      */ 
/*  258 */         if (this.fname != null) {
/*  259 */           localObject1 = new File(this.fname);
/*  260 */           localObject2 = createTempFileInSameDirectoryAs((File)localObject1);
/*  261 */           localObject3 = new FileInputStream((File)localObject1);
/*  262 */           localObject4 = new FileOutputStream((File)localObject2);
/*      */         } else {
/*  264 */           localObject3 = new FileInputStream(FileDescriptor.in);
/*  265 */           localObject4 = new FileOutputStream(FileDescriptor.out);
/*  266 */           this.vflag = false;
/*      */         }
/*  268 */         localObject5 = (!this.Mflag) && (this.mname != null) ? new FileInputStream(this.mname) : null;
/*      */         
/*  270 */         expand(null, this.files, true);
/*  271 */         boolean bool = update((InputStream)localObject3, new BufferedOutputStream((OutputStream)localObject4), (InputStream)localObject5, null);
/*      */         
/*  273 */         if (this.ok) {
/*  274 */           this.ok = bool;
/*      */         }
/*  276 */         ((FileInputStream)localObject3).close();
/*  277 */         ((FileOutputStream)localObject4).close();
/*  278 */         if (localObject5 != null) {
/*  279 */           ((InputStream)localObject5).close();
/*      */         }
/*  281 */         if ((this.ok) && (this.fname != null))
/*      */         {
/*  283 */           ((File)localObject1).delete();
/*  284 */           if (!((File)localObject2).renameTo((File)localObject1)) {
/*  285 */             ((File)localObject2).delete();
/*  286 */             throw new IOException(getMsg("error.write.file"));
/*      */           }
/*  288 */           ((File)localObject2).delete();
/*      */         }
/*  290 */       } else if (this.tflag) {
/*  291 */         replaceFSC(this.files);
/*  292 */         if (this.fname != null) {
/*  293 */           list(this.fname, this.files);
/*      */         } else {
/*  295 */           localObject1 = new FileInputStream(FileDescriptor.in);
/*      */           try {
/*  297 */             list(new BufferedInputStream((InputStream)localObject1), this.files);
/*      */           } finally {
/*  299 */             ((InputStream)localObject1).close();
/*      */           }
/*      */         }
/*  302 */       } else if (this.xflag) {
/*  303 */         replaceFSC(this.files);
/*  304 */         if ((this.fname != null) && (this.files != null)) {
/*  305 */           extract(this.fname, this.files);
/*      */         } else {
/*  307 */           localObject1 = this.fname == null ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);
/*      */           
/*      */           try
/*      */           {
/*  311 */             extract(new BufferedInputStream((InputStream)localObject1), this.files);
/*      */           } finally {
/*  313 */             ((InputStream)localObject1).close();
/*      */           }
/*      */         }
/*  316 */       } else if (this.iflag) {
/*  317 */         genIndex(this.rootjar, this.files);
/*      */       }
/*      */     } catch (IOException localIOException1) {
/*  320 */       fatalError(localIOException1);
/*  321 */       this.ok = false;
/*      */     } catch (Error localError) {
/*  323 */       localError.printStackTrace();
/*  324 */       this.ok = false;
/*      */     } catch (Throwable localThrowable) {
/*  326 */       localThrowable.printStackTrace();
/*  327 */       this.ok = false;
/*      */     }
/*  329 */     this.out.flush();
/*  330 */     this.err.flush();
/*  331 */     return this.ok;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   boolean parseArgs(String[] paramArrayOfString)
/*      */   {
/*      */     try
/*      */     {
/*  340 */       paramArrayOfString = CommandLine.parse(paramArrayOfString);
/*      */     } catch (FileNotFoundException localFileNotFoundException) {
/*  342 */       fatalError(formatMsg("error.cant.open", localFileNotFoundException.getMessage()));
/*  343 */       return false;
/*      */     } catch (IOException localIOException) {
/*  345 */       fatalError(localIOException);
/*  346 */       return false;
/*      */     }
/*      */     
/*  349 */     int i = 1;
/*      */     int k;
/*  351 */     try { String str1 = paramArrayOfString[0];
/*  352 */       if (str1.startsWith("-")) {
/*  353 */         str1 = str1.substring(1);
/*      */       }
/*  355 */       for (k = 0; k < str1.length(); k++) {
/*  356 */         switch (str1.charAt(k)) {
/*      */         case 'c': 
/*  358 */           if ((this.xflag) || (this.tflag) || (this.uflag) || (this.iflag)) {
/*  359 */             usageError();
/*  360 */             return false;
/*      */           }
/*  362 */           this.cflag = true;
/*  363 */           break;
/*      */         case 'u': 
/*  365 */           if ((this.cflag) || (this.xflag) || (this.tflag) || (this.iflag)) {
/*  366 */             usageError();
/*  367 */             return false;
/*      */           }
/*  369 */           this.uflag = true;
/*  370 */           break;
/*      */         case 'x': 
/*  372 */           if ((this.cflag) || (this.uflag) || (this.tflag) || (this.iflag)) {
/*  373 */             usageError();
/*  374 */             return false;
/*      */           }
/*  376 */           this.xflag = true;
/*  377 */           break;
/*      */         case 't': 
/*  379 */           if ((this.cflag) || (this.uflag) || (this.xflag) || (this.iflag)) {
/*  380 */             usageError();
/*  381 */             return false;
/*      */           }
/*  383 */           this.tflag = true;
/*  384 */           break;
/*      */         case 'M': 
/*  386 */           this.Mflag = true;
/*  387 */           break;
/*      */         case 'v': 
/*  389 */           this.vflag = true;
/*  390 */           break;
/*      */         case 'f': 
/*  392 */           this.fname = paramArrayOfString[(i++)];
/*  393 */           break;
/*      */         case 'm': 
/*  395 */           this.mname = paramArrayOfString[(i++)];
/*  396 */           break;
/*      */         case '0': 
/*  398 */           this.flag0 = true;
/*  399 */           break;
/*      */         case 'i': 
/*  401 */           if ((this.cflag) || (this.uflag) || (this.xflag) || (this.tflag)) {
/*  402 */             usageError();
/*  403 */             return false;
/*      */           }
/*      */           
/*  406 */           this.rootjar = paramArrayOfString[(i++)];
/*  407 */           this.iflag = true;
/*  408 */           break;
/*      */         case 'n': 
/*  410 */           this.nflag = true;
/*  411 */           break;
/*      */         case 'e': 
/*  413 */           this.ename = paramArrayOfString[(i++)];
/*  414 */           break;
/*      */         case 'P': 
/*  416 */           this.pflag = true;
/*  417 */           break;
/*      */         default: 
/*  419 */           error(formatMsg("error.illegal.option", 
/*  420 */             String.valueOf(str1.charAt(k))));
/*  421 */           usageError();
/*  422 */           return false;
/*      */         }
/*      */       }
/*      */     } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException1) {
/*  426 */       usageError();
/*  427 */       return false;
/*      */     }
/*  429 */     if ((!this.cflag) && (!this.tflag) && (!this.xflag) && (!this.uflag) && (!this.iflag)) {
/*  430 */       error(getMsg("error.bad.option"));
/*  431 */       usageError();
/*  432 */       return false;
/*      */     }
/*      */     
/*  435 */     int j = paramArrayOfString.length - i;
/*  436 */     if (j > 0) {
/*  437 */       k = 0;
/*  438 */       String[] arrayOfString = new String[j];
/*      */       try {
/*  440 */         for (int m = i; m < paramArrayOfString.length; m++) {
/*  441 */           if (paramArrayOfString[m].equals("-C"))
/*      */           {
/*  443 */             String str2 = paramArrayOfString[(++m)];
/*  444 */             str2 = str2 + File.separator;
/*      */             
/*  446 */             str2 = str2.replace(File.separatorChar, '/');
/*  447 */             while (str2.indexOf("//") > -1) {
/*  448 */               str2 = str2.replace("//", "/");
/*      */             }
/*  450 */             this.paths.add(str2.replace(File.separatorChar, '/'));
/*  451 */             arrayOfString[(k++)] = (str2 + paramArrayOfString[(++m)]);
/*      */           } else {
/*  453 */             arrayOfString[(k++)] = paramArrayOfString[m];
/*      */           }
/*      */         }
/*      */       } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException2) {
/*  457 */         usageError();
/*  458 */         return false;
/*      */       }
/*  460 */       this.files = new String[k];
/*  461 */       System.arraycopy(arrayOfString, 0, this.files, 0, k);
/*  462 */     } else { if ((this.cflag) && (this.mname == null)) {
/*  463 */         error(getMsg("error.bad.cflag"));
/*  464 */         usageError();
/*  465 */         return false; }
/*  466 */       if (this.uflag) {
/*  467 */         if ((this.mname != null) || (this.ename != null))
/*      */         {
/*  469 */           return true;
/*      */         }
/*  471 */         error(getMsg("error.bad.uflag"));
/*  472 */         usageError();
/*  473 */         return false;
/*      */       }
/*      */     }
/*  476 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void expand(File paramFile, String[] paramArrayOfString, boolean paramBoolean)
/*      */   {
/*  484 */     if (paramArrayOfString == null) {
/*  485 */       return;
/*      */     }
/*  487 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/*      */       File localFile;
/*  489 */       if (paramFile == null) {
/*  490 */         localFile = new File(paramArrayOfString[i]);
/*      */       } else {
/*  492 */         localFile = new File(paramFile, paramArrayOfString[i]);
/*      */       }
/*  494 */       if (localFile.isFile()) {
/*  495 */         if ((this.entries.add(localFile)) && 
/*  496 */           (paramBoolean)) {
/*  497 */           this.entryMap.put(entryName(localFile.getPath()), localFile);
/*      */         }
/*  499 */       } else if (localFile.isDirectory()) {
/*  500 */         if (this.entries.add(localFile)) {
/*  501 */           if (paramBoolean) {
/*  502 */             String str = localFile.getPath();
/*  503 */             str = str + File.separator;
/*      */             
/*  505 */             this.entryMap.put(entryName(str), localFile);
/*      */           }
/*  507 */           expand(localFile, localFile.list(), paramBoolean);
/*      */         }
/*      */       } else {
/*  510 */         error(formatMsg("error.nosuch.fileordir", String.valueOf(localFile)));
/*  511 */         this.ok = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void create(OutputStream paramOutputStream, Manifest paramManifest)
/*      */     throws IOException
/*      */   {
/*  522 */     JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
/*  523 */     if (this.flag0) {
/*  524 */       localJarOutputStream.setMethod(0);
/*      */     }
/*  526 */     if (paramManifest != null) {
/*  527 */       if (this.vflag) {
/*  528 */         output(getMsg("out.added.manifest"));
/*      */       }
/*  530 */       localObject = new ZipEntry("META-INF/");
/*  531 */       ((ZipEntry)localObject).setTime(System.currentTimeMillis());
/*  532 */       ((ZipEntry)localObject).setSize(0L);
/*  533 */       ((ZipEntry)localObject).setCrc(0L);
/*  534 */       localJarOutputStream.putNextEntry((ZipEntry)localObject);
/*  535 */       localObject = new ZipEntry("META-INF/MANIFEST.MF");
/*  536 */       ((ZipEntry)localObject).setTime(System.currentTimeMillis());
/*  537 */       if (this.flag0) {
/*  538 */         crc32Manifest((ZipEntry)localObject, paramManifest);
/*      */       }
/*  540 */       localJarOutputStream.putNextEntry((ZipEntry)localObject);
/*  541 */       paramManifest.write(localJarOutputStream);
/*  542 */       localJarOutputStream.closeEntry();
/*      */     }
/*  544 */     for (Object localObject = this.entries.iterator(); ((Iterator)localObject).hasNext();) { File localFile = (File)((Iterator)localObject).next();
/*  545 */       addFile(localJarOutputStream, localFile);
/*      */     }
/*  547 */     localJarOutputStream.close();
/*      */   }
/*      */   
/*      */   private char toUpperCaseASCII(char paramChar) {
/*  551 */     return (paramChar < 'a') || (paramChar > 'z') ? paramChar : (char)(paramChar + 'A' - 97);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean equalsIgnoreCase(String paramString1, String paramString2)
/*      */   {
/*  561 */     assert (paramString2.toUpperCase(java.util.Locale.ENGLISH).equals(paramString2));
/*      */     int i;
/*  563 */     if ((i = paramString1.length()) != paramString2.length())
/*  564 */       return false;
/*  565 */     for (int j = 0; j < i; j++) {
/*  566 */       char c1 = paramString1.charAt(j);
/*  567 */       char c2 = paramString2.charAt(j);
/*  568 */       if ((c1 != c2) && (toUpperCaseASCII(c1) != c2))
/*  569 */         return false;
/*      */     }
/*  571 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean update(InputStream paramInputStream1, OutputStream paramOutputStream, InputStream paramInputStream2, JarIndex paramJarIndex)
/*      */     throws IOException
/*      */   {
/*  581 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream1);
/*  582 */     JarOutputStream localJarOutputStream = new JarOutputStream(paramOutputStream);
/*  583 */     ZipEntry localZipEntry = null;
/*  584 */     int i = 0;
/*  585 */     boolean bool1 = true;
/*      */     
/*  587 */     if (paramJarIndex != null) {
/*  588 */       addIndex(paramJarIndex, localJarOutputStream);
/*      */     }
/*      */     
/*      */ 
/*  592 */     while ((localZipEntry = localZipInputStream.getNextEntry()) != null) {
/*  593 */       localObject1 = localZipEntry.getName();
/*      */       
/*  595 */       boolean bool2 = equalsIgnoreCase((String)localObject1, "META-INF/MANIFEST.MF");
/*      */       
/*  597 */       if (((paramJarIndex == null) || (!equalsIgnoreCase((String)localObject1, "META-INF/INDEX.LIST"))) && ((!this.Mflag) || (!bool2)))
/*      */       {
/*      */         Object localObject2;
/*  600 */         if ((bool2) && ((paramInputStream2 != null) || (this.ename != null)))
/*      */         {
/*  602 */           i = 1;
/*  603 */           if (paramInputStream2 != null)
/*      */           {
/*      */ 
/*      */ 
/*  607 */             localObject2 = new FileInputStream(this.mname);
/*  608 */             boolean bool3 = isAmbiguousMainClass(new Manifest((InputStream)localObject2));
/*  609 */             ((FileInputStream)localObject2).close();
/*  610 */             if (bool3) {
/*  611 */               return false;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  616 */           localObject2 = new Manifest(localZipInputStream);
/*  617 */           if (paramInputStream2 != null) {
/*  618 */             ((Manifest)localObject2).read(paramInputStream2);
/*      */           }
/*  620 */           if (!updateManifest((Manifest)localObject2, localJarOutputStream)) {
/*  621 */             return false;
/*      */           }
/*      */         }
/*  624 */         else if (!this.entryMap.containsKey(localObject1))
/*      */         {
/*  626 */           localObject2 = new ZipEntry((String)localObject1);
/*  627 */           ((ZipEntry)localObject2).setMethod(localZipEntry.getMethod());
/*  628 */           ((ZipEntry)localObject2).setTime(localZipEntry.getTime());
/*  629 */           ((ZipEntry)localObject2).setComment(localZipEntry.getComment());
/*  630 */           ((ZipEntry)localObject2).setExtra(localZipEntry.getExtra());
/*  631 */           if (localZipEntry.getMethod() == 0) {
/*  632 */             ((ZipEntry)localObject2).setSize(localZipEntry.getSize());
/*  633 */             ((ZipEntry)localObject2).setCrc(localZipEntry.getCrc());
/*      */           }
/*  635 */           localJarOutputStream.putNextEntry((ZipEntry)localObject2);
/*  636 */           copy(localZipInputStream, localJarOutputStream);
/*      */         } else {
/*  638 */           localObject2 = (File)this.entryMap.get(localObject1);
/*  639 */           addFile(localJarOutputStream, (File)localObject2);
/*  640 */           this.entryMap.remove(localObject1);
/*  641 */           this.entries.remove(localObject2);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  647 */     for (Object localObject1 = this.entries.iterator(); ((Iterator)localObject1).hasNext();) { File localFile = (File)((Iterator)localObject1).next();
/*  648 */       addFile(localJarOutputStream, localFile);
/*      */     }
/*  650 */     if (i == 0) {
/*  651 */       if (paramInputStream2 != null) {
/*  652 */         localObject1 = new Manifest(paramInputStream2);
/*  653 */         bool1 = !isAmbiguousMainClass((Manifest)localObject1);
/*  654 */         if ((bool1) && 
/*  655 */           (!updateManifest((Manifest)localObject1, localJarOutputStream))) {
/*  656 */           bool1 = false;
/*      */         }
/*      */       }
/*  659 */       else if ((this.ename != null) && 
/*  660 */         (!updateManifest(new Manifest(), localJarOutputStream))) {
/*  661 */         bool1 = false;
/*      */       }
/*      */     }
/*      */     
/*  665 */     localZipInputStream.close();
/*  666 */     localJarOutputStream.close();
/*  667 */     return bool1;
/*      */   }
/*      */   
/*      */   private void addIndex(JarIndex paramJarIndex, ZipOutputStream paramZipOutputStream)
/*      */     throws IOException
/*      */   {
/*  673 */     ZipEntry localZipEntry = new ZipEntry("META-INF/INDEX.LIST");
/*  674 */     localZipEntry.setTime(System.currentTimeMillis());
/*  675 */     if (this.flag0) {
/*  676 */       CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  677 */       paramJarIndex.write(localCRC32OutputStream);
/*  678 */       localCRC32OutputStream.updateEntry(localZipEntry);
/*      */     }
/*  680 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  681 */     paramJarIndex.write(paramZipOutputStream);
/*  682 */     paramZipOutputStream.closeEntry();
/*      */   }
/*      */   
/*      */   private boolean updateManifest(Manifest paramManifest, ZipOutputStream paramZipOutputStream)
/*      */     throws IOException
/*      */   {
/*  688 */     addVersion(paramManifest);
/*  689 */     addCreatedBy(paramManifest);
/*  690 */     if (this.ename != null) {
/*  691 */       addMainClass(paramManifest, this.ename);
/*      */     }
/*  693 */     ZipEntry localZipEntry = new ZipEntry("META-INF/MANIFEST.MF");
/*  694 */     localZipEntry.setTime(System.currentTimeMillis());
/*  695 */     if (this.flag0) {
/*  696 */       crc32Manifest(localZipEntry, paramManifest);
/*      */     }
/*  698 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  699 */     paramManifest.write(paramZipOutputStream);
/*  700 */     if (this.vflag) {
/*  701 */       output(getMsg("out.update.manifest"));
/*      */     }
/*  703 */     return true;
/*      */   }
/*      */   
/*      */   private static final boolean isWinDriveLetter(char paramChar) {
/*  707 */     return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z'));
/*      */   }
/*      */   
/*      */   private String safeName(String paramString) {
/*  711 */     if (!this.pflag) {
/*  712 */       int i = paramString.length();
/*  713 */       int j = paramString.lastIndexOf("../");
/*  714 */       if (j == -1) {
/*  715 */         j = 0;
/*      */       } else {
/*  717 */         j += 3;
/*      */       }
/*  719 */       if (File.separatorChar == '\\')
/*      */       {
/*      */ 
/*  722 */         while (j < i) {
/*  723 */           int k = j;
/*  724 */           if ((j + 1 < i) && 
/*  725 */             (paramString.charAt(j + 1) == ':') && 
/*  726 */             (isWinDriveLetter(paramString.charAt(j)))) {
/*  727 */             j += 2;
/*      */           }
/*  729 */           while ((j < i) && (paramString.charAt(j) == '/')) {
/*  730 */             j++;
/*      */           }
/*  732 */           if (j == k) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*  737 */       while ((j < i) && (paramString.charAt(j) == '/')) {
/*  738 */         j++;
/*      */       }
/*      */       
/*  741 */       if (j != 0) {
/*  742 */         paramString = paramString.substring(j);
/*      */       }
/*      */     }
/*  745 */     return paramString;
/*      */   }
/*      */   
/*      */   private String entryName(String paramString) {
/*  749 */     paramString = paramString.replace(File.separatorChar, '/');
/*  750 */     Object localObject = "";
/*  751 */     for (String str : this.paths) {
/*  752 */       if ((paramString.startsWith(str)) && 
/*  753 */         (str.length() > ((String)localObject).length())) {
/*  754 */         localObject = str;
/*      */       }
/*      */     }
/*  757 */     paramString = paramString.substring(((String)localObject).length());
/*  758 */     paramString = safeName(paramString);
/*      */     
/*      */ 
/*  761 */     if (paramString.startsWith("./")) {
/*  762 */       paramString = paramString.substring(2);
/*      */     }
/*  764 */     return paramString;
/*      */   }
/*      */   
/*      */   private void addVersion(Manifest paramManifest) {
/*  768 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*  769 */     if (localAttributes.getValue(Name.MANIFEST_VERSION) == null) {
/*  770 */       localAttributes.put(Name.MANIFEST_VERSION, "1.0");
/*      */     }
/*      */   }
/*      */   
/*      */   private void addCreatedBy(Manifest paramManifest) {
/*  775 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*  776 */     if (localAttributes.getValue(new Name("Created-By")) == null) {
/*  777 */       String str1 = System.getProperty("java.vendor");
/*  778 */       String str2 = System.getProperty("java.version");
/*  779 */       localAttributes.put(new Name("Created-By"), str2 + " (" + str1 + ")");
/*      */     }
/*      */   }
/*      */   
/*      */   private void addMainClass(Manifest paramManifest, String paramString)
/*      */   {
/*  785 */     Attributes localAttributes = paramManifest.getMainAttributes();
/*      */     
/*      */ 
/*  788 */     localAttributes.put(Name.MAIN_CLASS, paramString);
/*      */   }
/*      */   
/*      */   private boolean isAmbiguousMainClass(Manifest paramManifest) {
/*  792 */     if (this.ename != null) {
/*  793 */       Attributes localAttributes = paramManifest.getMainAttributes();
/*  794 */       if (localAttributes.get(Name.MAIN_CLASS) != null) {
/*  795 */         error(getMsg("error.bad.eflag"));
/*  796 */         usageError();
/*  797 */         return true;
/*      */       }
/*      */     }
/*  800 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   void addFile(ZipOutputStream paramZipOutputStream, File paramFile)
/*      */     throws IOException
/*      */   {
/*  807 */     String str = paramFile.getPath();
/*  808 */     boolean bool = paramFile.isDirectory();
/*  809 */     if (bool) {
/*  810 */       str = str + File.separator;
/*      */     }
/*      */     
/*  813 */     str = entryName(str);
/*      */     
/*  815 */     if ((str.equals("")) || (str.equals(".")) || (str.equals(this.zname)))
/*  816 */       return;
/*  817 */     if (((str.equals("META-INF/")) || (str.equals("META-INF/MANIFEST.MF"))) && (!this.Mflag))
/*      */     {
/*  819 */       if (this.vflag) {
/*  820 */         output(formatMsg("out.ignore.entry", str));
/*      */       }
/*  822 */       return;
/*      */     }
/*      */     
/*  825 */     long l1 = bool ? 0L : paramFile.length();
/*      */     
/*  827 */     if (this.vflag) {
/*  828 */       this.out.print(formatMsg("out.adding", str));
/*      */     }
/*  830 */     ZipEntry localZipEntry = new ZipEntry(str);
/*  831 */     localZipEntry.setTime(paramFile.lastModified());
/*  832 */     if (l1 == 0L) {
/*  833 */       localZipEntry.setMethod(0);
/*  834 */       localZipEntry.setSize(0L);
/*  835 */       localZipEntry.setCrc(0L);
/*  836 */     } else if (this.flag0) {
/*  837 */       crc32File(localZipEntry, paramFile);
/*      */     }
/*  839 */     paramZipOutputStream.putNextEntry(localZipEntry);
/*  840 */     if (!bool) {
/*  841 */       copy(paramFile, paramZipOutputStream);
/*      */     }
/*  843 */     paramZipOutputStream.closeEntry();
/*      */     
/*  845 */     if (this.vflag) {
/*  846 */       l1 = localZipEntry.getSize();
/*  847 */       long l2 = localZipEntry.getCompressedSize();
/*  848 */       this.out.print(formatMsg2("out.size", String.valueOf(l1), 
/*  849 */         String.valueOf(l2)));
/*  850 */       if (localZipEntry.getMethod() == 8) {
/*  851 */         long l3 = 0L;
/*  852 */         if (l1 != 0L) {
/*  853 */           l3 = (l1 - l2) * 100L / l1;
/*      */         }
/*  855 */         output(formatMsg("out.deflated", String.valueOf(l3)));
/*      */       } else {
/*  857 */         output(getMsg("out.stored"));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  867 */   private byte[] copyBuf = new byte[' '];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void copy(InputStream paramInputStream, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*      */     int i;
/*      */     
/*      */ 
/*      */ 
/*  879 */     while ((i = paramInputStream.read(this.copyBuf)) != -1) {
/*  880 */       paramOutputStream.write(this.copyBuf, 0, i);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void copy(File paramFile, OutputStream paramOutputStream)
/*      */     throws IOException
/*      */   {
/*  892 */     FileInputStream localFileInputStream = new FileInputStream(paramFile);
/*      */     try {
/*  894 */       copy(localFileInputStream, paramOutputStream);
/*      */     } finally {
/*  896 */       localFileInputStream.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void copy(InputStream paramInputStream, File paramFile)
/*      */     throws IOException
/*      */   {
/*  909 */     FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
/*      */     try {
/*  911 */       copy(paramInputStream, localFileOutputStream);
/*      */     } finally {
/*  913 */       localFileOutputStream.close();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void crc32Manifest(ZipEntry paramZipEntry, Manifest paramManifest)
/*      */     throws IOException
/*      */   {
/*  922 */     CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  923 */     paramManifest.write(localCRC32OutputStream);
/*  924 */     localCRC32OutputStream.updateEntry(paramZipEntry);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void crc32File(ZipEntry paramZipEntry, File paramFile)
/*      */     throws IOException
/*      */   {
/*  932 */     CRC32OutputStream localCRC32OutputStream = new CRC32OutputStream();
/*  933 */     copy(paramFile, localCRC32OutputStream);
/*  934 */     if (localCRC32OutputStream.n != paramFile.length()) {
/*  935 */       throw new JarException(formatMsg("error.incorrect.length", paramFile
/*  936 */         .getPath()));
/*      */     }
/*  938 */     localCRC32OutputStream.updateEntry(paramZipEntry);
/*      */   }
/*      */   
/*      */   void replaceFSC(String[] paramArrayOfString) {
/*  942 */     if (paramArrayOfString != null) {
/*  943 */       for (int i = 0; i < paramArrayOfString.length; i++) {
/*  944 */         paramArrayOfString[i] = paramArrayOfString[i].replace(File.separatorChar, '/');
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  951 */   Set<ZipEntry> newDirSet() { new HashSet() {
/*      */       public boolean add(ZipEntry paramAnonymousZipEntry) {
/*  953 */         return (paramAnonymousZipEntry == null) || (Main.useExtractionTime) ? false : super.add(paramAnonymousZipEntry);
/*      */       }
/*      */     }; }
/*      */   
/*      */   void updateLastModifiedTime(Set<ZipEntry> paramSet) throws IOException {
/*  958 */     for (ZipEntry localZipEntry : paramSet) {
/*  959 */       long l = localZipEntry.getTime();
/*  960 */       if (l != -1L) {
/*  961 */         String str = safeName(localZipEntry.getName().replace(File.separatorChar, '/'));
/*  962 */         if (str.length() != 0) {
/*  963 */           File localFile = new File(str.replace('/', File.separatorChar));
/*  964 */           localFile.setLastModified(l);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   void extract(InputStream paramInputStream, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/*  974 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
/*      */     
/*      */ 
/*      */ 
/*  978 */     Set localSet = newDirSet();
/*  979 */     ZipEntry localZipEntry; while ((localZipEntry = localZipInputStream.getNextEntry()) != null) {
/*  980 */       if (paramArrayOfString == null) {
/*  981 */         localSet.add(extractFile(localZipInputStream, localZipEntry));
/*      */       } else {
/*  983 */         String str1 = localZipEntry.getName();
/*  984 */         for (String str2 : paramArrayOfString) {
/*  985 */           if (str1.startsWith(str2)) {
/*  986 */             localSet.add(extractFile(localZipInputStream, localZipEntry));
/*  987 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  997 */     updateLastModifiedTime(localSet);
/*      */   }
/*      */   
/*      */ 
/*      */   void extract(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1004 */     ZipFile localZipFile = new ZipFile(paramString);
/* 1005 */     Set localSet = newDirSet();
/* 1006 */     Enumeration localEnumeration = localZipFile.entries();
/* 1007 */     while (localEnumeration.hasMoreElements()) {
/* 1008 */       ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
/* 1009 */       if (paramArrayOfString == null) {
/* 1010 */         localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
/*      */       } else {
/* 1012 */         String str1 = localZipEntry.getName();
/* 1013 */         for (String str2 : paramArrayOfString) {
/* 1014 */           if (str1.startsWith(str2)) {
/* 1015 */             localSet.add(extractFile(localZipFile.getInputStream(localZipEntry), localZipEntry));
/* 1016 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1021 */     localZipFile.close();
/* 1022 */     updateLastModifiedTime(localSet);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   ZipEntry extractFile(InputStream paramInputStream, ZipEntry paramZipEntry)
/*      */     throws IOException
/*      */   {
/* 1031 */     ZipEntry localZipEntry = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1037 */     String str = safeName(paramZipEntry.getName().replace(File.separatorChar, '/'));
/* 1038 */     if (str.length() == 0) {
/* 1039 */       return localZipEntry;
/*      */     }
/* 1041 */     File localFile1 = new File(str.replace('/', File.separatorChar));
/* 1042 */     if (paramZipEntry.isDirectory()) {
/* 1043 */       if (localFile1.exists()) {
/* 1044 */         if (!localFile1.isDirectory()) {
/* 1045 */           throw new IOException(formatMsg("error.create.dir", localFile1
/* 1046 */             .getPath()));
/*      */         }
/*      */       } else {
/* 1049 */         if (!localFile1.mkdirs()) {
/* 1050 */           throw new IOException(formatMsg("error.create.dir", localFile1
/* 1051 */             .getPath()));
/*      */         }
/* 1053 */         localZipEntry = paramZipEntry;
/*      */       }
/*      */       
/*      */ 
/* 1057 */       if (this.vflag) {
/* 1058 */         output(formatMsg("out.create", str));
/*      */       }
/*      */     } else {
/* 1061 */       if (localFile1.getParent() != null) {
/* 1062 */         File localFile2 = new File(localFile1.getParent());
/* 1063 */         if (((!localFile2.exists()) && (!localFile2.mkdirs())) || (!localFile2.isDirectory())) {
/* 1064 */           throw new IOException(formatMsg("error.create.dir", localFile2
/* 1065 */             .getPath()));
/*      */         }
/*      */       }
/*      */       try {
/* 1069 */         copy(paramInputStream, localFile1);
/*      */       } finally {
/* 1071 */         if ((paramInputStream instanceof ZipInputStream)) {
/* 1072 */           ((ZipInputStream)paramInputStream).closeEntry();
/*      */         } else
/* 1074 */           paramInputStream.close();
/*      */       }
/* 1076 */       if (this.vflag) {
/* 1077 */         if (paramZipEntry.getMethod() == 8) {
/* 1078 */           output(formatMsg("out.inflated", str));
/*      */         } else {
/* 1080 */           output(formatMsg("out.extracted", str));
/*      */         }
/*      */       }
/*      */     }
/* 1084 */     if (!useExtractionTime) {
/* 1085 */       long l = paramZipEntry.getTime();
/* 1086 */       if (l != -1L) {
/* 1087 */         localFile1.setLastModified(l);
/*      */       }
/*      */     }
/* 1090 */     return localZipEntry;
/*      */   }
/*      */   
/*      */ 
/*      */   void list(InputStream paramInputStream, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1097 */     ZipInputStream localZipInputStream = new ZipInputStream(paramInputStream);
/*      */     ZipEntry localZipEntry;
/* 1099 */     while ((localZipEntry = localZipInputStream.getNextEntry()) != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1106 */       localZipInputStream.closeEntry();
/* 1107 */       printEntry(localZipEntry, paramArrayOfString);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   void list(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1115 */     ZipFile localZipFile = new ZipFile(paramString);
/* 1116 */     Enumeration localEnumeration = localZipFile.entries();
/* 1117 */     while (localEnumeration.hasMoreElements()) {
/* 1118 */       printEntry((ZipEntry)localEnumeration.nextElement(), paramArrayOfString);
/*      */     }
/* 1120 */     localZipFile.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void dumpIndex(String paramString, JarIndex paramJarIndex)
/*      */     throws IOException
/*      */   {
/* 1128 */     File localFile = new File(paramString);
/* 1129 */     Path localPath1 = localFile.toPath();
/* 1130 */     Path localPath2 = createTempFileInSameDirectoryAs(localFile).toPath();
/*      */     try {
/* 1132 */       if (update(Files.newInputStream(localPath1, new OpenOption[0]), 
/* 1133 */         Files.newOutputStream(localPath2, new OpenOption[0]), null, paramJarIndex))
/*      */       {
/*      */         try {}catch (IOException localIOException)
/*      */         {
/*      */ 
/* 1138 */           throw new IOException(getMsg("error.write.file"), localIOException);
/*      */         }
/*      */       }
/*      */     } finally {
/* 1142 */       Files.deleteIfExists(localPath2);
/*      */     }
/*      */   }
/*      */   
/* 1146 */   private HashSet<String> jarPaths = new HashSet();
/*      */   
/*      */ 
/*      */ 
/*      */   List<String> getJarPath(String paramString)
/*      */     throws IOException
/*      */   {
/* 1153 */     ArrayList localArrayList = new ArrayList();
/* 1154 */     localArrayList.add(paramString);
/* 1155 */     this.jarPaths.add(paramString);
/*      */     
/*      */ 
/* 1158 */     String str1 = paramString.substring(0, Math.max(0, paramString.lastIndexOf('/') + 1));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1163 */     JarFile localJarFile = new JarFile(paramString.replace('/', File.separatorChar));
/*      */     
/* 1165 */     if (localJarFile != null) {
/* 1166 */       Manifest localManifest = localJarFile.getManifest();
/* 1167 */       if (localManifest != null) {
/* 1168 */         Attributes localAttributes = localManifest.getMainAttributes();
/* 1169 */         if (localAttributes != null) {
/* 1170 */           String str2 = localAttributes.getValue(Name.CLASS_PATH);
/* 1171 */           if (str2 != null) {
/* 1172 */             StringTokenizer localStringTokenizer = new StringTokenizer(str2);
/* 1173 */             while (localStringTokenizer.hasMoreTokens()) {
/* 1174 */               String str3 = localStringTokenizer.nextToken();
/* 1175 */               if (!str3.endsWith("/")) {
/* 1176 */                 str3 = str1.concat(str3);
/*      */                 
/* 1178 */                 if (!this.jarPaths.contains(str3)) {
/* 1179 */                   localArrayList.addAll(getJarPath(str3));
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1187 */     localJarFile.close();
/* 1188 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */   void genIndex(String paramString, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1195 */     List localList = getJarPath(paramString);
/* 1196 */     int i = localList.size();
/*      */     
/*      */ 
/* 1199 */     if ((i == 1) && (paramArrayOfString != null))
/*      */     {
/*      */ 
/* 1202 */       for (int j = 0; j < paramArrayOfString.length; j++) {
/* 1203 */         localList.addAll(getJarPath(paramArrayOfString[j]));
/*      */       }
/* 1205 */       i = localList.size();
/*      */     }
/* 1207 */     String[] arrayOfString = (String[])localList.toArray(new String[i]);
/* 1208 */     JarIndex localJarIndex = new JarIndex(arrayOfString);
/* 1209 */     dumpIndex(paramString, localJarIndex);
/*      */   }
/*      */   
/*      */ 
/*      */   void printEntry(ZipEntry paramZipEntry, String[] paramArrayOfString)
/*      */     throws IOException
/*      */   {
/* 1216 */     if (paramArrayOfString == null) {
/* 1217 */       printEntry(paramZipEntry);
/*      */     } else {
/* 1219 */       String str1 = paramZipEntry.getName();
/* 1220 */       for (String str2 : paramArrayOfString) {
/* 1221 */         if (str1.startsWith(str2)) {
/* 1222 */           printEntry(paramZipEntry);
/* 1223 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   void printEntry(ZipEntry paramZipEntry)
/*      */     throws IOException
/*      */   {
/* 1233 */     if (this.vflag) {
/* 1234 */       StringBuilder localStringBuilder = new StringBuilder();
/* 1235 */       String str = Long.toString(paramZipEntry.getSize());
/* 1236 */       for (int i = 6 - str.length(); i > 0; i--) {
/* 1237 */         localStringBuilder.append(' ');
/*      */       }
/* 1239 */       localStringBuilder.append(str).append(' ').append(new Date(paramZipEntry.getTime()).toString());
/* 1240 */       localStringBuilder.append(' ').append(paramZipEntry.getName());
/* 1241 */       output(localStringBuilder.toString());
/*      */     } else {
/* 1243 */       output(paramZipEntry.getName());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void usageError()
/*      */   {
/* 1251 */     error(getMsg("usage"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   void fatalError(Exception paramException)
/*      */   {
/* 1258 */     paramException.printStackTrace();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   void fatalError(String paramString)
/*      */   {
/* 1266 */     error(this.program + ": " + paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void output(String paramString)
/*      */   {
/* 1273 */     this.out.println(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void error(String paramString)
/*      */   {
/* 1280 */     this.err.println(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] paramArrayOfString)
/*      */   {
/* 1287 */     Main localMain = new Main(System.out, System.err, "jar");
/* 1288 */     System.exit(localMain.run(paramArrayOfString) ? 0 : 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class CRC32OutputStream
/*      */     extends OutputStream
/*      */   {
/* 1297 */     final CRC32 crc = new CRC32();
/* 1298 */     long n = 0L;
/*      */     
/*      */     public void write(int paramInt)
/*      */       throws IOException
/*      */     {
/* 1303 */       this.crc.update(paramInt);
/* 1304 */       this.n += 1L;
/*      */     }
/*      */     
/*      */     public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
/* 1308 */       this.crc.update(paramArrayOfByte, paramInt1, paramInt2);
/* 1309 */       this.n += paramInt2;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void updateEntry(ZipEntry paramZipEntry)
/*      */     {
/* 1317 */       paramZipEntry.setMethod(0);
/* 1318 */       paramZipEntry.setSize(this.n);
/* 1319 */       paramZipEntry.setCrc(this.crc.getValue());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private File createTemporaryFile(String paramString1, String paramString2)
/*      */   {
/* 1328 */     File localFile1 = null;
/*      */     try
/*      */     {
/* 1331 */       localFile1 = File.createTempFile(paramString1, paramString2);
/*      */     }
/*      */     catch (IOException|SecurityException localIOException1) {}
/*      */     
/* 1335 */     if (localFile1 == null)
/*      */     {
/* 1337 */       if (this.fname != null) {
/*      */         try {
/* 1339 */           File localFile2 = new File(this.fname).getAbsoluteFile().getParentFile();
/* 1340 */           localFile1 = File.createTempFile(this.fname, ".tmp" + paramString2, localFile2);
/*      */         }
/*      */         catch (IOException localIOException2) {
/* 1343 */           fatalError(localIOException2);
/*      */         }
/*      */         
/*      */       } else {
/* 1347 */         fatalError(new IOException(getMsg("error.create.tempfile")));
/*      */       }
/*      */     }
/* 1350 */     return localFile1;
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\tools\jar\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */