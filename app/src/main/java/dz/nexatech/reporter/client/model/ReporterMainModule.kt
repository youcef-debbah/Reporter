package dz.nexatech.reporter.client.model

import android.content.Context
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReporterMainModule {

    @Provides
    @Singleton
    fun getFirebaseApp(@ApplicationContext context: Context): FirebaseApp =
        FirebaseApp.initializeApp(context)!!
}