package com.patrykandpatrick.liftapp.domain.muscle

import kotlinx.serialization.Serializable

/**
 * The deltoid is three entries rather than one because the three heads receive wildly different
 * volume: every press loads the front delt, while the side and rear heads get almost none. A single
 * `Shoulders` row made weekly shoulder volume the least trustworthy number the app could report.
 *
 * Constant names are the persisted format — they are written into the exercise muscle columns as
 * JSON — so renaming one is a silent data break. `Shoulders` still appears in databases and in
 * backup archives written before the split; the database module's `MuscleListSerializer` maps it
 * onto [FrontDelts] on the way in.
 */
@Serializable
enum class Muscle {
    Abductors,
    Adductors,
    Abs,
    Biceps,
    Calves,
    Chest,
    Forearms,
    FrontDelts,
    Glutes,
    Hamstrings,
    Lats,
    LowerBack,
    Obliques,
    Quadriceps,
    RearDelts,
    Serratus,
    SideDelts,
    Traps,
    Triceps,
}
