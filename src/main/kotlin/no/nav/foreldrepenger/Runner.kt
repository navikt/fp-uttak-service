package no.nav.foreldrepenger

import io.javalin.*
import io.javalin.ApiBuilder.*
import org.slf4j.*

fun main(args: Array<String>) {

   val log = LoggerFactory.getLogger("runner")

   val app = Javalin.create().apply {
      port(7070)
      enableDynamicGzip()
      exception(Exception::class.java) { ex, ctx ->
         log.warn("An error occuredd", ex)
         ctx.status(500)
      }
      error(404) { ctx -> ctx.json("${ctx.url()} not found") }
   }.start()

   app.routes {
      get("/") { ctx ->
         ctx.json("her kommer det greier")
      }

      get("/isAlive") { ctx ->
         ctx.status(200)
      }

      get("/isReady") { ctx ->
         ctx.status(200)
      }
   }

   app.after {
      it.header("Server", "WebBuster 3000")
   }
}

