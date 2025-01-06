package com.patrykandpatrick.liftapp.feature.workout

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.patrykandpatrick.liftapp.feature.workout.model.RestTimerState
import com.patrykandpatryk.liftapp.core.permission.getPermissionGrantedState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestTimerService : Service() {

    private val timerConfig = MutableSharedFlow<TimerConfig?>(replay = 1)

    private var isForegroundActive = false

    private var showNotification = false

    private var isPaused = false

    @Inject lateinit var restTimerNotificationManager: RestTimerNotificationManager

    private lateinit var serviceScope: CoroutineScope

    lateinit var timer: StateFlow<RestTimerState?>

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main.immediate + Job())
        initializeTimer()
        timer.filterNotNull().onEach(::onTimerProgress).launchIn(serviceScope)
    }

    private fun initializeTimer() {
        timer =
            timerConfig
                .flatMapLatest { config ->
                    if (config != null) {
                        isPaused = false
                        getTimer(config.duration, config.workoutID)
                    } else {
                        flowOf(null)
                    }
                }
                .stateIn(serviceScope, SharingStarted.WhileSubscribed(), null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) handleIntent(intent)
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_TOGGLE_TIMER -> toggleTimer()
            ACTION_CANCEL_TIMER -> cancelTimer()
            ACTION_ADD_TIMER_SECONDS -> {
                val seconds = intent.getIntExtra(EXTRA_SECONDS, 0)
                addTimerSeconds(seconds)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = RestTimerBinder()

    fun setShowNotification(showNotification: Boolean) {
        this.showNotification = showNotification
        if (!showNotification) stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun startTimer(duration: Duration, workoutID: Long) {
        timerConfig.tryEmit(TimerConfig(duration, workoutID))
    }

    private fun getTimer(duration: Duration, workoutID: Long): Flow<RestTimerState> {
        var remainingTime = duration

        return flow {
                var previousTime = System.currentTimeMillis()
                while (currentCoroutineContext().isActive) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTime = currentTime - previousTime
                    if (!isPaused) {
                        remainingTime -= elapsedTime.milliseconds
                    }

                    if (remainingTime.isPositive()) {
                        emit(
                            RestTimerState(
                                remainingFraction =
                                    remainingTime.inWholeMilliseconds /
                                        duration.inWholeMilliseconds.toFloat(),
                                remainingDuration = remainingTime,
                                isPaused = isPaused,
                                workoutID = workoutID,
                            )
                        )
                    } else {
                        break
                    }
                    previousTime = currentTime
                    delay(16)
                }
            }
            .distinctUntilChangedBy {
                "${it.remainingDuration.inWholeSeconds}, ${it.isPaused}, ${it.isFinished}"
            }
            .onCompletion { emit(RestTimerState.finished(workoutID)) }
            .shareIn(serviceScope, SharingStarted.WhileSubscribed(), 1)
    }

    fun toggleTimer() {
        isPaused = !isPaused
    }

    fun cancelTimer() {
        serviceScope.launch {
            timerConfig.emit(null)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun addTimerSeconds(seconds: Int) {
        val timerState = timer.value ?: return
        val currentSeconds = timerState.remainingDuration.inWholeSeconds
        timerConfig.tryEmit(TimerConfig((currentSeconds + seconds).seconds, timerState.workoutID))
    }

    @SuppressLint("MissingPermission")
    private fun onTimerProgress(timerState: RestTimerState) {
        val isForeground = showNotification && !timerState.isFinished
        if (isForeground) {
            val notification = restTimerNotificationManager.getTimerNotification(timerState)
            startForeground(RestTimerNotificationManager.NOTIFICATION_ID, notification)
        } else if (isForegroundActive) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            if (showNotification) restTimerNotificationManager.showTimerNotification(timerState)
        }
        isForegroundActive = isForeground
    }

    private data class TimerConfig(val duration: Duration, val workoutID: Long)

    inner class RestTimerBinder : Binder() {
        val service: RestTimerService = this@RestTimerService
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        restTimerNotificationManager.cancelTimerNotification()
    }

    companion object {
        const val ACTION_TOGGLE_TIMER =
            "com.patrykandpatrick.liftapp.feature.workout.RestTimerService.TOGGLE_TIMER"
        const val ACTION_CANCEL_TIMER =
            "com.patrykandpatrick.liftapp.feature.workout.RestTimerService.CANCEL_TIMER"
        const val ACTION_ADD_TIMER_SECONDS =
            "com.patrykandpatrick.liftapp.feature.workout.RestTimerService.ADD_TIMER_SECONDS"
        const val EXTRA_SECONDS =
            "com.patrykandpatrick.liftapp.feature.workout.RestTimerService.EXTRA_SECONDS"
    }
}

class RestTimerServiceController(private val context: Context) {

    private val _restTimerService = MutableStateFlow<RestTimerService?>(null)

    val restTimerService: StateFlow<RestTimerService?> = _restTimerService

    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RestTimerService.RestTimerBinder
                _restTimerService.value = binder.service
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                _restTimerService.value = null
            }
        }

    init {
        startService()
    }

    private fun startService() {
        context.applicationContext.bindService(
            Intent(context, RestTimerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE,
        )
    }
}

@Composable
fun rememberRestTimerServiceController(): RestTimerServiceController {
    val context = LocalContext.current
    val controller = remember(context) { RestTimerServiceController(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val permissionGranted =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPermissionGrantedState(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            remember { mutableStateOf(true) }
        }
    LaunchedEffect(controller, lifecycle) {
        lifecycle.currentStateFlow
            .map { !it.isAtLeast(Lifecycle.State.STARTED) }
            .distinctUntilChanged()
            .collect { isNotStarted ->
                controller.restTimerService
                    .filterNotNull()
                    .first()
                    .setShowNotification(isNotStarted && permissionGranted.value)
            }
    }
    return controller
}
