package com.plantcare.ai.identifier.plantid.ui.component.remind.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.plantcare.ai.identifier.plantid.R
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.databinding.ItemReminderDeleteBinding
import com.plantcare.ai.identifier.plantid.domains.AlarmDomainDelete
import com.plantcare.ai.identifier.plantid.ui.bases.BaseRecyclerViewAdapter
import com.plantcare.ai.identifier.plantid.ui.bases.ext.click
import com.plantcare.ai.identifier.plantid.utils.toAlarmEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderDeleteAdapter(
    private val contextParams: Context,
    private val onItemClick: (reminder: AlarmDomainDelete, index: Int) -> Unit
) : BaseRecyclerViewAdapter<AlarmDomainDelete>() {
    override fun getItemLayout(): Int = R.layout.item_reminder_delete

    @SuppressLint("SetTextI18n")
    override fun setData(
        binding: ViewDataBinding, item: AlarmDomainDelete, layoutPosition: Int
    ) {
        if (binding is ItemReminderDeleteBinding) {
            val pairTime = getTimeTitle(item.time)

            binding.tvDateOfWeek.apply {
                text = "txtDayOfWeek"
                isSelected = true
            }
            binding.tvTitle.apply {
                text = item.label
                isSelected = true
            }
            binding.tvTime.text = "${pairTime.first}:${pairTime.second}"
            binding.icChbDelete.isActivated = item.isSelected

            val listDayOfWeek = mutableListOf<String>()
            if (item.toAlarmEntity().getDay(AlarmEntity.MON)) listDayOfWeek.add(contextParams.getString(R.string.monday))
            if (item.toAlarmEntity().getDay(AlarmEntity.TUES)) listDayOfWeek.add(contextParams.getString(R.string.tuesday))
            if (item.toAlarmEntity().getDay(AlarmEntity.WED)) listDayOfWeek.add(contextParams.getString(R.string.wednesday))
            if (item.toAlarmEntity().getDay(AlarmEntity.THURS)) listDayOfWeek.add(contextParams.getString(R.string.thursday))
            if (item.toAlarmEntity().getDay(AlarmEntity.FRI)) listDayOfWeek.add(contextParams.getString(R.string.friday))
            if (item.toAlarmEntity().getDay(AlarmEntity.SAT)) listDayOfWeek.add(contextParams.getString(R.string.saturday))
            if (item.toAlarmEntity().getDay(AlarmEntity.SUN)) listDayOfWeek.add(contextParams.getString(R.string.sunday))

            var result = ""
            for (i in listDayOfWeek.indices) {
                result += if (i == listDayOfWeek.size - 1) {
                    listDayOfWeek[i]
                } else "${listDayOfWeek[i]}, "
            }

            binding.tvDateOfWeek.text = result
        }
    }

    override fun onClickViews(
        binding: ViewDataBinding, obj: AlarmDomainDelete, layoutPosition: Int
    ) {
        if (binding is ItemReminderDeleteBinding) {
            binding.root.click { onItemClick.invoke(obj, layoutPosition) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun submitData(newData: List<AlarmDomainDelete>) {
        list.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun changeSelected(
        index: Int,
        onEventAllItemDone: () -> Unit,
        onEventAllItemNotDone: () -> Unit,
        onEventMarkSomeItem: () -> Unit
    ) {
        list[index].isSelected = !list[index].isSelected
        notifyItemChanged(index)

        if (isMarkAllItem()) onEventAllItemDone.invoke()
        else if (isNotMarkAnyItem()) onEventAllItemNotDone.invoke()
        else onEventMarkSomeItem.invoke()
    }

    private fun isMarkAllItem() = list.all { it.isSelected }

    private fun isNotMarkAnyItem() = list.all { !it.isSelected }

    fun markAllItem() {
        list.forEach { item -> item.isSelected = true }
        notifyItemRangeChanged(0, list.size)
    }

    fun unMarkAllItem() {
        list.forEach { item -> item.isSelected = false }
        notifyItemRangeChanged(0, list.size)
    }

    private fun getTimeTitle(timeStamp: Long): Pair<String, String> {
        val date = Date(timeStamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedDate = format.format(date)

        val listResult = formattedDate.split(":")
        val hour = listResult[0]
        val minute = listResult[1]

        return Pair(hour, minute)
    }

    fun hasAnyItemSelected(): Boolean = list.any { it.isSelected }
}