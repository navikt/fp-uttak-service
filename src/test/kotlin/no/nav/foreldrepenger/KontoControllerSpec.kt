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
               val familiehendelsesdato = LocalDate.of(2019, 1, 1)
               val startdatoUttak = LocalDate.of(2018, 12, 31)
               val chosenLaw = KontoController.chooseConfig((startdatoUttak ?: familiehendelsesdato) as LocalDate?)
               chosenLaw `should equal` OLD_LAW
            }
         }

         on("startdato uttak is after jan 1 2019") {
            it("chooses the new law") {
               val familiehendelsesdato = LocalDate.of(2018, 12, 31)
               val startdatoUttak = LocalDate.of(2019, 1, 1)
               val chosenLaw = KontoController.chooseConfig((startdatoUttak ?: familiehendelsesdato)) as LocalDate?)
               chosenLaw `should equal` NEW_LAW
            }
         }
      }

      given ("startdato uttak is unknown") {
         on("familiehendelsesdato is before jan 1 2019") {
            it("chooses the old law") {
               val familiehendelsesdato = LocalDate.of(2018, 12, 31)
               val chosenLaw = KontoController.chooseConfig((familiehendelsesdato)) as LocalDate?)
               chosenLaw `should equal` OLD_LAW
            }
         }

         on("familiehendelsesdato is after jan 1 2019") {
            it("chooses the new law") {
               val familiehendelsesdato = LocalDate.of(2019, 1, 1)
               val chosenLaw = KontoController.chooseConfig((familiehendelsesdato)) as LocalDate?)
               chosenLaw `should equal` NEW_LAW
            }
         }
      }

   }

})
