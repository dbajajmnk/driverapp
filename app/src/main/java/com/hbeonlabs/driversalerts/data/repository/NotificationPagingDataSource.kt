package com.hbeonlabs.driversalerts.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.mappers.toNotification
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess

class NotificationPagingDataSource(
    private val dataRepository: AppRepository
): PagingSource<Int, Warning>() {
    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Warning>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Warning> {
        val position = params.key ?: STARTING_PAGE_INDEX
        var res: LoadResult<Int, Warning>? = null
        val result = dataRepository.getAllNotificationsFromApi(position.toString(),"10")
        result.onSuccess {
            val list = arrayListOf<Warning>()
            it.list?.forEach {notificationResponseItem ->
                list.add(  notificationResponseItem.toNotification())
            }
            res =  LoadResult.Page(
                    data = list ?: emptyList(),
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (list.isEmpty()) null else position + 1
                )
        }.onError { _, message ->
            res = LoadResult.Error(Exception(message))
        }.onException {
            res = LoadResult.Error(it)
        }
        return res!!

    }

}
