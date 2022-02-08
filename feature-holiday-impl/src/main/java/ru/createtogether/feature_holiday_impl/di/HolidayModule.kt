package ru.createtogether.feature_holiday_impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.createtogether.feature_holiday_impl.data.HolidayRepositoryImpl
import ru.createtogether.feature_holiday_impl.domain.HolidayRepository
import ru.createtogether.feature_holiday_api.api.HolidayApi
import ru.createtogether.feature_network_impl.domain.ErrorHandlerRepository

@Module
@InstallIn(ViewModelComponent::class)
object HolidayModule {
    @Provides
    fun provideHolidayRepository(holidayApi: HolidayApi,
                                 errorHandlerRepository: ErrorHandlerRepository
    ): HolidayRepository {
        return HolidayRepositoryImpl(holidayApi = holidayApi,
            errorHandlerRepository = errorHandlerRepository
        )
    }
}