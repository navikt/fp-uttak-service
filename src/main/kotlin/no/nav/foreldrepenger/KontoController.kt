package no.nav.foreldrepenger

import io.javalin.http.Context
import no.nav.foreldrepenger.regler.uttak.beregnkontoer.grunnlag.*
import no.nav.foreldrepenger.regler.uttak.konfig.*
import no.nav.foreldrepenger.uttaksvilkår.*
import org.slf4j.LoggerFactory
import java.time.*

import no.nav.foreldrepenger.regler.uttak.konfig.StandardKonfigurasjon.SØKNADSDIALOG as LAW_CONFIG

object KontoController {
   private val log = LoggerFactory.getLogger("kontoController")
   private val kontoCalculator = StønadskontoRegelOrkestrering()

   fun calculateKonto(ctx: Context) {

      val missingParams = KontoParamParser.missingRequiredParams(ctx.req.parameterNames.toList())
      if (missingParams.isNotEmpty()) {
         log.warn("Missing parameters {}", missingParams.toString())
         ctx.status(400).json(KontoFailure(missingParams.map { "parameter $it is missing" }))
      } else {
         when (val parseResult = KontoParamParser.parseParams(ctx.req)) {
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
         medFødselsdato(req.fødselsdato)
         medTermindato(req.termindato)
         medOmsorgsovertakelseDato(req.omsorgsovertakelseDato)
         medAntallBarn(req.antallBarn)

         morRett(req.morHarRett)
         farRett(req.farHarRett)
         farAleneomsorg(req.farHarAleneomsorg)
         morAleneomsorg(req.morHarAleneomsorg)
         medDekningsgrad(dekningsgrad)
      }

      return try {
         KontoSuccess(kontoCalculator.beregnKontoer(grunnlag, LAW_CONFIG)
            .stønadskontoer
            .map { it.key.toString() to it.value }.toMap()
         )
      } catch (ex: Exception) {
         log.warn("calculate function failed {}", ex.message)
         KontoFailure(listOf(ex.message ?: "unknown error"))
      }
   }

   private fun grunnlag(actions: BeregnKontoerGrunnlag.Builder.() -> Unit): BeregnKontoerGrunnlag {
      val builder = BeregnKontoerGrunnlag.builder()
      builder.actions()
      return builder.build()
   }
}
