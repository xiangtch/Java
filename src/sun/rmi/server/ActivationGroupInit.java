/*    */ package sun.rmi.server;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.rmi.activation.ActivationGroup;
/*    */ import java.rmi.activation.ActivationGroupDesc;
/*    */ import java.rmi.activation.ActivationGroupID;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ActivationGroupInit
/*    */ {
/*    */   public static void main(String[] paramArrayOfString)
/*    */   {
/*    */     try
/*    */     {
/* 61 */       if (System.getSecurityManager() == null) {
/* 62 */         System.setSecurityManager(new SecurityManager());
/*    */       }
/*    */       
/* 65 */       MarshalInputStream localMarshalInputStream = new MarshalInputStream(System.in);
/* 66 */       ActivationGroupID localActivationGroupID = (ActivationGroupID)localMarshalInputStream.readObject();
/* 67 */       ActivationGroupDesc localActivationGroupDesc = (ActivationGroupDesc)localMarshalInputStream.readObject();
/* 68 */       long l = localMarshalInputStream.readLong();
/*    */       
/*    */ 
/* 71 */       ActivationGroup.createGroup(localActivationGroupID, localActivationGroupDesc, l); return;
/*    */     } catch (Exception localException2) {
/* 73 */       System.err.println("Exception in starting ActivationGroupInit:");
/* 74 */       localException2.printStackTrace();
/*    */     } finally {
/*    */       try {
/* 77 */         System.in.close();
/*    */       }
/*    */       catch (Exception localException4) {}
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\rmi\server\ActivationGroupInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */