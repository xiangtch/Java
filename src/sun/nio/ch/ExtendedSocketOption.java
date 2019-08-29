/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.net.SocketOption;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ExtendedSocketOption
/*    */ {
/* 38 */   static final SocketOption<Boolean> SO_OOBINLINE = new SocketOption()
/*    */   {
/* 40 */     public String name() { return "SO_OOBINLINE"; }
/* 41 */     public Class<Boolean> type() { return Boolean.class; }
/* 42 */     public String toString() { return name(); }
/*    */   };
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\ExtendedSocketOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */