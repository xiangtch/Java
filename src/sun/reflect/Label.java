/*    */ package sun.reflect;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ class Label
/*    */ {
/*    */   static class PatchInfo
/*    */   {
/*    */     final ClassFileAssembler asm;
/*    */     final short instrBCI;
/*    */     final short patchBCI;
/*    */     final int stackDepth;
/*    */     
/*    */     PatchInfo(ClassFileAssembler paramClassFileAssembler, short paramShort1, short paramShort2, int paramInt)
/*    */     {
/* 43 */       this.asm = paramClassFileAssembler;
/* 44 */       this.instrBCI = paramShort1;
/* 45 */       this.patchBCI = paramShort2;
/* 46 */       this.stackDepth = paramInt;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 55 */   private List<PatchInfo> patches = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   void add(ClassFileAssembler paramClassFileAssembler, short paramShort1, short paramShort2, int paramInt)
/*    */   {
/* 65 */     this.patches.add(new PatchInfo(paramClassFileAssembler, paramShort1, paramShort2, paramInt));
/*    */   }
/*    */   
/*    */   public void bind() {
/* 69 */     for (PatchInfo localPatchInfo : this.patches) {
/* 70 */       int i = localPatchInfo.asm.getLength();
/* 71 */       short s = (short)(i - localPatchInfo.instrBCI);
/* 72 */       localPatchInfo.asm.emitShort(localPatchInfo.patchBCI, s);
/* 73 */       localPatchInfo.asm.setStack(localPatchInfo.stackDepth);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */