package com.bignerdranch.android.criminal_intent.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer
import com.bignerdranch.android.criminal_intent.model.CrimeDetailViewModel
import android.text.format.DateFormat
import android.widget.*
import androidx.core.content.FileProvider
import com.bignerdranch.android.criminal_intent.model.Crime
import com.bignerdranch.android.criminal_intent.R
import java.io.File

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"


class CrimeFragment: Fragment() , DatePickerFragment.Callbacks{

    private lateinit var crime: Crime  // Храним преступления
    private lateinit var photoFile: File // ссылка на местонахождение файла фотографии
    private lateinit var photoUri: Uri

    private lateinit var titleField: EditText // Заголовок преступления
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox // Раскрыто ли ?
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

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


    // аналог setContentView, настраивает и возвращает готовую верстку экрана
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView

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
                                                    // вернет местонахождение снимка
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)

            // Функция преобразует локальный путь к файлу в Uri, который видит приложение камеры.
                    photoUri = FileProvider.getUriForFile(requireActivity(),
                            "com.bignerdranch.android.criminal_intent.fileprovider" , photoFile)

                    updateUI()
                }
            })
    }








    @SuppressLint("QueryPermissionsNeeded")
    override fun onStart() {
        super.onStart()

        // ****   НАЗНАЧЕНИЕ СУЛШАТЕЛЕЙ    **** //

  //************************************************************************************************

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

  //************************************************************************************************

        // слушатель для EditText
        titleField.addTextChangedListener(titleWatcher)


        // слушатель для CheckBox
        solvedCheckBox.apply {

            // оператор _ , заменяет неиспользуемый параметр в лямбде
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked         // изменяем поле Решено в нашем преступлении
             }
        }

  //************************************************************************************************

        // Слушатель для кнопки даты
        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {

                // назначение текущего фрагмента целевым по отношению к диалогу даты
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }   /* Конструкция this@CrimeFragment необходима для вызова функции requireFragmentManager
                 из CrimeFragment, а не из DatePickerFragment. Он ссылается на DatePickerFragment
                внутри блока apply, поэтому необходимо указать this из внешней области видимости. */

  //************************************************************************************************

    reportButton.setOnClickListener {
       // создание неявного интента
        Intent (Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, getCrimeReport()) // Пакуем отчет с форматной строкой.

            putExtra(
                Intent.EXTRA_SUBJECT, // Тема писльма
                getString(R.string.crime_report_subject))

        }.also { intent ->

            val chooserIntent = //
                Intent.createChooser(intent, getString(R.string.send_report))

            startActivity(chooserIntent)
        }
    }
    //**********************************************************************************************

    suspectButton.apply {

    val pickContactIntent = // Неявный интент, на запрос контакта из БД приложения Контакты через -
          Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI) // Content Provider

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

        /* PackageManager-у известно все о компонентах установленных на устройстве Android всключая
            все его activity, content provider, broadcast, service.
            Обьект ResolveInfo сообщает полную информацию о найденной активити
         */

            // Защита от отсутствия приложений адресной книги
            val packageManager: PackageManager = requireActivity().packageManager

            val resolvedActivity: ResolveInfo? =
        // Вызывая resolveActivity вы приказываете найти активити соответсвующую переданному интенту
         packageManager.resolveActivity(pickContactIntent,
             PackageManager.MATCH_DEFAULT_ONLY) // флаг ограничевает поиск по CATEGORY_DEFAULT

        // Если адресной книги нет - блокируем кнопку
            if (resolvedActivity == null) isEnabled = true

        }
       //*******************************************************************************************

        photoButton.apply {
       // Блокируем кнопку при отсутсвии приложения камеры или недоступности места хранения снимков
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent (MediaStore.ACTION_IMAGE_CAPTURE)

            val resolvedActivity: ResolveInfo? =
                    packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null) isEnabled = false
            // ***********************************************

            /* Что бы запрашивать photo uri мы должны предоставить разрешение приложению камеры.
        Для этого мы устанавливаем флаг Intent.FLAG_GRANT_WRITE_URI_PERMISSION, для каждой активити,
        которую может обрабатывать интент cameraImage. Так мы предоставляяем им всем разрешение на
        запись специально для этого uri.
             */

            setOnClickListener {     // кладем инфо о местонахождении снимка
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

            val cameraActivities: List<ResolveInfo> =
               packageManager.queryIntentActivities(
                                    captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                                cameraActivity.activityInfo.packageName,
                                photoUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }







    override fun onStop() {
        super.onStop()

            // при закрытии фрагмента сохраняем введенный текст
        crimeDetailViewModel.saveCrime(crime)
    }

    // принимаем дату из диалога , и отображаем ее в интерфейсе
    override fun onDateSelected (date: Date) {
        crime.date = date
        updateUI()
    }


    override fun onDetach() {
        super.onDetach()

        requireActivity().revokeUriPermission(photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

    }



    private fun updateUI () {

        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

        // Если у преступления есть подозреваемый - отобрози его на кнопке
        if (crime.suspect.isNotEmpty()) suspectButton.text = crime.suspect

        updatePhotoView()
    }





    private fun updatePhotoView () {

        if (photoFile.exists()) {
            val bitmap = getScaleBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
            photoView.contentDescription = getString(R.string.crime_photo_image_description)
        }
        else photoView.setImageBitmap(null)
        photoView.contentDescription = getString(R.string.crime_photo_no_image_description)
    }










 /* Обращение к Content Provider осуществляется через Content Resolver.
  */

// Получение интента который включает URI данных - ссылку на конкретный контакт из адресной книги
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when {  // Подозреваемый не найден (не выбран в списке контактов)
            resultCode != Activity.RESULT_OK -> return

     // Если мы получили результаты от нужной нам активити с контактами то их requestCode совпадут.
            requestCode == REQUEST_CONTACT && data != null -> {

                val contactUri: Uri? = data.data // указываем местонахождение данных в БД

     // Указать для каких полей ваш запрос должен возвращать значения (Вернет все имена контактов)
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                // Выполняемый здесь запрос Uri похож на предложение "where"
                val cursor = requireActivity().contentResolver
                        .query(contactUri!!, queryFields, null, null, null)


                cursor?.use {
                    // Убедитесь, что курсор содержит хотя бы один результат
                    if (it.count == 0) return


                    // Первый столбец первой строки данных это имя вашего подозреваемого
                    it.moveToFirst()
                    val suspect = it.getString(0)

                    // Сохраняем данные о подозреваемом в БД, и выводим на кнопке
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }

            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    updatePhotoView()
            }
        }
    }












// Функция создает четыре строки, соединяяет и возвращает полный отчет о преступлени.
    private fun getCrimeReport (): String {

        // Раскрыто ли преступление ?
        val solvedString = if (crime.isSolved) getString(R.string.crime_report_solved)
                            else (R.string.crime_report_unsolved)

        // Получаем дату в выше опреленном формате
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        // Имя подозреваемого , есле оно не пусто
        val suspect = if (crime.suspect.isBlank()) getString(R.string.crime_report_no_suspect)
                        else getString(R.string.crime_report_suspect, crime.suspect)


        // Форматная строка, заголовок преступления, дата, раскрыто ли, имя подозреваемого.
        return getString(R.string.crime_report, crime.title, dateString, solvedString , suspect)

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




