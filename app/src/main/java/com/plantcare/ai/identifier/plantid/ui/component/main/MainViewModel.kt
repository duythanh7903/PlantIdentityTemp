package com.plantcare.ai.identifier.plantid.ui.component.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.plantcare.ai.identifier.plantid.data.network.dto.ResponseLocationDto
import com.plantcare.ai.identifier.plantid.data.network.dto.ResponseWeatherDto
import com.plantcare.ai.identifier.plantid.data.network.repository.LocationRepository
import com.plantcare.ai.identifier.plantid.data.network.repository.WeatherRepository
import com.plantcare.ai.identifier.plantid.domains.WeatherDomain
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import com.plantcare.ai.identifier.plantid.utils.WeatherUtils.keys
import com.plantcare.ai.identifier.plantid.utils.WeatherUtils.values
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepo: WeatherRepository, private val locationRepo: LocationRepository
) : BaseViewModel() {

    val weatherStates: List<WeatherDomain> = keys.zip(values).map { (key, value) ->
        WeatherDomain(key, value)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseLocationDto = MutableLiveData<ResponseLocationDto?>()
    val responseLocationDto: LiveData<ResponseLocationDto?> = _responseLocationDto

    private val _responseWeatherDto = MutableLiveData<ResponseWeatherDto?>()
    val responseWeatherDto: LiveData<ResponseWeatherDto?> = _responseWeatherDto

    @SuppressLint("LogNotTimber")
    fun fetchDataLocation(lat: Double, lon: Double) = bgScope.launch {
        _isLoading.postValue(true)

        val res = locationRepo.fetchDataLocation(lat, lon)
        if (res is ResultWrapper.Success) {
            val dataResponseLocationDto = res.value
            _responseLocationDto.postValue(dataResponseLocationDto)
        } else _responseLocationDto.postValue(null)

        _isLoading.postValue(false)
    }

    @SuppressLint("LogNotTimber")
    fun fetchDataWeather(cityCode: String, lat: Double, lon: Double) = bgScope.launch {
        val res = weatherRepo.fetchDataWeather(
            cityCode = cityCode, lat = lat, long = lon
        )

        if (res is ResultWrapper.Success) {
            val data = res.value
            _responseWeatherDto.postValue(data)
        } else {
            _responseWeatherDto.postValue(null)
            Log.d("duylt", "Response Weather Wrong: ${res}")
        }
    }

    fun postResponseLocationToNull() =
        _responseLocationDto.postValue(null)
}