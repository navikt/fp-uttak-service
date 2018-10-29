package no.nav.foreldrepenger

import io.javalin.*
import no.nav.foreldrepenger.regler.uttak.beregnkontoer.grunnlag.*
import no.nav.foreldrepenger.regler.uttak.konfig.*
import no.nav.foreldrepenger.uttaksvilkår.*
import java.time.*

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

      val grunnlag = grunnlag {
         medFamiliehendelsesdato(req.familiehendelsesdato)
         medAntallBarn(req.antallBarn)
         erFødsel(req.erFødsel)

         morRett(req.morHarRett)
         farRett(req.farHarRett)
         farAleneomsorg(req.farHarAleneomsorg)
         morAleneomsorg(req.morHarAleneomsorg)
         medDekningsgrad(dekningsgrad)
      }

      return try {
         KontoSuccess(kontoCalculator.beregnKontoer(grunnlag, konfig(grunnlag.familiehendelsesdato))
            .stønadskontoer
            .map { it.key.toString() to it.value }.toMap())
      } catch (ex: Exception) {
         KontoFailure(listOf(ex.message ?: "unknown error"))
      }
   }

   private fun grunnlag(actions: BeregnKontoerGrunnlag.Builder.() -> Unit): BeregnKontoerGrunnlag {
      val builder = BeregnKontoerGrunnlag.builder()
      builder.actions()
      return builder.build()
   }

   private fun konfig(familiehendelsesdato: LocalDate): Konfigurasjon {
      return when (isWithinRange(familiehendelsesdato)) {
         true -> StandardKonfigurasjon.SØKNADSDIALOG
         false -> StandardKonfigurasjon.KONFIGURASJON
      }
   }

   fun isWithinRange(testDate: LocalDate): Boolean {
      val start = LocalDate.of(2018, 7, 1)
      val end = LocalDate.of(2018, 12, 31)
      return !(testDate.isBefore(start) || testDate.isAfter(end))
   }

}
