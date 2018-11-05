package org.wordpress.android.ui.stats.refresh

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.modules.DEFAULT_SCOPE
import org.wordpress.android.modules.UI_SCOPE
import org.wordpress.android.ui.pages.PageItem.Action
import org.wordpress.android.ui.pages.PageItem.Page
import org.wordpress.android.ui.pages.SnackbarMessageHolder
import org.wordpress.android.ui.stats.refresh.InsightsUiState.StatsListState
import org.wordpress.android.ui.stats.refresh.InsightsUiState.StatsListState.DONE
import org.wordpress.android.ui.stats.refresh.InsightsUiState.StatsListState.FETCHING
import org.wordpress.android.viewmodel.ScopedViewModel
import org.wordpress.android.viewmodel.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class StatsViewModel
@Inject constructor(
    private val insightsViewModel: InsightsViewModel,
    @Named(UI_SCOPE) private val uiScope: CoroutineScope,
    @Named(DEFAULT_SCOPE) private val defaultScope: CoroutineScope
) : ScopedViewModel() {
    private lateinit var site: SiteModel

    private val _listState = MutableLiveData<StatsListState>()
    val listState: LiveData<StatsListState> = _listState

    private var isInitialized = false

    private val _showSnackbarMessage = SingleLiveEvent<SnackbarMessageHolder>()
    val showSnackbarMessage: LiveData<SnackbarMessageHolder> = _showSnackbarMessage

    fun start(site: SiteModel) {
        // Check if VM is not already initialized
        if (!isInitialized) {
            isInitialized = true

            this.site = site
            this.insightsViewModel.reset()

            uiScope.loadStats()
        }
    }

    private fun CoroutineScope.loadStats() = launch {
        reloadStats()
    }

    private suspend fun reloadStats() {
        _listState.value = FETCHING

        insightsViewModel.loadInsightItems(site)

        _listState.value = DONE
    }

    // TODO: To be implemented in the future
    fun onMenuAction(action: Action, page: Page): Boolean {
        return when (action) {
            else -> true
        }
    }

    // TODO: To be implemented in the future
    fun onItemTapped(pageItem: Page) {
    }

    fun onPullToRefresh() {
        launch {
            reloadStats()
        }
    }
}
