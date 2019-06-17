package no.nav.foreldrepanger

import io.javalin.*
import no.nav.foreldrepenger.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*

object ComponentSpec : Spek({

   lateinit var app: Javalin
   val url = "http://localhost:7070/"

   describe("Integration tests") {

      beforeGroup {
         app = App(7070).init()
      }

      afterGroup {
         app.stop()
      }

      given ("all required query params") {

         on("all valid params") {
            it("calculates quotas") {
               val queryParams = "&antallBarn=0&morHarRett=true&farHarRett=true&fodselsdato=20180620&dekningsgrad=100"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 200
            }
         }

         on("invalid int params") {
            it("rejects the request") {
               val queryParams = "&antallBarn=X&morHarRett=true&farHarRett=true&fodselsdato=20180620&dekningsgrad=Y"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 400
               response.text `should contain` "is not an integer"
            }
         }

         on("invalid date params") {
            it("rejects the request") {
               val queryParams = "&antallBarn=1&morHarRett=true&farHarRett=true&fodselsdato=20189920&dekningsgrad=100"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 400
               response.text `should contain` "is not a valid date"
            }
         }

         on("invalid dekningsgrad param") {
            it("rejects the request") {
               val queryParams = "&antallBarn=1&morHarRett=true&farHarRett=true&fodselsdato=20180620&dekningsgrad=90"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 400
               response.text `should contain` "dekningsgrad must be 80 or 100"
            }
         }

         on("aleneomsorg present") {
            it("rejects the request if both are present and true") {
               val queryParams = "&antallBarn=1&morHarRett=true&farHarRett=true&" +
                  "fodselsdato=20180620&dekningsgrad=90&morHarAleneomsorg=true&farHarAleneomsorg=true"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 400
               response.text `should contain` "mutually exclusive"
            }

            it("accepts the request if both are present but only one is true") {
               val queryParams = "&antallBarn=1&morHarRett=true&farHarRett=true&" +
                  "fodselsdato=20180620&dekningsgrad=100&morHarAleneomsorg=true&farHarAleneomsorg=false"
               val response = khttp.get(url = url + "konto?$queryParams")
               response.statusCode `should equal` 200
            }
         }

      }

      given ("missing required query params") {
         it("rejects the request") {
            val queryParams = "&antallBarn=1&morHarRett=true&farHarRett=true&gfodselsdato=20180620"
            val response = khttp.get(url = url + "konto?$queryParams")
            response.statusCode `should equal` 400
            response.text `should contain` "parameter dekningsgrad is missing"
         }
      }

   }

})
