package com.neuronovaapps.sudolux.domain.premium

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PremiumAccessTest {
    @Test
    fun disabledDevelopmentFlagResolvesToFreeTier() {
        assertFalse(PremiumFeatureFlags.PREMIUM_ENABLED_FOR_DEVELOPMENT)
        assertEquals(AccessTier.FREE, PremiumFeatureFlags.currentTier)
    }
}
