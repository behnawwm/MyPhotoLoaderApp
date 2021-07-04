package com.example.myphotoloaderapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myphotoloaderapp.data.local.dao.FavoritePhotoDao
import com.example.myphotoloaderapp.data.model.FavoritePhoto
import com.example.myphotoloaderapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [FavoritePhoto::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoritePhotoDao

    class Callback @Inject constructor(
        private val database: Provider<FavoriteDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().favoriteDao()

            applicationScope.launch {
//                dao.insert(Task("Wash the dishes", "dsadasd"))
//                dao.insert(Task("Do the laundry", "wwwe"))
//                dao.insert(Task("Buy groceries", "hhhhh", isStarred = true))
//                dao.insert(Task("Call mom", "hhh", isDone = true))
//                dao.insert(Task("Visit grandma", "dsj;flk", isStarred = true, isDone = true))

            }
        }
    }
}