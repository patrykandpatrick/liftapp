package com.patrykandpatrick.liftapp.navigation.data

import kotlinx.serialization.Serializable

/**
 * [location] is the document URI of the file being restored, as a string. It arrives either from
 * the file picker or from another app opening a backup, so the route carries it rather than an ID.
 */
@Serializable data class BackupRestoreRouteData(val location: String)
