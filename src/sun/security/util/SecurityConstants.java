/*     */ package sun.security.util;
/*     */ 
/*     */ import java.net.NetPermission;
/*     */ import java.net.SocketPermission;
/*     */ import java.security.AllPermission;
/*     */ import java.security.Permission;
/*     */ import java.security.SecurityPermission;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class SecurityConstants
/*     */ {
/*     */   public static final String FILE_DELETE_ACTION = "delete";
/*     */   public static final String FILE_EXECUTE_ACTION = "execute";
/*     */   public static final String FILE_READ_ACTION = "read";
/*     */   public static final String FILE_WRITE_ACTION = "write";
/*     */   public static final String FILE_READLINK_ACTION = "readlink";
/*     */   public static final String SOCKET_RESOLVE_ACTION = "resolve";
/*     */   public static final String SOCKET_CONNECT_ACTION = "connect";
/*     */   public static final String SOCKET_LISTEN_ACTION = "listen";
/*     */   public static final String SOCKET_ACCEPT_ACTION = "accept";
/*     */   public static final String SOCKET_CONNECT_ACCEPT_ACTION = "connect,accept";
/*     */   public static final String PROPERTY_RW_ACTION = "read,write";
/*     */   public static final String PROPERTY_READ_ACTION = "read";
/*     */   public static final String PROPERTY_WRITE_ACTION = "write";
/*  71 */   public static final AllPermission ALL_PERMISSION = new AllPermission();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class AWT
/*     */   {
/*     */     private static final String AWTFactory = "sun.awt.AWTPermissionFactory";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  88 */     private static final PermissionFactory<?> factory = ;
/*     */     
/*     */     private static PermissionFactory<?> permissionFactory() {
/*     */       Class localClass;
/*     */       try {
/*  93 */         localClass = Class.forName("sun.awt.AWTPermissionFactory", false, AWT.class.getClassLoader());
/*     */       }
/*     */       catch (ClassNotFoundException localClassNotFoundException) {
/*  96 */         return null;
/*     */       }
/*     */       try
/*     */       {
/* 100 */         return (PermissionFactory)localClass.newInstance();
/*     */       } catch (ReflectiveOperationException localReflectiveOperationException) {
/* 102 */         throw new InternalError(localReflectiveOperationException);
/*     */       }
/*     */     }
/*     */     
/*     */     private static Permission newAWTPermission(String paramString) {
/* 107 */       return factory == null ? null : factory.newPermission(paramString);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 112 */     public static final Permission TOPLEVEL_WINDOW_PERMISSION = newAWTPermission("showWindowWithoutWarningBanner");
/*     */     
/*     */ 
/*     */ 
/* 116 */     public static final Permission ACCESS_CLIPBOARD_PERMISSION = newAWTPermission("accessClipboard");
/*     */     
/*     */ 
/*     */ 
/* 120 */     public static final Permission CHECK_AWT_EVENTQUEUE_PERMISSION = newAWTPermission("accessEventQueue");
/*     */     
/*     */ 
/*     */ 
/* 124 */     public static final Permission TOOLKIT_MODALITY_PERMISSION = newAWTPermission("toolkitModality");
/*     */     
/*     */ 
/*     */ 
/* 128 */     public static final Permission READ_DISPLAY_PIXELS_PERMISSION = newAWTPermission("readDisplayPixels");
/*     */     
/*     */ 
/*     */ 
/* 132 */     public static final Permission CREATE_ROBOT_PERMISSION = newAWTPermission("createRobot");
/*     */     
/*     */ 
/*     */ 
/* 136 */     public static final Permission WATCH_MOUSE_PERMISSION = newAWTPermission("watchMousePointer");
/*     */     
/*     */ 
/*     */ 
/* 140 */     public static final Permission SET_WINDOW_ALWAYS_ON_TOP_PERMISSION = newAWTPermission("setWindowAlwaysOnTop");
/*     */     
/*     */ 
/*     */ 
/* 144 */     public static final Permission ALL_AWT_EVENTS_PERMISSION = newAWTPermission("listenToAllAWTEvents");
/*     */     
/*     */ 
/*     */ 
/* 148 */     public static final Permission ACCESS_SYSTEM_TRAY_PERMISSION = newAWTPermission("accessSystemTray");
/*     */   }
/*     */   
/*     */ 
/* 152 */   public static final NetPermission SPECIFY_HANDLER_PERMISSION = new NetPermission("specifyStreamHandler");
/*     */   
/*     */ 
/*     */ 
/* 156 */   public static final NetPermission SET_PROXYSELECTOR_PERMISSION = new NetPermission("setProxySelector");
/*     */   
/*     */ 
/*     */ 
/* 160 */   public static final NetPermission GET_PROXYSELECTOR_PERMISSION = new NetPermission("getProxySelector");
/*     */   
/*     */ 
/*     */ 
/* 164 */   public static final NetPermission SET_COOKIEHANDLER_PERMISSION = new NetPermission("setCookieHandler");
/*     */   
/*     */ 
/*     */ 
/* 168 */   public static final NetPermission GET_COOKIEHANDLER_PERMISSION = new NetPermission("getCookieHandler");
/*     */   
/*     */ 
/*     */ 
/* 172 */   public static final NetPermission SET_RESPONSECACHE_PERMISSION = new NetPermission("setResponseCache");
/*     */   
/*     */ 
/*     */ 
/* 176 */   public static final NetPermission GET_RESPONSECACHE_PERMISSION = new NetPermission("getResponseCache");
/*     */   
/*     */ 
/*     */ 
/* 180 */   public static final RuntimePermission CREATE_CLASSLOADER_PERMISSION = new RuntimePermission("createClassLoader");
/*     */   
/*     */ 
/*     */ 
/* 184 */   public static final RuntimePermission CHECK_MEMBER_ACCESS_PERMISSION = new RuntimePermission("accessDeclaredMembers");
/*     */   
/*     */ 
/*     */ 
/* 188 */   public static final RuntimePermission MODIFY_THREAD_PERMISSION = new RuntimePermission("modifyThread");
/*     */   
/*     */ 
/*     */ 
/* 192 */   public static final RuntimePermission MODIFY_THREADGROUP_PERMISSION = new RuntimePermission("modifyThreadGroup");
/*     */   
/*     */ 
/*     */ 
/* 196 */   public static final RuntimePermission GET_PD_PERMISSION = new RuntimePermission("getProtectionDomain");
/*     */   
/*     */ 
/*     */ 
/* 200 */   public static final RuntimePermission GET_CLASSLOADER_PERMISSION = new RuntimePermission("getClassLoader");
/*     */   
/*     */ 
/*     */ 
/* 204 */   public static final RuntimePermission STOP_THREAD_PERMISSION = new RuntimePermission("stopThread");
/*     */   
/*     */ 
/*     */ 
/* 208 */   public static final RuntimePermission GET_STACK_TRACE_PERMISSION = new RuntimePermission("getStackTrace");
/*     */   
/*     */ 
/*     */ 
/* 212 */   public static final SecurityPermission CREATE_ACC_PERMISSION = new SecurityPermission("createAccessControlContext");
/*     */   
/*     */ 
/*     */ 
/* 216 */   public static final SecurityPermission GET_COMBINER_PERMISSION = new SecurityPermission("getDomainCombiner");
/*     */   
/*     */ 
/*     */ 
/* 220 */   public static final SecurityPermission GET_POLICY_PERMISSION = new SecurityPermission("getPolicy");
/*     */   
/*     */ 
/*     */ 
/* 224 */   public static final SocketPermission LOCAL_LISTEN_PERMISSION = new SocketPermission("localhost:0", "listen");
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\SecurityConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */