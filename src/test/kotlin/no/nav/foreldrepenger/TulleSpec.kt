package no.nav.foreldrepanger

import org.amshove.kluent.*
import org.jetbrains.spek.api.*
import org.jetbrains.spek.api.dsl.*

object GameSpek : Spek({

   given("Something") {

      on("a condition") {
         it("should do whatever") {
            2 `should be equal to` 2
         }
      }

   }

})
