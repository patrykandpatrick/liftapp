package com.patrykandpatryk.liftapp.functionality.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatryk.liftapp.functionality.database.exercise.InsertDefaultExercises
import com.patrykandpatryk.liftapp.functionality.database.measurement.InsertDefaultMeasurements
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class DatabaseCallback @Inject constructor(
    private val insertDefaultMeasurements: Lazy<InsertDefaultMeasurements>,
    private val insertDefaultExercises: Lazy<InsertDefaultExercises>,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        CoroutineScope(EmptyCoroutineContext).launch {
            insertDefaultMeasurements.get()()
            insertDefaultExercises.get()()
        }
    }
}
