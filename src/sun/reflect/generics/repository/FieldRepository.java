/*    */ package sun.reflect.generics.repository;
/*    */ 
/*    */ import java.lang.reflect.Type;
/*    */ import sun.reflect.generics.factory.GenericsFactory;
/*    */ import sun.reflect.generics.parser.SignatureParser;
/*    */ import sun.reflect.generics.tree.TypeSignature;
/*    */ import sun.reflect.generics.visitor.Reifier;
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
/*    */ public class FieldRepository
/*    */   extends AbstractRepository<TypeSignature>
/*    */ {
/*    */   private Type genericType;
/*    */   
/*    */   protected FieldRepository(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 48 */     super(paramString, paramGenericsFactory);
/*    */   }
/*    */   
/*    */   protected TypeSignature parse(String paramString) {
/* 52 */     return SignatureParser.make().parseTypeSig(paramString);
/*    */   }
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
/*    */   public static FieldRepository make(String paramString, GenericsFactory paramGenericsFactory)
/*    */   {
/* 66 */     return new FieldRepository(paramString, paramGenericsFactory);
/*    */   }
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
/*    */   public Type getGenericType()
/*    */   {
/* 83 */     if (this.genericType == null) {
/* 84 */       Reifier localReifier = getReifier();
/* 85 */       ((TypeSignature)getTree()).accept(localReifier);
/*    */       
/* 87 */       this.genericType = localReifier.getResult();
/*    */     }
/* 89 */     return this.genericType;
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\repository\FieldRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */