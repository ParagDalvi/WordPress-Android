package org.wordpress.android.ui.stats.refresh.lists.sections.granular.usecases

import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.wordpress.android.R.string
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.model.stats.time.VideoPlaysModel
import org.wordpress.android.fluxc.network.utils.StatsGranularity
import org.wordpress.android.fluxc.store.StatsStore.TimeStatsTypes.VIDEOS
import org.wordpress.android.fluxc.store.stats.time.VideoPlaysStore
import org.wordpress.android.modules.UI_THREAD
import org.wordpress.android.ui.stats.refresh.lists.NavigationTarget.ViewUrl
import org.wordpress.android.ui.stats.refresh.lists.NavigationTarget.ViewVideoPlays
import org.wordpress.android.ui.stats.refresh.lists.sections.BaseStatsUseCase.StatelessUseCase
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Empty
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Label
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Link
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.ListItemWithIcon
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.NavigationAction
import org.wordpress.android.ui.stats.refresh.lists.sections.BlockListItem.Title
import org.wordpress.android.ui.stats.refresh.lists.sections.granular.SelectedDateProvider
import org.wordpress.android.ui.stats.refresh.lists.sections.granular.UseCaseFactory
import org.wordpress.android.ui.stats.refresh.utils.StatsDateFormatter
import org.wordpress.android.ui.stats.refresh.utils.toFormattedString
import javax.inject.Inject
import javax.inject.Named

private const val PAGE_SIZE = 6

class VideoPlaysUseCase
constructor(
    private val statsGranularity: StatsGranularity,
    @Named(UI_THREAD) private val mainDispatcher: CoroutineDispatcher,
    private val store: VideoPlaysStore,
    private val selectedDateProvider: SelectedDateProvider,
    private val statsDateFormatter: StatsDateFormatter
) : StatelessUseCase<VideoPlaysModel>(VIDEOS, mainDispatcher) {
    override suspend fun loadCachedData(site: SiteModel) {
        val dbModel = store.getVideoPlays(
                site,
                statsGranularity,
                PAGE_SIZE,
                selectedDateProvider.getSelectedDate(statsGranularity)
        )
        dbModel?.let { onModel(it) }
    }

    override suspend fun fetchRemoteData(site: SiteModel, forced: Boolean) {
        val response = store.fetchVideoPlays(
                site,
                PAGE_SIZE,
                statsGranularity,
                selectedDateProvider.getSelectedDate(statsGranularity),
                forced
        )
        val model = response.model
        val error = response.error

        when {
            error != null -> onError(error.message ?: error.type.name)
            model != null -> onModel(model)
            else -> onEmpty()
        }
    }

    override fun buildUiModel(domainModel: VideoPlaysModel): List<BlockListItem> {
        val items = mutableListOf<BlockListItem>()
        items.add(Title(string.stats_videos))

        if (domainModel.plays.isEmpty()) {
            items.add(Empty)
        } else {
            items.add(Label(string.stats_videos_title_label, string.stats_videos_views_label))
            items.addAll(domainModel.plays.mapIndexed { index, videoPlays ->
                ListItemWithIcon(
                        text = videoPlays.title,
                        value = videoPlays.plays.toFormattedString(),
                        showDivider = index < domainModel.plays.size - 1,
                        navigationAction = videoPlays.url?.let { NavigationAction.create(it, this::onItemClick) }
                )
            })

            if (domainModel.hasMore) {
                items.add(
                        Link(
                                text = string.stats_insights_view_more,
                                navigateAction = NavigationAction.create(statsGranularity, this::onViewMoreClick)
                        )
                )
            }
        }
        return items
    }

    private fun onViewMoreClick(statsGranularity: StatsGranularity) {
        navigateTo(ViewVideoPlays(statsGranularity, statsDateFormatter.todaysDateInStatsFormat()))
    }

    private fun onItemClick(url: String) {
        navigateTo(ViewUrl(url))
    }

    class VideoPlaysUseCaseFactory
    @Inject constructor(
        @Named(UI_THREAD) private val mainDispatcher: CoroutineDispatcher,
        private val store: VideoPlaysStore,
        private val selectedDateProvider: SelectedDateProvider,
        private val statsDateFormatter: StatsDateFormatter
    ) : UseCaseFactory {
        override fun build(granularity: StatsGranularity) =
                VideoPlaysUseCase(
                        granularity,
                        mainDispatcher,
                        store,
                        selectedDateProvider,
                        statsDateFormatter
                )
    }
}
