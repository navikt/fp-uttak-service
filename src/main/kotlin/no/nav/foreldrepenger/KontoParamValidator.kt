package no.nav.foreldrepenger

import java.time.*
import java.time.format.*
import javax.servlet.http.*

object KontoParamValidator {

   private val params = mapOf("erFødsel" to true,
      "antallBarn" to true,
      "morHarRett" to true,
      "farHarRett" to true,
      "familiehendelsesdato" to true,
      "dekningsgrad" to true,
      "morHarAleneomsorg" to false,
      "farHarAleneomsorg" to false)

   private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

   fun missingRequiredParams(paramsInRequest: List<String>): List<String> {
      return params.filter { it.value == true }
         .map { it.key }
         .filterNot { paramsInRequest.contains(it) }
   }

   fun parseParams(req: HttpServletRequest): ParseResult {
      val errMsgs = mutableListOf<String>()

      val erFødsel = req.getParameter("erFødsel").orEmpty().toBoolean()
      val morHarRett = req.getParameter("morHarRett").orEmpty().toBoolean()
      val farHarRett = req.getParameter("farHarRett").orEmpty().toBoolean()

      val morHarAleneomsorg = req.getParameter("morHarAleneomsorg").orEmpty().toBoolean()
      val farHarAleneomsorg = req.getParameter("farHarAleneomsorg").orEmpty().toBoolean()
      if (morHarAleneomsorg and farHarAleneomsorg) {
         errMsgs.add("morHarAleneomsorg and farHarAleneomsorg are mutually exclusive, both can't be true")
      }

      val antallBarn = int(req, "antallBarn", errMsgs)
      val dekningsgrad = dekningsgrad(req, errMsgs)

      val familiehendelsesdato = date(req, "familiehendelsesdato", errMsgs)

      return when (errMsgs.isEmpty()) {
         true -> ParseSuccess(CalculateKontoRequest(erFødsel = erFødsel, dekningsgrad = dekningsgrad!!,
            familiehendelsesdato = familiehendelsesdato!!, farHarRett = farHarRett, morHarRett = morHarRett,
            antallBarn = antallBarn!!, farHarAleneomsorg = farHarAleneomsorg, morHarAleneomsorg = morHarAleneomsorg))
         false -> ParseFailure(errMsgs)
      }
   }

   private fun int(req: HttpServletRequest, paramName: String, errMsgs: MutableList<String>): Int? {
      return try {
         req.getParameter(paramName).orEmpty().toInt()
      } catch (ex: Exception) {
         errMsgs.add("$paramName is not an integer")
         null
      }
   }

   private fun date(req: HttpServletRequest, paramName: String, errMsgs: MutableList<String>): LocalDate? {
      return try {
         LocalDate.parse(req.getParameter(paramName).orEmpty(), dateFormatter)
      } catch (ex: Exception) {
         errMsgs.add("$paramName is not a valid date with format yyyyMMdd")
         null
      }
   }

   private fun dekningsgrad(req: HttpServletRequest, errMsgs: MutableList<String>): Dekningsgrad? {
      return int(req, "dekningsgrad", errMsgs)?.let {
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

