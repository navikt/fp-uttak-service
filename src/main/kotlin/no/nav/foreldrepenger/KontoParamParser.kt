package no.nav.foreldrepenger

import java.time.*
import java.time.format.*
import javax.servlet.http.*

object KontoParamParser {

   private val requiredParams = listOf(
      "antallBarn",
      "morHarRett",
      "farHarRett",
      "dekningsgrad"
   )

   private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

   fun missingRequiredParams(paramsInRequest: List<String>): List<String> {
      return requiredParams.filterNot { paramsInRequest.contains(it) }
   }

   fun parseParams(req: HttpServletRequest): ParseResult {
      val errMsgs = mutableListOf<String>()

      val morHarRett = req.getParameter("morHarRett").orEmpty().toBoolean()
      val farHarRett = req.getParameter("farHarRett").orEmpty().toBoolean()

      val morHarAleneomsorg = req.getParameter("morHarAleneomsorg").orEmpty().toBoolean()
      val farHarAleneomsorg = req.getParameter("farHarAleneomsorg").orEmpty().toBoolean()
      if (morHarAleneomsorg and farHarAleneomsorg) {
         errMsgs.add("morHarAleneomsorg and farHarAleneomsorg are mutually exclusive, both can't be true")
      }

      val antallBarn = int(req.getParameter("antallBarn"), errMsgs)
      val dekningsgrad = dekningsgrad(req.getParameter("dekningsgrad"), errMsgs)

      val fødselsdato = date(req.getParameter("fødselsdato"), errMsgs)
      val termindato = date(req.getParameter("termindato"), errMsgs)
      val omsorgsovertakelseDato = date(req.getParameter("omsorgsovertakelseDato"), errMsgs)
      val startDatoUttak = req.getParameter("startdatoUttak")?.let {
         date(it, errMsgs)
      }

      return when (errMsgs.isEmpty()) {
         false -> ParseFailure(errMsgs)
         true  -> ParseSuccess(CalculateKontoRequest(dekningsgrad = dekningsgrad!!,
            omsorgsovertakelseDato = omsorgsovertakelseDato!!, fødselsdato = fødselsdato!!,
            termindato = termindato!!, startdatoUttak = startDatoUttak,
            farHarRett = farHarRett, morHarRett = morHarRett, antallBarn = antallBarn!!,
            farHarAleneomsorg = farHarAleneomsorg, morHarAleneomsorg = morHarAleneomsorg))
      }
   }

   private fun int(param: String?, errMsgs: MutableList<String>): Int? {
      return try {
         param.orEmpty().toInt()
      } catch (ex: NumberFormatException) {
         errMsgs.add("$param is not an integer")
         null
      }
   }

   private fun date(param: String?, errMsgs: MutableList<String>): LocalDate? {
      return try {
         LocalDate.parse(param.orEmpty(), dateFormatter)
      } catch (ex: DateTimeParseException) {
         errMsgs.add("$param is not a valid date with format yyyyMMdd")
         null
      }
   }

   private fun dekningsgrad(param: String?, errMsgs: MutableList<String>): Dekningsgrad? {
      return int(param, errMsgs)?.let {
         when (it) {
            80 -> Dekningsgrad.DEKNING_80
            100 -> Dekningsgrad.DEKNING_100
            else -> {
               errMsgs.add("dekningsgrad must be 80 or 100")
               null
            }
         }
      }
   }

}

