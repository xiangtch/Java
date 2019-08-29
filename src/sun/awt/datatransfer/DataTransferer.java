/*      */ package sun.awt.datatransfer;
/*      */ 
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Image;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.datatransfer.DataFlavor;
/*      */ import java.awt.datatransfer.FlavorMap;
/*      */ import java.awt.datatransfer.FlavorTable;
/*      */ import java.awt.datatransfer.Transferable;
/*      */ import java.awt.datatransfer.UnsupportedFlavorException;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.awt.image.WritableRaster;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FilePermission;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.SequenceInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.io.StringReader;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.IllegalCharsetNameException;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.ProtectionDomain;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.SortedSet;
/*      */ import java.util.Stack;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageReadParam;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.spi.ImageWriterSpi;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ import sun.awt.AppContext;
/*      */ import sun.awt.ComponentFactory;
/*      */ import sun.awt.SunToolkit;
/*      */ import sun.awt.image.ImageRepresentation;
/*      */ import sun.awt.image.ToolkitImage;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ import sun.util.logging.PlatformLogger.Level;
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
/*      */ public abstract class DataTransferer
/*      */ {
/*      */   public static final DataFlavor plainTextStringFlavor;
/*      */   public static final DataFlavor javaTextEncodingFlavor;
/*      */   private static final Map textMIMESubtypeCharsetSupport;
/*      */   private static String defaultEncoding;
/*      */   
/*      */   private static class StandardEncodingsHolder
/*      */   {
/*  166 */     private static final SortedSet<String> standardEncodings = ;
/*      */     
/*      */     private static SortedSet<String> load() {
/*  169 */       CharsetComparator localCharsetComparator = new CharsetComparator(false);
/*      */       
/*  171 */       TreeSet localTreeSet = new TreeSet(localCharsetComparator);
/*  172 */       localTreeSet.add("US-ASCII");
/*  173 */       localTreeSet.add("ISO-8859-1");
/*  174 */       localTreeSet.add("UTF-8");
/*  175 */       localTreeSet.add("UTF-16BE");
/*  176 */       localTreeSet.add("UTF-16LE");
/*  177 */       localTreeSet.add("UTF-16");
/*  178 */       localTreeSet.add(DataTransferer.getDefaultTextCharset());
/*  179 */       return Collections.unmodifiableSortedSet(localTreeSet);
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  202 */   private static final Set textNatives = Collections.synchronizedSet(new HashSet());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  208 */   private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  214 */   private static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  220 */   private static final Map nativeTerminators = Collections.synchronizedMap(new HashMap());
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
/*      */   
/*      */ 
/*      */ 
/*      */   private static DataTransferer transferer;
/*      */   
/*      */ 
/*      */ 
/*  233 */   private static final PlatformLogger dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
/*      */   
/*      */   static {
/*  236 */     DataFlavor localDataFlavor1 = null;
/*      */     try {
/*  238 */       localDataFlavor1 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException1) {}
/*      */     
/*  242 */     plainTextStringFlavor = localDataFlavor1;
/*      */     
/*  244 */     DataFlavor localDataFlavor2 = null;
/*      */     try {
/*  246 */       localDataFlavor2 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException2) {}
/*      */     
/*  250 */     javaTextEncodingFlavor = localDataFlavor2;
/*      */     
/*  252 */     HashMap localHashMap = new HashMap(17);
/*  253 */     localHashMap.put("sgml", Boolean.TRUE);
/*  254 */     localHashMap.put("xml", Boolean.TRUE);
/*  255 */     localHashMap.put("html", Boolean.TRUE);
/*  256 */     localHashMap.put("enriched", Boolean.TRUE);
/*  257 */     localHashMap.put("richtext", Boolean.TRUE);
/*  258 */     localHashMap.put("uri-list", Boolean.TRUE);
/*  259 */     localHashMap.put("directory", Boolean.TRUE);
/*  260 */     localHashMap.put("css", Boolean.TRUE);
/*  261 */     localHashMap.put("calendar", Boolean.TRUE);
/*  262 */     localHashMap.put("plain", Boolean.TRUE);
/*  263 */     localHashMap.put("rtf", Boolean.FALSE);
/*  264 */     localHashMap.put("tab-separated-values", Boolean.FALSE);
/*  265 */     localHashMap.put("t140", Boolean.FALSE);
/*  266 */     localHashMap.put("rfc822-headers", Boolean.FALSE);
/*  267 */     localHashMap.put("parityfec", Boolean.FALSE);
/*  268 */     textMIMESubtypeCharsetSupport = Collections.synchronizedMap(localHashMap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static synchronized DataTransferer getInstance()
/*      */   {
/*  277 */     return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String canonicalName(String paramString)
/*      */   {
/*  284 */     if (paramString == null) {
/*  285 */       return null;
/*      */     }
/*      */     try {
/*  288 */       return Charset.forName(paramString).name();
/*      */     } catch (IllegalCharsetNameException localIllegalCharsetNameException) {
/*  290 */       return paramString;
/*      */     } catch (UnsupportedCharsetException localUnsupportedCharsetException) {}
/*  292 */     return paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getTextCharset(DataFlavor paramDataFlavor)
/*      */   {
/*  304 */     if (!isFlavorCharsetTextType(paramDataFlavor)) {
/*  305 */       return null;
/*      */     }
/*      */     
/*  308 */     String str = paramDataFlavor.getParameter("charset");
/*      */     
/*  310 */     return str != null ? str : getDefaultTextCharset();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getDefaultTextCharset()
/*      */   {
/*  317 */     if (defaultEncoding != null) {
/*  318 */       return defaultEncoding;
/*      */     }
/*  320 */     return defaultEncoding = Charset.defaultCharset().name();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean doesSubtypeSupportCharset(DataFlavor paramDataFlavor)
/*      */   {
/*  329 */     if ((dtLog.isLoggable(PlatformLogger.Level.FINE)) && 
/*  330 */       (!"text".equals(paramDataFlavor.getPrimaryType()))) {
/*  331 */       dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed");
/*      */     }
/*      */     
/*      */ 
/*  335 */     String str = paramDataFlavor.getSubType();
/*  336 */     if (str == null) {
/*  337 */       return false;
/*      */     }
/*      */     
/*  340 */     Object localObject = textMIMESubtypeCharsetSupport.get(str);
/*      */     
/*  342 */     if (localObject != null) {
/*  343 */       return localObject == Boolean.TRUE;
/*      */     }
/*      */     
/*  346 */     boolean bool = paramDataFlavor.getParameter("charset") != null;
/*  347 */     textMIMESubtypeCharsetSupport
/*  348 */       .put(str, bool ? Boolean.TRUE : Boolean.FALSE);
/*  349 */     return bool;
/*      */   }
/*      */   
/*      */   public static boolean doesSubtypeSupportCharset(String paramString1, String paramString2)
/*      */   {
/*  354 */     Object localObject = textMIMESubtypeCharsetSupport.get(paramString1);
/*      */     
/*  356 */     if (localObject != null) {
/*  357 */       return localObject == Boolean.TRUE;
/*      */     }
/*      */     
/*  360 */     boolean bool = paramString2 != null;
/*  361 */     textMIMESubtypeCharsetSupport
/*  362 */       .put(paramString1, bool ? Boolean.TRUE : Boolean.FALSE);
/*  363 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isFlavorCharsetTextType(DataFlavor paramDataFlavor)
/*      */   {
/*  375 */     if (DataFlavor.stringFlavor.equals(paramDataFlavor)) {
/*  376 */       return true;
/*      */     }
/*      */     
/*  379 */     if ((!"text".equals(paramDataFlavor.getPrimaryType())) || 
/*  380 */       (!doesSubtypeSupportCharset(paramDataFlavor)))
/*      */     {
/*  382 */       return false;
/*      */     }
/*      */     
/*  385 */     Class localClass = paramDataFlavor.getRepresentationClass();
/*      */     
/*  387 */     if ((paramDataFlavor.isRepresentationClassReader()) || 
/*  388 */       (String.class.equals(localClass)) || 
/*  389 */       (paramDataFlavor.isRepresentationClassCharBuffer()) || 
/*  390 */       (char[].class.equals(localClass)))
/*      */     {
/*  392 */       return true;
/*      */     }
/*      */     
/*  395 */     if ((!paramDataFlavor.isRepresentationClassInputStream()) && 
/*  396 */       (!paramDataFlavor.isRepresentationClassByteBuffer()) && 
/*  397 */       (!byte[].class.equals(localClass))) {
/*  398 */       return false;
/*      */     }
/*      */     
/*  401 */     String str = paramDataFlavor.getParameter("charset");
/*      */     
/*  403 */     return str != null ? 
/*  404 */       isEncodingSupported(str) : true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isFlavorNoncharsetTextType(DataFlavor paramDataFlavor)
/*      */   {
/*  413 */     if ((!"text".equals(paramDataFlavor.getPrimaryType())) || 
/*  414 */       (doesSubtypeSupportCharset(paramDataFlavor)))
/*      */     {
/*  416 */       return false;
/*      */     }
/*      */     
/*  419 */     return (paramDataFlavor.isRepresentationClassInputStream()) || 
/*  420 */       (paramDataFlavor.isRepresentationClassByteBuffer()) || 
/*  421 */       (byte[].class.equals(paramDataFlavor.getRepresentationClass()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isEncodingSupported(String paramString)
/*      */   {
/*  429 */     if (paramString == null) {
/*  430 */       return false;
/*      */     }
/*      */     try {
/*  433 */       return Charset.isSupported(paramString);
/*      */     } catch (IllegalCharsetNameException localIllegalCharsetNameException) {}
/*  435 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isRemote(Class<?> paramClass)
/*      */   {
/*  443 */     return RMI.isRemote(paramClass);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Set<String> standardEncodings()
/*      */   {
/*  454 */     return StandardEncodingsHolder.standardEncodings;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static FlavorTable adaptFlavorMap(FlavorMap paramFlavorMap)
/*      */   {
/*  461 */     if ((paramFlavorMap instanceof FlavorTable)) {
/*  462 */       return (FlavorTable)paramFlavorMap;
/*      */     }
/*      */     
/*  465 */     new FlavorTable() {
/*      */       public Map getNativesForFlavors(DataFlavor[] paramAnonymousArrayOfDataFlavor) {
/*  467 */         return this.val$map.getNativesForFlavors(paramAnonymousArrayOfDataFlavor);
/*      */       }
/*      */       
/*  470 */       public Map getFlavorsForNatives(String[] paramAnonymousArrayOfString) { return this.val$map.getFlavorsForNatives(paramAnonymousArrayOfString); }
/*      */       
/*      */       public List getNativesForFlavor(DataFlavor paramAnonymousDataFlavor)
/*      */       {
/*  474 */         Map localMap = getNativesForFlavors(new DataFlavor[] { paramAnonymousDataFlavor });
/*  475 */         String str = (String)localMap.get(paramAnonymousDataFlavor);
/*  476 */         if (str != null) {
/*  477 */           ArrayList localArrayList = new ArrayList(1);
/*  478 */           localArrayList.add(str);
/*  479 */           return localArrayList;
/*      */         }
/*  481 */         return Collections.EMPTY_LIST;
/*      */       }
/*      */       
/*      */       public List getFlavorsForNative(String paramAnonymousString)
/*      */       {
/*  486 */         Map localMap = getFlavorsForNatives(new String[] { paramAnonymousString });
/*  487 */         DataFlavor localDataFlavor = (DataFlavor)localMap.get(paramAnonymousString);
/*  488 */         if (localDataFlavor != null) {
/*  489 */           ArrayList localArrayList = new ArrayList(1);
/*  490 */           localArrayList.add(localDataFlavor);
/*  491 */           return localArrayList;
/*      */         }
/*  493 */         return Collections.EMPTY_LIST;
/*      */       }
/*      */     };
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerTextFlavorProperties(String paramString1, String paramString2, String paramString3, String paramString4)
/*      */   {
/*  514 */     Long localLong = getFormatForNativeAsLong(paramString1);
/*      */     
/*  516 */     textNatives.add(localLong);
/*  517 */     nativeCharsets.put(localLong, (paramString2 != null) && (paramString2.length() != 0) ? paramString2 : 
/*  518 */       getDefaultTextCharset());
/*  519 */     if ((paramString3 != null) && (paramString3.length() != 0) && (!paramString3.equals("\n"))) {
/*  520 */       nativeEOLNs.put(localLong, paramString3);
/*      */     }
/*  522 */     if ((paramString4 != null) && (paramString4.length() != 0)) {
/*  523 */       Integer localInteger = Integer.valueOf(paramString4);
/*  524 */       if (localInteger.intValue() > 0) {
/*  525 */         nativeTerminators.put(localLong, localInteger);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isTextFormat(long paramLong)
/*      */   {
/*  535 */     return textNatives.contains(Long.valueOf(paramLong));
/*      */   }
/*      */   
/*      */   protected String getCharsetForTextFormat(Long paramLong) {
/*  539 */     return (String)nativeCharsets.get(paramLong);
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
/*      */   protected boolean isURIListFormat(long paramLong)
/*      */   {
/*  567 */     return false;
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
/*      */   public SortedMap<Long, DataFlavor> getFormatsForTransferable(Transferable paramTransferable, FlavorTable paramFlavorTable)
/*      */   {
/*  582 */     DataFlavor[] arrayOfDataFlavor = paramTransferable.getTransferDataFlavors();
/*  583 */     if (arrayOfDataFlavor == null) {
/*  584 */       return new TreeMap();
/*      */     }
/*  586 */     return getFormatsForFlavors(arrayOfDataFlavor, paramFlavorTable);
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
/*      */   public SortedMap getFormatsForFlavor(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable)
/*      */   {
/*  599 */     return getFormatsForFlavors(new DataFlavor[] { paramDataFlavor }, paramFlavorTable);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable)
/*      */   {
/*  620 */     HashMap localHashMap1 = new HashMap(paramArrayOfDataFlavor.length);
/*      */     
/*  622 */     HashMap localHashMap2 = new HashMap(paramArrayOfDataFlavor.length);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  627 */     HashMap localHashMap3 = new HashMap(paramArrayOfDataFlavor.length);
/*  628 */     HashMap localHashMap4 = new HashMap(paramArrayOfDataFlavor.length);
/*      */     
/*  630 */     int i = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  635 */     for (int j = paramArrayOfDataFlavor.length - 1; j >= 0; j--) {
/*  636 */       localObject = paramArrayOfDataFlavor[j];
/*  637 */       if (localObject != null)
/*      */       {
/*      */ 
/*      */ 
/*  641 */         if ((((DataFlavor)localObject).isFlavorTextType()) || 
/*  642 */           (((DataFlavor)localObject).isFlavorJavaFileListType()) || 
/*  643 */           (DataFlavor.imageFlavor.equals((DataFlavor)localObject)) || 
/*  644 */           (((DataFlavor)localObject).isRepresentationClassSerializable()) || 
/*  645 */           (((DataFlavor)localObject).isRepresentationClassInputStream()) || 
/*  646 */           (((DataFlavor)localObject).isRepresentationClassRemote()))
/*      */         {
/*  648 */           List localList = paramFlavorTable.getNativesForFlavor((DataFlavor)localObject);
/*      */           
/*  650 */           i += localList.size();
/*      */           
/*  652 */           for (Iterator localIterator = localList.iterator(); localIterator.hasNext();)
/*      */           {
/*  654 */             Long localLong = getFormatForNativeAsLong((String)localIterator.next());
/*  655 */             Integer localInteger = Integer.valueOf(i--);
/*      */             
/*  657 */             localHashMap1.put(localLong, localObject);
/*  658 */             localHashMap3.put(localLong, localInteger);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  664 */             if ((("text".equals(((DataFlavor)localObject).getPrimaryType())) && 
/*  665 */               ("plain".equals(((DataFlavor)localObject).getSubType()))) || 
/*  666 */               (((DataFlavor)localObject).equals(DataFlavor.stringFlavor)))
/*      */             {
/*  668 */               localHashMap2.put(localLong, localObject);
/*  669 */               localHashMap4.put(localLong, localInteger);
/*      */             }
/*      */           }
/*      */           
/*  673 */           i += localList.size();
/*      */         }
/*      */       }
/*      */     }
/*  677 */     localHashMap1.putAll(localHashMap2);
/*  678 */     localHashMap3.putAll(localHashMap4);
/*      */     
/*      */ 
/*  681 */     IndexOrderComparator localIndexOrderComparator = new IndexOrderComparator(localHashMap3, false);
/*      */     
/*  683 */     Object localObject = new TreeMap(localIndexOrderComparator);
/*  684 */     ((SortedMap)localObject).putAll(localHashMap1);
/*      */     
/*  686 */     return (SortedMap<Long, DataFlavor>)localObject;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getFormatsForTransferableAsArray(Transferable paramTransferable, FlavorTable paramFlavorTable)
/*      */   {
/*  695 */     return keysToLongArray(getFormatsForTransferable(paramTransferable, paramFlavorTable));
/*      */   }
/*      */   
/*      */   public long[] getFormatsForFlavorAsArray(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable) {
/*  699 */     return keysToLongArray(getFormatsForFlavor(paramDataFlavor, paramFlavorTable));
/*      */   }
/*      */   
/*      */   public long[] getFormatsForFlavorsAsArray(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable) {
/*  703 */     return keysToLongArray(getFormatsForFlavors(paramArrayOfDataFlavor, paramFlavorTable));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map getFlavorsForFormat(long paramLong, FlavorTable paramFlavorTable)
/*      */   {
/*  713 */     return getFlavorsForFormats(new long[] { paramLong }, paramFlavorTable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map getFlavorsForFormats(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
/*      */   {
/*  723 */     HashMap localHashMap = new HashMap(paramArrayOfLong.length);
/*  724 */     HashSet localHashSet1 = new HashSet(paramArrayOfLong.length);
/*  725 */     HashSet localHashSet2 = new HashSet(paramArrayOfLong.length);
/*      */     
/*      */     long l;
/*      */     
/*      */     Object localObject1;
/*      */     
/*      */     Object localObject2;
/*      */     
/*      */     Object localObject3;
/*  734 */     for (int i = 0; i < paramArrayOfLong.length; i++) {
/*  735 */       l = paramArrayOfLong[i];
/*  736 */       localObject1 = getNativeForFormat(l);
/*  737 */       localObject2 = paramFlavorTable.getFlavorsForNative((String)localObject1);
/*      */       
/*  739 */       for (localObject3 = ((List)localObject2).iterator(); ((Iterator)localObject3).hasNext();) {
/*  740 */         DataFlavor localDataFlavor2 = (DataFlavor)((Iterator)localObject3).next();
/*      */         
/*      */ 
/*      */ 
/*  744 */         if ((localDataFlavor2.isFlavorTextType()) || 
/*  745 */           (localDataFlavor2.isFlavorJavaFileListType()) || 
/*  746 */           (DataFlavor.imageFlavor.equals(localDataFlavor2)) || 
/*  747 */           (localDataFlavor2.isRepresentationClassSerializable()) || 
/*  748 */           (localDataFlavor2.isRepresentationClassInputStream()) || 
/*  749 */           (localDataFlavor2.isRepresentationClassRemote()))
/*      */         {
/*  751 */           Long localLong = Long.valueOf(l);
/*      */           
/*  753 */           Object localObject4 = createMapping(localLong, localDataFlavor2);
/*  754 */           localHashMap.put(localDataFlavor2, localLong);
/*  755 */           localHashSet1.add(localObject4);
/*  756 */           localHashSet2.add(localDataFlavor2);
/*      */         }
/*      */       }
/*      */     }
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
/*  774 */     Iterator localIterator = localHashSet2.iterator();
/*  775 */     while (localIterator.hasNext()) {
/*  776 */       DataFlavor localDataFlavor1 = (DataFlavor)localIterator.next();
/*      */       
/*  778 */       List localList = paramFlavorTable.getNativesForFlavor(localDataFlavor1);
/*      */       
/*  780 */       localObject1 = localList.iterator();
/*  781 */       while (((Iterator)localObject1).hasNext())
/*      */       {
/*  783 */         localObject2 = getFormatForNativeAsLong((String)((Iterator)localObject1).next());
/*  784 */         localObject3 = createMapping(localObject2, localDataFlavor1);
/*      */         
/*  786 */         if (localHashSet1.contains(localObject3)) {
/*  787 */           localHashMap.put(localDataFlavor1, localObject2);
/*  788 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  793 */     return localHashMap;
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
/*      */   public Set getFlavorsForFormatsAsSet(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
/*      */   {
/*  809 */     HashSet localHashSet = new HashSet(paramArrayOfLong.length);
/*      */     Iterator localIterator;
/*  811 */     for (int i = 0; i < paramArrayOfLong.length; i++) {
/*  812 */       String str = getNativeForFormat(paramArrayOfLong[i]);
/*  813 */       List localList = paramFlavorTable.getFlavorsForNative(str);
/*      */       
/*  815 */       for (localIterator = localList.iterator(); localIterator.hasNext();) {
/*  816 */         DataFlavor localDataFlavor = (DataFlavor)localIterator.next();
/*      */         
/*      */ 
/*      */ 
/*  820 */         if ((localDataFlavor.isFlavorTextType()) || 
/*  821 */           (localDataFlavor.isFlavorJavaFileListType()) || 
/*  822 */           (DataFlavor.imageFlavor.equals(localDataFlavor)) || 
/*  823 */           (localDataFlavor.isRepresentationClassSerializable()) || 
/*  824 */           (localDataFlavor.isRepresentationClassInputStream()) || 
/*  825 */           (localDataFlavor.isRepresentationClassRemote()))
/*      */         {
/*  827 */           localHashSet.add(localDataFlavor);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  832 */     return localHashSet;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public DataFlavor[] getFlavorsForFormatAsArray(long paramLong, FlavorTable paramFlavorTable)
/*      */   {
/*  851 */     return getFlavorsForFormatsAsArray(new long[] { paramLong }, paramFlavorTable);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DataFlavor[] getFlavorsForFormatsAsArray(long[] paramArrayOfLong, FlavorTable paramFlavorTable)
/*      */   {
/*  873 */     return setToSortedDataFlavorArray(getFlavorsForFormatsAsSet(paramArrayOfLong, paramFlavorTable));
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
/*      */   private static Object createMapping(Object paramObject1, Object paramObject2)
/*      */   {
/*  889 */     return Arrays.asList(new Object[] { paramObject1, paramObject2 });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getBestCharsetForTextFormat(Long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/*  911 */     String str = null;
/*  912 */     if ((paramTransferable != null) && 
/*  913 */       (isLocaleDependentTextFormat(paramLong.longValue())) && 
/*  914 */       (paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor)))
/*      */     {
/*      */       try
/*      */       {
/*  918 */         str = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
/*      */ 
/*      */       }
/*      */       catch (UnsupportedFlavorException localUnsupportedFlavorException) {}
/*      */     }
/*      */     else {
/*  924 */       str = getCharsetForTextFormat(paramLong);
/*      */     }
/*  926 */     if (str == null)
/*      */     {
/*  928 */       str = getDefaultTextCharset();
/*      */     }
/*  930 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] translateTransferableString(String paramString, long paramLong)
/*      */     throws IOException
/*      */   {
/*  943 */     Long localLong = Long.valueOf(paramLong);
/*  944 */     String str1 = getBestCharsetForTextFormat(localLong, null);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  949 */     String str2 = (String)nativeEOLNs.get(localLong);
/*  950 */     int j; if (str2 != null) {
/*  951 */       int i = paramString.length();
/*  952 */       localObject2 = new StringBuffer(i * 2);
/*      */       
/*  954 */       for (j = 0; j < i; j++)
/*      */       {
/*  956 */         if (paramString.startsWith(str2, j)) {
/*  957 */           ((StringBuffer)localObject2).append(str2);
/*  958 */           j += str2.length() - 1;
/*      */         }
/*      */         else {
/*  961 */           char c = paramString.charAt(j);
/*  962 */           if (c == '\n') {
/*  963 */             ((StringBuffer)localObject2).append(str2);
/*      */           } else
/*  965 */             ((StringBuffer)localObject2).append(c);
/*      */         }
/*      */       }
/*  968 */       paramString = ((StringBuffer)localObject2).toString();
/*      */     }
/*      */     
/*      */ 
/*  972 */     Object localObject1 = paramString.getBytes(str1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  978 */     Object localObject2 = (Integer)nativeTerminators.get(localLong);
/*  979 */     if (localObject2 != null) {
/*  980 */       j = ((Integer)localObject2).intValue();
/*  981 */       byte[] arrayOfByte = new byte[localObject1.length + j];
/*      */       
/*  983 */       System.arraycopy(localObject1, 0, arrayOfByte, 0, localObject1.length);
/*  984 */       for (int k = localObject1.length; k < arrayOfByte.length; k++) {
/*  985 */         arrayOfByte[k] = 0;
/*      */       }
/*  987 */       localObject1 = arrayOfByte;
/*      */     }
/*  989 */     return (byte[])localObject1;
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
/*      */   private String translateBytesToString(byte[] paramArrayOfByte, long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/* 1003 */     Long localLong = Long.valueOf(paramLong);
/* 1004 */     String str1 = getBestCharsetForTextFormat(localLong, paramTransferable);
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
/* 1016 */     String str2 = (String)nativeEOLNs.get(localLong);
/* 1017 */     Integer localInteger = (Integer)nativeTerminators.get(localLong);
/*      */     int i;
/* 1019 */     if (localInteger != null) {
/* 1020 */       int j = localInteger.intValue();
/*      */       label106:
/* 1022 */       for (i = 0; i < paramArrayOfByte.length - j + 1; i += j) {
/* 1023 */         for (int k = i; k < i + j; k++) {
/* 1024 */           if (paramArrayOfByte[k] != 0) {
/*      */             break label106;
/*      */           }
/*      */         }
/*      */         
/* 1029 */         break;
/*      */       }
/*      */     } else {
/* 1032 */       i = paramArrayOfByte.length;
/*      */     }
/*      */     
/*      */ 
/* 1036 */     String str3 = new String(paramArrayOfByte, 0, i, str1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1045 */     if (str2 != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1051 */       char[] arrayOfChar1 = str3.toCharArray();
/* 1052 */       char[] arrayOfChar2 = str2.toCharArray();
/* 1053 */       str3 = null;
/* 1054 */       int m = 0;
/*      */       
/*      */ 
/* 1057 */       for (int i1 = 0; i1 < arrayOfChar1.length;)
/*      */       {
/* 1059 */         if (i1 + arrayOfChar2.length > arrayOfChar1.length) {
/* 1060 */           arrayOfChar1[(m++)] = arrayOfChar1[(i1++)];
/*      */         }
/*      */         else
/*      */         {
/* 1064 */           int n = 1;
/* 1065 */           int i2 = 0; for (int i3 = i1; i2 < arrayOfChar2.length; i3++) {
/* 1066 */             if (arrayOfChar2[i2] != arrayOfChar1[i3]) {
/* 1067 */               n = 0;
/* 1068 */               break;
/*      */             }
/* 1065 */             i2++;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1071 */           if (n != 0) {
/* 1072 */             arrayOfChar1[(m++)] = '\n';
/* 1073 */             i1 += arrayOfChar2.length;
/*      */           } else {
/* 1075 */             arrayOfChar1[(m++)] = arrayOfChar1[(i1++)];
/*      */           }
/*      */         } }
/* 1078 */       str3 = new String(arrayOfChar1, 0, m);
/*      */     }
/*      */     
/* 1081 */     return str3;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] translateTransferable(Transferable paramTransferable, DataFlavor paramDataFlavor, long paramLong)
/*      */     throws IOException
/*      */   {
/*      */     Object localObject1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     int i;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1105 */       localObject1 = paramTransferable.getTransferData(paramDataFlavor);
/* 1106 */       if (localObject1 == null) {
/* 1107 */         return null;
/*      */       }
/* 1109 */       if ((paramDataFlavor.equals(DataFlavor.plainTextFlavor)) && (!(localObject1 instanceof InputStream)))
/*      */       {
/*      */ 
/* 1112 */         localObject1 = paramTransferable.getTransferData(DataFlavor.stringFlavor);
/* 1113 */         if (localObject1 == null) {
/* 1114 */           return null;
/*      */         }
/* 1116 */         i = 1;
/*      */       } else {
/* 1118 */         i = 0;
/*      */       }
/*      */     } catch (UnsupportedFlavorException localUnsupportedFlavorException) {
/* 1121 */       throw new IOException(localUnsupportedFlavorException.getMessage());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1126 */     if ((i != 0) || (
/* 1127 */       (String.class.equals(paramDataFlavor.getRepresentationClass())) && 
/* 1128 */       (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))))
/*      */     {
/* 1130 */       localObject2 = removeSuspectedData(paramDataFlavor, paramTransferable, (String)localObject1);
/*      */       
/* 1132 */       return translateTransferableString((String)localObject2, paramLong);
/*      */     }
/*      */     
/*      */ 
/*      */     Object localObject4;
/*      */     
/* 1138 */     if (paramDataFlavor.isRepresentationClassReader()) {
/* 1139 */       if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1140 */         throw new IOException("cannot transfer non-text data as Reader");
/*      */       }
/*      */       
/*      */ 
/* 1144 */       localObject2 = new StringBuffer();
/* 1145 */       Reader localReader = (Reader)localObject1;localObject4 = null;
/*      */       try { int k;
/* 1147 */         while ((k = localReader.read()) != -1) {
/* 1148 */           ((StringBuffer)localObject2).append((char)k);
/*      */         }
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/* 1145 */         localObject4 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 1150 */         if (localReader != null) if (localObject4 != null) try { localReader.close(); } catch (Throwable localThrowable5) { ((Throwable)localObject4).addSuppressed(localThrowable5); } else localReader.close();
/*      */       }
/* 1152 */       return translateTransferableString(((StringBuffer)localObject2)
/* 1153 */         .toString(), paramLong);
/*      */     }
/*      */     
/*      */     int j;
/* 1157 */     if (paramDataFlavor.isRepresentationClassCharBuffer()) {
/* 1158 */       if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1159 */         throw new IOException("cannot transfer non-text data as CharBuffer");
/*      */       }
/*      */       
/*      */ 
/* 1163 */       localObject2 = (CharBuffer)localObject1;
/* 1164 */       j = ((CharBuffer)localObject2).remaining();
/* 1165 */       localObject4 = new char[j];
/* 1166 */       ((CharBuffer)localObject2).get((char[])localObject4, 0, j);
/*      */       
/* 1168 */       return translateTransferableString(new String((char[])localObject4), paramLong);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1173 */     if (char[].class.equals(paramDataFlavor.getRepresentationClass())) {
/* 1174 */       if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1175 */         throw new IOException("cannot transfer non-text data as char array");
/*      */       }
/*      */       
/*      */ 
/* 1179 */       return translateTransferableString(new String((char[])localObject1), paramLong);
/*      */     }
/*      */     
/*      */ 
/*      */     Object localObject5;
/*      */     
/*      */ 
/* 1186 */     if (paramDataFlavor.isRepresentationClassByteBuffer()) {
/* 1187 */       localObject2 = (ByteBuffer)localObject1;
/* 1188 */       j = ((ByteBuffer)localObject2).remaining();
/* 1189 */       localObject4 = new byte[j];
/* 1190 */       ((ByteBuffer)localObject2).get((byte[])localObject4, 0, j);
/*      */       
/* 1192 */       if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
/* 1193 */         localObject5 = getTextCharset(paramDataFlavor);
/* 1194 */         return translateTransferableString(new String((byte[])localObject4, (String)localObject5), paramLong);
/*      */       }
/*      */       
/*      */ 
/* 1198 */       return (byte[])localObject4;
/*      */     }
/*      */     
/*      */ 
/*      */     Object localObject3;
/*      */     
/* 1204 */     if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
/* 1205 */       localObject2 = (byte[])localObject1;
/*      */       
/* 1207 */       if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
/* 1208 */         localObject3 = getTextCharset(paramDataFlavor);
/* 1209 */         return translateTransferableString(new String((byte[])localObject2, (String)localObject3), paramLong);
/*      */       }
/*      */       
/*      */ 
/* 1213 */       return (byte[])localObject2;
/*      */     }
/*      */     
/* 1216 */     if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
/* 1217 */       if (!isImageFormat(paramLong)) {
/* 1218 */         throw new IOException("Data translation failed: not an image format");
/*      */       }
/*      */       
/*      */ 
/* 1222 */       localObject2 = (Image)localObject1;
/* 1223 */       localObject3 = imageToPlatformBytes((Image)localObject2, paramLong);
/*      */       
/* 1225 */       if (localObject3 == null) {
/* 1226 */         throw new IOException("Data translation failed: cannot convert java image to native format");
/*      */       }
/*      */       
/* 1229 */       return (byte[])localObject3;
/*      */     }
/*      */     
/* 1232 */     Object localObject2 = null;
/*      */     
/*      */     Object localObject8;
/*      */     Object localObject9;
/* 1236 */     if (isFileFormat(paramLong)) {
/* 1237 */       if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
/* 1238 */         throw new IOException("data translation failed");
/*      */       }
/*      */       
/* 1241 */       localObject3 = (List)localObject1;
/*      */       
/* 1243 */       localObject4 = getUserProtectionDomain(paramTransferable);
/*      */       
/* 1245 */       localObject5 = castToFiles((List)localObject3, (ProtectionDomain)localObject4);
/*      */       
/* 1247 */       localObject8 = convertFileListToBytes((ArrayList)localObject5);localObject9 = null;
/* 1248 */       try { localObject2 = ((ByteArrayOutputStream)localObject8).toByteArray();
/*      */       }
/*      */       catch (Throwable localThrowable9)
/*      */       {
/* 1247 */         localObject9 = localThrowable9;throw localThrowable9;
/*      */       } finally {
/* 1249 */         if (localObject8 != null) if (localObject9 != null) try { ((ByteArrayOutputStream)localObject8).close(); } catch (Throwable localThrowable11) { ((Throwable)localObject9).addSuppressed(localThrowable11); } else ((ByteArrayOutputStream)localObject8).close();
/*      */       }
/*      */     } else { Object localObject6;
/*      */       Object localObject11;
/* 1253 */       if (isURIListFormat(paramLong)) {
/* 1254 */         if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
/* 1255 */           throw new IOException("data translation failed");
/*      */         }
/* 1257 */         localObject3 = getNativeForFormat(paramLong);
/* 1258 */         localObject4 = null;
/* 1259 */         if (localObject3 != null) {
/*      */           try {
/* 1261 */             localObject4 = new DataFlavor((String)localObject3).getParameter("charset");
/*      */           } catch (ClassNotFoundException localClassNotFoundException) {
/* 1263 */             throw new IOException(localClassNotFoundException);
/*      */           }
/*      */         }
/* 1266 */         if (localObject4 == null) {
/* 1267 */           localObject4 = "UTF-8";
/*      */         }
/* 1269 */         localObject6 = (List)localObject1;
/* 1270 */         localObject8 = getUserProtectionDomain(paramTransferable);
/* 1271 */         localObject9 = castToFiles((List)localObject6, (ProtectionDomain)localObject8);
/* 1272 */         ArrayList localArrayList = new ArrayList(((ArrayList)localObject9).size());
/* 1273 */         for (localObject11 = ((ArrayList)localObject9).iterator(); ((Iterator)localObject11).hasNext();) { localObject12 = (String)((Iterator)localObject11).next();
/* 1274 */           localObject13 = new File((String)localObject12).toURI();
/*      */           try
/*      */           {
/* 1277 */             localArrayList.add(new URI(((URI)localObject13).getScheme(), "", ((URI)localObject13).getPath(), ((URI)localObject13).getFragment()).toString());
/*      */           } catch (URISyntaxException localURISyntaxException) {
/* 1279 */             throw new IOException(localURISyntaxException);
/*      */           }
/*      */         }
/*      */         
/* 1283 */         localObject11 = "\r\n".getBytes((String)localObject4);
/*      */         
/* 1285 */         Object localObject12 = new ByteArrayOutputStream();Object localObject13 = null;
/* 1286 */         try { for (int i2 = 0; i2 < localArrayList.size(); i2++) {
/* 1287 */             byte[] arrayOfByte2 = ((String)localArrayList.get(i2)).getBytes((String)localObject4);
/* 1288 */             ((ByteArrayOutputStream)localObject12).write(arrayOfByte2, 0, arrayOfByte2.length);
/* 1289 */             ((ByteArrayOutputStream)localObject12).write((byte[])localObject11, 0, localObject11.length);
/*      */           }
/* 1291 */           localObject2 = ((ByteArrayOutputStream)localObject12).toByteArray();
/*      */         }
/*      */         catch (Throwable localThrowable13)
/*      */         {
/* 1285 */           localObject13 = localThrowable13;throw localThrowable13;
/*      */ 
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*      */ 
/* 1292 */           if (localObject12 != null) { if (localObject13 != null) try { ((ByteArrayOutputStream)localObject12).close(); } catch (Throwable localThrowable14) { ((Throwable)localObject13).addSuppressed(localThrowable14); } else { ((ByteArrayOutputStream)localObject12).close();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1297 */       else if (paramDataFlavor.isRepresentationClassInputStream())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1303 */         if (!(localObject1 instanceof InputStream)) {
/* 1304 */           return new byte[0];
/*      */         }
/*      */         
/* 1307 */         localObject3 = new ByteArrayOutputStream();localObject4 = null;
/* 1308 */         try { localObject6 = (InputStream)localObject1;localObject8 = null;
/* 1309 */           try { int m = 0;
/* 1310 */             int n = ((InputStream)localObject6).available();
/* 1311 */             localObject11 = new byte[n > 8192 ? n : ' '];
/*      */             do {
/*      */               int i1;
/* 1314 */               if ((m = (i1 = ((InputStream)localObject6).read((byte[])localObject11, 0, localObject11.length)) == -1 ? 1 : 0) == 0) {
/* 1315 */                 ((ByteArrayOutputStream)localObject3).write((byte[])localObject11, 0, i1);
/*      */               }
/* 1317 */             } while (m == 0);
/*      */           }
/*      */           catch (Throwable localThrowable7)
/*      */           {
/* 1308 */             localObject8 = localThrowable7;throw localThrowable7;
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           finally
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1318 */             if (localObject6 != null) if (localObject8 != null) try { ((InputStream)localObject6).close(); } catch (Throwable localThrowable15) { ((Throwable)localObject8).addSuppressed(localThrowable15); } else ((InputStream)localObject6).close();
/*      */           }
/* 1320 */           if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong))) {
/* 1321 */             localObject6 = ((ByteArrayOutputStream)localObject3).toByteArray();
/* 1322 */             localObject8 = getTextCharset(paramDataFlavor);
/* 1323 */             return translateTransferableString(new String((byte[])localObject6, (String)localObject8), paramLong);
/*      */           }
/*      */           
/*      */ 
/* 1327 */           localObject2 = ((ByteArrayOutputStream)localObject3).toByteArray();
/*      */         }
/*      */         catch (Throwable localThrowable4)
/*      */         {
/* 1307 */           localObject4 = localThrowable4;throw localThrowable4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1328 */           if (localObject3 != null) { if (localObject4 != null) try { ((ByteArrayOutputStream)localObject3).close(); } catch (Throwable localThrowable16) { ((Throwable)localObject4).addSuppressed(localThrowable16); } else { ((ByteArrayOutputStream)localObject3).close();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1333 */       else if (paramDataFlavor.isRepresentationClassRemote())
/*      */       {
/* 1335 */         localObject3 = RMI.newMarshalledObject(localObject1);
/* 1336 */         localObject2 = convertObjectToBytes(localObject3);
/*      */ 
/*      */       }
/* 1339 */       else if (paramDataFlavor.isRepresentationClassSerializable())
/*      */       {
/* 1341 */         localObject2 = convertObjectToBytes(localObject1);
/*      */       }
/*      */       else {
/* 1344 */         throw new IOException("data translation failed");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1349 */     return (byte[])localObject2;
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
/*      */   private String removeSuspectedData(DataFlavor paramDataFlavor, Transferable paramTransferable, final String paramString)
/*      */     throws IOException
/*      */   {
/* 1366 */     if ((null == System.getSecurityManager()) || 
/* 1367 */       (!paramDataFlavor.isMimeTypeEqual("text/uri-list")))
/*      */     {
/* 1369 */       return paramString;
/*      */     }
/*      */     
/*      */ 
/* 1373 */     String str = "";
/* 1374 */     final ProtectionDomain localProtectionDomain = getUserProtectionDomain(paramTransferable);
/*      */     try
/*      */     {
/* 1377 */       str = (String)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*      */       {
/*      */         public Object run() {
/* 1380 */           StringBuffer localStringBuffer = new StringBuffer(paramString.length());
/* 1381 */           String[] arrayOfString1 = paramString.split("(\\s)+");
/*      */           
/* 1383 */           for (String str : arrayOfString1)
/*      */           {
/* 1385 */             File localFile = new File(str);
/* 1386 */             if ((localFile.exists()) && 
/* 1387 */               (!DataTransferer.isFileInWebstartedCache(localFile)) && 
/* 1388 */               (!DataTransferer.this.isForbiddenToRead(localFile, localProtectionDomain)))
/*      */             {
/*      */ 
/* 1391 */               if (0 != localStringBuffer.length())
/*      */               {
/* 1393 */                 localStringBuffer.append("\\r\\n");
/*      */               }
/*      */               
/* 1396 */               localStringBuffer.append(str);
/*      */             }
/*      */           }
/*      */           
/* 1400 */           return localStringBuffer.toString();
/*      */         }
/*      */       });
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 1404 */       throw new IOException(localPrivilegedActionException.getMessage(), localPrivilegedActionException);
/*      */     }
/*      */     
/* 1407 */     return str;
/*      */   }
/*      */   
/*      */   private static ProtectionDomain getUserProtectionDomain(Transferable paramTransferable) {
/* 1411 */     return paramTransferable.getClass().getProtectionDomain();
/*      */   }
/*      */   
/*      */   private boolean isForbiddenToRead(File paramFile, ProtectionDomain paramProtectionDomain)
/*      */   {
/* 1416 */     if (null == paramProtectionDomain) {
/* 1417 */       return false;
/*      */     }
/*      */     try
/*      */     {
/* 1421 */       FilePermission localFilePermission = new FilePermission(paramFile.getCanonicalPath(), "read, delete");
/* 1422 */       if (paramProtectionDomain.implies(localFilePermission)) {
/* 1423 */         return false;
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException) {}
/* 1427 */     return true;
/*      */   }
/*      */   
/*      */   private ArrayList<String> castToFiles(final List paramList, final ProtectionDomain paramProtectionDomain)
/*      */     throws IOException
/*      */   {
/* 1433 */     final ArrayList localArrayList = new ArrayList();
/*      */     try {
/* 1435 */       AccessController.doPrivileged(new PrivilegedExceptionAction() {
/*      */         public Object run() throws IOException {
/* 1437 */           for (Object localObject : paramList)
/*      */           {
/* 1439 */             File localFile = DataTransferer.this.castToFile(localObject);
/* 1440 */             if ((localFile != null) && (
/* 1441 */               (null == System.getSecurityManager()) || (
/* 1442 */               (!DataTransferer.isFileInWebstartedCache(localFile)) && 
/* 1443 */               (!DataTransferer.this.isForbiddenToRead(localFile, paramProtectionDomain)))))
/*      */             {
/* 1445 */               localArrayList.add(localFile.getCanonicalPath());
/*      */             }
/*      */           }
/* 1448 */           return null;
/*      */         }
/*      */       });
/*      */     } catch (PrivilegedActionException localPrivilegedActionException) {
/* 1452 */       throw new IOException(localPrivilegedActionException.getMessage());
/*      */     }
/* 1454 */     return localArrayList;
/*      */   }
/*      */   
/*      */   private File castToFile(Object paramObject)
/*      */     throws IOException
/*      */   {
/* 1460 */     String str = null;
/* 1461 */     if ((paramObject instanceof File)) {
/* 1462 */       str = ((File)paramObject).getCanonicalPath();
/* 1463 */     } else if ((paramObject instanceof String)) {
/* 1464 */       str = (String)paramObject;
/*      */     } else {
/* 1466 */       return null;
/*      */     }
/* 1468 */     return new File(str);
/*      */   }
/*      */   
/* 1471 */   private static final String[] DEPLOYMENT_CACHE_PROPERTIES = { "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1478 */   private static final ArrayList<File> deploymentCacheDirectoryList = new ArrayList();
/*      */   
/*      */ 
/*      */   private static boolean isFileInWebstartedCache(File paramFile)
/*      */   {
/* 1483 */     if (deploymentCacheDirectoryList.isEmpty()) {
/* 1484 */       for (String str1 : DEPLOYMENT_CACHE_PROPERTIES) {
/* 1485 */         String str2 = System.getProperty(str1);
/* 1486 */         if (str2 != null) {
/*      */           try {
/* 1488 */             File localFile3 = new File(str2).getCanonicalFile();
/* 1489 */             if (localFile3 != null) {
/* 1490 */               deploymentCacheDirectoryList.add(localFile3);
/*      */             }
/*      */           }
/*      */           catch (IOException localIOException) {}
/*      */         }
/*      */       }
/*      */     }
/* 1497 */     for (??? = deploymentCacheDirectoryList.iterator(); ((Iterator)???).hasNext();) { File localFile1 = (File)((Iterator)???).next();
/* 1498 */       for (File localFile2 = paramFile; localFile2 != null; localFile2 = localFile2.getParentFile()) {
/* 1499 */         if (localFile2.equals(localFile1)) {
/* 1500 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1505 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object translateBytes(byte[] paramArrayOfByte, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/* 1514 */     Object localObject1 = null;
/*      */     
/*      */     Object localObject2;
/*      */     
/*      */     Object localObject3;
/* 1519 */     if (isFileFormat(paramLong)) {
/* 1520 */       if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
/* 1521 */         throw new IOException("data translation failed");
/*      */       }
/* 1523 */       localObject2 = dragQueryFile(paramArrayOfByte);
/* 1524 */       if (localObject2 == null) {
/* 1525 */         return null;
/*      */       }
/*      */       
/*      */ 
/* 1529 */       localObject3 = new File[localObject2.length];
/* 1530 */       for (int i = 0; i < localObject2.length; i++) {
/* 1531 */         localObject3[i] = new File(localObject2[i]);
/*      */       }
/*      */       
/*      */ 
/* 1535 */       localObject1 = Arrays.asList((Object[])localObject3);
/*      */     }
/*      */     else {
/*      */       Object localObject4;
/* 1539 */       if ((isURIListFormat(paramLong)) && 
/* 1540 */         (DataFlavor.javaFileListFlavor.equals(paramDataFlavor)))
/*      */       {
/* 1542 */         localObject2 = new ByteArrayInputStream(paramArrayOfByte);localObject3 = null;
/*      */         try {
/* 1544 */           URI[] arrayOfURI1 = dragQueryURIs((InputStream)localObject2, paramLong, paramTransferable);
/* 1545 */           if (arrayOfURI1 == null) {
/* 1546 */             return null;
/*      */           }
/* 1548 */           localObject4 = new ArrayList();
/* 1549 */           for (URI localURI : arrayOfURI1) {
/*      */             try {
/* 1551 */               ((List)localObject4).add(new File(localURI));
/*      */             }
/*      */             catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1558 */           localObject1 = localObject4;
/*      */         }
/*      */         catch (Throwable localThrowable2)
/*      */         {
/* 1542 */           localObject3 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1559 */           if (localObject2 != null) if (localObject3 != null) try { ((ByteArrayInputStream)localObject2).close(); } catch (Throwable localThrowable14) { ((Throwable)localObject3).addSuppressed(localThrowable14); } else { ((ByteArrayInputStream)localObject2).close();
/*      */             }
/*      */         }
/*      */       }
/* 1563 */       else if ((String.class.equals(paramDataFlavor.getRepresentationClass())) && 
/* 1564 */         (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */       {
/* 1566 */         localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1571 */       else if (paramDataFlavor.isRepresentationClassReader()) {
/* 1572 */         localObject2 = new ByteArrayInputStream(paramArrayOfByte);localObject3 = null;
/* 1573 */         try { localObject1 = translateStream((InputStream)localObject2, paramDataFlavor, paramLong, paramTransferable);
/*      */         }
/*      */         catch (Throwable localThrowable4)
/*      */         {
/* 1572 */           localObject3 = localThrowable4;throw localThrowable4;
/*      */         }
/*      */         finally {
/* 1575 */           if (localObject2 != null) if (localObject3 != null) try { ((ByteArrayInputStream)localObject2).close(); } catch (Throwable localThrowable15) { ((Throwable)localObject3).addSuppressed(localThrowable15); } else ((ByteArrayInputStream)localObject2).close();
/*      */         }
/* 1577 */       } else if (paramDataFlavor.isRepresentationClassCharBuffer()) {
/* 1578 */         if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1579 */           throw new IOException("cannot transfer non-text data as CharBuffer");
/*      */         }
/*      */         
/*      */ 
/* 1583 */         localObject2 = CharBuffer.wrap(
/* 1584 */           translateBytesToString(paramArrayOfByte, paramLong, paramTransferable));
/*      */         
/* 1586 */         localObject1 = constructFlavoredObject(localObject2, paramDataFlavor, CharBuffer.class);
/*      */ 
/*      */ 
/*      */       }
/* 1590 */       else if (char[].class.equals(paramDataFlavor.getRepresentationClass())) {
/* 1591 */         if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1592 */           throw new IOException("cannot transfer non-text data as char array");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1597 */         localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).toCharArray();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1603 */       else if (paramDataFlavor.isRepresentationClassByteBuffer()) {
/* 1604 */         if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */         {
/* 1606 */           paramArrayOfByte = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(
/* 1607 */             getTextCharset(paramDataFlavor));
/*      */         }
/*      */         
/*      */ 
/* 1611 */         localObject2 = ByteBuffer.wrap(paramArrayOfByte);
/* 1612 */         localObject1 = constructFlavoredObject(localObject2, paramDataFlavor, ByteBuffer.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1618 */       else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
/* 1619 */         if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */         {
/*      */ 
/* 1622 */           localObject1 = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
/*      */         } else {
/* 1624 */           localObject1 = paramArrayOfByte;
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/* 1631 */       else if (paramDataFlavor.isRepresentationClassInputStream())
/*      */       {
/* 1633 */         localObject2 = new ByteArrayInputStream(paramArrayOfByte);localObject3 = null;
/* 1634 */         try { localObject1 = translateStream((InputStream)localObject2, paramDataFlavor, paramLong, paramTransferable);
/*      */         }
/*      */         catch (Throwable localThrowable6)
/*      */         {
/* 1633 */           localObject3 = localThrowable6;throw localThrowable6;
/*      */         } finally {
/* 1635 */           if (localObject2 != null) if (localObject3 != null) try { ((ByteArrayInputStream)localObject2).close(); } catch (Throwable localThrowable16) { ((Throwable)localObject3).addSuppressed(localThrowable16); } else ((ByteArrayInputStream)localObject2).close();
/*      */         }
/* 1637 */       } else if (paramDataFlavor.isRepresentationClassRemote()) {
/* 1638 */         try { localObject2 = new ByteArrayInputStream(paramArrayOfByte);localObject3 = null;
/* 1639 */           try { ObjectInputStream localObjectInputStream = new ObjectInputStream((InputStream)localObject2);localObject4 = null;
/*      */             try {
/* 1641 */               localObject1 = RMI.getMarshalledObject(localObjectInputStream.readObject());
/*      */             }
/*      */             catch (Throwable localThrowable13)
/*      */             {
/* 1638 */               localObject4 = localThrowable13;throw localThrowable13;
/*      */             }
/*      */             finally
/*      */             {
/* 1642 */               if (localObjectInputStream != null) if (localObject4 != null) try { localObjectInputStream.close(); } catch (Throwable localThrowable17) { ((Throwable)localObject4).addSuppressed(localThrowable17); } else localObjectInputStream.close();
/*      */             }
/*      */           }
/*      */           catch (Throwable localThrowable8)
/*      */           {
/* 1638 */             localObject3 = localThrowable8;throw localThrowable8;
/*      */           }
/*      */           finally
/*      */           {
/* 1642 */             if (localObject2 != null) if (localObject3 != null) try { ((ByteArrayInputStream)localObject2).close(); } catch (Throwable localThrowable18) { ((Throwable)localObject3).addSuppressed(localThrowable18); } else ((ByteArrayInputStream)localObject2).close();
/* 1643 */           } } catch (Exception localException) { throw new IOException(localException.getMessage());
/*      */         }
/*      */         
/*      */       }
/* 1647 */       else if (paramDataFlavor.isRepresentationClassSerializable())
/*      */       {
/* 1649 */         ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);localObject3 = null;
/* 1650 */         try { localObject1 = translateStream(localByteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
/*      */         }
/*      */         catch (Throwable localThrowable10)
/*      */         {
/* 1649 */           localObject3 = localThrowable10;throw localThrowable10;
/*      */         } finally {
/* 1651 */           if (localByteArrayInputStream != null) if (localObject3 != null) try { localByteArrayInputStream.close(); } catch (Throwable localThrowable19) { ((Throwable)localObject3).addSuppressed(localThrowable19); } else localByteArrayInputStream.close();
/*      */         }
/*      */       }
/* 1654 */       else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
/* 1655 */         if (!isImageFormat(paramLong)) {
/* 1656 */           throw new IOException("data translation failed");
/*      */         }
/*      */         
/* 1659 */         localObject1 = platformImageBytesToImage(paramArrayOfByte, paramLong);
/*      */       }
/*      */     }
/* 1662 */     if (localObject1 == null) {
/* 1663 */       throw new IOException("data translation failed");
/*      */     }
/*      */     
/* 1666 */     return localObject1;
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
/*      */   public Object translateStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/* 1680 */     Object localObject1 = null;
/*      */     Object localObject2;
/*      */     Object localObject3;
/* 1683 */     if ((isURIListFormat(paramLong)) && 
/* 1684 */       (DataFlavor.javaFileListFlavor.equals(paramDataFlavor)))
/*      */     {
/*      */ 
/* 1687 */       localObject2 = dragQueryURIs(paramInputStream, paramLong, paramTransferable);
/* 1688 */       if (localObject2 == null) {
/* 1689 */         return null;
/*      */       }
/* 1691 */       localObject3 = new ArrayList();
/* 1692 */       for (URI localURI : localObject2) {
/*      */         try {
/* 1694 */           ((ArrayList)localObject3).add(new File(localURI));
/*      */         }
/*      */         catch (IllegalArgumentException localIllegalArgumentException) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1701 */       localObject1 = localObject3;
/*      */     }
/*      */     else
/*      */     {
/* 1705 */       if ((String.class.equals(paramDataFlavor.getRepresentationClass())) && 
/* 1706 */         (isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */       {
/* 1708 */         return translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1714 */       if (DataFlavor.plainTextFlavor.equals(paramDataFlavor)) {
/* 1715 */         localObject1 = new StringReader(translateBytesToString(
/* 1716 */           inputStreamToByteArray(paramInputStream), paramLong, paramTransferable));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1723 */       else if (paramDataFlavor.isRepresentationClassInputStream()) {
/* 1724 */         localObject1 = translateStreamToInputStream(paramInputStream, paramDataFlavor, paramLong, paramTransferable);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1730 */       else if (paramDataFlavor.isRepresentationClassReader()) {
/* 1731 */         if ((!isFlavorCharsetTextType(paramDataFlavor)) || (!isTextFormat(paramLong))) {
/* 1732 */           throw new IOException("cannot transfer non-text data as Reader");
/*      */         }
/*      */         
/*      */ 
/* 1736 */         localObject2 = (InputStream)translateStreamToInputStream(paramInputStream, DataFlavor.plainTextFlavor, paramLong, paramTransferable);
/*      */         
/*      */ 
/*      */ 
/* 1740 */         localObject3 = getTextCharset(DataFlavor.plainTextFlavor);
/*      */         
/* 1742 */         ??? = new InputStreamReader((InputStream)localObject2, (String)localObject3);
/*      */         
/* 1744 */         localObject1 = constructFlavoredObject(???, paramDataFlavor, Reader.class);
/*      */       }
/* 1746 */       else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
/* 1747 */         if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */         {
/* 1749 */           localObject1 = translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
/*      */         } else {
/* 1751 */           localObject1 = inputStreamToByteArray(paramInputStream);
/*      */         }
/*      */       }
/* 1754 */       else if (paramDataFlavor.isRepresentationClassRemote()) {
/*      */         try {
/* 1756 */           localObject2 = new ObjectInputStream(paramInputStream);localObject3 = null;
/*      */           try
/*      */           {
/* 1759 */             localObject1 = RMI.getMarshalledObject(((ObjectInputStream)localObject2).readObject());
/*      */           }
/*      */           catch (Throwable localThrowable2)
/*      */           {
/* 1756 */             localObject3 = localThrowable2;throw localThrowable2;
/*      */           }
/*      */           finally
/*      */           {
/* 1760 */             if (localObject2 != null) if (localObject3 != null) try { ((ObjectInputStream)localObject2).close(); } catch (Throwable localThrowable5) { ((Throwable)localObject3).addSuppressed(localThrowable5); } else ((ObjectInputStream)localObject2).close();
/* 1761 */           } } catch (Exception localException1) { throw new IOException(localException1.getMessage());
/*      */         }
/*      */         
/*      */       }
/* 1765 */       else if (paramDataFlavor.isRepresentationClassSerializable()) {
/* 1766 */         try { ObjectInputStream localObjectInputStream = new ObjectInputStream(paramInputStream);localObject3 = null;
/*      */           try
/*      */           {
/* 1769 */             localObject1 = localObjectInputStream.readObject();
/*      */           }
/*      */           catch (Throwable localThrowable4)
/*      */           {
/* 1766 */             localObject3 = localThrowable4;throw localThrowable4;
/*      */           }
/*      */           finally
/*      */           {
/* 1770 */             if (localObjectInputStream != null) if (localObject3 != null) try { localObjectInputStream.close(); } catch (Throwable localThrowable6) { ((Throwable)localObject3).addSuppressed(localThrowable6); } else localObjectInputStream.close();
/* 1771 */           } } catch (Exception localException2) { throw new IOException(localException2.getMessage());
/*      */         }
/*      */       }
/* 1774 */       else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
/* 1775 */         if (!isImageFormat(paramLong)) {
/* 1776 */           throw new IOException("data translation failed");
/*      */         }
/* 1778 */         localObject1 = platformImageBytesToImage(inputStreamToByteArray(paramInputStream), paramLong);
/*      */       }
/*      */     }
/* 1781 */     if (localObject1 == null) {
/* 1782 */       throw new IOException("data translation failed");
/*      */     }
/*      */     
/* 1785 */     return localObject1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object translateStreamToInputStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/* 1798 */     if ((isFlavorCharsetTextType(paramDataFlavor)) && (isTextFormat(paramLong)))
/*      */     {
/* 1800 */       paramInputStream = new ReencodingInputStream(paramInputStream, paramLong, getTextCharset(paramDataFlavor), paramTransferable);
/*      */     }
/*      */     
/*      */ 
/* 1804 */     return constructFlavoredObject(paramInputStream, paramDataFlavor, InputStream.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object constructFlavoredObject(Object paramObject, DataFlavor paramDataFlavor, Class paramClass)
/*      */     throws IOException
/*      */   {
/* 1816 */     final Class localClass = paramDataFlavor.getRepresentationClass();
/*      */     
/* 1818 */     if (paramClass.equals(localClass)) {
/* 1819 */       return paramObject;
/*      */     }
/* 1821 */     Constructor[] arrayOfConstructor = null;
/*      */     
/*      */     try
/*      */     {
/* 1825 */       arrayOfConstructor = (Constructor[])AccessController.doPrivileged(new PrivilegedAction() {
/*      */         public Object run() {
/* 1827 */           return localClass.getConstructors();
/*      */         }
/*      */       });
/*      */     } catch (SecurityException localSecurityException) {
/* 1831 */       throw new IOException(localSecurityException.getMessage());
/*      */     }
/*      */     
/* 1834 */     Constructor localConstructor = null;
/*      */     
/* 1836 */     for (int i = 0; i < arrayOfConstructor.length; i++) {
/* 1837 */       if (Modifier.isPublic(arrayOfConstructor[i].getModifiers()))
/*      */       {
/*      */ 
/*      */ 
/* 1841 */         Class[] arrayOfClass = arrayOfConstructor[i].getParameterTypes();
/*      */         
/* 1843 */         if ((arrayOfClass != null) && (arrayOfClass.length == 1) && 
/* 1844 */           (paramClass.equals(arrayOfClass[0]))) {
/* 1845 */           localConstructor = arrayOfConstructor[i];
/* 1846 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1850 */     if (localConstructor == null)
/*      */     {
/* 1852 */       throw new IOException("can't find <init>(L" + paramClass + ";)V for class: " + localClass.getName());
/*      */     }
/*      */     try
/*      */     {
/* 1856 */       return localConstructor.newInstance(new Object[] { paramObject });
/*      */     } catch (Exception localException) {
/* 1858 */       throw new IOException(localException.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public class ReencodingInputStream
/*      */     extends InputStream
/*      */   {
/*      */     protected BufferedReader wrapped;
/*      */     
/* 1869 */     protected final char[] in = new char[2];
/*      */     
/*      */     protected byte[] out;
/*      */     
/*      */     protected CharsetEncoder encoder;
/*      */     
/*      */     protected CharBuffer inBuf;
/*      */     
/*      */     protected ByteBuffer outBuf;
/*      */     protected char[] eoln;
/*      */     protected int numTerminators;
/*      */     protected boolean eos;
/*      */     protected int index;
/*      */     protected int limit;
/*      */     
/*      */     public ReencodingInputStream(InputStream paramInputStream, long paramLong, String paramString, Transferable paramTransferable)
/*      */       throws IOException
/*      */     {
/* 1887 */       Long localLong = Long.valueOf(paramLong);
/*      */       
/* 1889 */       String str1 = null;
/* 1890 */       if ((DataTransferer.this.isLocaleDependentTextFormat(paramLong)) && (paramTransferable != null))
/*      */       {
/*      */ 
/* 1893 */         if (paramTransferable.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor))
/*      */         {
/*      */           try
/*      */           {
/* 1897 */             str1 = new String((byte[])paramTransferable.getTransferData(DataTransferer.javaTextEncodingFlavor), "UTF-8");
/*      */           }
/*      */           catch (UnsupportedFlavorException localUnsupportedFlavorException) {}
/*      */         }
/*      */       }
/* 1902 */       str1 = DataTransferer.this.getCharsetForTextFormat(localLong);
/*      */       
/*      */ 
/* 1905 */       if (str1 == null)
/*      */       {
/* 1907 */         str1 = DataTransferer.getDefaultTextCharset();
/*      */       }
/* 1909 */       this.wrapped = new BufferedReader(new InputStreamReader(paramInputStream, str1));
/*      */       
/*      */ 
/* 1912 */       if (paramString == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1917 */         throw new NullPointerException("null target encoding");
/*      */       }
/*      */       try
/*      */       {
/* 1921 */         this.encoder = Charset.forName(paramString).newEncoder();
/* 1922 */         this.out = new byte[(int)(this.encoder.maxBytesPerChar() * 2.0F + 0.5D)];
/* 1923 */         this.inBuf = CharBuffer.wrap(this.in);
/* 1924 */         this.outBuf = ByteBuffer.wrap(this.out);
/*      */       } catch (IllegalCharsetNameException localIllegalCharsetNameException) {
/* 1926 */         throw new IOException(localIllegalCharsetNameException.toString());
/*      */       } catch (UnsupportedCharsetException localUnsupportedCharsetException) {
/* 1928 */         throw new IOException(localUnsupportedCharsetException.toString());
/*      */       } catch (UnsupportedOperationException localUnsupportedOperationException) {
/* 1930 */         throw new IOException(localUnsupportedOperationException.toString());
/*      */       }
/*      */       
/* 1933 */       String str2 = (String)DataTransferer.nativeEOLNs.get(localLong);
/* 1934 */       if (str2 != null) {
/* 1935 */         this.eoln = str2.toCharArray();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1940 */       Integer localInteger = (Integer)DataTransferer.nativeTerminators.get(localLong);
/* 1941 */       if (localInteger != null) {
/* 1942 */         this.numTerminators = localInteger.intValue();
/*      */       }
/*      */     }
/*      */     
/*      */     private int readChar() throws IOException {
/* 1947 */       int i = this.wrapped.read();
/*      */       
/* 1949 */       if (i == -1) {
/* 1950 */         this.eos = true;
/* 1951 */         return -1;
/*      */       }
/*      */       
/*      */ 
/* 1955 */       if ((this.numTerminators > 0) && (i == 0)) {
/* 1956 */         this.eos = true;
/* 1957 */         return -1; }
/* 1958 */       if ((this.eoln != null) && (matchCharArray(this.eoln, i))) {
/* 1959 */         i = 10;
/*      */       }
/*      */       
/* 1962 */       return i;
/*      */     }
/*      */     
/*      */     public int read() throws IOException {
/* 1966 */       if (this.eos) {
/* 1967 */         return -1;
/*      */       }
/*      */       
/* 1970 */       if (this.index >= this.limit)
/*      */       {
/* 1972 */         int i = readChar();
/* 1973 */         if (i == -1) {
/* 1974 */           return -1;
/*      */         }
/*      */         
/* 1977 */         this.in[0] = ((char)i);
/* 1978 */         this.in[1] = '\000';
/* 1979 */         this.inBuf.limit(1);
/* 1980 */         if (Character.isHighSurrogate((char)i)) {
/* 1981 */           i = readChar();
/* 1982 */           if (i != -1) {
/* 1983 */             this.in[1] = ((char)i);
/* 1984 */             this.inBuf.limit(2);
/*      */           }
/*      */         }
/*      */         
/* 1988 */         this.inBuf.rewind();
/* 1989 */         this.outBuf.limit(this.out.length).rewind();
/* 1990 */         this.encoder.encode(this.inBuf, this.outBuf, false);
/* 1991 */         this.outBuf.flip();
/* 1992 */         this.limit = this.outBuf.limit();
/*      */         
/* 1994 */         this.index = 0;
/*      */         
/* 1996 */         return read();
/*      */       }
/* 1998 */       return this.out[(this.index++)] & 0xFF;
/*      */     }
/*      */     
/*      */     public int available() throws IOException
/*      */     {
/* 2003 */       return this.eos ? 0 : this.limit - this.index;
/*      */     }
/*      */     
/*      */     public void close() throws IOException {
/* 2007 */       this.wrapped.close();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean matchCharArray(char[] paramArrayOfChar, int paramInt)
/*      */       throws IOException
/*      */     {
/* 2020 */       this.wrapped.mark(paramArrayOfChar.length);
/*      */       
/* 2022 */       int i = 0;
/* 2023 */       if ((char)paramInt == paramArrayOfChar[0]) {
/* 2024 */         for (i = 1; i < paramArrayOfChar.length; i++) {
/* 2025 */           paramInt = this.wrapped.read();
/* 2026 */           if ((paramInt == -1) || ((char)paramInt != paramArrayOfChar[i])) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2032 */       if (i == paramArrayOfChar.length) {
/* 2033 */         return true;
/*      */       }
/* 2035 */       this.wrapped.reset();
/* 2036 */       return false;
/*      */     }
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
/*      */   protected URI[] dragQueryURIs(InputStream paramInputStream, long paramLong, Transferable paramTransferable)
/*      */     throws IOException
/*      */   {
/* 2054 */     throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Image standardImageBytesToImage(byte[] paramArrayOfByte, String paramString)
/*      */     throws IOException
/*      */   {
/* 2077 */     Iterator localIterator = ImageIO.getImageReadersByMIMEType(paramString);
/*      */     
/* 2079 */     if (!localIterator.hasNext()) {
/* 2080 */       throw new IOException("No registered service provider can decode  an image from " + paramString);
/*      */     }
/*      */     
/*      */ 
/* 2084 */     Object localObject1 = null;
/*      */     
/* 2086 */     while (localIterator.hasNext()) {
/* 2087 */       ImageReader localImageReader = (ImageReader)localIterator.next();
/* 2088 */       try { ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);Object localObject2 = null;
/*      */         try {
/* 2090 */           ImageInputStream localImageInputStream = ImageIO.createImageInputStream(localByteArrayInputStream);
/*      */           try
/*      */           {
/* 2093 */             ImageReadParam localImageReadParam = localImageReader.getDefaultReadParam();
/* 2094 */             localImageReader.setInput(localImageInputStream, true, true);
/*      */             
/* 2096 */             BufferedImage localBufferedImage1 = localImageReader.read(localImageReader.getMinIndex(), localImageReadParam);
/* 2097 */             if (localBufferedImage1 != null) {
/* 2098 */               BufferedImage localBufferedImage2 = localBufferedImage1;
/*      */               
/*      */ 
/* 2101 */               localImageInputStream.close();
/* 2102 */               localImageReader.dispose();return localBufferedImage2;
/*      */             }
/*      */           } finally {
/* 2101 */             localImageInputStream.close();
/* 2102 */             localImageReader.dispose();
/*      */           }
/*      */         }
/*      */         catch (Throwable localThrowable2)
/*      */         {
/* 2088 */           localObject2 = localThrowable2;throw localThrowable2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2104 */           if (localByteArrayInputStream != null) if (localObject2 != null) try { localByteArrayInputStream.close(); } catch (Throwable localThrowable4) { ((Throwable)localObject2).addSuppressed(localThrowable4); } else localByteArrayInputStream.close();
/* 2105 */         } } catch (IOException localIOException) { localObject1 = localIOException;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2110 */     if (localObject1 == null) {
/* 2111 */       localObject1 = new IOException("Registered service providers failed to decode an image from " + paramString);
/*      */     }
/*      */     
/*      */ 
/* 2115 */     throw ((Throwable)localObject1);
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
/*      */ 
/*      */   protected byte[] imageToStandardBytes(Image paramImage, String paramString)
/*      */     throws IOException
/*      */   {
/* 2133 */     Object localObject1 = null;
/*      */     
/* 2135 */     Iterator localIterator = ImageIO.getImageWritersByMIMEType(paramString);
/*      */     
/* 2137 */     if (!localIterator.hasNext()) {
/* 2138 */       throw new IOException("No registered service provider can encode  an image to " + paramString);
/*      */     }
/*      */     
/*      */ 
/* 2142 */     if ((paramImage instanceof RenderedImage)) {
/*      */       try
/*      */       {
/* 2145 */         return imageToStandardBytesImpl((RenderedImage)paramImage, paramString);
/*      */       } catch (IOException localIOException1) {
/* 2147 */         localObject1 = localIOException1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2152 */     int i = 0;
/* 2153 */     int j = 0;
/* 2154 */     if ((paramImage instanceof ToolkitImage)) {
/* 2155 */       localObject2 = ((ToolkitImage)paramImage).getImageRep();
/* 2156 */       ((ImageRepresentation)localObject2).reconstruct(32);
/* 2157 */       i = ((ImageRepresentation)localObject2).getWidth();
/* 2158 */       j = ((ImageRepresentation)localObject2).getHeight();
/*      */     } else {
/* 2160 */       i = paramImage.getWidth(null);
/* 2161 */       j = paramImage.getHeight(null);
/*      */     }
/*      */     
/* 2164 */     Object localObject2 = ColorModel.getRGBdefault();
/*      */     
/* 2166 */     WritableRaster localWritableRaster = ((ColorModel)localObject2).createCompatibleWritableRaster(i, j);
/*      */     
/*      */ 
/* 2169 */     BufferedImage localBufferedImage = new BufferedImage((ColorModel)localObject2, localWritableRaster, ((ColorModel)localObject2).isAlphaPremultiplied(), null);
/*      */     
/*      */ 
/* 2172 */     Graphics localGraphics = localBufferedImage.getGraphics();
/*      */     try {
/* 2174 */       localGraphics.drawImage(paramImage, 0, 0, i, j, null);
/*      */     } finally {
/* 2176 */       localGraphics.dispose();
/*      */     }
/*      */     try
/*      */     {
/* 2180 */       return imageToStandardBytesImpl(localBufferedImage, paramString);
/*      */     } catch (IOException localIOException2) {
/* 2182 */       if (localObject1 != null) {
/* 2183 */         throw ((Throwable)localObject1);
/*      */       }
/* 2185 */       throw localIOException2;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] imageToStandardBytesImpl(RenderedImage paramRenderedImage, String paramString)
/*      */     throws IOException
/*      */   {
/* 2194 */     Iterator localIterator = ImageIO.getImageWritersByMIMEType(paramString);
/*      */     
/* 2196 */     ImageTypeSpecifier localImageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
/*      */     
/*      */ 
/* 2199 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
/* 2200 */     Object localObject1 = null;
/*      */     
/* 2202 */     while (localIterator.hasNext()) {
/* 2203 */       ImageWriter localImageWriter = (ImageWriter)localIterator.next();
/* 2204 */       ImageWriterSpi localImageWriterSpi = localImageWriter.getOriginatingProvider();
/*      */       
/* 2206 */       if (localImageWriterSpi.canEncodeImage(localImageTypeSpecifier))
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/* 2212 */           ImageOutputStream localImageOutputStream = ImageIO.createImageOutputStream(localByteArrayOutputStream);
/*      */           try {
/* 2214 */             localImageWriter.setOutput(localImageOutputStream);
/* 2215 */             localImageWriter.write(paramRenderedImage);
/* 2216 */             localImageOutputStream.flush();
/*      */           } finally {
/* 2218 */             localImageOutputStream.close();
/*      */           }
/*      */         } catch (IOException localIOException) {
/* 2221 */           localImageWriter.dispose();
/* 2222 */           localByteArrayOutputStream.reset();
/* 2223 */           localObject1 = localIOException; }
/* 2224 */         continue;
/*      */         
/*      */ 
/* 2227 */         localImageWriter.dispose();
/* 2228 */         localByteArrayOutputStream.close();
/* 2229 */         return localByteArrayOutputStream.toByteArray();
/*      */       }
/*      */     }
/* 2232 */     localByteArrayOutputStream.close();
/*      */     
/* 2234 */     if (localObject1 == null) {
/* 2235 */       localObject1 = new IOException("Registered service providers failed to encode " + paramRenderedImage + " to " + paramString);
/*      */     }
/*      */     
/*      */ 
/* 2239 */     throw ((Throwable)localObject1);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object concatData(Object paramObject1, Object paramObject2)
/*      */   {
/* 2261 */     Object localObject1 = null;
/* 2262 */     Object localObject2 = null;
/*      */     
/* 2264 */     if ((paramObject1 instanceof byte[])) {
/* 2265 */       byte[] arrayOfByte1 = (byte[])paramObject1;
/* 2266 */       if ((paramObject2 instanceof byte[])) {
/* 2267 */         byte[] arrayOfByte2 = (byte[])paramObject2;
/* 2268 */         byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
/* 2269 */         System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
/* 2270 */         System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
/* 2271 */         return arrayOfByte3;
/*      */       }
/* 2273 */       localObject1 = new ByteArrayInputStream(arrayOfByte1);
/* 2274 */       localObject2 = (InputStream)paramObject2;
/*      */     }
/*      */     else {
/* 2277 */       localObject1 = (InputStream)paramObject1;
/* 2278 */       if ((paramObject2 instanceof byte[])) {
/* 2279 */         localObject2 = new ByteArrayInputStream((byte[])paramObject2);
/*      */       } else {
/* 2281 */         localObject2 = (InputStream)paramObject2;
/*      */       }
/*      */     }
/*      */     
/* 2285 */     return new SequenceInputStream((InputStream)localObject1, (InputStream)localObject2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] convertData(Object paramObject, Transferable paramTransferable, final long paramLong, final Map paramMap, boolean paramBoolean)
/*      */     throws IOException
/*      */   {
/* 2295 */     byte[] arrayOfByte = null;
/*      */     
/*      */ 
/*      */ 
/*      */     final Object localObject1;
/*      */     
/*      */ 
/*      */ 
/* 2303 */     if (paramBoolean) {
/* 2304 */       try { localObject1 = new Stack();
/* 2305 */         Runnable local5 = new Runnable()
/*      */         {
/* 2307 */           private boolean done = false;
/*      */           
/*      */           /* Error */
/*      */           public void run()
/*      */           {
/*      */             // Byte code:
/*      */             //   0: aload_0
/*      */             //   1: getfield 80	sun/awt/datatransfer/DataTransferer$5:done	Z
/*      */             //   4: ifeq +4 -> 8
/*      */             //   7: return
/*      */             //   8: aconst_null
/*      */             //   9: astore_1
/*      */             //   10: aload_0
/*      */             //   11: getfield 82	sun/awt/datatransfer/DataTransferer$5:val$formatMap	Ljava/util/Map;
/*      */             //   14: aload_0
/*      */             //   15: getfield 79	sun/awt/datatransfer/DataTransferer$5:val$format	J
/*      */             //   18: invokestatic 86	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */             //   21: invokeinterface 91 2 0
/*      */             //   26: checkcast 42	java/awt/datatransfer/DataFlavor
/*      */             //   29: astore_2
/*      */             //   30: aload_2
/*      */             //   31: ifnull +20 -> 51
/*      */             //   34: aload_0
/*      */             //   35: getfield 84	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
/*      */             //   38: aload_0
/*      */             //   39: getfield 81	sun/awt/datatransfer/DataTransferer$5:val$contents	Ljava/awt/datatransfer/Transferable;
/*      */             //   42: aload_2
/*      */             //   43: aload_0
/*      */             //   44: getfield 79	sun/awt/datatransfer/DataTransferer$5:val$format	J
/*      */             //   47: invokevirtual 90	sun/awt/datatransfer/DataTransferer:translateTransferable	(Ljava/awt/datatransfer/Transferable;Ljava/awt/datatransfer/DataFlavor;J)[B
/*      */             //   50: astore_1
/*      */             //   51: goto +10 -> 61
/*      */             //   54: astore_2
/*      */             //   55: aload_2
/*      */             //   56: invokevirtual 85	java/lang/Exception:printStackTrace	()V
/*      */             //   59: aconst_null
/*      */             //   60: astore_1
/*      */             //   61: aload_0
/*      */             //   62: getfield 84	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
/*      */             //   65: invokevirtual 89	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */             //   68: invokeinterface 93 1 0
/*      */             //   73: aload_0
/*      */             //   74: getfield 83	sun/awt/datatransfer/DataTransferer$5:val$stack	Ljava/util/Stack;
/*      */             //   77: aload_1
/*      */             //   78: invokevirtual 88	java/util/Stack:push	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */             //   81: pop
/*      */             //   82: aload_0
/*      */             //   83: getfield 84	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
/*      */             //   86: invokevirtual 89	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */             //   89: invokeinterface 92 1 0
/*      */             //   94: aload_0
/*      */             //   95: getfield 84	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
/*      */             //   98: invokevirtual 89	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */             //   101: invokeinterface 94 1 0
/*      */             //   106: aload_0
/*      */             //   107: iconst_1
/*      */             //   108: putfield 80	sun/awt/datatransfer/DataTransferer$5:done	Z
/*      */             //   111: goto +23 -> 134
/*      */             //   114: astore_3
/*      */             //   115: aload_0
/*      */             //   116: getfield 84	sun/awt/datatransfer/DataTransferer$5:this$0	Lsun/awt/datatransfer/DataTransferer;
/*      */             //   119: invokevirtual 89	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */             //   122: invokeinterface 94 1 0
/*      */             //   127: aload_0
/*      */             //   128: iconst_1
/*      */             //   129: putfield 80	sun/awt/datatransfer/DataTransferer$5:done	Z
/*      */             //   132: aload_3
/*      */             //   133: athrow
/*      */             //   134: return
/*      */             // Line number table:
/*      */             //   Java source line #2309	-> byte code offset #0
/*      */             //   Java source line #2310	-> byte code offset #7
/*      */             //   Java source line #2312	-> byte code offset #8
/*      */             //   Java source line #2314	-> byte code offset #10
/*      */             //   Java source line #2315	-> byte code offset #30
/*      */             //   Java source line #2316	-> byte code offset #34
/*      */             //   Java source line #2321	-> byte code offset #51
/*      */             //   Java source line #2318	-> byte code offset #54
/*      */             //   Java source line #2319	-> byte code offset #55
/*      */             //   Java source line #2320	-> byte code offset #59
/*      */             //   Java source line #2323	-> byte code offset #61
/*      */             //   Java source line #2324	-> byte code offset #73
/*      */             //   Java source line #2325	-> byte code offset #82
/*      */             //   Java source line #2327	-> byte code offset #94
/*      */             //   Java source line #2328	-> byte code offset #106
/*      */             //   Java source line #2329	-> byte code offset #111
/*      */             //   Java source line #2327	-> byte code offset #114
/*      */             //   Java source line #2328	-> byte code offset #127
/*      */             //   Java source line #2329	-> byte code offset #132
/*      */             //   Java source line #2330	-> byte code offset #134
/*      */             // Local variable table:
/*      */             //   start	length	slot	name	signature
/*      */             //   0	135	0	this	5
/*      */             //   9	69	1	arrayOfByte	byte[]
/*      */             //   29	14	2	localDataFlavor	DataFlavor
/*      */             //   54	2	2	localException	Exception
/*      */             //   114	19	3	localObject	Object
/*      */             // Exception table:
/*      */             //   from	to	target	type
/*      */             //   10	51	54	java/lang/Exception
/*      */             //   61	94	114	finally
/*      */           }
/* 2332 */         };
/* 2333 */         AppContext localAppContext = SunToolkit.targetToAppContext(paramObject);
/*      */         
/* 2335 */         getToolkitThreadBlockedHandler().lock();
/*      */         
/* 2337 */         if (localAppContext != null) {
/* 2338 */           localAppContext.put("DATA_CONVERTER_KEY", local5);
/*      */         }
/*      */         
/* 2341 */         SunToolkit.executeOnEventHandlerThread(paramObject, local5);
/*      */         
/* 2343 */         while (((Stack)localObject1).empty()) {
/* 2344 */           getToolkitThreadBlockedHandler().enter();
/*      */         }
/*      */         
/* 2347 */         if (localAppContext != null) {
/* 2348 */           localAppContext.remove("DATA_CONVERTER_KEY");
/*      */         }
/*      */         
/* 2351 */         arrayOfByte = (byte[])((Stack)localObject1).pop();
/*      */       } finally {
/* 2353 */         getToolkitThreadBlockedHandler().unlock();
/*      */       }
/*      */     } else {
/* 2356 */       localObject1 = (DataFlavor)paramMap.get(Long.valueOf(paramLong));
/* 2357 */       if (localObject1 != null) {
/* 2358 */         arrayOfByte = translateTransferable(paramTransferable, (DataFlavor)localObject1, paramLong);
/*      */       }
/*      */     }
/*      */     
/* 2362 */     return arrayOfByte;
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
/*      */   public static long[] keysToLongArray(SortedMap paramSortedMap)
/*      */   {
/* 2392 */     Set localSet = paramSortedMap.keySet();
/* 2393 */     long[] arrayOfLong = new long[localSet.size()];
/* 2394 */     int i = 0;
/* 2395 */     for (Iterator localIterator = localSet.iterator(); localIterator.hasNext(); i++) {
/* 2396 */       arrayOfLong[i] = ((Long)localIterator.next()).longValue();
/*      */     }
/* 2398 */     return arrayOfLong;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DataFlavor[] setToSortedDataFlavorArray(Set paramSet)
/*      */   {
/* 2406 */     DataFlavor[] arrayOfDataFlavor = new DataFlavor[paramSet.size()];
/* 2407 */     paramSet.toArray(arrayOfDataFlavor);
/* 2408 */     DataFlavorComparator localDataFlavorComparator = new DataFlavorComparator(false);
/*      */     
/* 2410 */     Arrays.sort(arrayOfDataFlavor, localDataFlavorComparator);
/* 2411 */     return arrayOfDataFlavor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static byte[] inputStreamToByteArray(InputStream paramInputStream)
/*      */     throws IOException
/*      */   {
/* 2420 */     ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();Object localObject1 = null;
/* 2421 */     try { int i = 0;
/* 2422 */       byte[] arrayOfByte1 = new byte[' '];
/*      */       
/* 2424 */       while ((i = paramInputStream.read(arrayOfByte1)) != -1) {
/* 2425 */         localByteArrayOutputStream.write(arrayOfByte1, 0, i);
/*      */       }
/*      */       
/* 2428 */       return localByteArrayOutputStream.toByteArray();
/*      */     }
/*      */     catch (Throwable localThrowable1)
/*      */     {
/* 2420 */       localObject1 = localThrowable1;throw localThrowable1;
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/* 2429 */       if (localByteArrayOutputStream != null) { if (localObject1 != null) try { localByteArrayOutputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else { localByteArrayOutputStream.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String paramString)
/*      */   {
/* 2438 */     return new LinkedHashSet();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor paramDataFlavor)
/*      */   {
/* 2447 */     return new LinkedHashSet();
/*      */   }
/*      */   
/*      */   public abstract String getDefaultUnicodeEncoding();
/*      */   
/*      */   public abstract boolean isLocaleDependentTextFormat(long paramLong);
/*      */   
/*      */   public abstract boolean isFileFormat(long paramLong);
/*      */   
/*      */   public abstract boolean isImageFormat(long paramLong);
/*      */   
/*      */   protected abstract Long getFormatForNativeAsLong(String paramString);
/*      */   
/*      */   protected abstract String getNativeForFormat(long paramLong);
/*      */   
/*      */   /* Error */
/*      */   private static byte[] convertObjectToBytes(Object paramObject)
/*      */     throws IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: new 470	java/io/ByteArrayOutputStream
/*      */     //   3: dup
/*      */     //   4: invokespecial 969	java/io/ByteArrayOutputStream:<init>	()V
/*      */     //   7: astore_1
/*      */     //   8: aconst_null
/*      */     //   9: astore_2
/*      */     //   10: new 476	java/io/ObjectOutputStream
/*      */     //   13: dup
/*      */     //   14: aload_1
/*      */     //   15: invokespecial 994	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
/*      */     //   18: astore_3
/*      */     //   19: aconst_null
/*      */     //   20: astore 4
/*      */     //   22: aload_3
/*      */     //   23: aload_0
/*      */     //   24: invokevirtual 995	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
/*      */     //   27: aload_1
/*      */     //   28: invokevirtual 972	java/io/ByteArrayOutputStream:toByteArray	()[B
/*      */     //   31: astore 5
/*      */     //   33: aload_3
/*      */     //   34: ifnull +31 -> 65
/*      */     //   37: aload 4
/*      */     //   39: ifnull +22 -> 61
/*      */     //   42: aload_3
/*      */     //   43: invokevirtual 993	java/io/ObjectOutputStream:close	()V
/*      */     //   46: goto +19 -> 65
/*      */     //   49: astore 6
/*      */     //   51: aload 4
/*      */     //   53: aload 6
/*      */     //   55: invokevirtual 1034	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*      */     //   58: goto +7 -> 65
/*      */     //   61: aload_3
/*      */     //   62: invokevirtual 993	java/io/ObjectOutputStream:close	()V
/*      */     //   65: aload_1
/*      */     //   66: ifnull +29 -> 95
/*      */     //   69: aload_2
/*      */     //   70: ifnull +21 -> 91
/*      */     //   73: aload_1
/*      */     //   74: invokevirtual 970	java/io/ByteArrayOutputStream:close	()V
/*      */     //   77: goto +18 -> 95
/*      */     //   80: astore 6
/*      */     //   82: aload_2
/*      */     //   83: aload 6
/*      */     //   85: invokevirtual 1034	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*      */     //   88: goto +7 -> 95
/*      */     //   91: aload_1
/*      */     //   92: invokevirtual 970	java/io/ByteArrayOutputStream:close	()V
/*      */     //   95: aload 5
/*      */     //   97: areturn
/*      */     //   98: astore 5
/*      */     //   100: aload 5
/*      */     //   102: astore 4
/*      */     //   104: aload 5
/*      */     //   106: athrow
/*      */     //   107: astore 7
/*      */     //   109: aload_3
/*      */     //   110: ifnull +31 -> 141
/*      */     //   113: aload 4
/*      */     //   115: ifnull +22 -> 137
/*      */     //   118: aload_3
/*      */     //   119: invokevirtual 993	java/io/ObjectOutputStream:close	()V
/*      */     //   122: goto +19 -> 141
/*      */     //   125: astore 8
/*      */     //   127: aload 4
/*      */     //   129: aload 8
/*      */     //   131: invokevirtual 1034	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*      */     //   134: goto +7 -> 141
/*      */     //   137: aload_3
/*      */     //   138: invokevirtual 993	java/io/ObjectOutputStream:close	()V
/*      */     //   141: aload 7
/*      */     //   143: athrow
/*      */     //   144: astore_3
/*      */     //   145: aload_3
/*      */     //   146: astore_2
/*      */     //   147: aload_3
/*      */     //   148: athrow
/*      */     //   149: astore 9
/*      */     //   151: aload_1
/*      */     //   152: ifnull +29 -> 181
/*      */     //   155: aload_2
/*      */     //   156: ifnull +21 -> 177
/*      */     //   159: aload_1
/*      */     //   160: invokevirtual 970	java/io/ByteArrayOutputStream:close	()V
/*      */     //   163: goto +18 -> 181
/*      */     //   166: astore 10
/*      */     //   168: aload_2
/*      */     //   169: aload 10
/*      */     //   171: invokevirtual 1034	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*      */     //   174: goto +7 -> 181
/*      */     //   177: aload_1
/*      */     //   178: invokevirtual 970	java/io/ByteArrayOutputStream:close	()V
/*      */     //   181: aload 9
/*      */     //   183: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1353	-> byte code offset #0
/*      */     //   Java source line #1354	-> byte code offset #10
/*      */     //   Java source line #1353	-> byte code offset #19
/*      */     //   Java source line #1356	-> byte code offset #22
/*      */     //   Java source line #1357	-> byte code offset #27
/*      */     //   Java source line #1358	-> byte code offset #33
/*      */     //   Java source line #1357	-> byte code offset #95
/*      */     //   Java source line #1353	-> byte code offset #98
/*      */     //   Java source line #1358	-> byte code offset #107
/*      */     //   Java source line #1353	-> byte code offset #144
/*      */     //   Java source line #1358	-> byte code offset #149
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	184	0	paramObject	Object
/*      */     //   7	171	1	localByteArrayOutputStream	ByteArrayOutputStream
/*      */     //   9	160	2	localObject1	Object
/*      */     //   18	120	3	localObjectOutputStream	java.io.ObjectOutputStream
/*      */     //   144	4	3	localThrowable1	Throwable
/*      */     //   20	108	4	localObject2	Object
/*      */     //   31	65	5	arrayOfByte	byte[]
/*      */     //   98	7	5	localThrowable2	Throwable
/*      */     //   49	5	6	localThrowable3	Throwable
/*      */     //   80	4	6	localThrowable4	Throwable
/*      */     //   107	35	7	localObject3	Object
/*      */     //   125	5	8	localThrowable5	Throwable
/*      */     //   149	33	9	localObject4	Object
/*      */     //   166	4	10	localThrowable6	Throwable
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   42	46	49	java/lang/Throwable
/*      */     //   73	77	80	java/lang/Throwable
/*      */     //   22	33	98	java/lang/Throwable
/*      */     //   22	33	107	finally
/*      */     //   98	109	107	finally
/*      */     //   118	122	125	java/lang/Throwable
/*      */     //   10	65	144	java/lang/Throwable
/*      */     //   98	144	144	java/lang/Throwable
/*      */     //   10	65	149	finally
/*      */     //   98	151	149	finally
/*      */     //   159	163	166	java/lang/Throwable
/*      */   }
/*      */   
/*      */   protected abstract ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList)
/*      */     throws IOException;
/*      */   
/*      */   protected abstract String[] dragQueryFile(byte[] paramArrayOfByte);
/*      */   
/*      */   protected abstract Image platformImageBytesToImage(byte[] paramArrayOfByte, long paramLong)
/*      */     throws IOException;
/*      */   
/*      */   protected abstract byte[] imageToPlatformBytes(Image paramImage, long paramLong)
/*      */     throws IOException;
/*      */   
/*      */   /* Error */
/*      */   public void processDataConversionRequests()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: invokestatic 940	java/awt/EventQueue:isDispatchThread	()Z
/*      */     //   3: ifeq +69 -> 72
/*      */     //   6: invokestatic 1095	sun/awt/AppContext:getAppContext	()Lsun/awt/AppContext;
/*      */     //   9: astore_1
/*      */     //   10: aload_0
/*      */     //   11: invokevirtual 1119	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */     //   14: invokeinterface 1190 1 0
/*      */     //   19: aload_1
/*      */     //   20: ldc_w 423
/*      */     //   23: invokevirtual 1096	sun/awt/AppContext:get	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   26: checkcast 487	java/lang/Runnable
/*      */     //   29: astore_2
/*      */     //   30: aload_2
/*      */     //   31: ifnull +17 -> 48
/*      */     //   34: aload_2
/*      */     //   35: invokeinterface 1169 1 0
/*      */     //   40: aload_1
/*      */     //   41: ldc_w 423
/*      */     //   44: invokevirtual 1097	sun/awt/AppContext:remove	(Ljava/lang/Object;)Ljava/lang/Object;
/*      */     //   47: pop
/*      */     //   48: aload_0
/*      */     //   49: invokevirtual 1119	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */     //   52: invokeinterface 1191 1 0
/*      */     //   57: goto +15 -> 72
/*      */     //   60: astore_3
/*      */     //   61: aload_0
/*      */     //   62: invokevirtual 1119	sun/awt/datatransfer/DataTransferer:getToolkitThreadBlockedHandler	()Lsun/awt/datatransfer/ToolkitThreadBlockedHandler;
/*      */     //   65: invokeinterface 1191 1 0
/*      */     //   70: aload_3
/*      */     //   71: athrow
/*      */     //   72: return
/*      */     // Line number table:
/*      */     //   Java source line #2366	-> byte code offset #0
/*      */     //   Java source line #2367	-> byte code offset #6
/*      */     //   Java source line #2368	-> byte code offset #10
/*      */     //   Java source line #2370	-> byte code offset #19
/*      */     //   Java source line #2371	-> byte code offset #23
/*      */     //   Java source line #2372	-> byte code offset #30
/*      */     //   Java source line #2373	-> byte code offset #34
/*      */     //   Java source line #2374	-> byte code offset #40
/*      */     //   Java source line #2377	-> byte code offset #48
/*      */     //   Java source line #2378	-> byte code offset #57
/*      */     //   Java source line #2377	-> byte code offset #60
/*      */     //   Java source line #2378	-> byte code offset #70
/*      */     //   Java source line #2380	-> byte code offset #72
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	73	0	this	DataTransferer
/*      */     //   9	32	1	localAppContext	AppContext
/*      */     //   29	6	2	localRunnable	Runnable
/*      */     //   60	11	3	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   19	48	60	finally
/*      */   }
/*      */   
/*      */   public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();
/*      */   
/*      */   public static abstract class IndexedComparator
/*      */     implements Comparator
/*      */   {
/*      */     public static final boolean SELECT_BEST = true;
/*      */     public static final boolean SELECT_WORST = false;
/*      */     protected final boolean order;
/*      */     
/*      */     public IndexedComparator()
/*      */     {
/* 2469 */       this(true);
/*      */     }
/*      */     
/*      */     public IndexedComparator(boolean paramBoolean) {
/* 2473 */       this.order = paramBoolean;
/*      */     }
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
/*      */     protected static int compareIndices(Map paramMap, Object paramObject1, Object paramObject2, Integer paramInteger)
/*      */     {
/* 2492 */       Integer localInteger1 = (Integer)paramMap.get(paramObject1);
/* 2493 */       Integer localInteger2 = (Integer)paramMap.get(paramObject2);
/*      */       
/* 2495 */       if (localInteger1 == null) {
/* 2496 */         localInteger1 = paramInteger;
/*      */       }
/* 2498 */       if (localInteger2 == null) {
/* 2499 */         localInteger2 = paramInteger;
/*      */       }
/*      */       
/* 2502 */       return localInteger1.compareTo(localInteger2);
/*      */     }
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
/*      */     protected static int compareLongs(Map paramMap, Object paramObject1, Object paramObject2, Long paramLong)
/*      */     {
/* 2521 */       Long localLong1 = (Long)paramMap.get(paramObject1);
/* 2522 */       Long localLong2 = (Long)paramMap.get(paramObject2);
/*      */       
/* 2524 */       if (localLong1 == null) {
/* 2525 */         localLong1 = paramLong;
/*      */       }
/* 2527 */       if (localLong2 == null) {
/* 2528 */         localLong2 = paramLong;
/*      */       }
/*      */       
/* 2531 */       return localLong1.compareTo(localLong2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class CharsetComparator
/*      */     extends IndexedComparator
/*      */   {
/*      */     private static final Map charsets;
/*      */     
/*      */ 
/*      */     private static String defaultEncoding;
/*      */     
/*      */ 
/* 2546 */     private static final Integer DEFAULT_CHARSET_INDEX = Integer.valueOf(2);
/* 2547 */     private static final Integer OTHER_CHARSET_INDEX = Integer.valueOf(1);
/* 2548 */     private static final Integer WORST_CHARSET_INDEX = Integer.valueOf(0);
/*      */     
/* 2550 */     private static final Integer UNSUPPORTED_CHARSET_INDEX = Integer.valueOf(Integer.MIN_VALUE);
/*      */     private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
/*      */     
/*      */     static
/*      */     {
/* 2555 */       HashMap localHashMap = new HashMap(8, 1.0F);
/*      */       
/*      */ 
/* 2558 */       localHashMap.put(DataTransferer.canonicalName("UTF-16LE"), Integer.valueOf(4));
/* 2559 */       localHashMap.put(DataTransferer.canonicalName("UTF-16BE"), Integer.valueOf(5));
/* 2560 */       localHashMap.put(DataTransferer.canonicalName("UTF-8"), Integer.valueOf(6));
/* 2561 */       localHashMap.put(DataTransferer.canonicalName("UTF-16"), Integer.valueOf(7));
/*      */       
/*      */ 
/* 2564 */       localHashMap.put(DataTransferer.canonicalName("US-ASCII"), WORST_CHARSET_INDEX);
/*      */       
/*      */ 
/* 2567 */       String str = DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
/*      */       
/* 2569 */       if (localHashMap.get(defaultEncoding) == null) {
/* 2570 */         localHashMap.put(defaultEncoding, DEFAULT_CHARSET_INDEX);
/*      */       }
/* 2572 */       localHashMap.put("UNSUPPORTED", UNSUPPORTED_CHARSET_INDEX);
/*      */       
/* 2574 */       charsets = Collections.unmodifiableMap(localHashMap);
/*      */     }
/*      */     
/*      */     public CharsetComparator() {
/* 2578 */       this(true);
/*      */     }
/*      */     
/*      */     public CharsetComparator(boolean paramBoolean) {
/* 2582 */       super();
/*      */     }
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
/*      */     public int compare(Object paramObject1, Object paramObject2)
/*      */     {
/* 2601 */       String str1 = null;
/* 2602 */       String str2 = null;
/* 2603 */       if (this.order == true) {
/* 2604 */         str1 = (String)paramObject1;
/* 2605 */         str2 = (String)paramObject2;
/*      */       } else {
/* 2607 */         str1 = (String)paramObject2;
/* 2608 */         str2 = (String)paramObject1;
/*      */       }
/*      */       
/* 2611 */       return compareCharsets(str1, str2);
/*      */     }
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
/*      */     protected int compareCharsets(String paramString1, String paramString2)
/*      */     {
/* 2638 */       paramString1 = getEncoding(paramString1);
/* 2639 */       paramString2 = getEncoding(paramString2);
/*      */       
/* 2641 */       int i = compareIndices(charsets, paramString1, paramString2, OTHER_CHARSET_INDEX);
/*      */       
/*      */ 
/* 2644 */       if (i == 0) {
/* 2645 */         return paramString2.compareTo(paramString1);
/*      */       }
/*      */       
/* 2648 */       return i;
/*      */     }
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
/*      */     protected static String getEncoding(String paramString)
/*      */     {
/* 2668 */       if (paramString == null)
/* 2669 */         return null;
/* 2670 */       if (!DataTransferer.isEncodingSupported(paramString)) {
/* 2671 */         return "UNSUPPORTED";
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2677 */       String str = DataTransferer.canonicalName(paramString);
/* 2678 */       return charsets.containsKey(str) ? str : paramString;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static class DataFlavorComparator
/*      */     extends IndexedComparator
/*      */   {
/*      */     private final CharsetComparator charsetComparator;
/*      */     
/*      */ 
/*      */     private static final Map exactTypes;
/*      */     
/*      */ 
/*      */     private static final Map primaryTypes;
/*      */     
/*      */ 
/*      */     private static final Map nonTextRepresentations;
/*      */     
/*      */ 
/*      */     private static final Map textTypes;
/*      */     
/*      */ 
/*      */     private static final Map decodedTextRepresentations;
/*      */     
/*      */ 
/*      */     private static final Map encodedTextRepresentations;
/*      */     
/* 2707 */     private static final Integer UNKNOWN_OBJECT_LOSES = Integer.valueOf(Integer.MIN_VALUE);
/*      */     
/* 2709 */     private static final Integer UNKNOWN_OBJECT_WINS = Integer.valueOf(Integer.MAX_VALUE);
/*      */     
/*      */ 
/* 2712 */     private static final Long UNKNOWN_OBJECT_LOSES_L = Long.valueOf(Long.MIN_VALUE);
/*      */     
/* 2714 */     private static final Long UNKNOWN_OBJECT_WINS_L = Long.valueOf(Long.MAX_VALUE);
/*      */     
/*      */     static
/*      */     {
/* 2718 */       HashMap localHashMap = new HashMap(4, 1.0F);
/*      */       
/*      */ 
/* 2721 */       localHashMap.put("application/x-java-file-list", 
/* 2722 */         Integer.valueOf(0));
/* 2723 */       localHashMap.put("application/x-java-serialized-object", 
/* 2724 */         Integer.valueOf(1));
/* 2725 */       localHashMap.put("application/x-java-jvm-local-objectref", 
/* 2726 */         Integer.valueOf(2));
/* 2727 */       localHashMap.put("application/x-java-remote-object", 
/* 2728 */         Integer.valueOf(3));
/*      */       
/* 2730 */       exactTypes = Collections.unmodifiableMap(localHashMap);
/*      */       
/*      */ 
/*      */ 
/* 2734 */       localHashMap = new HashMap(1, 1.0F);
/*      */       
/* 2736 */       localHashMap.put("application", Integer.valueOf(0));
/*      */       
/* 2738 */       primaryTypes = Collections.unmodifiableMap(localHashMap);
/*      */       
/*      */ 
/*      */ 
/* 2742 */       localHashMap = new HashMap(3, 1.0F);
/*      */       
/* 2744 */       localHashMap.put(InputStream.class, 
/* 2745 */         Integer.valueOf(0));
/* 2746 */       localHashMap.put(Serializable.class, 
/* 2747 */         Integer.valueOf(1));
/*      */       
/* 2749 */       Class localClass = RMI.remoteClass();
/* 2750 */       if (localClass != null) {
/* 2751 */         localHashMap.put(localClass, 
/* 2752 */           Integer.valueOf(2));
/*      */       }
/*      */       
/*      */ 
/* 2756 */       nonTextRepresentations = Collections.unmodifiableMap(localHashMap);
/*      */       
/*      */ 
/*      */ 
/* 2760 */       localHashMap = new HashMap(16, 1.0F);
/*      */       
/*      */ 
/* 2763 */       localHashMap.put("text/plain", Integer.valueOf(0));
/*      */       
/*      */ 
/* 2766 */       localHashMap.put("application/x-java-serialized-object", 
/* 2767 */         Integer.valueOf(1));
/*      */       
/*      */ 
/* 2770 */       localHashMap.put("text/calendar", Integer.valueOf(2));
/* 2771 */       localHashMap.put("text/css", Integer.valueOf(3));
/* 2772 */       localHashMap.put("text/directory", Integer.valueOf(4));
/* 2773 */       localHashMap.put("text/parityfec", Integer.valueOf(5));
/* 2774 */       localHashMap.put("text/rfc822-headers", Integer.valueOf(6));
/* 2775 */       localHashMap.put("text/t140", Integer.valueOf(7));
/* 2776 */       localHashMap.put("text/tab-separated-values", Integer.valueOf(8));
/* 2777 */       localHashMap.put("text/uri-list", Integer.valueOf(9));
/*      */       
/*      */ 
/* 2780 */       localHashMap.put("text/richtext", Integer.valueOf(10));
/* 2781 */       localHashMap.put("text/enriched", Integer.valueOf(11));
/* 2782 */       localHashMap.put("text/rtf", Integer.valueOf(12));
/*      */       
/*      */ 
/* 2785 */       localHashMap.put("text/html", Integer.valueOf(13));
/* 2786 */       localHashMap.put("text/xml", Integer.valueOf(14));
/* 2787 */       localHashMap.put("text/sgml", Integer.valueOf(15));
/*      */       
/* 2789 */       textTypes = Collections.unmodifiableMap(localHashMap);
/*      */       
/*      */ 
/*      */ 
/* 2793 */       localHashMap = new HashMap(4, 1.0F);
/*      */       
/* 2795 */       localHashMap
/* 2796 */         .put(char[].class, Integer.valueOf(0));
/* 2797 */       localHashMap
/* 2798 */         .put(CharBuffer.class, Integer.valueOf(1));
/* 2799 */       localHashMap
/* 2800 */         .put(String.class, Integer.valueOf(2));
/* 2801 */       localHashMap
/* 2802 */         .put(Reader.class, Integer.valueOf(3));
/*      */       
/*      */ 
/* 2805 */       decodedTextRepresentations = Collections.unmodifiableMap(localHashMap);
/*      */       
/*      */ 
/*      */ 
/* 2809 */       localHashMap = new HashMap(3, 1.0F);
/*      */       
/* 2811 */       localHashMap
/* 2812 */         .put(byte[].class, Integer.valueOf(0));
/* 2813 */       localHashMap
/* 2814 */         .put(ByteBuffer.class, Integer.valueOf(1));
/* 2815 */       localHashMap
/* 2816 */         .put(InputStream.class, Integer.valueOf(2));
/*      */       
/*      */ 
/* 2819 */       encodedTextRepresentations = Collections.unmodifiableMap(localHashMap);
/*      */     }
/*      */     
/*      */     public DataFlavorComparator()
/*      */     {
/* 2824 */       this(true);
/*      */     }
/*      */     
/*      */     public DataFlavorComparator(boolean paramBoolean) {
/* 2828 */       super();
/*      */       
/* 2830 */       this.charsetComparator = new CharsetComparator(paramBoolean);
/*      */     }
/*      */     
/*      */     public int compare(Object paramObject1, Object paramObject2) {
/* 2834 */       DataFlavor localDataFlavor1 = null;
/* 2835 */       DataFlavor localDataFlavor2 = null;
/* 2836 */       if (this.order == true) {
/* 2837 */         localDataFlavor1 = (DataFlavor)paramObject1;
/* 2838 */         localDataFlavor2 = (DataFlavor)paramObject2;
/*      */       } else {
/* 2840 */         localDataFlavor1 = (DataFlavor)paramObject2;
/* 2841 */         localDataFlavor2 = (DataFlavor)paramObject1;
/*      */       }
/*      */       
/* 2844 */       if (localDataFlavor1.equals(localDataFlavor2)) {
/* 2845 */         return 0;
/*      */       }
/*      */       
/* 2848 */       int i = 0;
/*      */       
/* 2850 */       String str1 = localDataFlavor1.getPrimaryType();
/* 2851 */       String str2 = localDataFlavor1.getSubType();
/* 2852 */       String str3 = str1 + "/" + str2;
/* 2853 */       Class localClass1 = localDataFlavor1.getRepresentationClass();
/*      */       
/* 2855 */       String str4 = localDataFlavor2.getPrimaryType();
/* 2856 */       String str5 = localDataFlavor2.getSubType();
/* 2857 */       String str6 = str4 + "/" + str5;
/* 2858 */       Class localClass2 = localDataFlavor2.getRepresentationClass();
/*      */       
/* 2860 */       if ((localDataFlavor1.isFlavorTextType()) && (localDataFlavor2.isFlavorTextType()))
/*      */       {
/* 2862 */         i = compareIndices(textTypes, str3, str6, UNKNOWN_OBJECT_LOSES);
/*      */         
/* 2864 */         if (i != 0) {
/* 2865 */           return i;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2875 */         if (DataTransferer.doesSubtypeSupportCharset(localDataFlavor1))
/*      */         {
/*      */ 
/* 2878 */           i = compareIndices(decodedTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
/*      */           
/* 2880 */           if (i != 0) {
/* 2881 */             return i;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2886 */           i = this.charsetComparator.compareCharsets(DataTransferer.getTextCharset(localDataFlavor1), 
/* 2887 */             DataTransferer.getTextCharset(localDataFlavor2));
/* 2888 */           if (i != 0) {
/* 2889 */             return i;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2895 */         i = compareIndices(encodedTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
/*      */         
/* 2897 */         if (i != 0) {
/* 2898 */           return i;
/*      */         }
/*      */       }
/*      */       else {
/* 2902 */         if (localDataFlavor1.isFlavorTextType()) {
/* 2903 */           return 1;
/*      */         }
/*      */         
/* 2906 */         if (localDataFlavor2.isFlavorTextType()) {
/* 2907 */           return -1;
/*      */         }
/*      */         
/*      */ 
/* 2911 */         i = compareIndices(primaryTypes, str1, str4, UNKNOWN_OBJECT_LOSES);
/*      */         
/* 2913 */         if (i != 0) {
/* 2914 */           return i;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2920 */         i = compareIndices(exactTypes, str3, str6, UNKNOWN_OBJECT_WINS);
/*      */         
/* 2922 */         if (i != 0) {
/* 2923 */           return i;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2928 */         i = compareIndices(nonTextRepresentations, localClass1, localClass2, UNKNOWN_OBJECT_LOSES);
/*      */         
/* 2930 */         if (i != 0) {
/* 2931 */           return i;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2937 */       return localDataFlavor1.getMimeType().compareTo(localDataFlavor2.getMimeType());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class IndexOrderComparator
/*      */     extends IndexedComparator
/*      */   {
/*      */     private final Map indexMap;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2957 */     private static final Integer FALLBACK_INDEX = Integer.valueOf(Integer.MIN_VALUE);
/*      */     
/*      */     public IndexOrderComparator(Map paramMap) {
/* 2960 */       super();
/* 2961 */       this.indexMap = paramMap;
/*      */     }
/*      */     
/*      */     public IndexOrderComparator(Map paramMap, boolean paramBoolean) {
/* 2965 */       super();
/* 2966 */       this.indexMap = paramMap;
/*      */     }
/*      */     
/*      */     public int compare(Object paramObject1, Object paramObject2) {
/* 2970 */       if (!this.order) {
/* 2971 */         return -compareIndices(this.indexMap, paramObject1, paramObject2, FALLBACK_INDEX);
/*      */       }
/* 2973 */       return compareIndices(this.indexMap, paramObject1, paramObject2, FALLBACK_INDEX);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static class RMI
/*      */   {
/* 2983 */     private static final Class<?> remoteClass = getClass("java.rmi.Remote");
/*      */     
/* 2985 */     private static final Class<?> marshallObjectClass = getClass("java.rmi.MarshalledObject");
/*      */     
/* 2987 */     private static final Constructor<?> marshallCtor = getConstructor(marshallObjectClass, new Class[] { Object.class });
/*      */     
/* 2989 */     private static final Method marshallGet = getMethod(marshallObjectClass, "get", new Class[0]);
/*      */     
/*      */     private static Class<?> getClass(String paramString) {
/*      */       try {
/* 2993 */         return Class.forName(paramString, true, null);
/*      */       } catch (ClassNotFoundException localClassNotFoundException) {}
/* 2995 */       return null;
/*      */     }
/*      */     
/*      */     private static Constructor<?> getConstructor(Class<?> paramClass, Class<?>... paramVarArgs)
/*      */     {
/*      */       try {
/* 3001 */         return paramClass == null ? null : paramClass.getDeclaredConstructor(paramVarArgs);
/*      */       } catch (NoSuchMethodException localNoSuchMethodException) {
/* 3003 */         throw new AssertionError(localNoSuchMethodException);
/*      */       }
/*      */     }
/*      */     
/*      */     private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) {
/*      */       try {
/* 3009 */         return paramClass == null ? null : paramClass.getMethod(paramString, paramVarArgs);
/*      */       } catch (NoSuchMethodException localNoSuchMethodException) {
/* 3011 */         throw new AssertionError(localNoSuchMethodException);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     static boolean isRemote(Class<?> paramClass)
/*      */     {
/* 3019 */       return (remoteClass == null ? null : Boolean.valueOf(remoteClass.isAssignableFrom(paramClass))).booleanValue();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     static Class<?> remoteClass()
/*      */     {
/* 3026 */       return remoteClass;
/*      */     }
/*      */     
/*      */ 
/*      */     static Object newMarshalledObject(Object paramObject)
/*      */       throws IOException
/*      */     {
/*      */       try
/*      */       {
/* 3035 */         return marshallCtor.newInstance(new Object[] { paramObject });
/*      */       } catch (InstantiationException localInstantiationException) {
/* 3037 */         throw new AssertionError(localInstantiationException);
/*      */       } catch (IllegalAccessException localIllegalAccessException) {
/* 3039 */         throw new AssertionError(localIllegalAccessException);
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 3041 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 3042 */         if ((localThrowable instanceof IOException))
/* 3043 */           throw ((IOException)localThrowable);
/* 3044 */         throw new AssertionError(localInvocationTargetException);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     static Object getMarshalledObject(Object paramObject)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*      */       try
/*      */       {
/* 3055 */         return marshallGet.invoke(paramObject, new Object[0]);
/*      */       } catch (IllegalAccessException localIllegalAccessException) {
/* 3057 */         throw new AssertionError(localIllegalAccessException);
/*      */       } catch (InvocationTargetException localInvocationTargetException) {
/* 3059 */         Throwable localThrowable = localInvocationTargetException.getCause();
/* 3060 */         if ((localThrowable instanceof IOException))
/* 3061 */           throw ((IOException)localThrowable);
/* 3062 */         if ((localThrowable instanceof ClassNotFoundException))
/* 3063 */           throw ((ClassNotFoundException)localThrowable);
/* 3064 */         throw new AssertionError(localInvocationTargetException);
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\datatransfer\DataTransferer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */