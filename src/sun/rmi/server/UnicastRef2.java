/*    */ package sun.rmi.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ import sun.rmi.transport.LiveRef;
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
/*    */ public class UnicastRef2
/*    */   extends UnicastRef
/*    */ {
/*    */   private static final long serialVersionUID = 1829537514995881838L;
/*    */   
/*    */   public UnicastRef2() {}
/*    */   
/*    */   public UnicastRef2(LiveRef paramLiveRef)
/*    */   {
/* 51 */     super(paramLiveRef);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getRefClass(ObjectOutput paramObjectOutput)
/*    */   {
/* 59 */     return "UnicastRef2";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void writeExternal(ObjectOutput paramObjectOutput)
/*    */     throws IOException
/*    */   {
/* 67 */     this.ref.write(paramObjectOutput, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void readExternal(ObjectInput paramObjectInput)
/*    */     throws IOException, ClassNotFoundException
/*    */   {
/* 78 */     this.ref = LiveRef.read(paramObjectInput, true);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\UnicastRef2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */