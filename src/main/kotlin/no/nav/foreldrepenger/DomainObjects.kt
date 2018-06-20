package no.nav.foreldrepenger

import java.time.*

enum class Dekningsgrad {
   DEKNING_100, DEKNING_80
}

data class CalculateKontoRequest(
   val erFødsel: Boolean,
   val antallBarn: Int,
   val morHarRett: Boolean,
   val farHarRett: Boolean,
   val morHarAleneomsorg: Boolean = false,
   val farHarAleneomsorg: Boolean = false,
   val familiehendelsesdato: LocalDate,
   val dekningsgrad: Dekningsgrad
)

sealed class KontoResponse
data class KontoSuccess(val kontoer: Map<String, Int>): KontoResponse()
data class KontoFailure(val errors: List<String>): KontoResponse()



sealed class ParseResult
data class ParseSuccess(val request: CalculateKontoRequest): ParseResult()
data class ParseFailure(val errors: List<String>): ParseResult()


