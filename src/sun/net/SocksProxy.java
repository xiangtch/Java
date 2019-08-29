/*    */ package sun.net;
/*    */ 
/*    */ import java.net.Proxy;
/*    */ import java.net.Proxy.Type;
/*    */ import java.net.SocketAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SocksProxy
/*    */   extends Proxy
/*    */ {
/*    */   private final int version;
/*    */   
/*    */   private SocksProxy(SocketAddress paramSocketAddress, int paramInt)
/*    */   {
/* 38 */     super(Type.SOCKS, paramSocketAddress);
/* 39 */     this.version = paramInt;
/*    */   }
/*    */   
/*    */   public static SocksProxy create(SocketAddress paramSocketAddress, int paramInt) {
/* 43 */     return new SocksProxy(paramSocketAddress, paramInt);
/*    */   }
/*    */   
/*    */   public int protocolVersion() {
/* 47 */     return this.version;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\SocksProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */