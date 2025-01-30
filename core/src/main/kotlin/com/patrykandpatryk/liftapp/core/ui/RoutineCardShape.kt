package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.ui.graphics.Shape
import com.patrykandpatrick.vico.compose.component.shape.composeShape
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatrick.vico.core.component.shape.cornered.CutCornerTreatment
import com.patrykandpatrick.vico.core.component.shape.cornered.RoundedCornerTreatment

val RoutineCardShape: Shape =
    CorneredShape(
            topLeft = Corner.Absolute(20f, CutCornerTreatment),
            topRight = Corner.Absolute(16f, RoundedCornerTreatment),
            bottomLeft = Corner.Absolute(16f, RoundedCornerTreatment),
            bottomRight = Corner.Absolute(16f, RoundedCornerTreatment),
        )
        .composeShape()
