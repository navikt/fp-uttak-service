package no.nav.foreldrepenger

import io.javalin.*

object KontoController {

   private val kontoer = mapOf(
      "MØDREKVOTE" to 75,
      "FEDREKVOTE" to 75,
      "FELLESPERIODE" to 165,
      "FORELDREPENGER_FØR_FØDSEL" to 15,
      "SAMTIDIGUTTAK" to 85
   )

   fun calculateKonto(ctx: Context) {
      val missingParams = KontoParamValidator.missingRequiredParams(ctx.request().parameterNames.toList())
      if (missingParams.isNotEmpty()) {
         ctx.status(400).json(KontoFailure(missingParams.map { "parameter $it is missing" }))
      } else {
         val parseResult = KontoParamValidator.parseParams(ctx.request())
         when (parseResult) {
            is ParseFailure -> ctx.status(400).json(KontoFailure(parseResult.errors))
            else -> ctx.json(KontoSuccess(kontoer))
         }
      }
   }

}
