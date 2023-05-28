package com.example.uavscoutproject.navigation

//Se definen las diferentes pantallas en las que se pueden navegar

/** Por otro lado, una sealed class (sealed class) es un tipo especial
de clase que se utiliza para representar una jerarquía de clases relacionadas,
es decir, una clase sellada puede tener varias subclases, pero todas ellas deben
estar definidas dentro del mismo archivo Kotlin. La palabra clave sealed significa
que la clase es cerrada para la extensión fuera del archivo donde se define. Esto
permite que el compilador verifique que todas las subclases están incluidas y ayuda
a evitar errores en tiempo de ejecución.
 */

sealed class AppScreens(val route:String){
    object SplashScreen: AppScreens("splash_screen")
    object MainScreen: AppScreens("main_screen")
    object AuthenticationScreen: AppScreens("authentication_screen")
    object RegisterScreen: AppScreens("register_screen")
    object ForgotPasswordScreen: AppScreens("forgot_screen")
    object DroneMakingScreen: AppScreens("drone_making_screen")
    object NewsScreen: AppScreens("news_screen")

}