package com.keepcoding.dragonballfinal

import androidx.lifecycle.ViewModel
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

const val BASE_URL = "https://dragonball.keepcoding.education/api/" // las variables constantes van en mayúscula y camelcase

var token = ""

// En el viewModel se colocan funciones relativas a los datos que necesitan las views. En el activity se
// ponen funciones de los elementos (botones, textos...), los toast, la navegacion entre activities...

class MainActivityViewModel : ViewModel() {


    fun isUserValid(user: String) = user.contains("@") && user.contains(".")

    fun isPassValid(pass: String) = pass.length >= 4

    suspend fun loguear(user: String, pass: String): LoginState { // Devuelve un objeto onSucces u onError
        val client = OkHttpClient() // El cliente que hace la llamada
        val url = "${BASE_URL}auth/login"
        val credentials = Credentials.basic("carlos.bellmont1@pruebmail.es", "123456")
        val formBody = FormBody.Builder().build() // formBody es el cuerpo de la solicitud HTTP
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", credentials)
            .post(formBody) // La solicitud es de tipo POST porque así está hecha la API, aunque funciona como un GET ¿?¿?
            .build()
        val call = client.newCall(request) // La llamada se crea con el cliente y la request
        val response = call.execute() // Se guarda el resultado de la ejecución de la llamada en una variable
        if (response.isSuccessful)
            response.body?.let {// El objeto ResponseBody puede ser nulo
                token = it.string() // it es el objeto ResponseBody, aquí lo pasamos a String y lo asignamos a toke, creada al principio
                // Devuelvo OnSuccess, OnError si ResponseBody es nulo y si response es otra cosa que successful
                return LoginState.OnSuccess
            } ?: return LoginState.OnError("No se ha recibido ningún token")
        else
            return LoginState.OnError(response.message)
    }

    // Las sealed class se usan mucho para gestionar resultados (éxitos, errores...) porque son flexibles
    // y a la vez limitan la creación de subclases al package
    sealed class LoginState {
        object OnSuccess : LoginState()
        data class OnError(val message: String): LoginState()
    }

}

