package no.nav.foreldrepenger

import no.nav.foreldrepenger.regler.uttak.fastsetteperiode.grunnlag.*
import no.nav.foreldrepenger.regler.uttak.felles.grunnlag.*
import no.nav.foreldrepenger.uttaksvilkår.*
import java.math.*
import java.time.*

fun main(args: Array<String>) {
   val dDay = LocalDate.of(2018, 6, 26)
   val firstLegalDay = dDay.minusWeeks(12)

   val periodeGrunnlag = FastsettePeriodeGrunnlagBuilder.create()
      .medFamiliehendelseDato(dDay)
      .medFarRett(true)
      .medMorRett(true)
      .medSøknadstype(Søknadstype.FØDSEL)
      .medFørsteLovligeUttaksdag(firstLegalDay)
      .medAktivitetIdentifikator(AktivitetIdentifikator.ingenAktivitet())
      .medStønadsPeriode(Stønadskontotype.MØDREKVOTE, dDay, dDay.plusWeeks(15), false, PeriodeVurderingType.IKKE_VURDERT)
      .medUtsettelsePeriode(Stønadskontotype.MØDREKVOTE, LocalDate.of(2018,11,19), LocalDate.of(2018,11,30), Utsettelseårsaktype.FERIE, PeriodeVurderingType.IKKE_VURDERT)
      .medStønadsPeriode(Stønadskontotype.FELLESPERIODE, LocalDate.of(2018,12,3), LocalDate.of(2019,3,22), false, PeriodeVurderingType.IKKE_VURDERT)
      .medStønadsPeriode(Stønadskontotype.FEDREKVOTE, LocalDate.of(2019,3,25), LocalDate.of(2019,7,5), false, PeriodeVurderingType.IKKE_VURDERT)
      .medArbeid(AktivitetIdentifikator.ingenAktivitet(), ArbeidTidslinje.Builder().build())
      .build()

   val fastsettePerioderRegelOrkestrering = FastsettePerioderRegelOrkestrering()
   val periodeResults = fastsettePerioderRegelOrkestrering.fastsettePerioder(periodeGrunnlag)
   println(periodeResults)
}

/*
.medAktivitetIdentifikator(AktivitetIdentifikator.forArbeid("org", "id"))


      .medSaldo(Stønadskontotype.SAMTIDIGUTTAK, 55)
      .medSøkerMor(true)
      .medArbeid(AktivitetIdentifikator.forArbeid("org", "id"), ArbeidTidslinje.Builder().medArbeid(LocalDate.now(), LocalDate.now(), Arbeid.forOrdinærtArbeid(BigDecimal.TEN, BigDecimal.TEN)).build())

      val arbeidsprosent = ArbeidTidslinje.Builder()
      .medArbeid(LocalDate.now().minusDays(347), LocalDate.now().minusDays(2),
         Arbeid.forOrdinærtArbeid(BigDecimal.TEN, BigDecimal.valueOf(100))).build()
   val arbeid = Arbeid.forOrdinærtArbeid(BigDecimal.TEN, BigDecimal(90))
 */
