package noprofit.foss

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.objectbox.BoxStore
import noprofit.foss.NOSQL.MyObjectBox

//for NOSQLHelper singleton
@HiltAndroidApp
class MyApplication : Application() {
    lateinit var boxStore: BoxStore
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        boxStore =  MyObjectBox.builder()
            .androidContext(this)
            .build()
        /**
         * //debug use
        android.util.Log.d("APPLICATION START","FFFFFFFFFFFF YEA")//tested ok.
        android.util.Log.d("MyApplication", "BoxStore initialized: $boxStore")
        */
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}