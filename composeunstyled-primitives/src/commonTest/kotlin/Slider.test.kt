package com.composeunstyled

import kotlin.test.Test
import kotlin.test.assertEquals

class SliderTest {

    @Test
    fun steppedSliderStateDoesNotSnapToHalfWhenSettingPointSix() {
        val sliderState = SliderState(
            initialValue = 0.5f,
            valueRange = 0f..1f,
            steps = 9
        )

        sliderState.value = 0.6f

        assertEquals(0.6f, sliderState.value, absoluteTolerance = 0.001f)
    }
}
