/*     */ package sun.reflect.generics.repository;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import sun.reflect.generics.factory.GenericsFactory;
/*     */ import sun.reflect.generics.parser.SignatureParser;
/*     */ import sun.reflect.generics.tree.ClassSignature;
/*     */ import sun.reflect.generics.tree.ClassTypeSignature;
/*     */ import sun.reflect.generics.tree.TypeTree;
/*     */ import sun.reflect.generics.visitor.Reifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassRepository
/*     */   extends GenericDeclRepository<ClassSignature>
/*     */ {
/*  43 */   public static final ClassRepository NONE = make("Ljava/lang/Object;", null);
/*     */   
/*     */ 
/*     */   private volatile Type superclass;
/*     */   
/*     */   private volatile Type[] superInterfaces;
/*     */   
/*     */ 
/*     */   private ClassRepository(String paramString, GenericsFactory paramGenericsFactory)
/*     */   {
/*  53 */     super(paramString, paramGenericsFactory);
/*     */   }
/*     */   
/*     */   protected ClassSignature parse(String paramString) {
/*  57 */     return SignatureParser.make().parseClassSig(paramString);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassRepository make(String paramString, GenericsFactory paramGenericsFactory)
/*     */   {
/*  70 */     return new ClassRepository(paramString, paramGenericsFactory);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Type getSuperclass()
/*     */   {
/*  86 */     Type localType = this.superclass;
/*  87 */     if (localType == null) {
/*  88 */       Reifier localReifier = getReifier();
/*     */       
/*  90 */       ((ClassSignature)getTree()).getSuperclass().accept(localReifier);
/*     */       
/*  92 */       localType = localReifier.getResult();
/*  93 */       this.superclass = localType;
/*     */     }
/*  95 */     return localType;
/*     */   }
/*     */   
/*     */   public Type[] getSuperInterfaces() {
/*  99 */     Type[] arrayOfType = this.superInterfaces;
/* 100 */     if (arrayOfType == null)
/*     */     {
/* 102 */       ClassTypeSignature[] arrayOfClassTypeSignature = ((ClassSignature)getTree()).getSuperInterfaces();
/*     */       
/* 104 */       arrayOfType = new Type[arrayOfClassTypeSignature.length];
/*     */       
/* 106 */       for (int i = 0; i < arrayOfClassTypeSignature.length; i++) {
/* 107 */         Reifier localReifier = getReifier();
/* 108 */         arrayOfClassTypeSignature[i].accept(localReifier);
/*     */         
/* 110 */         arrayOfType[i] = localReifier.getResult();
/*     */       }
/* 112 */       this.superInterfaces = arrayOfType;
/*     */     }
/* 114 */     return (Type[])arrayOfType.clone();
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\reflect\generics\repository\ClassRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */