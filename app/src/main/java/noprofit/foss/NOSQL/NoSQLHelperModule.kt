package noprofit.foss.NOSQL

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import noprofit.foss.MyApplication
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoSQLHelperModule {

    @Singleton
    @Provides
    fun provideBoxStore(@ApplicationContext context: Context): NoSQLHelper {
        val boxStore = (context.applicationContext as MyApplication).boxStore
        //android.util.Log.d("NoSQLHelperModule", "Providing NoSQLHelper with BoxStore: $boxStore")
        return NoSQLHelper(context,boxStore)
        //return NoSQLHelper(context)
    }
}