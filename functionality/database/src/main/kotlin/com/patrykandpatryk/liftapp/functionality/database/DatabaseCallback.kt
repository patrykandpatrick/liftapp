package com.patrykandpatryk.liftapp.functionality.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.InsertDefaultBodyMeasurementsUseCase
import com.patrykandpatryk.liftapp.functionality.database.exercise.InsertDefaultExercises
import com.patrykandpatryk.liftapp.functionality.database.exercise.InsertDefaultRoutines
import dagger.Lazy
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DatabaseCallback
@Inject
constructor(
    private val insertDefaultBodyMeasurements: Lazy<InsertDefaultBodyMeasurementsUseCase>,
    private val insertDefaultExercises: Lazy<InsertDefaultExercises>,
    private val insertDefaultRoutines: Lazy<InsertDefaultRoutines>,
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        CoroutineScope(EmptyCoroutineContext).launch {
            insertDefaultBodyMeasurements.get()()
            insertDefaultExercises.get()()
            insertDefaultRoutines.get()()
        }
    }
}
