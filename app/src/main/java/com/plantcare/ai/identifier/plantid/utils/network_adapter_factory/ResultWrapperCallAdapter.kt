package com.plantcare.ai.identifier.plantid.utils.network_adapter_factory

import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.lang.reflect.Type

class ResultWrapperCallAdapter<R>(
    private val responseType: Type
) : CallAdapter<R, Call<ResultWrapper<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Call<ResultWrapper<R>> {
        return object : Call<ResultWrapper<R>> {
            override fun execute(): Response<ResultWrapper<R>> {
                throw UnsupportedOperationException("ResultWrapperCallAdapter does not support execute")
            }

            override fun enqueue(callback: retrofit2.Callback<ResultWrapper<R>>) {
                call.enqueue(object : retrofit2.Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        val result = if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                ResultWrapper.Success(body)
                            } else {
                                ResultWrapper.GenericError(response.code(), "Empty body")
                            }
                        } else {
                            ResultWrapper.GenericError(response.code(), response.message())
                        }
                        callback.onResponse(
                            this@ResultWrapperCallAdapter.adapt(call),
                            Response.success(result)
                        )
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        val result = if (t is java.io.IOException) {
                            ResultWrapper.NetworkError
                        } else {
                            ResultWrapper.GenericError(null, t.message)
                        }
                        callback.onResponse(
                            this@ResultWrapperCallAdapter.adapt(call),
                            Response.success(result)
                        )
                    }
                })
            }

            override fun isExecuted() = call.isExecuted

            override fun cancel() = call.cancel()

            override fun isCanceled() = call.isCanceled

            override fun clone(): Call<ResultWrapper<R>> = this

            override fun request() = call.request()
            override fun timeout(): Timeout = call.timeout()
        }
    }
}