package com.patrykandpatryk.liftapp.domain.text

import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

interface StringProvider {

    val andInAList: String
    val name: String

    val list: String

    val dateFormatShort: String
    val dateFormatLong: String
    val timeFormatShort24h: String
    val timeFormatShort12h: String
    val timeFormatLong24h: String
    val timeFormatLong12h: String

    val errorMustBeHigherThanZero: String

    fun getDisplayUnit(
        unit: ValueUnit,
        respectLeadingSpaceSetting: Boolean = true,
    ): String

    fun quoted(value: String): String

    fun getErrorNameTooLong(actual: Int, limit: Int = Constants.Input.NAME_MAX_CHARS): String

    fun getErrorCannotBeEmpty(name: String): String

    fun getMuscleName(muscle: Muscle): String

    fun toPrettyString(goal: Goal): String

    fun getResolvedName(name: Name): String
}

inline fun StringProvider.getErrorCannotBeEmpty(getName: StringProvider.() -> String): String =
    getErrorCannotBeEmpty(getName())
