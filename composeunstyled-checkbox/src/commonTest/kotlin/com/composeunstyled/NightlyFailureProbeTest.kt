package com.composeunstyled

import kotlin.test.Test

class NightlyFailureProbeTest {
  @Test
  fun breaksNightlyBuild() {
    unresolvedNightlyFailureProbe()
  }
}
