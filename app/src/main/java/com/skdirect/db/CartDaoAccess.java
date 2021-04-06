package com.skdirect.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.skdirect.model.CartModel;

import java.util.List;

@Dao
public interface CartDaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertTask(CartModel model);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(List<CartModel> model);

    @Transaction
    @Query("SELECT * FROM ShoppingCart")
    LiveData<List<CartModel>> fetchAllTasks();

    @Query("SELECT * FROM ShoppingCart WHERE Id =:id")
    CartModel getItem(int id);

    @Query("SELECT * FROM ShoppingCart WHERE Id =:id")
    LiveData<CartModel> getCartItem(int id);

    @Transaction
    @Query("SELECT EXISTS(SELECT * FROM ShoppingCart WHERE Id = :id)")
    Boolean isItemExist(int id);

    @Query("SELECT SUM(Mrp * Quantity) FROM ShoppingCart")
    LiveData<Double> getCartValue();

    @Query("SELECT SUM(Mrp * Quantity) FROM ShoppingCart")
    Double getCartValue1();

    @Query("SELECT SUM(Quantity) FROM ShoppingCart")
    int getCartQtyCount();

    @Query("SELECT COUNT(*) FROM ShoppingCart")
    LiveData<Integer> getCartCount();

    @Query("SELECT COUNT(*) FROM ShoppingCart")
    int getCartCount1();

    @Update
    void updateTask(CartModel model);

    @Delete
    void deleteTask(CartModel model);

    @Query("DELETE FROM ShoppingCart WHERE Id = :id")
    void deleteItem(int id);

    @Query("DELETE FROM ShoppingCart")
    void truncateCart();
}