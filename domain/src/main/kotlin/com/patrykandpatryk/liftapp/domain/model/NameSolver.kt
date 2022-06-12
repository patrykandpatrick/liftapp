package com.patrykandpatryk.liftapp.domain.model

interface NameSolver {

    fun getSolvedString(name: Name): String
}
