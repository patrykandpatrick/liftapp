package com.patrykandpatryk.liftapp.domain.navigation

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.transform

@Singleton
class NavigationCommander @Inject constructor() {

    private val _navigationCommand = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 1)

    private val resultPublisher: MutableSharedFlow<Pair<String, Any>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    val navigationCommand: Flow<NavigationCommand> = _navigationCommand.asSharedFlow()

    suspend fun navigateTo(command: NavigationCommand) {
        _navigationCommand.emit(command)
    }

    suspend fun navigateTo(route: Any) {
        _navigationCommand.emit(NavigationCommand.Route(route))
    }

    suspend fun popBackStack(
        route: Any? = null,
        inclusive: Boolean = true,
        saveState: Boolean = false,
    ) {
        _navigationCommand.emit(NavigationCommand.PopBackStack(route, inclusive, saveState))
    }

    suspend fun publishResult(key: String, value: Any) {
        resultPublisher.emit(key to value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getResults(clazz: Class<T>, key: String): Flow<T> =
        resultPublisher.transform { (resultKey, resultValue) ->
            if (key == resultKey && clazz.isInstance(resultValue)) {
                emit(resultValue as T)
            }
        }

    inline fun <reified T> getResults(key: String): Flow<T> = getResults(T::class.java, key)
}
