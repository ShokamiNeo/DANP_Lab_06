package com.danp.danp_lab_06

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.withContext

class ExampleViewModel: ViewModel() {
    fun items(): Flow<PagingData<Pokemons.Pokemon>> {
        val loader: DataPageLoader = {pageIndex, pageSize ->
            getData(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {ExamplePagingSource(loader, PAGE_SIZE)}
        ).flow.cachedIn(viewModelScope).debounce(500)
    }

    private suspend fun getData(pageIndex: Int, pageSize: Int): List<Pokemons.Pokemon> = withContext(Dispatchers.IO) {
        delay(1000)
        val offset = pageIndex*pageSize
        val max = Pokemons.pokeList.size
        if (offset + pageSize <= max)
            return@withContext Pokemons.pokeList.subList(offset, offset+pageSize)
        else if(offset < max)
            return@withContext Pokemons.pokeList.subList(offset, max)
        else
            return@withContext listOf()
    }

    private companion object{
        const val PAGE_SIZE = 10
    }
}