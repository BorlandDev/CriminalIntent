package com.bignerdranch.android.criminal_intent

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class CrimeFragment: Fragment() , DatePickerFragment.Callbacks{

    private lateinit var crime: Crime  // Храним преступления
    private lateinit var titleField: EditText // Заголовок преступления
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox // Раскрыто ли ?

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }   // Получаем ссылку на вью модель




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime() // инициализируем первое преступление

        // получаем данные о выбраном преступлении в списке из базы данных
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        // загружаем преступление
        crimeDetailViewModel.loadCrime(crimeId)

     }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Следим за значением crimeLiveData во вьюМодели и обновляем UI при публикации новых данных
        crimeDetailViewModel.crimeLiveData.observe(

            viewLifecycleOwner,

            Observer { crime ->

                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })
    }








    override fun onStart() {
        super.onStart()

        // создаем анонимный класс реализующий интерфейс TextWatcher (Слушатель/наблюдатель)
        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                // преобразует ввод пользователя из CharSequence в String - используется для задания заголовка Crime
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequance: Editable?) {}
        }

        // слушатель для EditText
        titleField.addTextChangedListener(titleWatcher)

        // слушатель для CheckBox
        solvedCheckBox.apply {

            setOnCheckedChangeListener { _, isChecked ->  // оператор _ , заменяет неиспользуемый параметр в лямбде

                crime.isSolved = isChecked
                // изменяем поле Решено в нашем преступлении
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {

                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }   /* Конструкция this@CrimeFragment необходима для вызова функции requireFragmentManager
                 из CrimeFragment, а не из DatePickerFragment. Он ссылается на DatePickerFragment
                внутри блока apply, поэтому необходимо указать this из внешней области видимости. */
    }




    override fun onStop() {
        super.onStop()

            // при закрытии фрагмента сохраняем введенный текст
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected (date: Date) {
        crime.date = date
        updateUI()
    }



    private fun updateUI () {

        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }






    /*  Инкапсулируем получение экземпляра фрагмента с помощью статической функции, которая создает
    экземпляр фрагмента, упаковывает и задает его аргументы.
     */

    companion object {

        fun newInstance (crimeId: UUID): CrimeFragment {

            val args = Bundle().apply {         // Записываем данные в пакет аргументов
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args  // присоединяем пакет аргументов в свойство Fragment-та
            }
        }
    }



}




