package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Dashboard : AppRoute

    @Serializable
    data object CrisisSelection : AppRoute

    @Serializable
    data class CrisisDetail(val crisisId: String) : AppRoute

    @Serializable
    data object Sos : AppRoute

    @Serializable
    data object Map : AppRoute

    @Serializable
    data object Family : AppRoute

    @Serializable
    data object EmergencyKit : AppRoute

    @Serializable
    data object Training : AppRoute

    @Serializable
    data object UrbanReports : AppRoute

    @Serializable
    data object Settings : AppRoute
}
