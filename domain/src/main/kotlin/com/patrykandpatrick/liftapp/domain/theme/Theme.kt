package com.patrykandpatrick.liftapp.domain.theme

/** Which color scheme the app draws itself in. */
enum class Theme {
    FollowSystem,
    Light,
    Dark,
}

/**
 * Resolves [Theme] against the system's own dark mode setting, which only [Theme.FollowSystem]
 * defers to.
 */
fun Theme.isDarkMode(systemDarkMode: Boolean): Boolean =
    when (this) {
        Theme.FollowSystem -> systemDarkMode
        Theme.Light -> false
        Theme.Dark -> true
    }
