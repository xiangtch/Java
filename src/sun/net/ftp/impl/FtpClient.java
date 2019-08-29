/*      */ package sun.net.ftp.impl;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.net.Proxy.Type;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Vector;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSocket;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import sun.misc.BASE64Decoder;
/*      */ import sun.misc.BASE64Encoder;
/*      */ import sun.net.TelnetInputStream;
/*      */ import sun.net.TelnetOutputStream;
/*      */ import sun.net.ftp.FtpClient.TransferType;
/*      */ import sun.net.ftp.FtpDirEntry;
/*      */ import sun.net.ftp.FtpDirEntry.Type;
/*      */ import sun.net.ftp.FtpDirParser;
/*      */ import sun.net.ftp.FtpProtocolException;
/*      */ import sun.net.ftp.FtpReplyCode;
/*      */ import sun.util.logging.PlatformLogger;
/*      */ import sun.util.logging.PlatformLogger.Level;
/*      */ 
/*      */ public class FtpClient extends sun.net.ftp.FtpClient
/*      */ {
/*      */   private static int defaultSoTimeout;
/*      */   private static int defaultConnectTimeout;
/*   56 */   private static final PlatformLogger logger = PlatformLogger.getLogger("sun.net.ftp.FtpClient");
/*      */   private Proxy proxy;
/*      */   private Socket server;
/*      */   private PrintStream out;
/*      */   private InputStream in;
/*   61 */   private int readTimeout = -1;
/*   62 */   private int connectTimeout = -1;
/*      */   
/*      */ 
/*   65 */   private static String encoding = "ISO8859_1";
/*      */   
/*      */   private InetSocketAddress serverAddr;
/*   68 */   private boolean replyPending = false;
/*   69 */   private boolean loggedIn = false;
/*   70 */   private boolean useCrypto = false;
/*      */   
/*      */   private SSLSocketFactory sslFact;
/*      */   private Socket oldSocket;
/*   74 */   private Vector<String> serverResponse = new Vector(1);
/*      */   
/*   76 */   private FtpReplyCode lastReplyCode = null;
/*      */   
/*      */ 
/*      */   private String welcomeMsg;
/*      */   
/*      */ 
/*   82 */   private final boolean passiveMode = true;
/*   83 */   private FtpClient.TransferType type = FtpClient.TransferType.BINARY;
/*   84 */   private long restartOffset = 0L;
/*   85 */   private long lastTransSize = -1L;
/*      */   
/*      */ 
/*      */   private String lastFileName;
/*      */   
/*   90 */   private static String[] patStrings = { "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d\\d:\\d\\d)\\s*(\\p{Print}*)", "([\\-ld](?:[r\\-][w\\-][x\\-]){3})\\s*\\d+ (\\w+)\\s*(\\w+)\\s*(\\d+)\\s*([A-Z][a-z][a-z]\\s*\\d+)\\s*(\\d{4})\\s*(\\p{Print}*)", "(\\d{2}/\\d{2}/\\d{4})\\s*(\\d{2}:\\d{2}[ap])\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)", "(\\d{2}-\\d{2}-\\d{2})\\s*(\\d{2}:\\d{2}[AP]M)\\s*((?:[0-9,]+)|(?:<DIR>))\\s*(\\p{Graph}*)" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  100 */   private static int[][] patternGroups = { { 7, 4, 5, 6, 0, 1, 2, 3 }, { 7, 4, 5, 0, 6, 1, 2, 3 }, { 4, 3, 1, 2, 0, 0, 0, 0 }, { 4, 3, 1, 2, 0, 0, 0, 0 } };
/*      */   
/*      */ 
/*      */ 
/*      */   private static Pattern[] patterns;
/*      */   
/*      */ 
/*      */ 
/*  108 */   private static Pattern linkp = Pattern.compile("(\\p{Print}+) \\-\\> (\\p{Print}+)$");
/*  109 */   private DateFormat df = DateFormat.getDateInstance(2, Locale.US);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean isASCIISuperset(String paramString)
/*      */     throws Exception
/*      */   {
/*  171 */     String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'();/?:@&=+$,";
/*      */     
/*      */ 
/*      */ 
/*  175 */     byte[] arrayOfByte1 = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 45, 95, 46, 33, 126, 42, 39, 40, 41, 59, 47, 63, 58, 64, 38, 61, 43, 36, 44 };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  181 */     byte[] arrayOfByte2 = str.getBytes(paramString);
/*  182 */     return Arrays.equals(arrayOfByte2, arrayOfByte1);
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
/*      */   private class DefaultParser
/*      */     implements FtpDirParser
/*      */   {
/*      */     private DefaultParser() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public FtpDirEntry parseLine(String paramString)
/*      */     {
/*  208 */       String str1 = null;
/*  209 */       String str2 = null;
/*  210 */       String str3 = null;
/*  211 */       String str4 = null;
/*  212 */       String str5 = null;
/*  213 */       String str6 = null;
/*  214 */       String str7 = null;
/*  215 */       boolean bool = false;
/*  216 */       Calendar localCalendar = Calendar.getInstance();
/*  217 */       int i = localCalendar.get(1);
/*      */       
/*  219 */       Matcher localMatcher1 = null;
/*  220 */       for (int j = 0; j < FtpClient.patterns.length; j++) {
/*  221 */         localMatcher1 = FtpClient.patterns[j].matcher(paramString);
/*  222 */         if (localMatcher1.find())
/*      */         {
/*      */ 
/*  225 */           str4 = localMatcher1.group(FtpClient.patternGroups[j][0]);
/*  226 */           str2 = localMatcher1.group(FtpClient.patternGroups[j][1]);
/*  227 */           str1 = localMatcher1.group(FtpClient.patternGroups[j][2]);
/*  228 */           if (FtpClient.patternGroups[j][4] > 0) {
/*  229 */             str1 = str1 + ", " + localMatcher1.group(FtpClient.patternGroups[j][4]);
/*  230 */           } else if (FtpClient.patternGroups[j][3] > 0) {
/*  231 */             str1 = str1 + ", " + String.valueOf(i);
/*      */           }
/*  233 */           if (FtpClient.patternGroups[j][3] > 0) {
/*  234 */             str3 = localMatcher1.group(FtpClient.patternGroups[j][3]);
/*      */           }
/*  236 */           if (FtpClient.patternGroups[j][5] > 0) {
/*  237 */             str5 = localMatcher1.group(FtpClient.patternGroups[j][5]);
/*  238 */             bool = str5.startsWith("d");
/*      */           }
/*  240 */           if (FtpClient.patternGroups[j][6] > 0) {
/*  241 */             str6 = localMatcher1.group(FtpClient.patternGroups[j][6]);
/*      */           }
/*  243 */           if (FtpClient.patternGroups[j][7] > 0) {
/*  244 */             str7 = localMatcher1.group(FtpClient.patternGroups[j][7]);
/*      */           }
/*      */           
/*  247 */           if ("<DIR>".equals(str2)) {
/*  248 */             bool = true;
/*  249 */             str2 = null;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  254 */       if (str4 != null) {
/*      */         Date localDate;
/*      */         try {
/*  257 */           localDate = FtpClient.this.df.parse(str1);
/*      */         } catch (Exception localException) {
/*  259 */           localDate = null;
/*      */         }
/*  261 */         if ((localDate != null) && (str3 != null)) {
/*  262 */           int k = str3.indexOf(":");
/*  263 */           localCalendar.setTime(localDate);
/*  264 */           localCalendar.set(10, Integer.parseInt(str3.substring(0, k)));
/*  265 */           localCalendar.set(12, Integer.parseInt(str3.substring(k + 1)));
/*  266 */           localDate = localCalendar.getTime();
/*      */         }
/*      */         
/*      */ 
/*  270 */         Matcher localMatcher2 = FtpClient.linkp.matcher(str4);
/*  271 */         if (localMatcher2.find())
/*      */         {
/*  273 */           str4 = localMatcher2.group(1);
/*      */         }
/*  275 */         boolean[][] arrayOfBoolean = new boolean[3][3];
/*  276 */         for (int m = 0; m < 3; m++) {
/*  277 */           for (int n = 0; n < 3; n++) {
/*  278 */             arrayOfBoolean[m][n] = (str5.charAt(m * 3 + n) != '-' ? 1 : 0);
/*      */           }
/*      */         }
/*  281 */         FtpDirEntry localFtpDirEntry = new FtpDirEntry(str4);
/*  282 */         localFtpDirEntry.setUser(str6).setGroup(str7);
/*  283 */         localFtpDirEntry.setSize(Long.parseLong(str2)).setLastModified(localDate);
/*  284 */         localFtpDirEntry.setPermissions(arrayOfBoolean);
/*  285 */         localFtpDirEntry.setType(paramString.charAt(0) == 'l' ? FtpDirEntry.Type.LINK : bool ? FtpDirEntry.Type.DIR : FtpDirEntry.Type.FILE);
/*  286 */         return localFtpDirEntry;
/*      */       }
/*  288 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   private class MLSxParser implements FtpDirParser { private MLSxParser() {}
/*      */     
/*  294 */     private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
/*      */     
/*      */     public FtpDirEntry parseLine(String paramString) {
/*  297 */       String str1 = null;
/*  298 */       int i = paramString.lastIndexOf(";");
/*  299 */       if (i > 0) {
/*  300 */         str1 = paramString.substring(i + 1).trim();
/*  301 */         paramString = paramString.substring(0, i);
/*      */       } else {
/*  303 */         str1 = paramString.trim();
/*  304 */         paramString = "";
/*      */       }
/*  306 */       FtpDirEntry localFtpDirEntry = new FtpDirEntry(str1);
/*  307 */       Object localObject; while (!paramString.isEmpty())
/*      */       {
/*  309 */         i = paramString.indexOf(";");
/*  310 */         if (i > 0) {
/*  311 */           str2 = paramString.substring(0, i);
/*  312 */           paramString = paramString.substring(i + 1);
/*      */         } else {
/*  314 */           str2 = paramString;
/*  315 */           paramString = "";
/*      */         }
/*  317 */         i = str2.indexOf("=");
/*  318 */         if (i > 0) {
/*  319 */           localObject = str2.substring(0, i);
/*  320 */           String str3 = str2.substring(i + 1);
/*  321 */           localFtpDirEntry.addFact((String)localObject, str3);
/*      */         }
/*      */       }
/*  324 */       String str2 = localFtpDirEntry.getFact("Size");
/*  325 */       if (str2 != null) {
/*  326 */         localFtpDirEntry.setSize(Long.parseLong(str2));
/*      */       }
/*  328 */       str2 = localFtpDirEntry.getFact("Modify");
/*  329 */       if (str2 != null) {
/*  330 */         localObject = null;
/*      */         try {
/*  332 */           localObject = this.df.parse(str2);
/*      */         }
/*      */         catch (ParseException localParseException1) {}
/*  335 */         if (localObject != null) {
/*  336 */           localFtpDirEntry.setLastModified((Date)localObject);
/*      */         }
/*      */       }
/*  339 */       str2 = localFtpDirEntry.getFact("Create");
/*  340 */       if (str2 != null) {
/*  341 */         localObject = null;
/*      */         try {
/*  343 */           localObject = this.df.parse(str2);
/*      */         }
/*      */         catch (ParseException localParseException2) {}
/*  346 */         if (localObject != null) {
/*  347 */           localFtpDirEntry.setCreated((Date)localObject);
/*      */         }
/*      */       }
/*  350 */       str2 = localFtpDirEntry.getFact("Type");
/*  351 */       if (str2 != null) {
/*  352 */         if (str2.equalsIgnoreCase("file")) {
/*  353 */           localFtpDirEntry.setType(FtpDirEntry.Type.FILE);
/*      */         }
/*  355 */         if (str2.equalsIgnoreCase("dir")) {
/*  356 */           localFtpDirEntry.setType(FtpDirEntry.Type.DIR);
/*      */         }
/*  358 */         if (str2.equalsIgnoreCase("cdir")) {
/*  359 */           localFtpDirEntry.setType(FtpDirEntry.Type.CDIR);
/*      */         }
/*  361 */         if (str2.equalsIgnoreCase("pdir")) {
/*  362 */           localFtpDirEntry.setType(FtpDirEntry.Type.PDIR);
/*      */         }
/*      */       }
/*  365 */       return localFtpDirEntry;
/*      */     } }
/*      */   
/*  368 */   private FtpDirParser parser = new DefaultParser(null);
/*  369 */   private FtpDirParser mlsxParser = new MLSxParser(null);
/*      */   private static Pattern transPat;
/*      */   private static Pattern epsvPat;
/*      */   
/*  373 */   private void getTransferSize() { this.lastTransSize = -1L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  380 */     String str1 = getLastResponseString();
/*  381 */     if (transPat == null) {
/*  382 */       transPat = Pattern.compile("150 Opening .*\\((\\d+) bytes\\).");
/*      */     }
/*  384 */     Matcher localMatcher = transPat.matcher(str1);
/*  385 */     if (localMatcher.find()) {
/*  386 */       String str2 = localMatcher.group(1);
/*  387 */       this.lastTransSize = Long.parseLong(str2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void getTransferName()
/*      */   {
/*  397 */     this.lastFileName = null;
/*  398 */     String str = getLastResponseString();
/*  399 */     int i = str.indexOf("unique file name:");
/*  400 */     int j = str.lastIndexOf(')');
/*  401 */     if (i >= 0) {
/*  402 */       i += 17;
/*  403 */       this.lastFileName = str.substring(i, j);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int readServerResponse()
/*      */     throws IOException
/*      */   {
/*  412 */     StringBuffer localStringBuffer = new StringBuffer(32);
/*      */     
/*  414 */     int j = -1;
/*      */     
/*      */ 
/*      */ 
/*  418 */     this.serverResponse.setSize(0);
/*      */     int k;
/*  420 */     for (;;) { int i; if ((i = this.in.read()) != -1) {
/*  421 */         if ((i == 13) && 
/*  422 */           ((i = this.in.read()) != 10)) {
/*  423 */           localStringBuffer.append('\r');
/*      */         }
/*      */         
/*  426 */         localStringBuffer.append((char)i);
/*  427 */         if (i != 10) {
/*      */           continue;
/*      */         }
/*      */       }
/*  431 */       String str = localStringBuffer.toString();
/*  432 */       localStringBuffer.setLength(0);
/*  433 */       if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
/*  434 */         logger.finest("Server [" + this.serverAddr + "] --> " + str);
/*      */       }
/*      */       
/*  437 */       if (str.length() == 0) {
/*  438 */         k = -1;
/*      */       } else {
/*      */         try {
/*  441 */           k = Integer.parseInt(str.substring(0, 3));
/*      */         } catch (NumberFormatException localNumberFormatException) {
/*  443 */           k = -1;
/*      */         }
/*      */         catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {}
/*      */         
/*  447 */         continue;
/*      */       }
/*      */       
/*  450 */       this.serverResponse.addElement(str);
/*  451 */       if (j != -1)
/*      */       {
/*  453 */         if ((k == j) && (
/*  454 */           (str.length() < 4) || (str.charAt(3) != '-')))
/*      */         {
/*      */ 
/*      */ 
/*  458 */           j = -1;
/*  459 */           break;
/*      */         }
/*  461 */       } else { if ((str.length() < 4) || (str.charAt(3) != '-')) break;
/*  462 */         j = k;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  469 */     return k;
/*      */   }
/*      */   
/*      */   private void sendServer(String paramString)
/*      */   {
/*  474 */     this.out.print(paramString);
/*  475 */     if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
/*  476 */       logger.finest("Server [" + this.serverAddr + "] <-- " + paramString);
/*      */     }
/*      */   }
/*      */   
/*      */   private String getResponseString()
/*      */   {
/*  482 */     return (String)this.serverResponse.elementAt(0);
/*      */   }
/*      */   
/*      */   private Vector<String> getResponseStrings()
/*      */   {
/*  487 */     return this.serverResponse;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean readReply()
/*      */     throws IOException
/*      */   {
/*  497 */     this.lastReplyCode = FtpReplyCode.find(readServerResponse());
/*      */     
/*  499 */     if (this.lastReplyCode.isPositivePreliminary()) {
/*  500 */       this.replyPending = true;
/*  501 */       return true;
/*      */     }
/*  503 */     if ((this.lastReplyCode.isPositiveCompletion()) || (this.lastReplyCode.isPositiveIntermediate())) {
/*  504 */       if (this.lastReplyCode == FtpReplyCode.CLOSING_DATA_CONNECTION) {
/*  505 */         getTransferName();
/*      */       }
/*  507 */       return true;
/*      */     }
/*  509 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean issueCommand(String paramString)
/*      */     throws IOException, FtpProtocolException
/*      */   {
/*  522 */     if (!isConnected()) {
/*  523 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  525 */     if (this.replyPending) {
/*      */       try {
/*  527 */         completePending();
/*      */       }
/*      */       catch (FtpProtocolException localFtpProtocolException1) {}
/*      */     }
/*      */     
/*  532 */     if (paramString.indexOf('\n') != -1) {
/*  533 */       FtpProtocolException localFtpProtocolException2 = new FtpProtocolException("Illegal FTP command");
/*      */       
/*  535 */       localFtpProtocolException2.initCause(new IllegalArgumentException("Illegal carriage return"));
/*  536 */       throw localFtpProtocolException2;
/*      */     }
/*  538 */     sendServer(paramString + "\r\n");
/*  539 */     return readReply();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void issueCommandCheck(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*  550 */     if (!issueCommand(paramString)) {
/*  551 */       throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
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
/*      */   private Socket openPassiveDataConnection(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*  567 */     InetSocketAddress localInetSocketAddress = null;
/*      */     
/*      */ 
/*      */ 
/*      */     String str;
/*      */     
/*      */ 
/*      */     Object localObject1;
/*      */     
/*      */ 
/*      */     int i;
/*      */     
/*      */ 
/*  580 */     if (issueCommand("EPSV ALL"))
/*      */     {
/*  582 */       issueCommandCheck("EPSV");
/*  583 */       str = getResponseString();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  591 */       if (epsvPat == null) {
/*  592 */         epsvPat = Pattern.compile("^229 .* \\(\\|\\|\\|(\\d+)\\|\\)");
/*      */       }
/*  594 */       localObject1 = epsvPat.matcher(str);
/*  595 */       if (!((Matcher)localObject1).find()) {
/*  596 */         throw new FtpProtocolException("EPSV failed : " + str);
/*      */       }
/*      */       
/*  599 */       localObject2 = ((Matcher)localObject1).group(1);
/*  600 */       i = Integer.parseInt((String)localObject2);
/*  601 */       InetAddress localInetAddress = this.server.getInetAddress();
/*  602 */       if (localInetAddress != null) {
/*  603 */         localInetSocketAddress = new InetSocketAddress(localInetAddress, i);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  609 */         localInetSocketAddress = InetSocketAddress.createUnresolved(this.serverAddr.getHostName(), i);
/*      */       }
/*      */     }
/*      */     else {
/*  613 */       issueCommandCheck("PASV");
/*  614 */       str = getResponseString();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  628 */       if (pasvPat == null) {
/*  629 */         pasvPat = Pattern.compile("227 .* \\(?(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})\\)?");
/*      */       }
/*  631 */       localObject1 = pasvPat.matcher(str);
/*  632 */       if (!((Matcher)localObject1).find()) {
/*  633 */         throw new FtpProtocolException("PASV failed : " + str);
/*      */       }
/*      */       
/*  636 */       i = Integer.parseInt(((Matcher)localObject1).group(3)) + (Integer.parseInt(((Matcher)localObject1).group(2)) << 8);
/*      */       
/*  638 */       localObject2 = ((Matcher)localObject1).group(1).replace(',', '.');
/*  639 */       localInetSocketAddress = new InetSocketAddress((String)localObject2, i);
/*      */     }
/*      */     
/*      */ 
/*  643 */     if (this.proxy != null) {
/*  644 */       if (this.proxy.type() == Type.SOCKS) {
/*  645 */         localObject1 = (Socket)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Socket run()
/*      */           {
/*  649 */             return new Socket(FtpClient.this.proxy);
/*      */           }
/*      */         });
/*      */       } else {
/*  653 */         localObject1 = new Socket(Proxy.NO_PROXY);
/*      */       }
/*      */     } else {
/*  656 */       localObject1 = new Socket();
/*      */     }
/*      */     
/*  659 */     Object localObject2 = (InetAddress)AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public InetAddress run()
/*      */       {
/*  663 */         return FtpClient.this.server.getLocalAddress();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  668 */     });
/*  669 */     ((Socket)localObject1).bind(new InetSocketAddress((InetAddress)localObject2, 0));
/*  670 */     if (this.connectTimeout >= 0) {
/*  671 */       ((Socket)localObject1).connect(localInetSocketAddress, this.connectTimeout);
/*      */     }
/*  673 */     else if (defaultConnectTimeout > 0) {
/*  674 */       ((Socket)localObject1).connect(localInetSocketAddress, defaultConnectTimeout);
/*      */     } else {
/*  676 */       ((Socket)localObject1).connect(localInetSocketAddress);
/*      */     }
/*      */     
/*  679 */     if (this.readTimeout >= 0) {
/*  680 */       ((Socket)localObject1).setSoTimeout(this.readTimeout);
/*  681 */     } else if (defaultSoTimeout > 0) {
/*  682 */       ((Socket)localObject1).setSoTimeout(defaultSoTimeout);
/*      */     }
/*  684 */     if (this.useCrypto) {
/*      */       try {
/*  686 */         localObject1 = this.sslFact.createSocket((Socket)localObject1, localInetSocketAddress.getHostName(), localInetSocketAddress.getPort(), true);
/*      */       } catch (Exception localException) {
/*  688 */         throw new FtpProtocolException("Can't open secure data channel: " + localException);
/*      */       }
/*      */     }
/*  691 */     if (!issueCommand(paramString)) {
/*  692 */       ((Socket)localObject1).close();
/*  693 */       if (getLastReplyCode() == FtpReplyCode.FILE_UNAVAILABLE)
/*      */       {
/*  695 */         throw new java.io.FileNotFoundException(paramString);
/*      */       }
/*  697 */       throw new FtpProtocolException(paramString + ":" + getResponseString(), getLastReplyCode());
/*      */     }
/*  699 */     return (Socket)localObject1;
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
/*      */   private Socket openDataConnection(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  715 */       return openPassiveDataConnection(paramString);
/*      */     }
/*      */     catch (FtpProtocolException localFtpProtocolException)
/*      */     {
/*  719 */       Object localObject1 = localFtpProtocolException.getMessage();
/*  720 */       if ((!((String)localObject1).startsWith("PASV")) && (!((String)localObject1).startsWith("EPSV"))) {
/*  721 */         throw localFtpProtocolException;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  729 */       if ((this.proxy != null) && (this.proxy.type() == Type.SOCKS))
/*      */       {
/*      */ 
/*      */ 
/*  733 */         throw new FtpProtocolException("Passive mode failed");
/*      */       }
/*      */       
/*      */ 
/*  737 */       ServerSocket localServerSocket = new ServerSocket(0, 1, this.server.getLocalAddress());
/*      */       Socket localSocket;
/*  739 */       try { localObject1 = localServerSocket.getInetAddress();
/*  740 */         if (((InetAddress)localObject1).isAnyLocalAddress()) {
/*  741 */           localObject1 = this.server.getLocalAddress();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  751 */         String str = "EPRT |" + ((localObject1 instanceof Inet6Address) ? "2" : "1") + "|" + ((InetAddress)localObject1).getHostAddress() + "|" + localServerSocket.getLocalPort() + "|";
/*  752 */         if ((!issueCommand(str)) || (!issueCommand(paramString)))
/*      */         {
/*  754 */           str = "PORT ";
/*  755 */           byte[] arrayOfByte = ((InetAddress)localObject1).getAddress();
/*      */           
/*      */ 
/*  758 */           for (int i = 0; i < arrayOfByte.length; i++) {
/*  759 */             str = str + (arrayOfByte[i] & 0xFF) + ",";
/*      */           }
/*      */           
/*      */ 
/*  763 */           str = str + (localServerSocket.getLocalPort() >>> 8 & 0xFF) + "," + (localServerSocket.getLocalPort() & 0xFF);
/*  764 */           issueCommandCheck(str);
/*  765 */           issueCommandCheck(paramString);
/*      */         }
/*      */         
/*      */ 
/*  769 */         if (this.connectTimeout >= 0) {
/*  770 */           localServerSocket.setSoTimeout(this.connectTimeout);
/*      */         }
/*  772 */         else if (defaultConnectTimeout > 0) {
/*  773 */           localServerSocket.setSoTimeout(defaultConnectTimeout);
/*      */         }
/*      */         
/*  776 */         localSocket = localServerSocket.accept();
/*  777 */         if (this.readTimeout >= 0) {
/*  778 */           localSocket.setSoTimeout(this.readTimeout);
/*      */         }
/*  780 */         else if (defaultSoTimeout > 0) {
/*  781 */           localSocket.setSoTimeout(defaultSoTimeout);
/*      */         }
/*      */       }
/*      */       finally {
/*  785 */         localServerSocket.close();
/*      */       }
/*  787 */       if (this.useCrypto) {
/*      */         try {
/*  789 */           localSocket = this.sslFact.createSocket(localSocket, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
/*      */         } catch (Exception localException) {
/*  791 */           throw new IOException(localException.getLocalizedMessage());
/*      */         }
/*      */       }
/*  794 */       return localSocket;
/*      */     }
/*      */   }
/*      */   
/*  798 */   private InputStream createInputStream(InputStream paramInputStream) { if (this.type == FtpClient.TransferType.ASCII) {
/*  799 */       return new TelnetInputStream(paramInputStream, false);
/*      */     }
/*  801 */     return paramInputStream;
/*      */   }
/*      */   
/*      */   private OutputStream createOutputStream(OutputStream paramOutputStream) {
/*  805 */     if (this.type == FtpClient.TransferType.ASCII) {
/*  806 */       return new TelnetOutputStream(paramOutputStream, false);
/*      */     }
/*  808 */     return paramOutputStream;
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
/*      */   public static sun.net.ftp.FtpClient create()
/*      */   {
/*  825 */     return new FtpClient();
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
/*      */   public sun.net.ftp.FtpClient enablePassiveMode(boolean paramBoolean)
/*      */   {
/*  841 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isPassiveModeEnabled()
/*      */   {
/*  850 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient setConnectTimeout(int paramInt)
/*      */   {
/*  862 */     this.connectTimeout = paramInt;
/*  863 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/*  873 */     return this.connectTimeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient setReadTimeout(int paramInt)
/*      */   {
/*  884 */     this.readTimeout = paramInt;
/*  885 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getReadTimeout()
/*      */   {
/*  895 */     return this.readTimeout;
/*      */   }
/*      */   
/*      */   public sun.net.ftp.FtpClient setProxy(Proxy paramProxy) {
/*  899 */     this.proxy = paramProxy;
/*  900 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Proxy getProxy()
/*      */   {
/*  911 */     return this.proxy;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void tryConnect(InetSocketAddress paramInetSocketAddress, int paramInt)
/*      */     throws IOException
/*      */   {
/*  921 */     if (isConnected()) {
/*  922 */       disconnect();
/*      */     }
/*  924 */     this.server = doConnect(paramInetSocketAddress, paramInt);
/*      */     try {
/*  926 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/*  929 */       throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
/*      */     }
/*  931 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */   }
/*      */   
/*      */   private Socket doConnect(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException {
/*      */     Socket localSocket;
/*  936 */     if (this.proxy != null) {
/*  937 */       if (this.proxy.type() == Type.SOCKS) {
/*  938 */         localSocket = (Socket)AccessController.doPrivileged(new PrivilegedAction()
/*      */         {
/*      */           public Socket run()
/*      */           {
/*  942 */             return new Socket(FtpClient.this.proxy);
/*      */           }
/*      */         });
/*      */       } else {
/*  946 */         localSocket = new Socket(Proxy.NO_PROXY);
/*      */       }
/*      */     } else {
/*  949 */       localSocket = new Socket();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  955 */     if (paramInt >= 0) {
/*  956 */       localSocket.connect(paramInetSocketAddress, paramInt);
/*      */     }
/*  958 */     else if (this.connectTimeout >= 0) {
/*  959 */       localSocket.connect(paramInetSocketAddress, this.connectTimeout);
/*      */     }
/*  961 */     else if (defaultConnectTimeout > 0) {
/*  962 */       localSocket.connect(paramInetSocketAddress, defaultConnectTimeout);
/*      */     } else {
/*  964 */       localSocket.connect(paramInetSocketAddress);
/*      */     }
/*      */     
/*      */ 
/*  968 */     if (this.readTimeout >= 0) {
/*  969 */       localSocket.setSoTimeout(this.readTimeout);
/*  970 */     } else if (defaultSoTimeout > 0) {
/*  971 */       localSocket.setSoTimeout(defaultSoTimeout);
/*      */     }
/*  973 */     return localSocket;
/*      */   }
/*      */   
/*      */   private void disconnect() throws IOException {
/*  977 */     if (isConnected()) {
/*  978 */       this.server.close();
/*      */     }
/*  980 */     this.server = null;
/*  981 */     this.in = null;
/*  982 */     this.out = null;
/*  983 */     this.lastTransSize = -1L;
/*  984 */     this.lastFileName = null;
/*  985 */     this.restartOffset = 0L;
/*  986 */     this.welcomeMsg = null;
/*  987 */     this.lastReplyCode = null;
/*  988 */     this.serverResponse.setSize(0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  997 */     return this.server != null;
/*      */   }
/*      */   
/*      */   public SocketAddress getServerAddress() {
/* 1001 */     return this.server == null ? null : this.server.getRemoteSocketAddress();
/*      */   }
/*      */   
/*      */   public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress) throws FtpProtocolException, IOException {
/* 1005 */     return connect(paramSocketAddress, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient connect(SocketAddress paramSocketAddress, int paramInt)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1015 */     if (!(paramSocketAddress instanceof InetSocketAddress)) {
/* 1016 */       throw new IllegalArgumentException("Wrong address type");
/*      */     }
/* 1018 */     this.serverAddr = ((InetSocketAddress)paramSocketAddress);
/* 1019 */     tryConnect(this.serverAddr, paramInt);
/* 1020 */     if (!readReply())
/*      */     {
/* 1022 */       throw new FtpProtocolException("Welcome message: " + getResponseString(), this.lastReplyCode);
/*      */     }
/* 1024 */     this.welcomeMsg = getResponseString().substring(4);
/* 1025 */     return this;
/*      */   }
/*      */   
/*      */   private void tryLogin(String paramString, char[] paramArrayOfChar) throws FtpProtocolException, IOException {
/* 1029 */     issueCommandCheck("USER " + paramString);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1034 */     if ((this.lastReplyCode == FtpReplyCode.NEED_PASSWORD) && 
/* 1035 */       (paramArrayOfChar != null) && (paramArrayOfChar.length > 0)) {
/* 1036 */       issueCommandCheck("PASS " + String.valueOf(paramArrayOfChar));
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
/*      */   public sun.net.ftp.FtpClient login(String paramString, char[] paramArrayOfChar)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1050 */     if (!isConnected()) {
/* 1051 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 1053 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1054 */       throw new IllegalArgumentException("User name can't be null or empty");
/*      */     }
/* 1056 */     tryLogin(paramString, paramArrayOfChar);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1061 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1062 */     for (int i = 0; i < this.serverResponse.size(); i++) {
/* 1063 */       String str = (String)this.serverResponse.elementAt(i);
/* 1064 */       if (str != null) {
/* 1065 */         if ((str.length() >= 4) && (str.startsWith("230")))
/*      */         {
/* 1067 */           str = str.substring(4);
/*      */         }
/* 1069 */         localStringBuffer.append(str);
/*      */       }
/*      */     }
/* 1072 */     this.welcomeMsg = localStringBuffer.toString();
/* 1073 */     this.loggedIn = true;
/* 1074 */     return this;
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
/*      */   public sun.net.ftp.FtpClient login(String paramString1, char[] paramArrayOfChar, String paramString2)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1089 */     if (!isConnected()) {
/* 1090 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 1092 */     if ((paramString1 == null) || (paramString1.length() == 0)) {
/* 1093 */       throw new IllegalArgumentException("User name can't be null or empty");
/*      */     }
/* 1095 */     tryLogin(paramString1, paramArrayOfChar);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1100 */     if (this.lastReplyCode == FtpReplyCode.NEED_ACCOUNT) {
/* 1101 */       issueCommandCheck("ACCT " + paramString2);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1106 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1107 */     if (this.serverResponse != null) {
/* 1108 */       for (String str : this.serverResponse) {
/* 1109 */         if (str != null) {
/* 1110 */           if ((str.length() >= 4) && (str.startsWith("230")))
/*      */           {
/* 1112 */             str = str.substring(4);
/*      */           }
/* 1114 */           localStringBuffer.append(str);
/*      */         }
/*      */       }
/*      */     }
/* 1118 */     this.welcomeMsg = localStringBuffer.toString();
/* 1119 */     this.loggedIn = true;
/* 1120 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/* 1129 */     if (isConnected()) {
/*      */       try {
/* 1131 */         issueCommand("QUIT");
/*      */       }
/*      */       catch (FtpProtocolException localFtpProtocolException) {}
/* 1134 */       this.loggedIn = false;
/*      */     }
/* 1136 */     disconnect();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isLoggedIn()
/*      */   {
/* 1145 */     return this.loggedIn;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient changeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1156 */     if ((paramString == null) || ("".equals(paramString))) {
/* 1157 */       throw new IllegalArgumentException("directory can't be null or empty");
/*      */     }
/*      */     
/* 1160 */     issueCommandCheck("CWD " + paramString);
/* 1161 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient changeToParentDirectory()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1171 */     issueCommandCheck("CDUP");
/* 1172 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getWorkingDirectory()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1184 */     issueCommandCheck("PWD");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1190 */     String str = getResponseString();
/* 1191 */     if (!str.startsWith("257")) {
/* 1192 */       return null;
/*      */     }
/* 1194 */     return str.substring(5, str.lastIndexOf('"'));
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
/*      */   public sun.net.ftp.FtpClient setRestartOffset(long paramLong)
/*      */   {
/* 1209 */     if (paramLong < 0L) {
/* 1210 */       throw new IllegalArgumentException("offset can't be negative");
/*      */     }
/* 1212 */     this.restartOffset = paramLong;
/* 1213 */     return this;
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
/*      */   public sun.net.ftp.FtpClient getFile(String paramString, OutputStream paramOutputStream)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1231 */     int i = 1500;
/* 1232 */     Socket localSocket; InputStream localInputStream; byte[] arrayOfByte; int j; if (this.restartOffset > 0L)
/*      */     {
/*      */       try {
/* 1235 */         localSocket = openDataConnection("REST " + this.restartOffset);
/*      */       } finally {
/* 1237 */         this.restartOffset = 0L;
/*      */       }
/* 1239 */       issueCommandCheck("RETR " + paramString);
/* 1240 */       getTransferSize();
/* 1241 */       localInputStream = createInputStream(localSocket.getInputStream());
/* 1242 */       arrayOfByte = new byte[i * 10];
/*      */       
/* 1244 */       while ((j = localInputStream.read(arrayOfByte)) >= 0) {
/* 1245 */         if (j > 0) {
/* 1246 */           paramOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1249 */       localInputStream.close();
/*      */     } else {
/* 1251 */       localSocket = openDataConnection("RETR " + paramString);
/* 1252 */       getTransferSize();
/* 1253 */       localInputStream = createInputStream(localSocket.getInputStream());
/* 1254 */       arrayOfByte = new byte[i * 10];
/*      */       
/* 1256 */       while ((j = localInputStream.read(arrayOfByte)) >= 0) {
/* 1257 */         if (j > 0) {
/* 1258 */           paramOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1261 */       localInputStream.close();
/*      */     }
/* 1263 */     return completePending();
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
/*      */   private static Pattern pasvPat;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String[] MDTMformats;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static SimpleDateFormat[] dateFormats;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public OutputStream putFileStream(String paramString, boolean paramBoolean)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1329 */     String str = paramBoolean ? "STOU " : "STOR ";
/* 1330 */     Socket localSocket = openDataConnection(str + paramString);
/* 1331 */     if (localSocket == null) {
/* 1332 */       return null;
/*      */     }
/* 1334 */     boolean bool = this.type == FtpClient.TransferType.BINARY;
/* 1335 */     return new TelnetOutputStream(localSocket.getOutputStream(), bool);
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
/*      */   public sun.net.ftp.FtpClient putFile(String paramString, InputStream paramInputStream, boolean paramBoolean)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1357 */     String str = paramBoolean ? "STOU " : "STOR ";
/* 1358 */     int i = 1500;
/* 1359 */     if (this.type == FtpClient.TransferType.BINARY) {
/* 1360 */       Socket localSocket = openDataConnection(str + paramString);
/* 1361 */       OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
/* 1362 */       byte[] arrayOfByte = new byte[i * 10];
/*      */       int j;
/* 1364 */       while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
/* 1365 */         if (j > 0) {
/* 1366 */           localOutputStream.write(arrayOfByte, 0, j);
/*      */         }
/*      */       }
/* 1369 */       localOutputStream.close();
/*      */     }
/* 1371 */     return completePending();
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
/*      */   public sun.net.ftp.FtpClient appendFile(String paramString, InputStream paramInputStream)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1387 */     int i = 1500;
/* 1388 */     Socket localSocket = openDataConnection("APPE " + paramString);
/* 1389 */     OutputStream localOutputStream = createOutputStream(localSocket.getOutputStream());
/* 1390 */     byte[] arrayOfByte = new byte[i * 10];
/*      */     int j;
/* 1392 */     while ((j = paramInputStream.read(arrayOfByte)) >= 0) {
/* 1393 */       if (j > 0) {
/* 1394 */         localOutputStream.write(arrayOfByte, 0, j);
/*      */       }
/*      */     }
/* 1397 */     localOutputStream.close();
/* 1398 */     return completePending();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient rename(String paramString1, String paramString2)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1409 */     issueCommandCheck("RNFR " + paramString1);
/* 1410 */     issueCommandCheck("RNTO " + paramString2);
/* 1411 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient deleteFile(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1423 */     issueCommandCheck("DELE " + paramString);
/* 1424 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient makeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1436 */     issueCommandCheck("MKD " + paramString);
/* 1437 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient removeDirectory(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1450 */     issueCommandCheck("RMD " + paramString);
/* 1451 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient noop()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1461 */     issueCommandCheck("NOOP");
/* 1462 */     return this;
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
/*      */   public String getStatus(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1481 */     issueCommandCheck("STAT " + paramString);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1506 */     Vector localVector = getResponseStrings();
/* 1507 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1508 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 1509 */       localStringBuffer.append((String)localVector.get(i));
/*      */     }
/* 1511 */     return localStringBuffer.toString();
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
/*      */ 
/*      */   public List<String> getFeatures()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1543 */     ArrayList localArrayList = new ArrayList();
/* 1544 */     issueCommandCheck("FEAT");
/* 1545 */     Vector localVector = getResponseStrings();
/*      */     
/*      */ 
/* 1548 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 1549 */       String str = (String)localVector.get(i);
/*      */       
/* 1551 */       localArrayList.add(str.substring(1, str.length() - 1));
/*      */     }
/* 1553 */     return localArrayList;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient abort()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1564 */     issueCommandCheck("ABOR");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1583 */     return this;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient completePending()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1620 */     while (this.replyPending) {
/* 1621 */       this.replyPending = false;
/* 1622 */       if (!readReply()) {
/* 1623 */         throw new FtpProtocolException(getLastResponseString(), this.lastReplyCode);
/*      */       }
/*      */     }
/* 1626 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient reInit()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1635 */     issueCommandCheck("REIN");
/* 1636 */     this.loggedIn = false;
/* 1637 */     if ((this.useCrypto) && 
/* 1638 */       ((this.server instanceof SSLSocket))) {
/* 1639 */       SSLSession localSSLSession = ((SSLSocket)this.server).getSession();
/* 1640 */       localSSLSession.invalidate();
/*      */       
/* 1642 */       this.server = this.oldSocket;
/* 1643 */       this.oldSocket = null;
/*      */       try {
/* 1645 */         this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */       }
/*      */       catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1648 */         throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
/*      */       }
/* 1650 */       this.in = new BufferedInputStream(this.server.getInputStream());
/*      */     }
/*      */     
/* 1653 */     this.useCrypto = false;
/* 1654 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient setType(FtpClient.TransferType paramTransferType)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1666 */     String str = "NOOP";
/*      */     
/* 1668 */     this.type = paramTransferType;
/* 1669 */     if (paramTransferType == FtpClient.TransferType.ASCII) {
/* 1670 */       str = "TYPE A";
/*      */     }
/* 1672 */     if (paramTransferType == FtpClient.TransferType.BINARY) {
/* 1673 */       str = "TYPE I";
/*      */     }
/* 1675 */     if (paramTransferType == FtpClient.TransferType.EBCDIC) {
/* 1676 */       str = "TYPE E";
/*      */     }
/* 1678 */     issueCommandCheck(str);
/* 1679 */     return this;
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
/*      */   public InputStream list(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1697 */     Socket localSocket = openDataConnection("LIST " + paramString);
/* 1698 */     if (localSocket != null) {
/* 1699 */       return createInputStream(localSocket.getInputStream());
/*      */     }
/* 1701 */     return null;
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
/*      */   public InputStream nameList(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1721 */     Socket localSocket = openDataConnection("NLST " + paramString);
/* 1722 */     if (localSocket != null) {
/* 1723 */       return createInputStream(localSocket.getInputStream());
/*      */     }
/* 1725 */     return null;
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
/*      */   public long getSize(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1742 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 1743 */       throw new IllegalArgumentException("path can't be null or empty");
/*      */     }
/* 1745 */     issueCommandCheck("SIZE " + paramString);
/* 1746 */     if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
/* 1747 */       String str = getResponseString();
/* 1748 */       str = str.substring(4, str.length() - 1);
/* 1749 */       return Long.parseLong(str);
/*      */     }
/* 1751 */     return -1L;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  112 */     int[] arrayOfInt = { 0, 0 };
/*  113 */     final String[] arrayOfString = { null };
/*      */     
/*  115 */     AccessController.doPrivileged(new PrivilegedAction()
/*      */     {
/*      */       public Object run()
/*      */       {
/*  119 */         this.val$vals[0] = Integer.getInteger("sun.net.client.defaultReadTimeout", 300000).intValue();
/*  120 */         this.val$vals[1] = Integer.getInteger("sun.net.client.defaultConnectTimeout", 300000).intValue();
/*  121 */         arrayOfString[0] = System.getProperty("file.encoding", "ISO8859_1");
/*  122 */         return null;
/*      */       }
/*      */     });
/*  125 */     if (arrayOfInt[0] == 0) {
/*  126 */       defaultSoTimeout = -1;
/*      */     } else {
/*  128 */       defaultSoTimeout = arrayOfInt[0];
/*      */     }
/*      */     
/*  131 */     if (arrayOfInt[1] == 0) {
/*  132 */       defaultConnectTimeout = -1;
/*      */     } else {
/*  134 */       defaultConnectTimeout = arrayOfInt[1];
/*      */     }
/*      */     
/*  137 */     encoding = arrayOfString[0];
/*      */     try {
/*  139 */       if (!isASCIISuperset(encoding)) {
/*  140 */         encoding = "ISO8859_1";
/*      */       }
/*      */     } catch (Exception localException) {
/*  143 */       encoding = "ISO8859_1";
/*      */     }
/*      */     
/*  146 */     patterns = new Pattern[patStrings.length];
/*  147 */     for (int j = 0; j < patStrings.length; j++) {
/*  148 */       patterns[j] = Pattern.compile(patStrings[j]);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  370 */     transPat = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  554 */     epsvPat = null;
/*  555 */     pasvPat = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1753 */     MDTMformats = new String[] { "yyyyMMddHHmmss.SSS", "yyyyMMddHHmmss" };
/*      */     
/*      */ 
/*      */ 
/* 1757 */     dateFormats = new SimpleDateFormat[MDTMformats.length];
/*      */     
/*      */ 
/* 1760 */     for (int i = 0; i < MDTMformats.length; i++) {
/* 1761 */       dateFormats[i] = new SimpleDateFormat(MDTMformats[i]);
/* 1762 */       dateFormats[i].setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
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
/*      */   public Date getLastModified(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1779 */     issueCommandCheck("MDTM " + paramString);
/* 1780 */     if (this.lastReplyCode == FtpReplyCode.FILE_STATUS) {
/* 1781 */       String str = getResponseString().substring(4);
/* 1782 */       Date localDate = null;
/* 1783 */       for (SimpleDateFormat localSimpleDateFormat : dateFormats) {
/*      */         try {
/* 1785 */           localDate = localSimpleDateFormat.parse(str);
/*      */         }
/*      */         catch (ParseException localParseException) {}
/* 1788 */         if (localDate != null) {
/* 1789 */           return localDate;
/*      */         }
/*      */       }
/*      */     }
/* 1793 */     return null;
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
/*      */   public sun.net.ftp.FtpClient setDirParser(FtpDirParser paramFtpDirParser)
/*      */   {
/* 1807 */     this.parser = paramFtpDirParser;
/* 1808 */     return this;
/*      */   }
/*      */   
/*      */   private class FtpFileIterator implements Iterator<FtpDirEntry>, java.io.Closeable
/*      */   {
/* 1813 */     private BufferedReader in = null;
/* 1814 */     private FtpDirEntry nextFile = null;
/* 1815 */     private FtpDirParser fparser = null;
/* 1816 */     private boolean eof = false;
/*      */     
/*      */     public FtpFileIterator(FtpDirParser paramFtpDirParser, BufferedReader paramBufferedReader) {
/* 1819 */       this.in = paramBufferedReader;
/* 1820 */       this.fparser = paramFtpDirParser;
/* 1821 */       readNext();
/*      */     }
/*      */     
/*      */     private void readNext() {
/* 1825 */       this.nextFile = null;
/* 1826 */       if (this.eof) {
/* 1827 */         return;
/*      */       }
/* 1829 */       String str = null;
/*      */       try {
/*      */         do {
/* 1832 */           str = this.in.readLine();
/* 1833 */           if (str != null) {
/* 1834 */             this.nextFile = this.fparser.parseLine(str);
/* 1835 */             if (this.nextFile != null) {
/* 1836 */               return;
/*      */             }
/*      */           }
/* 1839 */         } while (str != null);
/* 1840 */         this.in.close();
/*      */       }
/*      */       catch (IOException localIOException) {}
/* 1843 */       this.eof = true;
/*      */     }
/*      */     
/*      */     public boolean hasNext() {
/* 1847 */       return this.nextFile != null;
/*      */     }
/*      */     
/*      */     public FtpDirEntry next() {
/* 1851 */       FtpDirEntry localFtpDirEntry = this.nextFile;
/* 1852 */       readNext();
/* 1853 */       return localFtpDirEntry;
/*      */     }
/*      */     
/*      */     public void remove() {
/* 1857 */       throw new UnsupportedOperationException("Not supported yet.");
/*      */     }
/*      */     
/*      */     public void close() throws IOException {
/* 1861 */       if ((this.in != null) && (!this.eof)) {
/* 1862 */         this.in.close();
/*      */       }
/* 1864 */       this.eof = true;
/* 1865 */       this.nextFile = null;
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
/*      */   public Iterator<FtpDirEntry> listFiles(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1889 */     Socket localSocket = null;
/* 1890 */     BufferedReader localBufferedReader = null;
/*      */     try {
/* 1892 */       localSocket = openDataConnection("MLSD " + paramString);
/*      */     }
/*      */     catch (FtpProtocolException localFtpProtocolException) {}
/*      */     
/*      */ 
/*      */ 
/* 1898 */     if (localSocket != null) {
/* 1899 */       localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/* 1900 */       return new FtpFileIterator(this.mlsxParser, localBufferedReader);
/*      */     }
/* 1902 */     localSocket = openDataConnection("LIST " + paramString);
/* 1903 */     if (localSocket != null) {
/* 1904 */       localBufferedReader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
/* 1905 */       return new FtpFileIterator(this.parser, localBufferedReader);
/*      */     }
/*      */     
/* 1908 */     return null;
/*      */   }
/*      */   
/*      */   private boolean sendSecurityData(byte[] paramArrayOfByte) throws IOException, FtpProtocolException
/*      */   {
/* 1913 */     BASE64Encoder localBASE64Encoder = new BASE64Encoder();
/* 1914 */     String str = localBASE64Encoder.encode(paramArrayOfByte);
/* 1915 */     return issueCommand("ADAT " + str);
/*      */   }
/*      */   
/*      */   private byte[] getSecurityData() {
/* 1919 */     String str = getLastResponseString();
/* 1920 */     if (str.substring(4, 9).equalsIgnoreCase("ADAT=")) {
/* 1921 */       BASE64Decoder localBASE64Decoder = new BASE64Decoder();
/*      */       
/*      */       try
/*      */       {
/* 1925 */         return localBASE64Decoder.decodeBuffer(str.substring(9, str.length() - 1));
/*      */       }
/*      */       catch (IOException localIOException) {}
/*      */     }
/*      */     
/* 1930 */     return null;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient useKerberos()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 1977 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getWelcomeMsg()
/*      */   {
/* 1987 */     return this.welcomeMsg;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public FtpReplyCode getLastReplyCode()
/*      */   {
/* 1996 */     return this.lastReplyCode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getLastResponseString()
/*      */   {
/* 2006 */     StringBuffer localStringBuffer = new StringBuffer();
/* 2007 */     if (this.serverResponse != null) {
/* 2008 */       for (String str : this.serverResponse) {
/* 2009 */         if (str != null) {
/* 2010 */           localStringBuffer.append(str);
/*      */         }
/*      */       }
/*      */     }
/* 2014 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getLastTransferSize()
/*      */   {
/* 2026 */     return this.lastTransSize;
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
/*      */   public String getLastFileName()
/*      */   {
/* 2039 */     return this.lastFileName;
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
/*      */   public sun.net.ftp.FtpClient startSecureSession()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2056 */     if (!isConnected()) {
/* 2057 */       throw new FtpProtocolException("Not connected yet", FtpReplyCode.BAD_SEQUENCE);
/*      */     }
/* 2059 */     if (this.sslFact == null) {
/*      */       try {
/* 2061 */         this.sslFact = ((SSLSocketFactory)SSLSocketFactory.getDefault());
/*      */       } catch (Exception localException1) {
/* 2063 */         throw new IOException(localException1.getLocalizedMessage());
/*      */       }
/*      */     }
/* 2066 */     issueCommandCheck("AUTH TLS");
/* 2067 */     Socket localSocket = null;
/*      */     try {
/* 2069 */       localSocket = this.sslFact.createSocket(this.server, this.serverAddr.getHostName(), this.serverAddr.getPort(), true);
/*      */     } catch (SSLException localSSLException) {
/*      */       try {
/* 2072 */         disconnect();
/*      */       }
/*      */       catch (Exception localException2) {}
/* 2075 */       throw localSSLException;
/*      */     }
/*      */     
/* 2078 */     this.oldSocket = this.server;
/* 2079 */     this.server = localSocket;
/*      */     try {
/* 2081 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 2084 */       throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
/*      */     }
/* 2086 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */     
/* 2088 */     issueCommandCheck("PBSZ 0");
/* 2089 */     issueCommandCheck("PROT P");
/* 2090 */     this.useCrypto = true;
/* 2091 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient endSecureSession()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2104 */     if (!this.useCrypto) {
/* 2105 */       return this;
/*      */     }
/*      */     
/* 2108 */     issueCommandCheck("CCC");
/* 2109 */     issueCommandCheck("PROT C");
/* 2110 */     this.useCrypto = false;
/*      */     
/* 2112 */     this.server = this.oldSocket;
/* 2113 */     this.oldSocket = null;
/*      */     try {
/* 2115 */       this.out = new PrintStream(new BufferedOutputStream(this.server.getOutputStream()), true, encoding);
/*      */     }
/*      */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 2118 */       throw new InternalError(encoding + "encoding not found", localUnsupportedEncodingException);
/*      */     }
/* 2120 */     this.in = new BufferedInputStream(this.server.getInputStream());
/*      */     
/* 2122 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient allocate(long paramLong)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2134 */     issueCommandCheck("ALLO " + paramLong);
/* 2135 */     return this;
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
/*      */   public sun.net.ftp.FtpClient structureMount(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2149 */     issueCommandCheck("SMNT " + paramString);
/* 2150 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSystem()
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2163 */     issueCommandCheck("SYST");
/*      */     
/*      */ 
/*      */ 
/* 2167 */     String str = getResponseString();
/*      */     
/* 2169 */     return str.substring(4);
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
/*      */   public String getHelp(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2183 */     issueCommandCheck("HELP " + paramString);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2202 */     Vector localVector = getResponseStrings();
/* 2203 */     if (localVector.size() == 1)
/*      */     {
/* 2205 */       return ((String)localVector.get(0)).substring(4);
/*      */     }
/*      */     
/*      */ 
/* 2209 */     StringBuffer localStringBuffer = new StringBuffer();
/* 2210 */     for (int i = 1; i < localVector.size() - 1; i++) {
/* 2211 */       localStringBuffer.append(((String)localVector.get(i)).substring(3));
/*      */     }
/* 2213 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public sun.net.ftp.FtpClient siteCmd(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/* 2226 */     issueCommandCheck("SITE " + paramString);
/* 2227 */     return this;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public InputStream getFileStream(String paramString)
/*      */     throws FtpProtocolException, IOException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 861	sun/net/ftp/impl/FtpClient:restartOffset	J
/*      */     //   4: lconst_0
/*      */     //   5: lcmp
/*      */     //   6: ifle +88 -> 94
/*      */     //   9: aload_0
/*      */     //   10: new 521	java/lang/StringBuilder
/*      */     //   13: dup
/*      */     //   14: invokespecial 931	java/lang/StringBuilder:<init>	()V
/*      */     //   17: ldc 35
/*      */     //   19: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   22: aload_0
/*      */     //   23: getfield 861	sun/net/ftp/impl/FtpClient:restartOffset	J
/*      */     //   26: invokevirtual 934	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
/*      */     //   29: invokevirtual 932	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   32: invokespecial 1022	sun/net/ftp/impl/FtpClient:openDataConnection	(Ljava/lang/String;)Ljava/net/Socket;
/*      */     //   35: astore_2
/*      */     //   36: aload_0
/*      */     //   37: lconst_0
/*      */     //   38: putfield 861	sun/net/ftp/impl/FtpClient:restartOffset	J
/*      */     //   41: goto +11 -> 52
/*      */     //   44: astore_3
/*      */     //   45: aload_0
/*      */     //   46: lconst_0
/*      */     //   47: putfield 861	sun/net/ftp/impl/FtpClient:restartOffset	J
/*      */     //   50: aload_3
/*      */     //   51: athrow
/*      */     //   52: aload_2
/*      */     //   53: ifnonnull +5 -> 58
/*      */     //   56: aconst_null
/*      */     //   57: areturn
/*      */     //   58: aload_0
/*      */     //   59: new 521	java/lang/StringBuilder
/*      */     //   62: dup
/*      */     //   63: invokespecial 931	java/lang/StringBuilder:<init>	()V
/*      */     //   66: ldc 36
/*      */     //   68: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   71: aload_1
/*      */     //   72: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   75: invokevirtual 932	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   78: invokespecial 1011	sun/net/ftp/impl/FtpClient:issueCommandCheck	(Ljava/lang/String;)V
/*      */     //   81: aload_0
/*      */     //   82: invokespecial 1006	sun/net/ftp/impl/FtpClient:getTransferSize	()V
/*      */     //   85: aload_0
/*      */     //   86: aload_2
/*      */     //   87: invokevirtual 955	java/net/Socket:getInputStream	()Ljava/io/InputStream;
/*      */     //   90: invokespecial 1020	sun/net/ftp/impl/FtpClient:createInputStream	(Ljava/io/InputStream;)Ljava/io/InputStream;
/*      */     //   93: areturn
/*      */     //   94: aload_0
/*      */     //   95: new 521	java/lang/StringBuilder
/*      */     //   98: dup
/*      */     //   99: invokespecial 931	java/lang/StringBuilder:<init>	()V
/*      */     //   102: ldc 36
/*      */     //   104: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   107: aload_1
/*      */     //   108: invokevirtual 936	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   111: invokevirtual 932	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   114: invokespecial 1022	sun/net/ftp/impl/FtpClient:openDataConnection	(Ljava/lang/String;)Ljava/net/Socket;
/*      */     //   117: astore_2
/*      */     //   118: aload_2
/*      */     //   119: ifnonnull +5 -> 124
/*      */     //   122: aconst_null
/*      */     //   123: areturn
/*      */     //   124: aload_0
/*      */     //   125: invokespecial 1006	sun/net/ftp/impl/FtpClient:getTransferSize	()V
/*      */     //   128: aload_0
/*      */     //   129: aload_2
/*      */     //   130: invokevirtual 955	java/net/Socket:getInputStream	()Ljava/io/InputStream;
/*      */     //   133: invokespecial 1020	sun/net/ftp/impl/FtpClient:createInputStream	(Ljava/io/InputStream;)Ljava/io/InputStream;
/*      */     //   136: areturn
/*      */     // Line number table:
/*      */     //   Java source line #1279	-> byte code offset #0
/*      */     //   Java source line #1281	-> byte code offset #9
/*      */     //   Java source line #1283	-> byte code offset #36
/*      */     //   Java source line #1284	-> byte code offset #41
/*      */     //   Java source line #1283	-> byte code offset #44
/*      */     //   Java source line #1284	-> byte code offset #50
/*      */     //   Java source line #1285	-> byte code offset #52
/*      */     //   Java source line #1286	-> byte code offset #56
/*      */     //   Java source line #1288	-> byte code offset #58
/*      */     //   Java source line #1289	-> byte code offset #81
/*      */     //   Java source line #1290	-> byte code offset #85
/*      */     //   Java source line #1293	-> byte code offset #94
/*      */     //   Java source line #1294	-> byte code offset #118
/*      */     //   Java source line #1295	-> byte code offset #122
/*      */     //   Java source line #1297	-> byte code offset #124
/*      */     //   Java source line #1298	-> byte code offset #128
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	137	0	this	FtpClient
/*      */     //   0	137	1	paramString	String
/*      */     //   35	95	2	localSocket	Socket
/*      */     //   44	7	3	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   9	36	44	finally
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\net\ftp\impl\FtpClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */