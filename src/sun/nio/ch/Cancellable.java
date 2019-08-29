package sun.nio.ch;

abstract interface Cancellable
{
  public abstract void onCancel(PendingFuture<?, ?> paramPendingFuture);
}


/* Location:              E:\java_source\rt.jar!\sun\nio\ch\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */