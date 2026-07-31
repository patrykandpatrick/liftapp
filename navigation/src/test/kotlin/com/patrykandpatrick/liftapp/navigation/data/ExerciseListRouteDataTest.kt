package com.patrykandpatrick.liftapp.navigation.data

import com.patrykandpatrick.liftapp.navigation.serialization.ExercisesSerializer
import kotlin.test.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class ExerciseListRouteDataTest {

    @Test
    fun `mode serialization does not depend on class names`() {
        assertEquals(
            """{"type":"pick","resultKey":"result"}""",
            Json.encodeToString<ExerciseListRouteData.Mode>(
                ExerciseListRouteData.Mode.Pick("result")
            ),
        )
        assertEquals(
            """{"type":"view"}""",
            Json.encodeToString<ExerciseListRouteData.Mode>(ExerciseListRouteData.Mode.View),
        )
    }

    @Test
    fun `custom descriptors have stable names`() {
        assertEquals(
            "com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData.Mode",
            ExerciseListRouteData.Mode.serializer().descriptor.serialName,
        )
        assertEquals(
            "com.patrykandpatrick.liftapp.navigation.Routes.Home.Exercises",
            ExercisesSerializer.descriptor.serialName,
        )
    }
}
