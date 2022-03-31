package com.bignerdranch.android.criminal_intent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime (@PrimaryKey val id: UUID = UUID.randomUUID(),
                  var title: String = "",
                  var date: Date = Date(),
                  var isSolved: Boolean = false,
                  var suspect: String = "")


/* 1. Опредиление сущностей БД , сущности - классы моделей аннтотированые @Entity (сущность).
Room создаст таблицу БД для любого класса с такой аннотацией. Свойства модели станут столбцами базы.
 Отдельные преступления - строчками. Тут определяется структура таблиц БД , но не сама таблица.
 */