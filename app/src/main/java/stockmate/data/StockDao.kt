package stockmate.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM items ORDER BY name")
    fun getItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItem(id: Long): Item?

    @Insert
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Insert
    suspend fun insertSale(sale: Sale)

    @Query("SELECT * FROM sales WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getSalesSince(since: Long): Flow<List<Sale>>

    @Query("SELECT * FROM items WHERE quantity <= lowStockLimit")
    suspend fun getLowStockItems(): List<Item>

    // Records a sale and reduces stock in ONE transaction.
    // Returns false if there is not enough stock.
    @Transaction
    suspend fun recordSale(itemId: Long, qty: Int): Boolean {
        val item = getItem(itemId) ?: return false
        if (qty <= 0 || qty > item.quantity) return false
        updateItem(item.copy(quantity = item.quantity - qty))
        insertSale(Sale(itemId = item.id, itemName = item.name, quantity = qty, total = qty * item.price))
        return true
    }
}
