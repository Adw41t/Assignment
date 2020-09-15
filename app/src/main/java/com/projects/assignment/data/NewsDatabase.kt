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
/*        @Volatile private var instance : NewsDatabase?=null
        //so that no two threads access at same time
        private val LOCK=Any()
        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance?: buildDatabase(context).also { instance=it }
        }

        private fun buildDatabase(context: Context)=
                Room.databaseBuilder(context.applicationContext,
                        NewsDatabase::class.java,"newsDatabase.db")
                        .build()
    }*/

        private val DB_NAME = "newsDatabase.db"

        @Volatile
        private var instance: NewsDatabase? = null

/*    @Synchronized
    open fun getInstance(context: Context): NewsDatabase? {
        if (instance == null) {
            instance = create(context)
        }
        return instance
    }

     open fun NewsDatabase() {}

     open fun create(context: Context): NewsDatabase? {
        return Room.databaseBuilder(
                context,
                NewsDatabase::class.java,
                DB_NAME).build()
    }*/

        open fun NewsDatabase() {}

        // Use this to call on any place
        open fun getInstance(): NewsDatabase? {
            return instance
        }

        // Use once to Create and setup the object
        open fun setInstance(context: Context): NewsDatabase? {
            if (instance == null) {
                synchronized(NewsDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                                NewsDatabase::class.java, DB_NAME).build()
                    }
                }
            }
            return instance
        }
    }
}