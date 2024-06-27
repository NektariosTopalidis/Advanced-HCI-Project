package gr.nektariostop.ergasiaadvancedhci.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories-table")
data class Category (
    @PrimaryKey(autoGenerate = false)
    val categoryId: Long = 0L,
    @ColumnInfo(name = "category-name")
    val categoryName: String = "",
    @ColumnInfo(name = "products-count")
    val productsCount: Int = 0,
    @ColumnInfo(name = "category-icon")
    val icon: String? = null,
    @ColumnInfo(name = "category-icon-white")
    val iconWhite: String? = null,
    @ColumnInfo(name = "category-icon-dark-blue")
    val iconDarkBlue: String? = null
){
    fun doesMatchSearchQuery(query: String): Boolean{
        return categoryName.contains(query,ignoreCase = true)
    }

    @SuppressLint("DiscouragedApi")
    fun getIconID(context: Context,iconColor: String): Int?{
        if (!icon.isNullOrEmpty()){
            if (iconColor == "White"){
                return context.resources.getIdentifier(iconWhite,"drawable",context.packageName)
            }
            else if(iconColor == "Dark Blue"){
                return context.resources.getIdentifier(iconDarkBlue,"drawable",context.packageName)
            }
            else{
                return context.resources.getIdentifier(icon,"drawable",context.packageName)
            }
        }
        else{
            return null
        }
    }
}

