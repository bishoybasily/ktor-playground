package com.gmail.bishoybasily.ktor.demo

import com.google.inject.AbstractModule
import io.ktor.application.Application
import io.ktor.application.ApplicationCall

/**
 * @author bishoybasily
 * @since 2020-04-03
 */

class ApplicationModule(private val application: Application) : AbstractModule() {
    override fun configure() {
        bind(Application::class.java).toInstance(application)
        bind(Routing::class.java).asEagerSingleton()
    }
}

class CallModule(private val applicationCall: ApplicationCall) : AbstractModule() {
    override fun configure() {
        bind(ApplicationCall::class.java).toInstance(applicationCall)
    }
}