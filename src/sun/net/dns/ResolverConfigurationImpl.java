/*     */ package sun.net.dns;
/*     */ 
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class ResolverConfigurationImpl
/*     */   extends ResolverConfiguration
/*     */ {
/*     */   private static Object lock;
/*     */   private final Options opts;
/*     */   private static boolean changed;
/*     */   private static long lastRefresh;
/*     */   private static final int TIMEOUT = 120000;
/*     */   private static String os_searchlist;
/*     */   private static String os_nameservers;
/*     */   private static LinkedList<String> searchlist;
/*     */   private static LinkedList<String> nameservers;
/*     */   
/*     */   private LinkedList<String> stringToList(String paramString)
/*     */   {
/*  66 */     LinkedList localLinkedList = new LinkedList();
/*     */     
/*     */ 
/*  69 */     StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ", ");
/*  70 */     while (localStringTokenizer.hasMoreTokens()) {
/*  71 */       String str = localStringTokenizer.nextToken();
/*  72 */       if (!localLinkedList.contains(str)) {
/*  73 */         localLinkedList.add(str);
/*     */       }
/*     */     }
/*  76 */     return localLinkedList;
/*     */   }
/*     */   
/*     */ 
/*     */   private void loadConfig()
/*     */   {
/*  82 */     assert (Thread.holdsLock(lock));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  87 */     if (changed) {
/*  88 */       changed = false;
/*     */     }
/*  90 */     else if (lastRefresh >= 0L) {
/*  91 */       long l = System.currentTimeMillis();
/*  92 */       if (l - lastRefresh < 120000L) {
/*  93 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     loadDNSconfig0();
/*     */     
/* 103 */     lastRefresh = System.currentTimeMillis();
/* 104 */     searchlist = stringToList(os_searchlist);
/* 105 */     nameservers = stringToList(os_nameservers);
/* 106 */     os_searchlist = null;
/* 107 */     os_nameservers = null;
/*     */   }
/*     */   
/*     */   ResolverConfigurationImpl() {
/* 111 */     this.opts = new OptionsImpl();
/*     */   }
/*     */   
/*     */   public List<String> searchlist()
/*     */   {
/* 116 */     synchronized (lock) {
/* 117 */       loadConfig();
/*     */       
/*     */ 
/* 120 */       return (List)searchlist.clone();
/*     */     }
/*     */   }
/*     */   
/*     */   public List<String> nameservers()
/*     */   {
/* 126 */     synchronized (lock) {
/* 127 */       loadConfig();
/*     */       
/*     */ 
/* 130 */       return (List)nameservers.clone();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 135 */   public Options options() { return this.opts; }
/*     */   
/*     */   static native void init0();
/*     */   
/*     */   static native void loadDNSconfig0();
/*     */   
/*     */   static native int notifyAddrChange0();
/*     */   
/*     */   static class AddressChangeListener extends Thread {
/* 144 */     public void run() { for (;;) { if (ResolverConfigurationImpl.notifyAddrChange0() != 0)
/* 145 */           return;
/* 146 */         synchronized (ResolverConfigurationImpl.lock) {
/* 147 */           ResolverConfigurationImpl.access$102(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  40 */     lock = new Object();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  46 */     changed = false;
/*     */     
/*     */ 
/*  49 */     lastRefresh = -1L;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/* 166 */         System.loadLibrary("net");
/* 167 */         return null;
/*     */       }
/* 169 */     });
/* 170 */     init0();
/*     */     
/*     */ 
/* 173 */     AddressChangeListener localAddressChangeListener = new AddressChangeListener();
/* 174 */     localAddressChangeListener.setDaemon(true);
/* 175 */     localAddressChangeListener.start();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\net\dns\ResolverConfigurationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */