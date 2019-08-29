/*    */ package sun.net.www.protocol.http.spnego;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
/*    */ import java.util.Arrays;
/*    */ import javax.security.auth.callback.Callback;
/*    */ import javax.security.auth.callback.CallbackHandler;
/*    */ import javax.security.auth.callback.NameCallback;
/*    */ import javax.security.auth.callback.PasswordCallback;
/*    */ import javax.security.auth.callback.UnsupportedCallbackException;
/*    */ import sun.net.www.protocol.http.HttpCallerInfo;
/*    */ import sun.security.jgss.LoginConfigImpl;
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
/*    */ public class NegotiateCallbackHandler
/*    */   implements CallbackHandler
/*    */ {
/*    */   private String username;
/*    */   private char[] password;
/*    */   private boolean answered;
/*    */   private final HttpCallerInfo hci;
/*    */   
/*    */   public NegotiateCallbackHandler(HttpCallerInfo paramHttpCallerInfo)
/*    */   {
/* 59 */     this.hci = paramHttpCallerInfo;
/*    */   }
/*    */   
/*    */   private void getAnswer() {
/* 63 */     if (!this.answered) {
/* 64 */       this.answered = true;
/*    */       
/* 66 */       if (LoginConfigImpl.HTTP_USE_GLOBAL_CREDS)
/*    */       {
/* 68 */         PasswordAuthentication localPasswordAuthentication = Authenticator.requestPasswordAuthentication(this.hci.host, this.hci.addr, this.hci.port, this.hci.protocol, this.hci.prompt, this.hci.scheme, this.hci.url, this.hci.authType);
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 76 */         if (localPasswordAuthentication != null) {
/* 77 */           this.username = localPasswordAuthentication.getUserName();
/* 78 */           this.password = localPasswordAuthentication.getPassword();
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public void handle(Callback[] paramArrayOfCallback) throws UnsupportedCallbackException, IOException
/*    */   {
/* 86 */     for (int i = 0; i < paramArrayOfCallback.length; i++) {
/* 87 */       Callback localCallback = paramArrayOfCallback[i];
/*    */       
/* 89 */       if ((localCallback instanceof NameCallback)) {
/* 90 */         getAnswer();
/* 91 */         ((NameCallback)localCallback).setName(this.username);
/* 92 */       } else if ((localCallback instanceof PasswordCallback)) {
/* 93 */         getAnswer();
/* 94 */         ((PasswordCallback)localCallback).setPassword(this.password);
/* 95 */         if (this.password != null) Arrays.fill(this.password, ' ');
/*    */       } else {
/* 97 */         throw new UnsupportedCallbackException(localCallback, "Call back not supported");
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\java_source\rt.jar!\sun\net\www\protocol\http\spnego\NegotiateCallbackHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */