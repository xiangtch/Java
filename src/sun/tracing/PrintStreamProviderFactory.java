/*    */ package sun.tracing;
/*    */ 
/*    */ import com.sun.tracing.Provider;
/*    */ import com.sun.tracing.ProviderFactory;
/*    */ import java.io.PrintStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrintStreamProviderFactory
/*    */   extends ProviderFactory
/*    */ {
/*    */   private PrintStream stream;
/*    */   
/*    */   public PrintStreamProviderFactory(PrintStream paramPrintStream)
/*    */   {
/* 51 */     this.stream = paramPrintStream;
/*    */   }
/*    */   
/*    */   public <T extends Provider> T createProvider(Class<T> paramClass) {
/* 55 */     PrintStreamProvider localPrintStreamProvider = new PrintStreamProvider(paramClass, this.stream);
/* 56 */     localPrintStreamProvider.init();
/* 57 */     return localPrintStreamProvider.newProxyInstance();
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\tracing\PrintStreamProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */