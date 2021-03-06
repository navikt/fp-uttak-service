package no.nav.foreldrepenger

import io.javalin.*
import io.javalin.apibuilder.ApiBuilder.*
import org.slf4j.*

fun main(args: Array<String>) {
   App().init()
}

class App(private val port: Int = 8080) {

   fun init(): Javalin {
      val log = LoggerFactory.getLogger("runner")

      return Javalin.create { config ->
         config.defaultContentType = "application/json; charset=utf-8"
         config.requestLogger { ctx, ms -> log.info("{} - {} : {} ms - {}", ctx.req.method ,ctx.req.pathInfo, ms, ctx.res.status) }
      }.apply {
         exception(Exception::class.java) { ex, ctx ->
            log.warn("An error occuredd", ex)
            ctx.status(500)
         }
         error(404) { ctx -> ctx.json("${ctx.url()} not found") }
      }.routes {
         path("/konto") {
            get(KontoController::calculateKonto)
         }

         path("/isAlive") {
            get { it.status(200) }
         }

         path("/isReady") {
            get { it.status(200) }
         }
      }.after {
         it.header("Server", "WebBuster 3000")
         it.header("Access-Control-Allow-Origin", "*")
         charset("utf-8")
      }.start(port)
   }
}

