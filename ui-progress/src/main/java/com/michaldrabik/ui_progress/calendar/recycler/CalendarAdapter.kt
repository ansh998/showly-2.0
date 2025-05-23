package com.michaldrabik.ui_progress.calendar.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.michaldrabik.ui_base.BaseAdapter
import com.michaldrabik.ui_model.CalendarMode
import com.michaldrabik.ui_progress.calendar.views.CalendarFiltersView
import com.michaldrabik.ui_progress.calendar.views.CalendarHeaderView
import com.michaldrabik.ui_progress.calendar.views.CalendarItemView

class CalendarAdapter(
  private val itemClickListener: (CalendarListItem) -> Unit,
  private val missingImageListener: (CalendarListItem, Boolean) -> Unit,
  private val missingTranslationListener: (CalendarListItem) -> Unit,
  var detailsClickListener: ((CalendarListItem.Episode) -> Unit),
  var checkClickListener: ((CalendarListItem.Episode) -> Unit),
  var modeClickListener: ((CalendarMode) -> Unit),
  var premieresClickListener: (() -> Unit),
) : BaseAdapter<CalendarListItem>() {

  companion object {
    private const val VIEW_TYPE_ITEM = 1
    private const val VIEW_TYPE_HEADER = 2
    private const val VIEW_TYPE_FILTERS = 3
  }

  override val asyncDiffer = AsyncListDiffer(this, CalendarItemDiffCallback())

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ) = when (viewType) {
    VIEW_TYPE_ITEM -> BaseViewHolder(
      CalendarItemView(parent.context).apply {
        itemClickListener = this@CalendarAdapter.itemClickListener
        missingImageListener = this@CalendarAdapter.missingImageListener
        missingTranslationListener = this@CalendarAdapter.missingTranslationListener
        detailsClickListener = this@CalendarAdapter.detailsClickListener
        checkClickListener = this@CalendarAdapter.checkClickListener
      },
    )
    VIEW_TYPE_HEADER -> BaseViewHolder(CalendarHeaderView(parent.context))
    VIEW_TYPE_FILTERS -> BaseViewHolder(
      CalendarFiltersView(parent.context).apply {
        onModeChipClick = this@CalendarAdapter.modeClickListener
        onPremieresChipClick = this@CalendarAdapter.premieresClickListener
      },
    )
    else -> throw IllegalStateException()
  }

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder,
    position: Int,
  ) {
    when (val item = asyncDiffer.currentList[position]) {
      is CalendarListItem.Episode -> (holder.itemView as CalendarItemView).bind(item)
      is CalendarListItem.Header -> (holder.itemView as CalendarHeaderView).bind(item, position)
      is CalendarListItem.Filters -> (holder.itemView as CalendarFiltersView).bind(item)
    }
  }

  override fun getItemViewType(position: Int) =
    when (asyncDiffer.currentList[position]) {
      is CalendarListItem.Header -> VIEW_TYPE_HEADER
      is CalendarListItem.Episode -> VIEW_TYPE_ITEM
      is CalendarListItem.Filters -> VIEW_TYPE_FILTERS
      else -> throw IllegalStateException()
    }
}
