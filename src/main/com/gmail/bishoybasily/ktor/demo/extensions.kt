package com.gmail.bishoybasily.ktor.demo

import com.google.inject.Injector
import io.ktor.application.ApplicationCall

/**
 * @author bishoybasily
 * @since 2020-04-03
 */

val ApplicationCall.injector: Injector get() = attributes[CALL_INJECTOR_KEY]
