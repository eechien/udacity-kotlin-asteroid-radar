package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.util.Constants.API_KEY
import com.udacity.asteroidradar.util.Constants.BASE_URL
import com.udacity.asteroidradar.domain.PictureOfDay
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type
import kotlin.reflect.KClass


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Json {}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scalar {}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(
        AnnotatedConverterFactory.Builder()
            .add(Json::class, MoshiConverterFactory.create(moshi))
            .add(Scalar::class, ScalarsConverterFactory.create())
            .build()
    )
    .baseUrl(BASE_URL)
    .build()

interface NasaApiService {

    @GET("neo/rest/v1/feed")
    @Scalar
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = API_KEY
    ): String

    @GET("planetary/apod")
    @Json
    suspend fun getPictureOfTheDay(@Query("api_key") apiKey: String = API_KEY):
            PictureOfDay
}

class AnnotatedConverterFactory(val factories: Map<KClass<*>, Converter.Factory>): Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            var factory: Converter.Factory? = factories[annotation.annotationClass]
            if (factory != null) {
                return factory.responseBodyConverter(type, annotations, retrofit)
            }
        }
        return null
    }

    class Builder {
        val factories: LinkedHashMap<KClass<*>, Converter.Factory> = LinkedHashMap()

        fun add(cls: KClass<*>, factory: Converter.Factory): Builder {
            if (cls == null) {
                throw NullPointerException("cls")
            }
            if (factory == null) {
                throw NullPointerException("factory")
            }

            factories[cls] = factory
            return this
        }

        fun build(): AnnotatedConverterFactory {
            return AnnotatedConverterFactory(factories)
        }
    }
}

object NasaApi {
    val retrofitService : NasaApiService by lazy {
        retrofit.create(NasaApiService::class.java)
    }
}