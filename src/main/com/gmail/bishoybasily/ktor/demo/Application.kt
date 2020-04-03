package com.gmail.bishoybasily.ktor.demo

import com.fasterxml.jackson.databind.SerializationFeature
import com.google.inject.Guice
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain

//fun main(args: Array<String>) {
//
//
//    val client = KMongo.createClient("mongodb://root:toor@127.0.0.1:27017/test?authSource=admin")
//    val database = client.getDatabase("test")
//    val collection = database.getCollection("users", User::class.java)
//
//    runBlocking {
//
//        collection.drop()
//
//        collection.insertOne(
//            User().apply {
//                name = "user 1"
//                age = 5
//                username = "u1"
//                password = "123456"
//            }
//        )
//        collection.insertOne(
//            User(StringId("user_2_id")).apply {
//                name = "user 2"
//                age = 10
//                username = "u2"
//                password = "123456"
//            }
//        )
//
//        val user: User? = collection.findOne(User::id eq "user_2_id")
//
//    }
//
//    EngineMain.main(args)
//}
//
//data class User(@BsonId val id: Id<User> = newId()) {
//
//    var name: String? = null
//    var age: Int? = null
//    var username: String? = null
//    var password: String? = null
//
//}


fun main(args: Array<String>) {
    EngineMain.main(args)
}

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    Guice.createInjector(
        ApplicationModule(
            this
        )
    ).also { injector ->
        intercept(ApplicationCallPipeline.Features) {
            call.attributes.put(
                CALL_INJECTOR_KEY, injector.createChildInjector(
                    CallModule(call)
                )
            )
        }
    }

}

class Routing
@Inject
constructor(application: Application) {

    init {

        application.install(StatusPages) {
            exception<AuthenticationException> { call.respond(HttpStatusCode.Unauthorized) }
            exception<AuthorizationException> { call.respond(HttpStatusCode.Forbidden) }
        }
        application.install(Authentication) {
            basic("basic") {
                realm = "Ktor Server"
                validate {
                    if (it.name == "test" && it.password == "test") UserIdPrincipal(it.name) else null
                }
            }
        }
        application.install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }

        application.routing {

            static("/static") {
                resources("static")
            }

            get("/") {
                call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
            }

            authenticate("basic") {
                get("/protected/route/basic") {


                    call.principal<UserIdPrincipal>()?.let { call.respondText("Hello ${it.name}") }
                }
            }

            get("/json/jackson") {
                call.respond(mapOf("hello" to "world", Pair("key", "value")))
            }

        }

    }

}

