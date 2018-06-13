package no.nav.foreldrepenger

import java.time.*

enum class Dekningsgrad {
   DEKNING_100, DEKNING_80
}

data class KontoRequest(
   val erFødsel: Boolean = false,
   val antallBarn: Int = 0,
   val morHarRett: Boolean = false,
   val farHarRett: Boolean = false,
   val morHarAleneomsorg: Boolean = false,
   val farHarAleneomsorg: Boolean = false,
   val familiehendelsesdato: LocalDate,
   val dekningsgrad: Dekningsgrad
)

