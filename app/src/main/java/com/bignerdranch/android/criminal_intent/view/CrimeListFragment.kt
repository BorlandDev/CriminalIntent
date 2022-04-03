package com.bignerdranch.android.criminal_intent.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminal_intent.model.Crime
import com.bignerdranch.android.criminal_intent.R
import com.bignerdranch.android.criminal_intent.model.CrimeListViewModel
import java.util.*


private const val TAG = "CrimeListFragment"

class CrimeListFragment: Fragment() {

    // Требуемый Интерфейс !
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView

    // инициализируем адаптер пустым списком, пока ждем результаты из БД
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    // получаем ссылку на нашу вьюМодель через провайдера (инициализируем при первом обращеннии к нему)
    private val crimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }






    // Вызывается когда фрагмент прикрепляется к активити
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // context в данном случае == activity, обратный вызов будет идти именно в активити
        callbacks = context as Callbacks?
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // явно указываем фрагмент менеджеру вызвывать функию обртаного вызова
        setHasOptionsMenu(true)
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // заполняем макет фрагмента
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView

        // определяем как отображать элементы в ресайклере и как работает прокрутка , с помощью специальных менеджеров
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        /* когда новые данные будут опубликованы в LiveData, обновляем этими данными адаптер,
            заполняя утилизатор (Recycler)
                */
        crimeRecyclerView.adapter = adapter

        return view
    }








    override fun onViewCreated (view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            /* Функция регестрирует наблюдателя за экземпляром LiveData и связи наблюдателя с
            жизненным циклом другого компонента

             */
        crimeListViewModel.crimeListLiveData.observe (

            viewLifecycleOwner, /* Первый параметр функции observe - Владелец ЖЦ.
    (наблюдатель будет (жить) получать обновления данных столько,сколько живет Fragment) */

            /* Второй параметр, реализация Observer - наблюдатель. Он отвечает за реакцию на новые
данные из LiveData. Блок кода выполняется всякий раз когда обновляется список в  LiveData.
                  */

            Observer { crimes ->

                crimes?.let {    // если список не пуст
                    Log.i(TAG , "Got crimes ${crimes.size}")

                 /* Когда все виджеты будут готовы и отрисованы на экране и выпонятся запросы из БД,
              можно обновлять интерфейс. */
                    updateUI(crimes)
                }
            })
    }






    override fun onDetach() {
        super.onDetach()

        // после отсоединения фрагмента от активити, обратный вызов нам больше не нужен
        callbacks = null
    }


    // Вызывается когда возникает необходимость в меню
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // запонялем меню
        inflater.inflate(R.menu.fragment_crime_list, menu)

    }


    // когда пользователь выбирает команду в меню фрагмент получает обратный вызов этой функции
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // реагируем в зависимости от выбора команды в меню
        return when (item.itemId) {

            R.id.new_crime -> {
                val crime = Crime() // создаем новое преступление
                crimeListViewModel.addCrime(crime) // добавляем его в базу данных

                callbacks?.onCrimeSelected(crime.id) // уведомляет родительский компонент о том,
                            // что запрошено добавление нового преступления

                true // флаг - дальнейшая обработка менюшки не требуется
            }
            else -> return super.onOptionsItemSelected(item)

        }

    }




    private fun updateUI (crimes: List<Crime>) {

        // берем настроенный нами адаптер , с данными из модели
        adapter = CrimeAdapter(crimes)

        // устанавливаем адаптер в ресайклер
        crimeRecyclerView.adapter = adapter
    }










    // холдер - ячейка (визуальный элемент списка, контейнер для наших данных),
    // он будет содержать в себе данные заголовка и даты
    private inner class CrimeHolder(view: View) :
        RecyclerView.ViewHolder(view), View.OnClickListener {


        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)


        init { // ставим слушателя на каждую вьюшку вьюХолдера
            itemView.setOnClickListener (this)
        }

        fun bind (crime: Crime) {

            this.crime = crime

            // холдер обновляет заголовок и дату соответствующего преступления
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()

            // если раскрыто преступление , покажи наручники
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE

        }


        override fun onClick (v: View) {

        /* Уведомляем нашу хост-актиити через интерфейс обратного вызова,
                          о том какое преступление было выбрано в списке */
            callbacks?.onCrimeSelected(crime.id)


        }
    }







    /* Сам ресайклер ничего не знает об обькте преступления (Crime) или перечне перступлений которые будут отображаться,
       зато знает Адаптер. Он является прослойкой между RecycleView и данными которые тот должен отображать.
        Адаптер поставляет заполненые вьюХолдеры (контейнеры для наших данных)
            RecyclerView (утилизатор) вызывает эти три метода Адаптера .
    */

    private inner class CrimeAdapter (var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        // функция отвечает за создание вьюХолдера на дисплее
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : CrimeHolder {

            // заполняем list_item_view и оборачиваем эту вью в вьюХолдер
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_crime, parent, false)

            return CrimeHolder(view)
        }

        // утилизатор узнает заранее сколько элементов ему нужно будет отобразить
        override fun getItemCount() = crimes.size


        // в этой функции должно быть минимум вычислений для более плавной прокрутки интерфеса
        // отвечает за заполнение данного холдера преступлением из данной позиции (индекс списка преступлений)
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {

            val crime = crimes[position]

            // заполняем заголовки и даты по каждой позиции холдера в списке преступлений (связываем данные с вьюХолдером)
            holder.bind(crime)
        }
    }




    companion object { // Инкапуслируем получение нашего фрагмента , для активити и пр.
        fun newInstance(): CrimeListFragment = CrimeListFragment()
    }


}