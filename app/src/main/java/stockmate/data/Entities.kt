package stockmate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Int,
    val price: Double,                 // price per unit in Kwacha
    val lowStockLimit: Int = 5         // warn when quantity <= this
)

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val itemName: String,
    val quantity: Int,
    val total: Double,
    val timestamp: Long = System.currentTimeMillis()
)
