/*     */ package sun.management.jdp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.RuntimeMXBean;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.UUID;
/*     */ import sun.management.VMManagement;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JdpController
/*     */ {
/*     */   private static class JDPControllerRunner
/*     */     implements Runnable
/*     */   {
/*     */     private final JdpJmxPacket packet;
/*     */     private final JdpBroadcaster bcast;
/*     */     private final int pause;
/*  72 */     private volatile boolean shutdown = false;
/*     */     
/*     */     private JDPControllerRunner(JdpBroadcaster paramJdpBroadcaster, JdpJmxPacket paramJdpJmxPacket, int paramInt) {
/*  75 */       this.bcast = paramJdpBroadcaster;
/*  76 */       this.packet = paramJdpJmxPacket;
/*  77 */       this.pause = paramInt;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try {
/*  83 */         while (!this.shutdown) {
/*  84 */           this.bcast.sendPacket(this.packet);
/*     */           try {
/*  86 */             Thread.sleep(this.pause);
/*     */           }
/*     */           catch (InterruptedException localInterruptedException) {}
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException1) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*  99 */         stop();
/* 100 */         this.bcast.shutdown();
/*     */       }
/*     */       catch (IOException localIOException2) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 107 */     public void stop() { this.shutdown = true; }
/*     */   }
/*     */   
/* 110 */   private static JDPControllerRunner controller = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static int getInteger(String paramString1, int paramInt, String paramString2)
/*     */     throws JdpException
/*     */   {
/*     */     try
/*     */     {
/* 120 */       return paramString1 == null ? paramInt : Integer.parseInt(paramString1);
/*     */     } catch (NumberFormatException localNumberFormatException) {
/* 122 */       throw new JdpException(paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */   private static InetAddress getInetAddress(String paramString1, InetAddress paramInetAddress, String paramString2) throws JdpException
/*     */   {
/*     */     try {
/* 129 */       return paramString1 == null ? paramInetAddress : InetAddress.getByName(paramString1);
/*     */     } catch (UnknownHostException localUnknownHostException) {
/* 131 */       throw new JdpException(paramString2);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Integer getProcessId()
/*     */   {
/*     */     try
/*     */     {
/* 139 */       RuntimeMXBean localRuntimeMXBean = ManagementFactory.getRuntimeMXBean();
/* 140 */       Field localField = localRuntimeMXBean.getClass().getDeclaredField("jvm");
/* 141 */       localField.setAccessible(true);
/*     */       
/* 143 */       VMManagement localVMManagement = (VMManagement)localField.get(localRuntimeMXBean);
/* 144 */       Method localMethod = localVMManagement.getClass().getDeclaredMethod("getProcessId", new Class[0]);
/* 145 */       localMethod.setAccessible(true);
/* 146 */       return (Integer)localMethod.invoke(localVMManagement, new Object[0]);
/*     */     }
/*     */     catch (Exception localException) {}
/* 149 */     return null;
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
/*     */   public static synchronized void startDiscoveryService(InetAddress paramInetAddress, int paramInt, String paramString1, String paramString2)
/*     */     throws IOException, JdpException
/*     */   {
/* 167 */     int i = getInteger(
/* 168 */       System.getProperty("com.sun.management.jdp.ttl"), 1, "Invalid jdp packet ttl");
/*     */     
/*     */ 
/*     */ 
/* 172 */     int j = getInteger(
/* 173 */       System.getProperty("com.sun.management.jdp.pause"), 5, "Invalid jdp pause");
/*     */     
/*     */ 
/*     */ 
/* 177 */     j *= 1000;
/*     */     
/*     */ 
/* 180 */     InetAddress localInetAddress = getInetAddress(
/* 181 */       System.getProperty("com.sun.management.jdp.source_addr"), null, "Invalid source address provided");
/*     */     
/*     */ 
/*     */ 
/* 185 */     UUID localUUID = UUID.randomUUID();
/*     */     
/* 187 */     JdpJmxPacket localJdpJmxPacket = new JdpJmxPacket(localUUID, paramString2);
/*     */     
/*     */ 
/*     */ 
/* 191 */     String str = System.getProperty("sun.java.command");
/* 192 */     if (str != null) {
/* 193 */       localObject = str.split(" ", 2);
/* 194 */       localJdpJmxPacket.setMainClass(localObject[0]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 199 */     localJdpJmxPacket.setInstanceName(paramString1);
/*     */     
/*     */ 
/*     */ 
/* 203 */     Object localObject = System.getProperty("java.rmi.server.hostname");
/* 204 */     localJdpJmxPacket.setRmiHostname((String)localObject);
/*     */     
/*     */ 
/* 207 */     localJdpJmxPacket.setBroadcastInterval(new Integer(j).toString());
/*     */     
/*     */ 
/* 210 */     Integer localInteger = getProcessId();
/* 211 */     if (localInteger != null) {
/* 212 */       localJdpJmxPacket.setProcessId(localInteger.toString());
/*     */     }
/*     */     
/* 215 */     JdpBroadcaster localJdpBroadcaster = new JdpBroadcaster(paramInetAddress, localInetAddress, paramInt, i);
/*     */     
/*     */ 
/* 218 */     stopDiscoveryService();
/*     */     
/* 220 */     controller = new JDPControllerRunner(localJdpBroadcaster, localJdpJmxPacket, j, null);
/*     */     
/* 222 */     Thread localThread = new Thread(controller, "JDP broadcaster");
/* 223 */     localThread.setDaemon(true);
/* 224 */     localThread.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized void stopDiscoveryService()
/*     */   {
/* 232 */     if (controller != null) {
/* 233 */       controller.stop();
/* 234 */       controller = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\management\jdp\JdpController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */