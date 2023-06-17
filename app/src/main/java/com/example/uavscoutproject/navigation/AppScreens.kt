package com.example.uavscoutproject.navigation

/**
 * Sealed class representing different screens in the app.
 *
 * @property route The route string associated with the screen.
 */
sealed class AppScreens(val route: String) {
    /**
     * Represents the splash screen.
     */
    object SplashScreen : AppScreens("splash_screen")

    /**
     * Represents the main screen.
     */
    object MainScreen : AppScreens("main_screen")

    /**
     * Represents the authentication screen.
     */
    object AuthenticationScreen : AppScreens("authentication_screen")

    /**
     * Represents the register screen.
     */
    object RegisterScreen : AppScreens("register_screen")

    /**
     * Represents the forgot password screen.
     */
    object ForgotPasswordScreen : AppScreens("forgot_screen")

    /**
     * Represents the drone making screen.
     */
    object DroneMakingScreen : AppScreens("drone_making_screen")

    /**
     * Represents the news screen.
     */
    object NewsScreen : AppScreens("news_screen")

    /**
     * Represents the profile screen.
     */
    object ProfileScreen : AppScreens("profile_screen")

    /**
     * Represents the settings screen.
     */
    object SettingsScreen : AppScreens("settings_screen")

    /**
     * Represents the rule set info screen.
     */
    object RuleSetInfoScreen : AppScreens("rulesetinfo_screen")

    /**
     * Represents the support screen.
     */
    object SupportScreen : AppScreens("support_screen")
}
