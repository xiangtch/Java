/*    */ package sun.nio.ch;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.spi.AbstractSelector;
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
/*    */ public class WindowsSelectorProvider
/*    */   extends SelectorProviderImpl
/*    */ {
/*    */   public AbstractSelector openSelector()
/*    */     throws IOException
/*    */   {
/* 44 */     return new WindowsSelectorImpl(this);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\WindowsSelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */