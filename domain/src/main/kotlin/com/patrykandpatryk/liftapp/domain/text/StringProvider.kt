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
    val dateFormatFull: String
    val dateFormatEdit: String

    val errorMustBeHigherThanZero: String

    fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean = true): String

    fun quoted(value: String): String

    fun getErrorNameTooLong(actual: Int, limit: Int = Constants.Input.NAME_MAX_CHARS): String

    fun getErrorCannotBeEmpty(name: String): String

    fun getMuscleName(muscle: Muscle): String

    fun toPrettyString(goal: Goal): String

    fun getResolvedName(name: Name): String

    fun fieldTooShort(actual: Int, minLength: Int): String

    fun fieldTooLong(actual: Int, maxLength: Int): String

    fun valueTooSmall(minValue: String): String

    fun valueTooBig(maxValue: String): String

    fun valueNotValidNumber(): String

    fun fieldCannotBeEmpty(): String

    fun fieldMustBeHigherThanZero(): String

    fun fieldMustBeHigherOrEqualTo(value: String): String

    fun doesNotEqual(formula: String, actual: String): String
}

inline fun StringProvider.getErrorCannotBeEmpty(getName: StringProvider.() -> String): String =
    getErrorCannotBeEmpty(getName())
