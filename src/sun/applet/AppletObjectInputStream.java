/*     */ package sun.applet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.io.StreamCorruptedException;
/*     */ import java.lang.reflect.Array;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class AppletObjectInputStream
/*     */   extends ObjectInputStream
/*     */ {
/*     */   private AppletClassLoader loader;
/*     */   
/*     */   public AppletObjectInputStream(InputStream paramInputStream, AppletClassLoader paramAppletClassLoader)
/*     */     throws IOException, StreamCorruptedException
/*     */   {
/*  50 */     super(paramInputStream);
/*  51 */     if (paramAppletClassLoader == null) {
/*  52 */       throw new AppletIllegalArgumentException("appletillegalargumentexception.objectinputstream");
/*     */     }
/*     */     
/*  55 */     this.loader = paramAppletClassLoader;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Class primitiveType(char paramChar)
/*     */   {
/*  63 */     switch (paramChar) {
/*  64 */     case 'B':  return Byte.TYPE;
/*  65 */     case 'C':  return Character.TYPE;
/*  66 */     case 'D':  return Double.TYPE;
/*  67 */     case 'F':  return Float.TYPE;
/*  68 */     case 'I':  return Integer.TYPE;
/*  69 */     case 'J':  return Long.TYPE;
/*  70 */     case 'S':  return Short.TYPE;
/*  71 */     case 'Z':  return Boolean.TYPE; }
/*  72 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class resolveClass(ObjectStreamClass paramObjectStreamClass)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/*  82 */     String str = paramObjectStreamClass.getName();
/*  83 */     if (str.startsWith("["))
/*     */     {
/*     */ 
/*     */ 
/*  87 */       for (int i = 1; str.charAt(i) == '['; i++) {}
/*  88 */       Class localClass; if (str.charAt(i) == 'L') {
/*  89 */         localClass = this.loader.loadClass(str.substring(i + 1, str
/*  90 */           .length() - 1));
/*     */       } else {
/*  92 */         if (str.length() != i + 1) {
/*  93 */           throw new ClassNotFoundException(str);
/*     */         }
/*  95 */         localClass = primitiveType(str.charAt(i));
/*     */       }
/*  97 */       int[] arrayOfInt = new int[i];
/*  98 */       for (int j = 0; j < i; j++) {
/*  99 */         arrayOfInt[j] = 0;
/*     */       }
/* 101 */       return Array.newInstance(localClass, arrayOfInt).getClass();
/*     */     }
/* 103 */     return this.loader.loadClass(str);
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\applet\AppletObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */