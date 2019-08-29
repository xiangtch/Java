/*     */ package sun.management;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Properties;
/*     */ import java.util.ResourceBundle;
/*     */ import javax.management.remote.JMXConnectorServer;
/*     */ import javax.management.remote.JMXServiceURL;
/*     */ import sun.management.jdp.JdpController;
/*     */ import sun.management.jdp.JdpException;
/*     */ import sun.management.jmxremote.ConnectorBootstrap;
/*     */ import sun.misc.VMSupport;
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
/*     */ public class Agent
/*     */ {
/*     */   private static Properties mgmtProps;
/*     */   private static ResourceBundle messageRB;
/*     */   private static final String CONFIG_FILE = "com.sun.management.config.file";
/*     */   private static final String SNMP_PORT = "com.sun.management.snmp.port";
/*     */   private static final String JMXREMOTE = "com.sun.management.jmxremote";
/*     */   private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
/*     */   private static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
/*     */   private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
/*     */   private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
/*     */   private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
/*     */   private static final String JDP_DEFAULT_ADDRESS = "224.0.23.178";
/*     */   private static final int JDP_DEFAULT_PORT = 7095;
/*  83 */   private static JMXConnectorServer jmxServer = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private static Properties parseString(String paramString)
/*     */   {
/*  89 */     Properties localProperties = new Properties();
/*  90 */     if ((paramString != null) && (!paramString.trim().equals(""))) {
/*  91 */       for (String str1 : paramString.split(",")) {
/*  92 */         String[] arrayOfString2 = str1.split("=", 2);
/*  93 */         String str2 = arrayOfString2[0].trim();
/*  94 */         String str3 = arrayOfString2.length > 1 ? arrayOfString2[1].trim() : "";
/*     */         
/*  96 */         if (!str2.startsWith("com.sun.management.")) {
/*  97 */           error("agent.err.invalid.option", str2);
/*     */         }
/*     */         
/* 100 */         localProperties.setProperty(str2, str3);
/*     */       }
/*     */     }
/*     */     
/* 104 */     return localProperties;
/*     */   }
/*     */   
/*     */   public static void premain(String paramString) throws Exception
/*     */   {
/* 109 */     agentmain(paramString);
/*     */   }
/*     */   
/*     */   public static void agentmain(String paramString) throws Exception
/*     */   {
/* 114 */     if ((paramString == null) || (paramString.length() == 0)) {
/* 115 */       paramString = "com.sun.management.jmxremote";
/*     */     }
/*     */     
/* 118 */     Properties localProperties1 = parseString(paramString);
/*     */     
/*     */ 
/* 121 */     Properties localProperties2 = new Properties();
/* 122 */     String str = localProperties1.getProperty("com.sun.management.config.file");
/* 123 */     readConfiguration(str, localProperties2);
/*     */     
/*     */ 
/* 126 */     localProperties2.putAll(localProperties1);
/* 127 */     startAgent(localProperties2);
/*     */   }
/*     */   
/*     */ 
/*     */   private static synchronized void startLocalManagementAgent()
/*     */   {
/* 133 */     Properties localProperties = VMSupport.getAgentProperties();
/*     */     
/*     */ 
/* 136 */     if (localProperties.get("com.sun.management.jmxremote.localConnectorAddress") == null) {
/* 137 */       JMXConnectorServer localJMXConnectorServer = ConnectorBootstrap.startLocalConnectorServer();
/* 138 */       String str = localJMXConnectorServer.getAddress().toString();
/*     */       
/* 140 */       localProperties.put("com.sun.management.jmxremote.localConnectorAddress", str);
/*     */       
/*     */       try
/*     */       {
/* 144 */         ConnectorAddressLink.export(str);
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 148 */         warning("agent.err.exportaddress.failed", localException.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static synchronized void startRemoteManagementAgent(String paramString)
/*     */     throws Exception
/*     */   {
/* 158 */     if (jmxServer != null) {
/* 159 */       throw new RuntimeException(getText("agent.err.invalid.state", new String[] { "Agent already started" }));
/*     */     }
/*     */     try
/*     */     {
/* 163 */       Properties localProperties1 = parseString(paramString);
/* 164 */       Properties localProperties2 = new Properties();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 170 */       String str1 = System.getProperty("com.sun.management.config.file");
/* 171 */       readConfiguration(str1, localProperties2);
/*     */       
/*     */ 
/*     */ 
/* 175 */       Properties localProperties3 = System.getProperties();
/* 176 */       synchronized (localProperties3) {
/* 177 */         localProperties2.putAll(localProperties3);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 183 */       ??? = localProperties1.getProperty("com.sun.management.config.file");
/* 184 */       if (??? != null) {
/* 185 */         readConfiguration((String)???, localProperties2);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 191 */       localProperties2.putAll(localProperties1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 197 */       String str2 = localProperties2.getProperty("com.sun.management.enableThreadContentionMonitoring");
/*     */       
/* 199 */       if (str2 != null)
/*     */       {
/* 201 */         ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
/*     */       }
/*     */       
/* 204 */       String str3 = localProperties2.getProperty("com.sun.management.jmxremote.port");
/* 205 */       if (str3 != null)
/*     */       {
/* 207 */         jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, localProperties2);
/*     */         
/* 209 */         startDiscoveryService(localProperties2);
/*     */       } else {
/* 211 */         throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { "No port specified" });
/*     */       }
/*     */     } catch (AgentConfigurationError localAgentConfigurationError) {
/* 214 */       error(localAgentConfigurationError.getError(), localAgentConfigurationError.getParams());
/*     */     }
/*     */   }
/*     */   
/*     */   private static synchronized void stopRemoteManagementAgent()
/*     */     throws Exception
/*     */   {
/*     */     
/* 222 */     if (jmxServer != null) {
/* 223 */       ConnectorBootstrap.unexportRegistry();
/*     */       
/*     */ 
/*     */ 
/* 227 */       jmxServer.stop();
/* 228 */       jmxServer = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static void startAgent(Properties paramProperties) throws Exception {
/* 233 */     String str1 = paramProperties.getProperty("com.sun.management.snmp.port");
/* 234 */     String str2 = paramProperties.getProperty("com.sun.management.jmxremote");
/* 235 */     String str3 = paramProperties.getProperty("com.sun.management.jmxremote.port");
/*     */     
/*     */ 
/*     */ 
/* 239 */     String str4 = paramProperties.getProperty("com.sun.management.enableThreadContentionMonitoring");
/* 240 */     if (str4 != null)
/*     */     {
/* 242 */       ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
/*     */     }
/*     */     try
/*     */     {
/* 246 */       if (str1 != null) {
/* 247 */         loadSnmpAgent(str1, paramProperties);
/*     */       }
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
/* 259 */       if ((str2 != null) || (str3 != null)) {
/* 260 */         if (str3 != null)
/*     */         {
/* 262 */           jmxServer = ConnectorBootstrap.startRemoteConnectorServer(str3, paramProperties);
/* 263 */           startDiscoveryService(paramProperties);
/*     */         }
/* 265 */         startLocalManagementAgent();
/*     */       }
/*     */     }
/*     */     catch (AgentConfigurationError localAgentConfigurationError) {
/* 269 */       error(localAgentConfigurationError.getError(), localAgentConfigurationError.getParams());
/*     */     } catch (Exception localException) {
/* 271 */       error(localException);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void startDiscoveryService(Properties paramProperties)
/*     */     throws IOException
/*     */   {
/* 278 */     String str1 = paramProperties.getProperty("com.sun.management.jdp.port");
/* 279 */     String str2 = paramProperties.getProperty("com.sun.management.jdp.address");
/* 280 */     String str3 = paramProperties.getProperty("com.sun.management.jmxremote.autodiscovery");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */     boolean bool = false;
/* 287 */     if (str3 == null) {
/* 288 */       bool = str1 != null;
/*     */     } else {
/*     */       try
/*     */       {
/* 292 */         bool = Boolean.parseBoolean(str3);
/*     */       } catch (NumberFormatException localNumberFormatException1) {
/* 294 */         throw new AgentConfigurationError("Couldn't parse autodiscovery argument");
/*     */       }
/*     */     }
/*     */     
/* 298 */     if (bool)
/*     */     {
/*     */       InetAddress localInetAddress;
/*     */       try
/*     */       {
/* 303 */         localInetAddress = str2 == null ? InetAddress.getByName("224.0.23.178") : InetAddress.getByName(str2);
/*     */       } catch (UnknownHostException localUnknownHostException) {
/* 305 */         throw new AgentConfigurationError("Unable to broadcast to requested address", localUnknownHostException);
/*     */       }
/*     */       
/* 308 */       int i = 7095;
/* 309 */       if (str1 != null) {
/*     */         try {
/* 311 */           i = Integer.parseInt(str1);
/*     */         } catch (NumberFormatException localNumberFormatException2) {
/* 313 */           throw new AgentConfigurationError("Couldn't parse JDP port argument");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 318 */       String str4 = paramProperties.getProperty("com.sun.management.jmxremote.port");
/* 319 */       String str5 = paramProperties.getProperty("com.sun.management.jmxremote.rmi.port");
/*     */       
/* 321 */       JMXServiceURL localJMXServiceURL = jmxServer.getAddress();
/* 322 */       String str6 = localJMXServiceURL.getHost();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 328 */       String str7 = str5 != null ? String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str5, str6, str4 }) : String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", new Object[] { str6, str4 });
/*     */       
/*     */ 
/* 331 */       String str8 = paramProperties.getProperty("com.sun.management.jdp.name");
/*     */       try
/*     */       {
/* 334 */         JdpController.startDiscoveryService(localInetAddress, i, str8, str7);
/*     */       }
/*     */       catch (JdpException localJdpException) {
/* 337 */         throw new AgentConfigurationError("Couldn't start JDP service", localJdpException);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static Properties loadManagementProperties() {
/* 343 */     Properties localProperties1 = new Properties();
/*     */     
/*     */ 
/*     */ 
/* 347 */     String str = System.getProperty("com.sun.management.config.file");
/* 348 */     readConfiguration(str, localProperties1);
/*     */     
/*     */ 
/*     */ 
/* 352 */     Properties localProperties2 = System.getProperties();
/* 353 */     synchronized (localProperties2) {
/* 354 */       localProperties1.putAll(localProperties2);
/*     */     }
/*     */     
/* 357 */     return localProperties1;
/*     */   }
/*     */   
/*     */   public static synchronized Properties getManagementProperties() {
/* 361 */     if (mgmtProps == null) {
/* 362 */       String str1 = System.getProperty("com.sun.management.config.file");
/* 363 */       String str2 = System.getProperty("com.sun.management.snmp.port");
/* 364 */       String str3 = System.getProperty("com.sun.management.jmxremote");
/* 365 */       String str4 = System.getProperty("com.sun.management.jmxremote.port");
/*     */       
/* 367 */       if ((str1 == null) && (str2 == null) && (str3 == null) && (str4 == null))
/*     */       {
/*     */ 
/* 370 */         return null;
/*     */       }
/* 372 */       mgmtProps = loadManagementProperties();
/*     */     }
/* 374 */     return mgmtProps;
/*     */   }
/*     */   
/*     */ 
/*     */   private static void loadSnmpAgent(String paramString, Properties paramProperties)
/*     */   {
/*     */     try
/*     */     {
/* 382 */       Class localClass = Class.forName("sun.management.snmp.AdaptorBootstrap", true, null);
/*     */       
/* 384 */       localObject = localClass.getMethod("initialize", new Class[] { String.class, Properties.class });
/*     */       
/* 386 */       ((Method)localObject).invoke(null, new Object[] { paramString, paramProperties });
/*     */     }
/*     */     catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException localClassNotFoundException) {
/* 389 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", localClassNotFoundException);
/*     */     } catch (InvocationTargetException localInvocationTargetException) {
/* 391 */       Object localObject = localInvocationTargetException.getCause();
/* 392 */       if ((localObject instanceof RuntimeException))
/* 393 */         throw ((RuntimeException)localObject);
/* 394 */       if ((localObject instanceof Error)) {
/* 395 */         throw ((Error)localObject);
/*     */       }
/*     */       
/* 398 */       throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", (Throwable)localObject);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void readConfiguration(String paramString, Properties paramProperties)
/*     */   {
/* 404 */     if (paramString == null) {
/* 405 */       localObject1 = System.getProperty("java.home");
/* 406 */       if (localObject1 == null) {
/* 407 */         throw new Error("Can't find java.home ??");
/*     */       }
/* 409 */       localObject2 = new StringBuffer((String)localObject1);
/* 410 */       ((StringBuffer)localObject2).append(File.separator).append("lib");
/* 411 */       ((StringBuffer)localObject2).append(File.separator).append("management");
/* 412 */       ((StringBuffer)localObject2).append(File.separator).append("management.properties");
/*     */       
/* 414 */       paramString = ((StringBuffer)localObject2).toString();
/*     */     }
/* 416 */     Object localObject1 = new File(paramString);
/* 417 */     if (!((File)localObject1).exists()) {
/* 418 */       error("agent.err.configfile.notfound", paramString);
/*     */     }
/*     */     
/* 421 */     Object localObject2 = null;
/*     */     try {
/* 423 */       localObject2 = new FileInputStream((File)localObject1);
/* 424 */       BufferedInputStream localBufferedInputStream = new BufferedInputStream((InputStream)localObject2);
/* 425 */       paramProperties.load(localBufferedInputStream); return;
/*     */     } catch (FileNotFoundException localFileNotFoundException) {
/* 427 */       error("agent.err.configfile.failed", localFileNotFoundException.getMessage());
/*     */     } catch (IOException localIOException3) {
/* 429 */       error("agent.err.configfile.failed", localIOException3.getMessage());
/*     */     } catch (SecurityException localSecurityException) {
/* 431 */       error("agent.err.configfile.access.denied", paramString);
/*     */     } finally {
/* 433 */       if (localObject2 != null) {
/*     */         try {
/* 435 */           ((InputStream)localObject2).close();
/*     */         } catch (IOException localIOException6) {
/* 437 */           error("agent.err.configfile.closed.failed", paramString);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void startAgent() throws Exception {
/* 444 */     String str1 = System.getProperty("com.sun.management.agent.class");
/*     */     
/*     */ 
/*     */ 
/* 448 */     if (str1 == null)
/*     */     {
/* 450 */       localObject1 = getManagementProperties();
/* 451 */       if (localObject1 != null) {
/* 452 */         startAgent((Properties)localObject1);
/*     */       }
/* 454 */       return;
/*     */     }
/*     */     
/*     */ 
/* 458 */     Object localObject1 = str1.split(":");
/* 459 */     if ((localObject1.length < 1) || (localObject1.length > 2)) {
/* 460 */       error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
/*     */     }
/* 462 */     String str2 = localObject1[0];
/* 463 */     Object localObject2 = localObject1.length == 2 ? localObject1[1] : null;
/*     */     
/* 465 */     if ((str2 == null) || (str2.length() == 0)) {
/* 466 */       error("agent.err.invalid.agentclass", "\"" + str1 + "\"");
/*     */     }
/*     */     
/* 469 */     if (str2 != null)
/*     */     {
/*     */       try
/*     */       {
/* 473 */         Class localClass = ClassLoader.getSystemClassLoader().loadClass(str2);
/* 474 */         localObject3 = localClass.getMethod("premain", new Class[] { String.class });
/*     */         
/* 476 */         ((Method)localObject3).invoke(null, new Object[] { localObject2 });
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {
/* 479 */         error("agent.err.agentclass.notfound", "\"" + str2 + "\"");
/*     */       } catch (NoSuchMethodException localNoSuchMethodException) {
/* 481 */         error("agent.err.premain.notfound", "\"" + str2 + "\"");
/*     */       } catch (SecurityException localSecurityException) {
/* 483 */         error("agent.err.agentclass.access.denied");
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/* 487 */         Object localObject3 = localException.getCause() == null ? localException.getMessage() : localException.getCause().getMessage();
/* 488 */         error("agent.err.agentclass.failed", (String)localObject3);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void error(String paramString) {
/* 494 */     String str = getText(paramString);
/* 495 */     System.err.print(getText("agent.err.error") + ": " + str);
/* 496 */     throw new RuntimeException(str);
/*     */   }
/*     */   
/*     */   public static void error(String paramString, String[] paramArrayOfString) {
/* 500 */     if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
/* 501 */       error(paramString);
/*     */     } else {
/* 503 */       StringBuffer localStringBuffer = new StringBuffer(paramArrayOfString[0]);
/* 504 */       for (int i = 1; i < paramArrayOfString.length; i++) {
/* 505 */         localStringBuffer.append(" " + paramArrayOfString[i]);
/*     */       }
/* 507 */       error(paramString, localStringBuffer.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public static void error(String paramString1, String paramString2) {
/* 512 */     String str = getText(paramString1);
/* 513 */     System.err.print(getText("agent.err.error") + ": " + str);
/* 514 */     System.err.println(": " + paramString2);
/* 515 */     throw new RuntimeException(str + ": " + paramString2);
/*     */   }
/*     */   
/*     */   public static void error(Exception paramException) {
/* 519 */     paramException.printStackTrace();
/* 520 */     System.err.println(getText("agent.err.exception") + ": " + paramException.toString());
/* 521 */     throw new RuntimeException(paramException);
/*     */   }
/*     */   
/*     */   public static void warning(String paramString1, String paramString2) {
/* 525 */     System.err.print(getText("agent.err.warning") + ": " + getText(paramString1));
/* 526 */     System.err.println(": " + paramString2);
/*     */   }
/*     */   
/*     */   private static void initResource()
/*     */   {
/*     */     try {
/* 532 */       messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
/*     */     } catch (MissingResourceException localMissingResourceException) {
/* 534 */       throw new Error("Fatal: Resource for management agent is missing");
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getText(String paramString) {
/* 539 */     if (messageRB == null) {
/* 540 */       initResource();
/*     */     }
/*     */     try {
/* 543 */       return messageRB.getString(paramString);
/*     */     } catch (MissingResourceException localMissingResourceException) {}
/* 545 */     return "Missing management agent resource bundle: key = \"" + paramString + "\"";
/*     */   }
/*     */   
/*     */   public static String getText(String paramString, String... paramVarArgs)
/*     */   {
/* 550 */     if (messageRB == null) {
/* 551 */       initResource();
/*     */     }
/* 553 */     String str = messageRB.getString(paramString);
/* 554 */     if (str == null) {
/* 555 */       str = "missing resource key: key = \"" + paramString + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
/*     */     }
/*     */     
/* 558 */     return MessageFormat.format(str, (Object[])paramVarArgs);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\Agent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */