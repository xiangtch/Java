/*    */ package sun.awt.windows;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.Toolkit;
/*    */ import java.awt.im.spi.InputMethod;
/*    */ import java.awt.im.spi.InputMethodDescriptor;
/*    */ import java.util.Locale;
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
/*    */ final class WInputMethodDescriptor
/*    */   implements InputMethodDescriptor
/*    */ {
/*    */   public Locale[] getAvailableLocales()
/*    */   {
/* 51 */     Locale[] arrayOfLocale1 = getAvailableLocalesInternal();
/* 52 */     Locale[] arrayOfLocale2 = new Locale[arrayOfLocale1.length];
/* 53 */     System.arraycopy(arrayOfLocale1, 0, arrayOfLocale2, 0, arrayOfLocale1.length);
/* 54 */     return arrayOfLocale2;
/*    */   }
/*    */   
/*    */   static Locale[] getAvailableLocalesInternal() {
/* 58 */     return getNativeAvailableLocales();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean hasDynamicLocaleList()
/*    */   {
/* 66 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public synchronized String getInputMethodDisplayName(Locale paramLocale1, Locale paramLocale2)
/*    */   {
/* 77 */     String str = "System Input Methods";
/* 78 */     if (Locale.getDefault().equals(paramLocale2)) {
/* 79 */       str = Toolkit.getProperty("AWT.HostInputMethodDisplayName", str);
/*    */     }
/* 81 */     return str;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Image getInputMethodIcon(Locale paramLocale)
/*    */   {
/* 89 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public InputMethod createInputMethod()
/*    */     throws Exception
/*    */   {
/* 97 */     return new WInputMethod();
/*    */   }
/*    */   
/*    */   private static native Locale[] getNativeAvailableLocales();
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\awt\windows\WInputMethodDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */