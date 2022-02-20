package ru.createtogether.bottom_calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.createtogether.birthday.imageCalendar.helpers.DayDiffUtilCallback
import ru.createtogether.bottom_calendar.model.DayModel
import ru.createtogether.birthday.imageCalendar.presenter.viewHolder.DayViewHolder
import ru.createtogether.bottom_calendar.databinding.ItemDayBinding
import ru.createtogether.common.helpers.AdapterActions
import kotlin.properties.Delegates

class DayAdapter(private val initDays: List<DayModel>, private val onDayClick: (DayModel) -> Unit) : RecyclerView.Adapter<DayViewHolder>(),
    AdapterActions {

    var days: List<DayModel> by Delegates.observable(emptyList()) { _, old, new ->
        DayDiffUtilCallback(old, new).calculateDiff().dispatchUpdatesTo(this)
    }

    init {
        days = initDays
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder(ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false), onDayClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size
    override fun getData() = days

    override fun setData(days: List<Any>) {
        this.days = days as List<DayModel>
    }

}