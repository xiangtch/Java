/*    */ package sun.security.util;
/*    */ 
/*    */ import java.util.Comparator;
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
/*    */ 
/*    */ public class ByteArrayTagOrder
/*    */   implements Comparator<byte[]>
/*    */ {
/*    */   public final int compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
/*    */   {
/* 57 */     return (paramArrayOfByte1[0] | 0x20) - (paramArrayOfByte2[0] | 0x20);
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\security\util\ByteArrayTagOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */