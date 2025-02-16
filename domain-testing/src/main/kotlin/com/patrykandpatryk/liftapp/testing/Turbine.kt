package com.patrykandpatryk.liftapp.testing

import app.cash.turbine.ReceiveTurbine
import com.patrykandpatryk.liftapp.domain.model.Loadable

fun <T : Any> ReceiveTurbine<Loadable<T>>.expectMostRecentSuccessData(): T {
    return (expectMostRecentItem() as Loadable.Success<T>).data
}

fun <T : Any> ReceiveTurbine<Loadable<T>>.expectMostRecentErrorThrowable(): Throwable {
    return (expectMostRecentItem() as Loadable.Error).error
}
