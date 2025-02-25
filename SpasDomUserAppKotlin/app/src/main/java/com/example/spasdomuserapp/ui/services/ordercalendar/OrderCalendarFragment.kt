package com.example.spasdomuserapp.ui.services.ordercalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spasdomuserapp.R
import com.example.spasdomuserapp.databinding.FragmentOrderCalendarBinding
import com.example.spasdomuserapp.models.WorkerDay
import com.example.spasdomuserapp.models.WorkerMonth
import com.example.spasdomuserapp.models.WorkerTime
import com.example.spasdomuserapp.network.Resource
import com.example.spasdomuserapp.util.formatDate
import com.example.spasdomuserapp.util.handleApiError
import com.example.spasdomuserapp.util.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class PlannedDateFragment : Fragment(R.layout.fragment_order_calendar) {

    private lateinit var selectedDate: LocalDate
    private var calendarViewAdapter: CalendarViewAdapter? = null
    private val viewModel by viewModels<OrderCalendarViewModel>()
    private lateinit var binding: FragmentOrderCalendarBinding
    private val args by navArgs<PlannedDateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_calendar, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.progressBar.visible(false)

        // TODO(In some reason doesn't work on devices <= O)
        selectedDate = LocalDate.now()

        binding.btnLeft.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            drawMonthCalendar()
        }

        binding.btnRight.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            drawMonthCalendar()
        }

        drawMonthCalendar()

        binding.btnSendOrder.setOnClickListener {
            if (viewModel.date == null) {
                Snackbar.make(requireView(), "Дата не может быть пустой", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (viewModel.time == null || viewModel.workerId == null) {
                Snackbar.make(requireView(), "Время не может быть пустым", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val date = viewModel.date + " " + viewModel.time
            val time: List<String>? = viewModel.time?.split("-")
            val timeStartForFormat = viewModel.date + " " + time?.get(0)?.trim()
            val timeEndForFormat = viewModel.date + " " + time?.get(1)?.trim()

            val timeStart = formatDate(timeStartForFormat)
            val timeEnd = formatDate(timeEndForFormat)

            // TODO(the same for market post)

            if (args.plannedOrderPost != null) {
                var finalOrder = args.plannedOrderPost
                finalOrder = finalOrder!!.copy(timeStart = timeStart, timeEnd = timeEnd, workerId = viewModel.workerId)
                viewModel.postPlannedOrder(finalOrder)
            } else if (args.marketOrderPost != null) {
                var marketOrder = args.marketOrderPost
                marketOrder = marketOrder!!.copy(date = date)
                viewModel.postMarketOrder(marketOrder)
            }
        }


        viewModel.plannedResponsePlanned.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                       // Log.i("orderResponse", it.value.plannedOrder.toString())
                        val action = PlannedDateFragmentDirections.actionPlannedDateFragmentToSuccessFragment()
                        findNavController().navigate(action)
                    }
                }
                is Resource.Loading -> { }
                is Resource.Failure -> handleApiError(it) {  }
            }
        }


        viewModel.marketResponsePlanned.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                       // Log.i("marketResponse", it.value.marketOrder.toString())
                        val action = PlannedDateFragmentDirections.actionPlannedDateFragmentToSuccessFragment()
                        findNavController().navigate(action)
                    }
                }
                is Resource.Loading -> { }
                is Resource.Failure -> handleApiError(it) {  }
            }
        }

        return binding.root
    }

    @SuppressLint("ResourceType")
    private fun drawTimeSchedule(timeList: List<WorkerTime>) {

        val layoutParamsVar = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        clearRadioGroup()

        for (i in timeList.indices) {
            val radioButton = RadioButton(requireContext())
            radioButton.layoutParams = layoutParamsVar
            radioButton.text = timeList[i].time
            radioButton.id = i
            binding.radioGroupTimes.addView(radioButton)
        }

        binding.radioGroupTimes.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            if (rb != null) {
                viewModel.time = rb.text.toString()

                timeList.forEach {
                    if (it.time == viewModel.time) {
                        viewModel.workerId = it.workerId
                    }
                }
            }
        }
    }

    private fun drawMonthCalendar() {
        calendarViewAdapter = CalendarViewAdapter(DateClick {

            val dateText = monthYearFromDate(selectedDate)
            val textForView = it.day + " " + dateText
            binding.textViewDate.text = textForView

            viewModel.date = textForView

            if (it.timesList != null) {
                drawTimeSchedule(it.timesList)
            } else {
                clearRadioGroup()
            }
        })

        binding.root.findViewById<RecyclerView>(R.id.calendarRecyclerView).apply {
            layoutManager = GridLayoutManager(activity, 7)
            adapter = calendarViewAdapter
        }


        // TODO(Uncomment when you will receive preview workers from server)
        if (args.marketOrderPost != null) {
            val order = args.marketOrderPost
            viewModel.getWorkerCalendarById(order!!.workerId)
        } else if (args.plannedOrderPost != null) {
            val order = args.plannedOrderPost
            viewModel.getWorkerCalendar(order!!.subcategoryId)
        }

        viewModel.calendar.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    setUpCalendar(it.value)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) { }
            }
        }

        setUpCalendar(viewModel.workerMonth)
    }

    private fun setUpCalendar(workerMonth: List<WorkerMonth>) {
        val dateText = monthYearFromDate(selectedDate)
        for (i in workerMonth.indices) {
            if (workerMonth[i].month == dateText) {
                calendarViewAdapter?.daysOfMonth = workerMonth[i].workerMonth
                calendarViewAdapter?.activeMonth = workerMonth[i].month
                calendarViewAdapter?.currentDay = currentDay()
                calendarViewAdapter?.currentMonth = currentMonth()
            }
        }

        binding.monthYearTV.text = dateText
    }


    private fun clearRadioGroup() {
        binding.radioGroupTimes.clearCheck()
        binding.radioGroupTimes.removeAllViews()
        viewModel.time = null
    }

    private fun monthYearFromDate(date: LocalDate): String? {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    private fun currentDay(): String {
        val sdf = SimpleDateFormat("dd")
        return sdf.format(Date())
    }

    private fun currentMonth(): String {
        val sdf = SimpleDateFormat("MMMM yyyy")
        return sdf.format(Date())
    }
}

class DateClick(val block: (WorkerDay) -> Unit) {
    fun onClick(workerDate: WorkerDay) = block(workerDate)
}