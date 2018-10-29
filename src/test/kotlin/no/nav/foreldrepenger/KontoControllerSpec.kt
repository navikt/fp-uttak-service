package no.nav.foreldrepanger

import no.nav.foreldrepenger.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

object KontoControllerSpec : Spek({

   describe("KontoController decides if old or new laws apply") {

      given ("familiehendelsesdato") {
         on("before july 1 2018") {
            it("is not within period of old law") {
               val familiehendelsesdato = LocalDate.of(2018, 6, 30)
               val isWithinPeriod = KontoController.isWithinOldLawRange(familiehendelsesdato)
               isWithinPeriod `should equal` false
            }
         }

         on("after dec 31 2018") {
            it("is not within period of old law") {
               val familiehendelsesdato = LocalDate.of(2019, 1, 1)
               val isWithinPeriod = KontoController.isWithinOldLawRange(familiehendelsesdato)
               isWithinPeriod `should equal` false
            }
         }

         on("between july 1 and dec 31 2018") {
            it("is within period of old law") {
               val familiehendelsesdato = LocalDate.of(2018, 10, 1)
               val isWithinPeriod = KontoController.isWithinOldLawRange(familiehendelsesdato)
               isWithinPeriod `should equal` true
            }
         }
      }

   }

})
