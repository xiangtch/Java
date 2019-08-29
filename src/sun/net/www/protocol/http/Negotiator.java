/*    */ package sun.net.www.protocol.http;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Constructor;
/*    */ import sun.util.logging.PlatformLogger;
/*    */ import sun.util.logging.PlatformLogger.Level;
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
/*    */ public abstract class Negotiator
/*    */ {
/*    */   static Negotiator getNegotiator(HttpCallerInfo paramHttpCallerInfo)
/*    */   {
/*    */     Constructor localConstructor;
/*    */     try
/*    */     {
/* 51 */       Class localClass = Class.forName("sun.net.www.protocol.http.spnego.NegotiatorImpl", true, null);
/* 52 */       localConstructor = localClass.getConstructor(new Class[] { HttpCallerInfo.class });
/*    */     } catch (ClassNotFoundException localClassNotFoundException) {
/* 54 */       finest(localClassNotFoundException);
/* 55 */       return null;
/*    */     }
/*    */     catch (ReflectiveOperationException localReflectiveOperationException1)
/*    */     {
/* 59 */       throw new AssertionError(localReflectiveOperationException1);
/*    */     }
/*    */     try
/*    */     {
/* 63 */       return (Negotiator)localConstructor.newInstance(new Object[] { paramHttpCallerInfo });
/*    */     } catch (ReflectiveOperationException localReflectiveOperationException2) {
/* 65 */       finest(localReflectiveOperationException2);
/* 66 */       Throwable localThrowable = localReflectiveOperationException2.getCause();
/* 67 */       if ((localThrowable != null) && ((localThrowable instanceof Exception)))
/* 68 */         finest((Exception)localThrowable); }
/* 69 */     return null;
/*    */   }
/*    */   
/*    */   public abstract byte[] firstToken() throws IOException;
/*    */   
/*    */   public abstract byte[] nextToken(byte[] paramArrayOfByte) throws IOException;
/*    */   
/*    */   private static void finest(Exception paramException)
/*    */   {
/* 78 */     PlatformLogger localPlatformLogger = HttpURLConnection.getHttpLogger();
/* 79 */     if (localPlatformLogger.isLoggable(PlatformLogger.Level.FINEST)) {
/* 80 */       localPlatformLogger.finest("NegotiateAuthentication: " + paramException);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\Negotiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */