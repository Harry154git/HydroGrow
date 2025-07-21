package com.pemrogamanmobile.hydrogrow.data.mapper
//
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.google.firebase.crashlytics.ktx.crashlytics
//import com.google.firebase.ktx.Firebase
//import com.shiinasoftware.sedottinja.base.data.source.remote.api.response.ApiResponse
//import com.shiinasoftware.sedottinja.base.data.source.remote.api.response.ErrorApiResponse
//import com.shiinasoftware.sedottinja.base.domain.model.Failure
//import com.shiinasoftware.sedottinja.base.domain.model.Loading
//import com.shiinasoftware.sedottinja.base.domain.model.Success
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import retrofit2.HttpException
//import timber.log.Timber
//
//object ResourceMapper {
//    fun <RESPONSE, DOMAIN> fromApi(
//        sourceCall: suspend () -> ApiResponse<RESPONSE>,
//        mapResponse: (RESPONSE) -> DOMAIN,
//        cacheAction: (DOMAIN) -> Unit = {}
//    ) = flow {
//        emit(Loading)
//
//        val response = sourceCall()
//        val mappedData = response.result.let(mapResponse)
//        cacheAction(mappedData)
//        val result = Success(mappedData)
//
//        emit(result)
//    }.catch { throwable ->
//        Timber.e(throwable)
//
//        val message =
//            if (throwable is HttpException) {
//                try {
//                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                    val adapter = moshi.adapter(ErrorApiResponse::class.java)
//                    throwable.response()?.errorBody()?.string()?.let {
//                        adapter.fromJson(it)?.message.orEmpty()
//                    } ?: ""
//                } catch (exception: Exception) {
//                    exception.message.orEmpty()
//                }
//            } else {
//                throwable.message.orEmpty()
//            }
//
//        val result = Failure(message)
//
//        Firebase.crashlytics.recordException(Exception(message))
//        Firebase.crashlytics.log(message)
//
//        emit(result)
//    }.flowOn(Dispatchers.IO)
//
//
//    fun <RESPONSE, DOMAIN> fromMapApi(
//        sourceCall: suspend () -> RESPONSE,
//        mapResponse: (RESPONSE) -> DOMAIN,
//    ) = flow {
//        emit(Loading)
//        val response = sourceCall()
//        val mappedData = response.let(mapResponse)
//        val result = Success(mappedData)
//        emit(result)
//    }.catch { throwable ->
//        Timber.e(throwable)
//
//        val message =
//            if (throwable is HttpException) {
//                try {
//                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                    val adapter = moshi.adapter(ErrorApiResponse::class.java)
//                    throwable.response()?.errorBody()?.string()?.let {
//                        adapter.fromJson(it)?.message.orEmpty()
//                    } ?: ""
//                } catch (exception: Exception) {
//                    exception.message.orEmpty()
//                }
//            } else {
//                throwable.message.orEmpty()
//            }
//        val result = Failure(message)
//
//        Firebase.crashlytics.recordException(Exception(message))
//        Firebase.crashlytics.log(message)
//        emit(result)
//    }.flowOn(Dispatchers.IO)
//
//
//}