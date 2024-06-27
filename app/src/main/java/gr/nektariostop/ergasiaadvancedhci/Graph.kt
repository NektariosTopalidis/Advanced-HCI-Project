package gr.nektariostop.ergasiaadvancedhci

import android.content.Context
import androidx.room.Room
import gr.nektariostop.ergasiaadvancedhci.data.Database

object Graph {
    lateinit var database: Database

    val alterationsRepository by lazy{
        AlterationsRepository(alterationDAO = database.alterationDao())
    }

    val productsRepository by lazy {
        ProductsRepository(productDAO = database.productDao())
    }

    val categoriesRepository by lazy {
        CategoriesRepository(categoriesDAO = database.categoriesDao())
    }

    val subcategoriesRepository by lazy {
        SubcategoriesRepository(subcategoriesDAO = database.subcategoriesDao())
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(context,Database::class.java,"shelfit.db")
            .createFromAsset("database/shelfit.db")
            .build()
    }
}