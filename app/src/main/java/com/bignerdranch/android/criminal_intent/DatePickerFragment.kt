package com.bignerdranch.android.criminal_intent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

class DatePickerFragment: DialogFragment() {



    interface Callbacks {
        fun onDateSelected (date: Date)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // реализовываем слушателя для выбора даты
        val dateListener = DatePickerDialog.OnDateSetListener {
            _: DatePicker, year: Int, month: Int, day: Int ->

            val resultDate: Date = GregorianCalendar(year,month,day).time

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onDateSelected(resultDate)
            }
        }



        val date = arguments?.getSerializable(ARG_DATE) as Date // приняли дату преступления

        val calendar = Calendar.getInstance() // получаем обьект Календарь
        calendar.time = date

        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

    return DatePickerDialog(
        requireContext(), // требует контекст
        dateListener,
        initialYear,  // День, месяц, год - к которым должно быть иницализировано окно выбора даты
        initialMonth,
        initialDay
    )}





    companion object {

        fun newInstance(date: Date): DatePickerFragment {

            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args    // передали инфо о дате, через свойство - арументы фрагмента.
            }

        }
    }










}