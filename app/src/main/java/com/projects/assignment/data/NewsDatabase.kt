package com.projects.assignment.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.projects.assignment.models.Article

@Database(
        entities = [Article::class],
        version = 1
)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun articleDao():ArticleDao
    companion object {
        private val DB_NAME = "newsDatabase.db"

        @Volatile
        private var instance: NewsDatabase? = null

        @Synchronized
        fun getInstance(context: Context): NewsDatabase {
            if (instance == null) {
                instance = create(context)
            }
            return instance as NewsDatabase
        }

         fun NewsDatabase() {}

         fun create(context: Context): NewsDatabase {
            return Room.databaseBuilder(
                    context,
                    NewsDatabase::class.java,
                    DB_NAME).build()
        }
    }
}