/*      */ package sun.management.jmxremote;
/*      */ 
/*      */ import com.sun.jmx.remote.internal.RMIExporter;
/*      */ import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
/*      */ import com.sun.jmx.remote.util.ClassLogger;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.lang.management.ManagementFactory;
/*      */ import java.net.InetAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.UnknownHostException;
/*      */ import java.rmi.NoSuchObjectException;
/*      */ import java.rmi.Remote;
/*      */ import java.rmi.RemoteException;
/*      */ import java.rmi.registry.Registry;
/*      */ import java.rmi.server.RMIClientSocketFactory;
/*      */ import java.rmi.server.RMIServerSocketFactory;
/*      */ import java.rmi.server.RemoteObject;
/*      */ import java.rmi.server.UnicastRemoteObject;
/*      */ import java.security.KeyStore;
/*      */ import java.security.Principal;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.management.MBeanServer;
/*      */ import javax.management.remote.JMXAuthenticator;
/*      */ import javax.management.remote.JMXConnectorServer;
/*      */ import javax.management.remote.JMXConnectorServerFactory;
/*      */ import javax.management.remote.JMXServiceURL;
/*      */ import javax.net.ssl.KeyManagerFactory;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLSocket;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManagerFactory;
/*      */ import javax.rmi.ssl.SslRMIClientSocketFactory;
/*      */ import javax.rmi.ssl.SslRMIServerSocketFactory;
/*      */ import javax.security.auth.Subject;
/*      */ import sun.management.Agent;
/*      */ import sun.management.AgentConfigurationError;
/*      */ import sun.management.ConnectorAddressLink;
/*      */ import sun.management.FileSystem;
/*      */ import sun.rmi.server.UnicastRef;
/*      */ import sun.rmi.server.UnicastServerRef;
/*      */ import sun.rmi.server.UnicastServerRef2;
/*      */ import sun.rmi.transport.LiveRef;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ConnectorBootstrap
/*      */ {
/*      */   private static class JMXConnectorServerData
/*      */   {
/*      */     JMXConnectorServer jmxConnectorServer;
/*      */     JMXServiceURL jmxRemoteURL;
/*      */     
/*      */     public JMXConnectorServerData(JMXConnectorServer paramJMXConnectorServer, JMXServiceURL paramJMXServiceURL)
/*      */     {
/*  154 */       this.jmxConnectorServer = paramJMXConnectorServer;
/*  155 */       this.jmxRemoteURL = paramJMXServiceURL;
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
/*      */   private static class PermanentExporter
/*      */     implements RMIExporter
/*      */   {
/*      */     Remote firstExported;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Remote exportObject(Remote paramRemote, int paramInt, RMIClientSocketFactory paramRMIClientSocketFactory, RMIServerSocketFactory paramRMIServerSocketFactory)
/*      */       throws RemoteException
/*      */     {
/*  187 */       synchronized (this) {
/*  188 */         if (this.firstExported == null) {
/*  189 */           this.firstExported = paramRemote;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  194 */       if ((paramRMIClientSocketFactory == null) && (paramRMIServerSocketFactory == null)) {
/*  195 */         ??? = new UnicastServerRef(paramInt);
/*      */       } else {
/*  197 */         ??? = new UnicastServerRef2(paramInt, paramRMIClientSocketFactory, paramRMIServerSocketFactory);
/*      */       }
/*  199 */       return ((UnicastServerRef)???).exportObject(paramRemote, null, true);
/*      */     }
/*      */     
/*      */     public boolean unexportObject(Remote paramRemote, boolean paramBoolean)
/*      */       throws NoSuchObjectException
/*      */     {
/*  205 */       return UnicastRemoteObject.unexportObject(paramRemote, paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class AccessFileCheckerAuthenticator
/*      */     implements JMXAuthenticator
/*      */   {
/*      */     private final Map<String, Object> environment;
/*      */     private final Properties properties;
/*      */     private final String accessFile;
/*      */     
/*      */     public AccessFileCheckerAuthenticator(Map<String, Object> paramMap)
/*      */       throws IOException
/*      */     {
/*  219 */       this.environment = paramMap;
/*  220 */       this.accessFile = ((String)paramMap.get("jmx.remote.x.access.file"));
/*  221 */       this.properties = propertiesFromFile(this.accessFile);
/*      */     }
/*      */     
/*      */     public Subject authenticate(Object paramObject) {
/*  225 */       JMXPluggableAuthenticator localJMXPluggableAuthenticator = new JMXPluggableAuthenticator(this.environment);
/*      */       
/*  227 */       Subject localSubject = localJMXPluggableAuthenticator.authenticate(paramObject);
/*  228 */       checkAccessFileEntries(localSubject);
/*  229 */       return localSubject;
/*      */     }
/*      */     
/*      */     private void checkAccessFileEntries(Subject paramSubject) {
/*  233 */       if (paramSubject == null) {
/*  234 */         throw new SecurityException("Access denied! No matching entries found in the access file [" + this.accessFile + "] as the authenticated Subject is null");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  239 */       Set localSet = paramSubject.getPrincipals();
/*  240 */       for (Object localObject1 = localSet.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (Principal)((Iterator)localObject1).next();
/*  241 */         if (this.properties.containsKey(((Principal)localObject2).getName())) {
/*  242 */           return;
/*      */         }
/*      */       }
/*      */       
/*  246 */       localObject1 = new HashSet();
/*  247 */       for (Object localObject2 = localSet.iterator(); ((Iterator)localObject2).hasNext();) { Principal localPrincipal = (Principal)((Iterator)localObject2).next();
/*  248 */         ((Set)localObject1).add(localPrincipal.getName());
/*      */       }
/*  250 */       throw new SecurityException("Access denied! No entries found in the access file [" + this.accessFile + "] for any of the authenticated identities " + localObject1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static Properties propertiesFromFile(String paramString)
/*      */       throws IOException
/*      */     {
/*  258 */       Properties localProperties = new Properties();
/*  259 */       if (paramString == null) {
/*  260 */         return localProperties;
/*      */       }
/*  262 */       FileInputStream localFileInputStream = new FileInputStream(paramString);Object localObject1 = null;
/*  263 */       try { localProperties.load(localFileInputStream);
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/*  262 */         localObject1 = localThrowable2;throw localThrowable2;
/*      */       } finally {
/*  264 */         if (localFileInputStream != null) if (localObject1 != null) try { localFileInputStream.close(); } catch (Throwable localThrowable3) { ((Throwable)localObject1).addSuppressed(localThrowable3); } else localFileInputStream.close(); }
/*  265 */       return localProperties;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  275 */   private static Registry registry = null;
/*      */   
/*      */   public static void unexportRegistry()
/*      */   {
/*      */     try {
/*  280 */       if (registry != null) {
/*  281 */         UnicastRemoteObject.unexportObject(registry, true);
/*  282 */         registry = null;
/*      */       }
/*      */     }
/*      */     catch (NoSuchObjectException localNoSuchObjectException) {}
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
/*      */   public static synchronized JMXConnectorServer initialize()
/*      */   {
/*  303 */     Properties localProperties = Agent.loadManagementProperties();
/*  304 */     if (localProperties == null) {
/*  305 */       return null;
/*      */     }
/*      */     
/*  308 */     String str = localProperties.getProperty("com.sun.management.jmxremote.port");
/*  309 */     return startRemoteConnectorServer(str, localProperties);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static synchronized JMXConnectorServer initialize(String paramString, Properties paramProperties)
/*      */   {
/*  319 */     return startRemoteConnectorServer(paramString, paramProperties);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static synchronized JMXConnectorServer startRemoteConnectorServer(String paramString, Properties paramProperties)
/*      */   {
/*      */     int i;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  331 */       i = Integer.parseInt(paramString);
/*      */     } catch (NumberFormatException localNumberFormatException1) {
/*  333 */       throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", localNumberFormatException1, new String[] { paramString });
/*      */     }
/*  335 */     if (i < 0) {
/*  336 */       throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { paramString });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  342 */     int j = 0;
/*  343 */     String str1 = paramProperties.getProperty("com.sun.management.jmxremote.rmi.port");
/*      */     try {
/*  345 */       if (str1 != null) {
/*  346 */         j = Integer.parseInt(str1);
/*      */       }
/*      */     } catch (NumberFormatException localNumberFormatException2) {
/*  349 */       throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", localNumberFormatException2, new String[] { str1 });
/*      */     }
/*  351 */     if (j < 0) {
/*  352 */       throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", new String[] { str1 });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  357 */     String str2 = paramProperties.getProperty("com.sun.management.jmxremote.authenticate", "true");
/*      */     
/*      */ 
/*  360 */     boolean bool1 = Boolean.valueOf(str2).booleanValue();
/*      */     
/*      */ 
/*      */ 
/*  364 */     String str3 = paramProperties.getProperty("com.sun.management.jmxremote.ssl", "true");
/*      */     
/*      */ 
/*  367 */     boolean bool2 = Boolean.valueOf(str3).booleanValue();
/*      */     
/*      */ 
/*      */ 
/*  371 */     String str4 = paramProperties.getProperty("com.sun.management.jmxremote.registry.ssl", "false");
/*      */     
/*      */ 
/*  374 */     boolean bool3 = Boolean.valueOf(str4).booleanValue();
/*      */     
/*      */ 
/*  377 */     String str5 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.enabled.cipher.suites");
/*  378 */     String[] arrayOfString1 = null;
/*  379 */     if (str5 != null) {
/*  380 */       localObject1 = new StringTokenizer(str5, ",");
/*  381 */       int k = ((StringTokenizer)localObject1).countTokens();
/*  382 */       arrayOfString1 = new String[k];
/*  383 */       for (int m = 0; m < k; m++) {
/*  384 */         arrayOfString1[m] = ((StringTokenizer)localObject1).nextToken();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  389 */     Object localObject1 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.enabled.protocols");
/*  390 */     String[] arrayOfString2 = null;
/*  391 */     if (localObject1 != null) {
/*  392 */       localObject2 = new StringTokenizer((String)localObject1, ",");
/*  393 */       int n = ((StringTokenizer)localObject2).countTokens();
/*  394 */       arrayOfString2 = new String[n];
/*  395 */       for (int i1 = 0; i1 < n; i1++) {
/*  396 */         arrayOfString2[i1] = ((StringTokenizer)localObject2).nextToken();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  401 */     Object localObject2 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.need.client.auth", "false");
/*      */     
/*      */ 
/*  404 */     boolean bool4 = Boolean.valueOf((String)localObject2).booleanValue();
/*      */     
/*      */ 
/*      */ 
/*  408 */     String str6 = paramProperties.getProperty("com.sun.management.jmxremote.ssl.config.file");
/*      */     
/*  410 */     String str7 = null;
/*  411 */     String str8 = null;
/*  412 */     String str9 = null;
/*      */     
/*      */ 
/*  415 */     if (bool1)
/*      */     {
/*      */ 
/*      */ 
/*  419 */       str7 = paramProperties.getProperty("com.sun.management.jmxremote.login.config");
/*      */       
/*  421 */       if (str7 == null)
/*      */       {
/*      */ 
/*  424 */         str8 = paramProperties.getProperty("com.sun.management.jmxremote.password.file", 
/*  425 */           getDefaultFileName("jmxremote.password"));
/*  426 */         checkPasswordFile(str8);
/*      */       }
/*      */       
/*      */ 
/*  430 */       str9 = paramProperties.getProperty("com.sun.management.jmxremote.access.file", 
/*  431 */         getDefaultFileName("jmxremote.access"));
/*  432 */       checkAccessFile(str9);
/*      */     }
/*      */     
/*      */ 
/*  436 */     String str10 = paramProperties.getProperty("com.sun.management.jmxremote.host");
/*      */     
/*  438 */     if (log.debugOn()) {
/*  439 */       log.debug("startRemoteConnectorServer", 
/*  440 */         Agent.getText("jmxremote.ConnectorBootstrap.starting") + "\n\t" + "com.sun.management.jmxremote.port" + "=" + i + (str10 == null ? "" : new StringBuilder().append("\n\tcom.sun.management.jmxremote.host=").append(str10).toString()) + "\n\t" + "com.sun.management.jmxremote.rmi.port" + "=" + j + "\n\t" + "com.sun.management.jmxremote.ssl" + "=" + bool2 + "\n\t" + "com.sun.management.jmxremote.registry.ssl" + "=" + bool3 + "\n\t" + "com.sun.management.jmxremote.ssl.config.file" + "=" + str6 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.cipher.suites" + "=" + str5 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.protocols" + "=" + (String)localObject1 + "\n\t" + "com.sun.management.jmxremote.ssl.need.client.auth" + "=" + bool4 + "\n\t" + "com.sun.management.jmxremote.authenticate" + "=" + bool1 + (bool1 ? "\n\tcom.sun.management.jmxremote.login.config=" + str7 : str7 == null ? "\n\tcom.sun.management.jmxremote.password.file=" + str8 : new StringBuilder().append("\n\t")
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  458 */         .append(Agent.getText("jmxremote.ConnectorBootstrap.noAuthentication")).toString()) + (bool1 ? "\n\tcom.sun.management.jmxremote.access.file=" + str9 : "") + "");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  464 */     MBeanServer localMBeanServer = ManagementFactory.getPlatformMBeanServer();
/*  465 */     JMXConnectorServer localJMXConnectorServer = null;
/*  466 */     JMXServiceURL localJMXServiceURL = null;
/*      */     try {
/*  468 */       JMXConnectorServerData localJMXConnectorServerData = exportMBeanServer(localMBeanServer, i, j, bool2, bool3, str6, arrayOfString1, arrayOfString2, bool4, bool1, str7, str8, str9, str10);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  474 */       localJMXConnectorServer = localJMXConnectorServerData.jmxConnectorServer;
/*  475 */       localJMXServiceURL = localJMXConnectorServerData.jmxRemoteURL;
/*  476 */       log.config("startRemoteConnectorServer", 
/*  477 */         Agent.getText("jmxremote.ConnectorBootstrap.ready", new String[] {localJMXServiceURL
/*  478 */         .toString() }));
/*      */     } catch (Exception localException1) {
/*  480 */       throw new AgentConfigurationError("agent.err.exception", localException1, new String[] { localException1.toString() });
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  485 */       HashMap localHashMap = new HashMap();
/*  486 */       localHashMap.put("remoteAddress", localJMXServiceURL.toString());
/*  487 */       localHashMap.put("authenticate", str2);
/*  488 */       localHashMap.put("ssl", str3);
/*  489 */       localHashMap.put("sslRegistry", str4);
/*  490 */       localHashMap.put("sslNeedClientAuth", localObject2);
/*  491 */       ConnectorAddressLink.exportRemote(localHashMap);
/*      */ 
/*      */     }
/*      */     catch (Exception localException2)
/*      */     {
/*  496 */       log.debug("startRemoteConnectorServer", localException2);
/*      */     }
/*  498 */     return localJMXConnectorServer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static JMXConnectorServer startLocalConnectorServer()
/*      */   {
/*  508 */     System.setProperty("java.rmi.server.randomIDs", "true");
/*      */     
/*      */ 
/*  511 */     HashMap localHashMap = new HashMap();
/*  512 */     localHashMap.put("com.sun.jmx.remote.rmi.exporter", new PermanentExporter(null));
/*  513 */     localHashMap.put("jmx.remote.rmi.server.credential.types", new String[] {String[].class
/*  514 */       .getName(), String.class.getName() });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  519 */     String str1 = "localhost";
/*  520 */     InetAddress localInetAddress = null;
/*      */     try {
/*  522 */       localInetAddress = InetAddress.getByName(str1);
/*  523 */       str1 = localInetAddress.getHostAddress();
/*      */     }
/*      */     catch (UnknownHostException localUnknownHostException) {}
/*      */     
/*      */ 
/*      */ 
/*  529 */     if ((localInetAddress == null) || (!localInetAddress.isLoopbackAddress())) {
/*  530 */       str1 = "127.0.0.1";
/*      */     }
/*      */     
/*  533 */     MBeanServer localMBeanServer = ManagementFactory.getPlatformMBeanServer();
/*      */     try {
/*  535 */       JMXServiceURL localJMXServiceURL = new JMXServiceURL("rmi", str1, 0);
/*      */       
/*  537 */       Properties localProperties = Agent.getManagementProperties();
/*  538 */       if (localProperties == null) {
/*  539 */         localProperties = new Properties();
/*      */       }
/*  541 */       String str2 = localProperties.getProperty("com.sun.management.jmxremote.local.only", "true");
/*      */       
/*  543 */       boolean bool = Boolean.valueOf(str2).booleanValue();
/*  544 */       if (bool) {
/*  545 */         localHashMap.put("jmx.remote.rmi.server.socket.factory", new LocalRMIServerSocketFactory());
/*      */       }
/*      */       
/*      */ 
/*  549 */       JMXConnectorServer localJMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(localJMXServiceURL, localHashMap, localMBeanServer);
/*  550 */       localJMXConnectorServer.start();
/*  551 */       return localJMXConnectorServer;
/*      */     } catch (Exception localException) {
/*  553 */       throw new AgentConfigurationError("agent.err.exception", localException, new String[] { localException.toString() });
/*      */     }
/*      */   }
/*      */   
/*      */   private static void checkPasswordFile(String paramString) {
/*  558 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  559 */       throw new AgentConfigurationError("agent.err.password.file.notset");
/*      */     }
/*  561 */     File localFile = new File(paramString);
/*  562 */     if (!localFile.exists()) {
/*  563 */       throw new AgentConfigurationError("agent.err.password.file.notfound", new String[] { paramString });
/*      */     }
/*      */     
/*  566 */     if (!localFile.canRead()) {
/*  567 */       throw new AgentConfigurationError("agent.err.password.file.not.readable", new String[] { paramString });
/*      */     }
/*      */     
/*  570 */     FileSystem localFileSystem = FileSystem.open();
/*      */     try {
/*  572 */       if ((localFileSystem.supportsFileSecurity(localFile)) && 
/*  573 */         (!localFileSystem.isAccessUserOnly(localFile))) {
/*  574 */         String str = Agent.getText("jmxremote.ConnectorBootstrap.password.readonly", new String[] { paramString });
/*      */         
/*  576 */         log.config("startRemoteConnectorServer", str);
/*  577 */         throw new AgentConfigurationError("agent.err.password.file.access.notrestricted", new String[] { paramString });
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  582 */       throw new AgentConfigurationError("agent.err.password.file.read.failed", localIOException, new String[] { paramString });
/*      */     }
/*      */   }
/*      */   
/*      */   private static void checkAccessFile(String paramString)
/*      */   {
/*  588 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  589 */       throw new AgentConfigurationError("agent.err.access.file.notset");
/*      */     }
/*  591 */     File localFile = new File(paramString);
/*  592 */     if (!localFile.exists()) {
/*  593 */       throw new AgentConfigurationError("agent.err.access.file.notfound", new String[] { paramString });
/*      */     }
/*      */     
/*  596 */     if (!localFile.canRead()) {
/*  597 */       throw new AgentConfigurationError("agent.err.access.file.not.readable", new String[] { paramString });
/*      */     }
/*      */   }
/*      */   
/*      */   private static void checkRestrictedFile(String paramString) {
/*  602 */     if ((paramString == null) || (paramString.length() == 0)) {
/*  603 */       throw new AgentConfigurationError("agent.err.file.not.set");
/*      */     }
/*  605 */     File localFile = new File(paramString);
/*  606 */     if (!localFile.exists()) {
/*  607 */       throw new AgentConfigurationError("agent.err.file.not.found", new String[] { paramString });
/*      */     }
/*  609 */     if (!localFile.canRead()) {
/*  610 */       throw new AgentConfigurationError("agent.err.file.not.readable", new String[] { paramString });
/*      */     }
/*  612 */     FileSystem localFileSystem = FileSystem.open();
/*      */     try {
/*  614 */       if ((localFileSystem.supportsFileSecurity(localFile)) && 
/*  615 */         (!localFileSystem.isAccessUserOnly(localFile))) {
/*  616 */         String str = Agent.getText("jmxremote.ConnectorBootstrap.file.readonly", new String[] { paramString });
/*      */         
/*      */ 
/*  619 */         log.config("startRemoteConnectorServer", str);
/*  620 */         throw new AgentConfigurationError("agent.err.file.access.not.restricted", new String[] { paramString });
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*  625 */       throw new AgentConfigurationError("agent.err.file.read.failed", localIOException, new String[] { paramString });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String getDefaultFileName(String paramString)
/*      */   {
/*  636 */     String str = File.separator;
/*  637 */     return System.getProperty("java.home") + str + "lib" + str + "management" + str + paramString;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static SslRMIServerSocketFactory createSslRMIServerSocketFactory(String paramString1, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString2)
/*      */   {
/*  648 */     if (paramString1 == null) {
/*  649 */       return new HostAwareSslSocketFactory(paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString2, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  654 */     checkRestrictedFile(paramString1);
/*      */     try
/*      */     {
/*  657 */       Properties localProperties = new Properties();
/*  658 */       Object localObject1 = new FileInputStream(paramString1);Object localObject2 = null;
/*  659 */       try { BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject1);
/*  660 */         localProperties.load(localBufferedInputStream);
/*      */       }
/*      */       catch (Throwable localThrowable2)
/*      */       {
/*  658 */         localObject2 = localThrowable2;throw localThrowable2;
/*      */       }
/*      */       finally {
/*  661 */         if (localObject1 != null) if (localObject2 != null) try { ((InputStream)localObject1).close(); } catch (Throwable localThrowable3) { ((Throwable)localObject2).addSuppressed(localThrowable3); } else ((InputStream)localObject1).close();
/*      */       }
/*  663 */       localObject1 = localProperties.getProperty("javax.net.ssl.keyStore");
/*      */       
/*  665 */       localObject2 = localProperties.getProperty("javax.net.ssl.keyStorePassword", "");
/*      */       
/*  667 */       String str1 = localProperties.getProperty("javax.net.ssl.trustStore");
/*      */       
/*  669 */       String str2 = localProperties.getProperty("javax.net.ssl.trustStorePassword", "");
/*      */       
/*  671 */       char[] arrayOfChar1 = null;
/*  672 */       if (((String)localObject2).length() != 0) {
/*  673 */         arrayOfChar1 = ((String)localObject2).toCharArray();
/*      */       }
/*      */       
/*  676 */       char[] arrayOfChar2 = null;
/*  677 */       if (str2.length() != 0) {
/*  678 */         arrayOfChar2 = str2.toCharArray();
/*      */       }
/*      */       
/*  681 */       KeyStore localKeyStore = null;
/*  682 */       if (localObject1 != null) {
/*  683 */         localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
/*  684 */         localObject4 = new FileInputStream((String)localObject1);localObject5 = null;
/*  685 */         try { localKeyStore.load((InputStream)localObject4, arrayOfChar1);
/*      */         }
/*      */         catch (Throwable localThrowable5)
/*      */         {
/*  684 */           localObject5 = localThrowable5;throw localThrowable5;
/*      */         } finally {
/*  686 */           if (localObject4 != null) if (localObject5 != null) try { ((FileInputStream)localObject4).close(); } catch (Throwable localThrowable6) { ((Throwable)localObject5).addSuppressed(localThrowable6); } else ((FileInputStream)localObject4).close();
/*      */         } }
/*  688 */       Object localObject4 = KeyManagerFactory.getInstance(
/*  689 */         KeyManagerFactory.getDefaultAlgorithm());
/*  690 */       ((KeyManagerFactory)localObject4).init(localKeyStore, arrayOfChar1);
/*      */       
/*  692 */       Object localObject5 = null;
/*  693 */       if (str1 != null) {
/*  694 */         localObject5 = KeyStore.getInstance(KeyStore.getDefaultType());
/*  695 */         localObject6 = new FileInputStream(str1);localObject8 = null;
/*  696 */         try { ((KeyStore)localObject5).load((InputStream)localObject6, arrayOfChar2);
/*      */         }
/*      */         catch (Throwable localThrowable8)
/*      */         {
/*  695 */           localObject8 = localThrowable8;throw localThrowable8;
/*      */         } finally {
/*  697 */           if (localObject6 != null) if (localObject8 != null) try { ((FileInputStream)localObject6).close(); } catch (Throwable localThrowable9) { ((Throwable)localObject8).addSuppressed(localThrowable9); } else ((FileInputStream)localObject6).close();
/*      */         } }
/*  699 */       Object localObject6 = TrustManagerFactory.getInstance(
/*  700 */         TrustManagerFactory.getDefaultAlgorithm());
/*  701 */       ((TrustManagerFactory)localObject6).init((KeyStore)localObject5);
/*      */       
/*  703 */       Object localObject8 = SSLContext.getInstance("SSL");
/*  704 */       ((SSLContext)localObject8).init(((KeyManagerFactory)localObject4).getKeyManagers(), ((TrustManagerFactory)localObject6).getTrustManagers(), null);
/*      */       
/*  706 */       return new HostAwareSslSocketFactory((SSLContext)localObject8, paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString2, null);
/*      */ 
/*      */     }
/*      */     catch (Exception localException)
/*      */     {
/*      */ 
/*  712 */       throw new AgentConfigurationError("agent.err.exception", localException, new String[] { localException.toString() });
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
/*      */   private static JMXConnectorServerData exportMBeanServer(MBeanServer paramMBeanServer, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, boolean paramBoolean4, String paramString2, String paramString3, String paramString4, String paramString5)
/*      */     throws IOException, MalformedURLException
/*      */   {
/*  737 */     System.setProperty("java.rmi.server.randomIDs", "true");
/*      */     
/*  739 */     JMXServiceURL localJMXServiceURL1 = new JMXServiceURL("rmi", paramString5, paramInt2);
/*      */     
/*  741 */     HashMap localHashMap = new HashMap();
/*      */     
/*  743 */     PermanentExporter localPermanentExporter = new PermanentExporter(null);
/*      */     
/*  745 */     localHashMap.put("com.sun.jmx.remote.rmi.exporter", localPermanentExporter);
/*  746 */     localHashMap.put("jmx.remote.rmi.server.credential.types", new String[] {String[].class
/*  747 */       .getName(), String.class.getName() });
/*      */     
/*      */ 
/*  750 */     int i = (paramString5 != null) && (!paramBoolean1) ? 1 : 0;
/*      */     
/*  752 */     if (paramBoolean4) {
/*  753 */       if (paramString2 != null) {
/*  754 */         localHashMap.put("jmx.remote.x.login.config", paramString2);
/*      */       }
/*  756 */       if (paramString3 != null) {
/*  757 */         localHashMap.put("jmx.remote.x.password.file", paramString3);
/*      */       }
/*      */       
/*  760 */       localHashMap.put("jmx.remote.x.access.file", paramString4);
/*      */       
/*  762 */       if ((localHashMap.get("jmx.remote.x.password.file") != null) || 
/*  763 */         (localHashMap.get("jmx.remote.x.login.config") != null)) {
/*  764 */         localHashMap.put("jmx.remote.authenticator", new AccessFileCheckerAuthenticator(localHashMap));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  769 */     SslRMIClientSocketFactory localSslRMIClientSocketFactory = null;
/*  770 */     Object localObject = null;
/*      */     
/*  772 */     if ((paramBoolean1) || (paramBoolean2)) {
/*  773 */       localSslRMIClientSocketFactory = new SslRMIClientSocketFactory();
/*  774 */       localObject = createSslRMIServerSocketFactory(paramString1, paramArrayOfString1, paramArrayOfString2, paramBoolean3, paramString5);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  779 */     if (paramBoolean1) {
/*  780 */       localHashMap.put("jmx.remote.rmi.client.socket.factory", localSslRMIClientSocketFactory);
/*      */       
/*  782 */       localHashMap.put("jmx.remote.rmi.server.socket.factory", localObject);
/*      */     }
/*      */     
/*      */ 
/*  786 */     if (i != 0) {
/*  787 */       localObject = new HostAwareSocketFactory(paramString5, null);
/*  788 */       localHashMap.put("jmx.remote.rmi.server.socket.factory", localObject);
/*      */     }
/*      */     
/*      */ 
/*  792 */     JMXConnectorServer localJMXConnectorServer = null;
/*      */     try
/*      */     {
/*  795 */       localJMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(localJMXServiceURL1, localHashMap, paramMBeanServer);
/*  796 */       localJMXConnectorServer.start();
/*      */     } catch (IOException localIOException) {
/*  798 */       if ((localJMXConnectorServer == null) || (localJMXConnectorServer.getAddress() == null))
/*      */       {
/*  800 */         throw new AgentConfigurationError("agent.err.connector.server.io.error", localIOException, new String[] {localJMXServiceURL1.toString() });
/*      */       }
/*      */       
/*  803 */       throw new AgentConfigurationError("agent.err.connector.server.io.error", localIOException, new String[] {localJMXConnectorServer.getAddress().toString() });
/*      */     }
/*      */     
/*      */ 
/*  807 */     if (paramBoolean2) {
/*  808 */       registry = new SingleEntryRegistry(paramInt1, localSslRMIClientSocketFactory, (RMIServerSocketFactory)localObject, "jmxrmi", localPermanentExporter.firstExported);
/*      */ 
/*      */     }
/*  811 */     else if (i != 0) {
/*  812 */       registry = new SingleEntryRegistry(paramInt1, localSslRMIClientSocketFactory, (RMIServerSocketFactory)localObject, "jmxrmi", localPermanentExporter.firstExported);
/*      */     }
/*      */     else
/*      */     {
/*  816 */       registry = new SingleEntryRegistry(paramInt1, "jmxrmi", localPermanentExporter.firstExported);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  823 */     int j = ((UnicastRef)((RemoteObject)registry).getRef()).getLiveRef().getPort();
/*  824 */     String str = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", new Object[] {localJMXServiceURL1
/*  825 */       .getHost(), Integer.valueOf(j) });
/*  826 */     JMXServiceURL localJMXServiceURL2 = new JMXServiceURL(str);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  836 */     return new JMXConnectorServerData(localJMXConnectorServer, localJMXServiceURL2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  845 */   private static final ClassLogger log = new ClassLogger(ConnectorBootstrap.class
/*  846 */     .getPackage().getName(), "ConnectorBootstrap");
/*      */   
/*      */   private static class HostAwareSocketFactory implements RMIServerSocketFactory
/*      */   {
/*      */     private final String bindAddress;
/*      */     
/*      */     private HostAwareSocketFactory(String paramString)
/*      */     {
/*  854 */       this.bindAddress = paramString;
/*      */     }
/*      */     
/*      */     public ServerSocket createServerSocket(int paramInt) throws IOException
/*      */     {
/*  859 */       if (this.bindAddress == null) {
/*  860 */         return new ServerSocket(paramInt);
/*      */       }
/*      */       try {
/*  863 */         InetAddress localInetAddress = InetAddress.getByName(this.bindAddress);
/*  864 */         return new ServerSocket(paramInt, 0, localInetAddress);
/*      */       } catch (UnknownHostException localUnknownHostException) {}
/*  866 */       return new ServerSocket(paramInt);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class HostAwareSslSocketFactory
/*      */     extends SslRMIServerSocketFactory
/*      */   {
/*      */     private final String bindAddress;
/*      */     
/*      */     private final String[] enabledCipherSuites;
/*      */     private final String[] enabledProtocols;
/*      */     private final boolean needClientAuth;
/*      */     private final SSLContext context;
/*      */     
/*      */     private HostAwareSslSocketFactory(String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString)
/*      */       throws IllegalArgumentException
/*      */     {
/*  884 */       this(null, paramArrayOfString1, paramArrayOfString2, paramBoolean, paramString);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private HostAwareSslSocketFactory(SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean, String paramString)
/*      */       throws IllegalArgumentException
/*      */     {
/*  892 */       this.context = paramSSLContext;
/*  893 */       this.bindAddress = paramString;
/*  894 */       this.enabledProtocols = paramArrayOfString2;
/*  895 */       this.enabledCipherSuites = paramArrayOfString1;
/*  896 */       this.needClientAuth = paramBoolean;
/*  897 */       checkValues(paramSSLContext, paramArrayOfString1, paramArrayOfString2);
/*      */     }
/*      */     
/*      */     public ServerSocket createServerSocket(int paramInt) throws IOException
/*      */     {
/*  902 */       if (this.bindAddress != null) {
/*      */         try {
/*  904 */           InetAddress localInetAddress = InetAddress.getByName(this.bindAddress);
/*  905 */           return new SslServerSocket(paramInt, 0, localInetAddress, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
/*      */         }
/*      */         catch (UnknownHostException localUnknownHostException) {
/*  908 */           return new SslServerSocket(paramInt, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
/*      */         }
/*      */       }
/*      */       
/*  912 */       return new SslServerSocket(paramInt, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth, null);
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
/*      */     private static void checkValues(SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2)
/*      */       throws IllegalArgumentException
/*      */     {
/*  926 */       SSLSocketFactory localSSLSocketFactory = paramSSLContext == null ? (SSLSocketFactory)SSLSocketFactory.getDefault() : paramSSLContext.getSocketFactory();
/*  927 */       SSLSocket localSSLSocket = null;
/*  928 */       if ((paramArrayOfString1 != null) || (paramArrayOfString2 != null)) {
/*      */         try {
/*  930 */           localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket();
/*      */ 
/*      */         }
/*      */         catch (Exception localException)
/*      */         {
/*  935 */           throw ((IllegalArgumentException)new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported").initCause(localException));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  943 */       if (paramArrayOfString1 != null) {
/*  944 */         localSSLSocket.setEnabledCipherSuites(paramArrayOfString1);
/*      */       }
/*  946 */       if (paramArrayOfString2 != null) {
/*  947 */         localSSLSocket.setEnabledProtocols(paramArrayOfString2);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class SslServerSocket
/*      */     extends ServerSocket
/*      */   {
/*      */     private static SSLSocketFactory defaultSSLSocketFactory;
/*      */     private final String[] enabledCipherSuites;
/*      */     private final String[] enabledProtocols;
/*      */     private final boolean needClientAuth;
/*      */     private final SSLContext context;
/*      */     
/*      */     private SslServerSocket(int paramInt, SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/*  965 */       super();
/*  966 */       this.enabledProtocols = paramArrayOfString2;
/*  967 */       this.enabledCipherSuites = paramArrayOfString1;
/*  968 */       this.needClientAuth = paramBoolean;
/*  969 */       this.context = paramSSLContext;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private SslServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress, SSLContext paramSSLContext, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
/*      */       throws IOException
/*      */     {
/*  979 */       super(paramInt2, paramInetAddress);
/*  980 */       this.enabledProtocols = paramArrayOfString2;
/*  981 */       this.enabledCipherSuites = paramArrayOfString1;
/*  982 */       this.needClientAuth = paramBoolean;
/*  983 */       this.context = paramSSLContext;
/*      */     }
/*      */     
/*      */ 
/*      */     public Socket accept()
/*      */       throws IOException
/*      */     {
/*  990 */       SSLSocketFactory localSSLSocketFactory = this.context == null ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
/*  991 */       Socket localSocket = super.accept();
/*  992 */       SSLSocket localSSLSocket = (SSLSocket)localSSLSocketFactory.createSocket(localSocket, localSocket
/*  993 */         .getInetAddress().getHostName(), localSocket
/*  994 */         .getPort(), true);
/*  995 */       localSSLSocket.setUseClientMode(false);
/*  996 */       if (this.enabledCipherSuites != null) {
/*  997 */         localSSLSocket.setEnabledCipherSuites(this.enabledCipherSuites);
/*      */       }
/*  999 */       if (this.enabledProtocols != null) {
/* 1000 */         localSSLSocket.setEnabledProtocols(this.enabledProtocols);
/*      */       }
/* 1002 */       localSSLSocket.setNeedClientAuth(this.needClientAuth);
/* 1003 */       return localSSLSocket;
/*      */     }
/*      */     
/*      */     private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
/* 1007 */       if (defaultSSLSocketFactory == null) {
/* 1008 */         defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
/* 1009 */         return defaultSSLSocketFactory;
/*      */       }
/* 1011 */       return defaultSSLSocketFactory;
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface DefaultValues
/*      */   {
/*      */     public static final String PORT = "0";
/*      */     public static final String CONFIG_FILE_NAME = "management.properties";
/*      */     public static final String USE_SSL = "true";
/*      */     public static final String USE_LOCAL_ONLY = "true";
/*      */     public static final String USE_REGISTRY_SSL = "false";
/*      */     public static final String USE_AUTHENTICATION = "true";
/*      */     public static final String PASSWORD_FILE_NAME = "jmxremote.password";
/*      */     public static final String ACCESS_FILE_NAME = "jmxremote.access";
/*      */     public static final String SSL_NEED_CLIENT_AUTH = "false";
/*      */   }
/*      */   
/*      */   public static abstract interface PropertyNames
/*      */   {
/*      */     public static final String PORT = "com.sun.management.jmxremote.port";
/*      */     public static final String HOST = "com.sun.management.jmxremote.host";
/*      */     public static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
/*      */     public static final String CONFIG_FILE_NAME = "com.sun.management.config.file";
/*      */     public static final String USE_LOCAL_ONLY = "com.sun.management.jmxremote.local.only";
/*      */     public static final String USE_SSL = "com.sun.management.jmxremote.ssl";
/*      */     public static final String USE_REGISTRY_SSL = "com.sun.management.jmxremote.registry.ssl";
/*      */     public static final String USE_AUTHENTICATION = "com.sun.management.jmxremote.authenticate";
/*      */     public static final String PASSWORD_FILE_NAME = "com.sun.management.jmxremote.password.file";
/*      */     public static final String ACCESS_FILE_NAME = "com.sun.management.jmxremote.access.file";
/*      */     public static final String LOGIN_CONFIG_NAME = "com.sun.management.jmxremote.login.config";
/*      */     public static final String SSL_ENABLED_CIPHER_SUITES = "com.sun.management.jmxremote.ssl.enabled.cipher.suites";
/*      */     public static final String SSL_ENABLED_PROTOCOLS = "com.sun.management.jmxremote.ssl.enabled.protocols";
/*      */     public static final String SSL_NEED_CLIENT_AUTH = "com.sun.management.jmxremote.ssl.need.client.auth";
/*      */     public static final String SSL_CONFIG_FILE_NAME = "com.sun.management.jmxremote.ssl.config.file";
/*      */   }
/*      */ }


/* Location:              E:\java_source\rt.jar!\sun\management\jmxremote\ConnectorBootstrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */