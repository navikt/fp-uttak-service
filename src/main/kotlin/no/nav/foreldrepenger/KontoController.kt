package no.nav.foreldrepenger

import io.javalin.*
import no.nav.foreldrepenger.regler.uttak.beregnkontoer.grunnlag.*
import no.nav.foreldrepenger.regler.uttak.konfig.*
import no.nav.foreldrepenger.uttaksvilkår.*
import java.time.*

import no.nav.foreldrepenger.regler.uttak.konfig.StandardKonfigurasjon.SØKNADSDIALOG as OLD_LAW_CONFIG
import no.nav.foreldrepenger.regler.uttak.konfig.StandardKonfigurasjon.KONFIGURASJON as NEW_LAW_CONFIG

object KontoController {

   private val kontoCalculator = StønadskontoRegelOrkestrering()

   fun calculateKonto(ctx: Context) {

      val missingParams = KontoParamParser.missingRequiredParams(ctx.req.parameterNames.toList())
      if (missingParams.isNotEmpty()) {
         ctx.status(400).json(KontoFailure(missingParams.map { "parameter $it is missing" }))
      } else {
         val parseResult = KontoParamParser.parseParams(ctx.req)
         when (parseResult) {
            is ParseFailure -> ctx.status(400).json(KontoFailure(parseResult.errors))
            is ParseSuccess -> ctx.json(calculate(parseResult.request))
         }
      }
   }

   private fun calculate(req: CalculateKontoRequest): KontoResponse {
      val dekningsgrad = when (req.dekningsgrad) {
         Dekningsgrad.DEKNING_100 -> no.nav.foreldrepenger.regler.uttak.beregnkontoer.grunnlag.Dekningsgrad.DEKNINGSGRAD_100
         Dekningsgrad.DEKNING_80 -> no.nav.foreldrepenger.regler.uttak.beregnkontoer.grunnlag.Dekningsgrad.DEKNINGSGRAD_80
      }

      val significantDate = req.startdatoUttak ?: req.familiehendelsesdato
      val config = chooseConfig(significantDate)

      val grunnlag = grunnlag {
         medFamiliehendelsesdato(significantDate)
         medAntallBarn(req.antallBarn)
         erFødsel(req.erFødsel)

         morRett(req.morHarRett)
         farRett(req.farHarRett)
         farAleneomsorg(req.farHarAleneomsorg)
         morAleneomsorg(req.morHarAleneomsorg)
         medDekningsgrad(dekningsgrad)
      }

      return try {
         KontoSuccess(kontoCalculator.beregnKontoer(grunnlag, config)
            .stønadskontoer
            .map { it.key.toString() to it.value }.toMap()
         )
      } catch (ex: Exception) {
         KontoFailure(listOf(ex.message ?: "unknown error"))
      }
   }

   private fun grunnlag(actions: BeregnKontoerGrunnlag.Builder.() -> Unit): BeregnKontoerGrunnlag {
      val builder = BeregnKontoerGrunnlag.builder()
      builder.actions()
      return builder.build()
   }

   fun chooseConfig(significantDate: LocalDate): Konfigurasjon {
      return if (oldLawApplies(significantDate)) OLD_LAW_CONFIG else NEW_LAW_CONFIG
   }

   fun oldLawApplies(testDate: LocalDate): Boolean {
      val cutoff = LocalDate.of(2019, 1, 1)
      return testDate.isBefore(cutoff)
   }

}
