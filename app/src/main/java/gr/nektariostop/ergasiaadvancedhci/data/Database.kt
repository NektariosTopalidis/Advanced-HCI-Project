package gr.nektariostop.ergasiaadvancedhci.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.nektariostop.ergasiaadvancedhci.util.Converters


@Database(
    entities = [Alteration::class,Product::class,Category::class,SubCategory::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract fun alterationDao(): AlterationDAO
    abstract fun productDao(): ProductDAO
    abstract fun categoriesDao(): CategoryDAO
    abstract fun subcategoriesDao(): SubCategoryDAO
}