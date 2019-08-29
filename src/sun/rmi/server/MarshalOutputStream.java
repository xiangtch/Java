/*     */ package sun.rmi.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.rmi.Remote;
/*     */ import java.rmi.server.RMIClassLoader;
/*     */ import java.rmi.server.RemoteStub;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import sun.rmi.transport.ObjectTable;
/*     */ import sun.rmi.transport.Target;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MarshalOutputStream
/*     */   extends ObjectOutputStream
/*     */ {
/*     */   public MarshalOutputStream(OutputStream paramOutputStream)
/*     */     throws IOException
/*     */   {
/*  55 */     this(paramOutputStream, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MarshalOutputStream(OutputStream paramOutputStream, int paramInt)
/*     */     throws IOException
/*     */   {
/*  64 */     super(paramOutputStream);
/*  65 */     useProtocolVersion(paramInt);
/*  66 */     AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Void run() {
/*  69 */         MarshalOutputStream.this.enableReplaceObject(true);
/*  70 */         return null;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected final Object replaceObject(Object paramObject)
/*     */     throws IOException
/*     */   {
/*  80 */     if (((paramObject instanceof Remote)) && (!(paramObject instanceof RemoteStub))) {
/*  81 */       Target localTarget = ObjectTable.getTarget((Remote)paramObject);
/*  82 */       if (localTarget != null) {
/*  83 */         return localTarget.getStub();
/*     */       }
/*     */     }
/*  86 */     return paramObject;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void annotateClass(Class<?> paramClass)
/*     */     throws IOException
/*     */   {
/*  93 */     writeLocation(RMIClassLoader.getClassAnnotation(paramClass));
/*     */   }
/*     */   
/*     */ 
/*     */   protected void annotateProxyClass(Class<?> paramClass)
/*     */     throws IOException
/*     */   {
/* 100 */     annotateClass(paramClass);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeLocation(String paramString)
/*     */     throws IOException
/*     */   {
/* 109 */     writeObject(paramString);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\MarshalOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */