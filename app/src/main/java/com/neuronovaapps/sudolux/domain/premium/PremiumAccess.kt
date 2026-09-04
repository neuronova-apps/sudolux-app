package com.neuronovaapps.sudolux.domain.premium

enum class AccessTier { FREE, PREMIUM }

/**
 * Punto provisional de integración. Cambiar esta constante permite probar PREMIUM sin pagos.
 * Una suscripción real podrá reemplazar este proveedor sin tocar el dominio del juego.
 */
object PremiumFeatureFlags {
    const val PREMIUM_ENABLED_FOR_DEVELOPMENT: Boolean = false

    val currentTier: AccessTier
        get() = if (PREMIUM_ENABLED_FOR_DEVELOPMENT) AccessTier.PREMIUM else AccessTier.FREE
}
