package no.nav.foreldrepenger

import io.javalin.*
import io.javalin.ApiBuilder.*
import org.slf4j.*

fun main(args: Array<String>) {
   App().init()
}

class App(private val port: Int = 7070) {

   fun init(): Javalin {
      val log = LoggerFactory.getLogger("runner")

      val app = Javalin.create().apply {
         port(port)
         enableDynamicGzip()
         exception(Exception::class.java) { ex, ctx ->
            log.warn("An error occuredd", ex)
            ctx.status(500)
         }
         error(404) { ctx -> ctx.json("${ctx.url()} not found") }
      }.start()

      app.routes {
         path("/konto") {
            get(KontoController::calculateKonto)
         }

         path("/isAlive") {
            get { it.status(200) }
         }

         path("/isReady") {
            get { it.status(200) }
         }
      }

      app.after {
         it.header("Server", "WebBuster 3000")
      }

      return app
   }

}

