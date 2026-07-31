package com.patrykandpatrick.liftapp.core.backup

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.patrykandpatrick.liftapp.domain.backup.BackupFile
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.GetShareableLocationUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** A file to hand to whichever app the user picks. */
data class ShareBackupEvent(val location: BackupLocation)

/**
 * Starting an activity needs a `Context`, which a `ViewModel` has no business holding. The view
 * model decides that something should be shared and this carries that decision out to the screen,
 * which fires the intent.
 */
class ShareBackupEvents
@Inject
constructor(private val getShareableLocation: GetShareableLocationUseCase) {

    private val _events = MutableSharedFlow<ShareBackupEvent>(extraBufferCapacity = 1)

    val events: Flow<ShareBackupEvent> = _events.asSharedFlow()

    suspend fun share(file: BackupFile) {
        _events.emit(ShareBackupEvent(getShareableLocation.getShareableLocation(file.location)))
    }
}

/** Fires the share intent [events] asks for. */
@Composable
fun HandleShareBackupEvents(events: Flow<ShareBackupEvent>) {
    val context = LocalContext.current
    LaunchedEffect(events) {
        events.collect { event ->
            val intent =
                Intent(Intent.ACTION_SEND)
                    .setType(MIME_TYPE)
                    .putExtra(Intent.EXTRA_STREAM, event.location.value.toUri())
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(
                Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}

private const val MIME_TYPE = "application/octet-stream"
