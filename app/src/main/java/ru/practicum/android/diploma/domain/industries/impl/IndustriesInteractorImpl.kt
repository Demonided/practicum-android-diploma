package ru.practicum.android.diploma.domain.industries.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.domain.industries.IndustriesAllDeal
import ru.practicum.android.diploma.domain.industries.IndustriesInteractor
import ru.practicum.android.diploma.domain.industries.IndustriesRepository
import ru.practicum.android.diploma.util.Resource

class IndustriesInteractorImpl(
    val industriesRepository: IndustriesRepository
) : IndustriesInteractor {
    override fun searchIndustries(): Flow<Pair<List<IndustriesAllDeal>?, Int?>> {
        return industriesRepository.searchIndustries().map { resource ->
            when (resource) {
                is Resource.Success -> Pair(resource.data, null)
                is Resource.Error -> Pair(null, resource.message)
            }
        }
    }
}
