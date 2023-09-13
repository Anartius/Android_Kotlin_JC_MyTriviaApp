package com.example.jcmytriviaapp.di

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.jcmytriviaapp.data.DataOrException
import com.example.jcmytriviaapp.model.QuestionItem
import com.example.jcmytriviaapp.network.QuestionApi
import com.example.jcmytriviaapp.repository.QuestionRepository
import com.example.jcmytriviaapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideQuestionRepository(api: QuestionApi) = QuestionRepository(api)

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }
}