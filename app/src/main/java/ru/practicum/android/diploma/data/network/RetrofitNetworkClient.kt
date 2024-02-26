package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.data.NetworkClient
import ru.practicum.android.diploma.data.Response
import ru.practicum.android.diploma.data.ResponseCodes
import ru.practicum.android.diploma.data.vacancydetail.dto.DetailRequest
import ru.practicum.android.diploma.data.vacancylist.dto.VacanciesSearchRequest

class RetrofitNetworkClient(
    private val context: Context,
    private val jobVacancySearchApi: JobVacancySearchApi
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = ResponseCodes.NO_CONNECTION }
        }

        return when (dto) {
            is VacanciesSearchRequest ->
                try {
                    val response = jobVacancySearchApi.getVacancyDetail(dto.queryMap)
                    response.apply { resultCode = ResponseCodes.SUCCESS }
                } catch (e: Throwable) {
                Response().apply { resultCode = ResponseCodes.ERROR }
            }

            is DetailRequest ->
                try {
                    val response = jobVacancySearchApi.getVacancyDetail(dto.id)
                    response.apply { resultCode = ResponseCodes.SUCCESS }
                } catch (e: Throwable) {
                Response().apply { resultCode = ResponseCodes.ERROR }
            }

            else ->
                Response().apply { resultCode = ResponseCodes.ERROR }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
