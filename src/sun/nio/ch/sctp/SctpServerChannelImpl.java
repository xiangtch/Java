/*     */ package sun.nio.ch.sctp;
/*     */ 
/*     */ import com.sun.nio.sctp.SctpChannel;
/*     */ import com.sun.nio.sctp.SctpServerChannel;
/*     */ import com.sun.nio.sctp.SctpSocketOption;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Set;
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
/*     */ public class SctpServerChannelImpl
/*     */   extends SctpServerChannel
/*     */ {
/*     */   private static final String message = "SCTP not supported on this platform";
/*     */   
/*     */   public SctpServerChannelImpl(SelectorProvider paramSelectorProvider)
/*     */   {
/*  44 */     super(paramSelectorProvider);
/*  45 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public SctpChannel accept() throws IOException
/*     */   {
/*  50 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public SctpServerChannel bind(SocketAddress paramSocketAddress, int paramInt)
/*     */     throws IOException
/*     */   {
/*  56 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public SctpServerChannel bindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/*  62 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public SctpServerChannel unbindAddress(InetAddress paramInetAddress)
/*     */     throws IOException
/*     */   {
/*  68 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public Set<SocketAddress> getAllLocalAddresses()
/*     */     throws IOException
/*     */   {
/*  74 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public <T> T getOption(SctpSocketOption<T> paramSctpSocketOption) throws IOException
/*     */   {
/*  79 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public <T> SctpServerChannel setOption(SctpSocketOption<T> paramSctpSocketOption, T paramT)
/*     */     throws IOException
/*     */   {
/*  85 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public Set<SctpSocketOption<?>> supportedOptions()
/*     */   {
/*  90 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   protected void implConfigureBlocking(boolean paramBoolean) throws IOException
/*     */   {
/*  95 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */   
/*     */   public void implCloseSelectableChannel() throws IOException
/*     */   {
/* 100 */     throw new UnsupportedOperationException("SCTP not supported on this platform");
/*     */   }
/*     */ }


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\sctp\SctpServerChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */