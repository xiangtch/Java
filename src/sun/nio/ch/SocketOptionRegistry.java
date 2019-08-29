/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.net.ProtocolFamily;
/*    */ import java.net.SocketOption;
/*    */ import java.net.StandardProtocolFamily;
/*    */ import java.net.StandardSocketOptions;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ class SocketOptionRegistry
/*    */ {
/*    */   private static class RegistryKey
/*    */   {
/*    */     private final SocketOption<?> name;
/*    */     private final ProtocolFamily family;
/*    */     
/*    */     RegistryKey(SocketOption<?> paramSocketOption, ProtocolFamily paramProtocolFamily)
/*    */     {
/* 41 */       this.name = paramSocketOption;
/* 42 */       this.family = paramProtocolFamily;
/*    */     }
/*    */     
/* 45 */     public int hashCode() { return this.name.hashCode() + this.family.hashCode(); }
/*    */     
/*    */     public boolean equals(Object paramObject) {
/* 48 */       if (paramObject == null) return false;
/* 49 */       if (!(paramObject instanceof RegistryKey)) return false;
/* 50 */       RegistryKey localRegistryKey = (RegistryKey)paramObject;
/* 51 */       if (this.name != localRegistryKey.name) return false;
/* 52 */       if (this.family != localRegistryKey.family) return false;
/* 53 */       return true;
/*    */     }
/*    */   }
/*    */   
/* 57 */   private static class LazyInitialization { static final Map<RegistryKey, OptionKey> options = ;
/*    */     
/* 59 */     private static Map<RegistryKey, OptionKey> options() { HashMap localHashMap = new HashMap();
/*    */       
/* 61 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_BROADCAST, Net.UNSPEC), new OptionKey(65535, 32));
/* 62 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_KEEPALIVE, Net.UNSPEC), new OptionKey(65535, 8));
/* 63 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_LINGER, Net.UNSPEC), new OptionKey(65535, 128));
/* 64 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_SNDBUF, Net.UNSPEC), new OptionKey(65535, 4097));
/* 65 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_RCVBUF, Net.UNSPEC), new OptionKey(65535, 4098));
/* 66 */       localHashMap.put(new RegistryKey(StandardSocketOptions.SO_REUSEADDR, Net.UNSPEC), new OptionKey(65535, 4));
/* 67 */       localHashMap.put(new RegistryKey(StandardSocketOptions.TCP_NODELAY, Net.UNSPEC), new OptionKey(6, 1));
/* 68 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET), new OptionKey(0, 3));
/* 69 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET), new OptionKey(0, 9));
/* 70 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET), new OptionKey(0, 10));
/* 71 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET), new OptionKey(0, 11));
/* 72 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET6), new OptionKey(41, 39));
/* 73 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET6), new OptionKey(41, 9));
/* 74 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET6), new OptionKey(41, 10));
/* 75 */       localHashMap.put(new RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET6), new OptionKey(41, 11));
/* 76 */       localHashMap.put(new RegistryKey(ExtendedSocketOption.SO_OOBINLINE, Net.UNSPEC), new OptionKey(65535, 256));
/* 77 */       return localHashMap;
/*    */     }
/*    */   }
/*    */   
/* 81 */   public static OptionKey findOption(SocketOption<?> paramSocketOption, ProtocolFamily paramProtocolFamily) { RegistryKey localRegistryKey = new RegistryKey(paramSocketOption, paramProtocolFamily);
/* 82 */     return (OptionKey)LazyInitialization.options.get(localRegistryKey);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\SocketOptionRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */