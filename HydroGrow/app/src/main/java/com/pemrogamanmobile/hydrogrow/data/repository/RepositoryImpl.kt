package com.pemrogamanmobile.hydrogrow.data.repository

import com.pemrogamanmobile.hydrogrow.domain.repository.GardenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



class RepositoryImpl @Inject constructor(

) : GardenRepository {

    override fun postMapTransactionBooking(mapTransactionBookingRequest: MapTransactionBookingRequest): Flow<Resource<Result<Booking>>> =
        ResourceMapper.fromApi(
            sourceCall = {
                bookingService.postMapTruckTransaction(
                    MapTransactionBookingRequestMapper.mapToBody(
                        mapTransactionBookingRequest
                    )
                )
            },
            mapResponse = {
                Result(
                    Status.getByStatus(it.status.orEmpty()),
                    BookingResponseMapper.mapToDomain(it.data)
                )
            }
        )

}

