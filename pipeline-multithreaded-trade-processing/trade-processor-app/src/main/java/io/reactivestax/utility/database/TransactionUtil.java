package io.reactivestax.utility.database;

public interface TransactionUtil<C,T> {
   public C getConnection();

   public T startTransaction();

//   public void closeConnection();

   public void commitTransaction();

   public void rollbackTransaction();
}
