package com.patrykandpatrick.liftapp.domain.target

interface TargetRepository :
    GetExerciseTargetsContract, GetPlanDayTargetsContract, UpsertExerciseTargetsContract
