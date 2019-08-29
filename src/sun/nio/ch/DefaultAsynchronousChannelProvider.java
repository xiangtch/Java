/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.nio.channels.spi.AsynchronousChannelProvider;
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
/*    */ 
/*    */ public class DefaultAsynchronousChannelProvider
/*    */ {
/*    */   public static AsynchronousChannelProvider create()
/*    */   {
/* 41 */     return new WindowsAsynchronousChannelProvider();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\DefaultAsynchronousChannelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */