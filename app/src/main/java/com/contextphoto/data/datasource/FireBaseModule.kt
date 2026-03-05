package com.contextphoto.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FireBaseModule {
    @Provides
    @Singleton
    fun provideFireBaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()
}
