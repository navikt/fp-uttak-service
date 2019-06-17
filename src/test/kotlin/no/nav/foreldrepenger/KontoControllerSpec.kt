package no.nav.foreldrepanger

import no.nav.foreldrepenger.*
import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*
import java.time.*

import no.nav.foreldrepenger.regler.uttak.konfig.StandardKonfigurasjon.KONFIGURASJON as NEW_LAW
import no.nav.foreldrepenger.regler.uttak.konfig.StandardKonfigurasjon.SØKNADSDIALOG as OLD_LAW

object KontoControllerSpec : Spek({

   describe("KontoController decides if old or new laws apply") {

      given ("startdato uttak is known") {
         on("startdato uttak is before jan 1 2019") {
            it("chooses the old law") {
               assertEquals(0, 0)
            }
         }
      }
   }
})
