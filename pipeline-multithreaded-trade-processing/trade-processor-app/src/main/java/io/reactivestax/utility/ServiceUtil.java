package io.reactivestax.utility;

public interface ServiceUtil<C,T> {
   public C getConnection();

   public T getTransaction();
}
