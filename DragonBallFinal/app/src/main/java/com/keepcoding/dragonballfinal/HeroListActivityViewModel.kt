package com.keepcoding.dragonballfinal

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class HeroListActivityViewModel: ViewModel() {

    // Este proceso es muy parecido al del login
    suspend fun descargarListaHerores(): HeroListState {
        val client = OkHttpClient()
        val url = "${BASE_URL}heros/all"
        val formBody =
            FormBody.Builder()
                .add("name", "") // Esto se añade porque lo necesita la consulta a la API
                .build()
        println("Carlos $token")
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(formBody) // Esto dice que es un POST
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        if (response.isSuccessful)
            response.body?.let {
                // En MainActivityViewModel era un token en forma de String, ahora es un json.
                // Este necesita una string y una clase para construir la lista de objetos HeroDto.
                // DTO es "Data Transfer Object". En este caso se usa para recibir los datos de la API.

                val herosDto = Gson().fromJson(it.string(), Array<HeroDto>::class.java)

                return HeroListState.OnSuccess(
                    // Transformamos cada HeroDto en Hero, ya que nuestra app es simple y solo usamos
                    // esos valores
                    herosDto.map {
                        Hero(it.name, it.photo)
                    }
                )
            } ?: return HeroListState.OnError("No se ha recibido ningún token")
        else
            return HeroListState.OnError(response.message)
    }


    // Esta es la parte del modelo y lo puso aquí porque es muy simple, pero debería estar en sus propios archivos.
    // El heroDto para obtener todos los datos. Los DTO se usan mucho para mostrar ciertos campos de un objeto y evitar
    // trabajar con datos sensibles como contraseñas.
    data class HeroDto(
        val id: String,
        val photo: String,
        var favorite: Boolean,
        val name: String,
        val description: String,
    )

    data class Hero(
        val nombre: String,
        val imageUrl: String,
    )

    sealed class HeroListState {
        data class OnSuccess(val list: List<Hero>) : HeroListState()
        data class OnError(val message: String): HeroListState()
    }
}