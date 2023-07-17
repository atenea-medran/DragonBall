package com.keepcoding.dragonballfinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.keepcoding.dragonballfinal.databinding.ActivityHeroListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Hereda de HeroAdapterInterface para que este le obligue a implementar sus funciones, en este caso 1.
class HeroListActivity : AppCompatActivity(), HeroAdapter.HeroAdapterInterface {

    private lateinit var binding: ActivityHeroListBinding
    private val viewModel : HeroListActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeroListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se crea el adapter con el constructor y le pasamos this (HeroListActivity). HeroAdapter tiene como parámetro la interfaz,
        // pero como esta clase (this) implementa la interfaz se lo podemos pasar como argumento. Esto es la inyección de dependencias.
        val adapter = HeroAdapter(this)
        binding.rvHeroes.adapter = adapter // Le asignamos el adapter creado al adapter del recyclerView
        binding.rvHeroes.layoutManager = LinearLayoutManager(this) // Le dice como mostrarlos (Vertical, en cuadricula, etc...)
        lifecycleScope.launch(Dispatchers.IO) { // Salimos del Main Thread para no bloquearlo
            val state = viewModel.descargarListaHerores() // Vemos si la descargar de la API es exitosa o no
            when (state) {
                is HeroListActivityViewModel.HeroListState.OnSuccess ->
                    withContext(Dispatchers.Main) {
                        adapter.ponerListaHeroes(state.list) } // Si es exitosa volvemos al main y llenamos la lista del adapter

                is HeroListActivityViewModel.HeroListState.OnError ->
                    withContext(Dispatchers.Main) { // Si es fallida volvemos al main y mostramos un Toast
                        Toast.makeText(this@HeroListActivity, state.message, Toast.LENGTH_LONG).show() }
            }
        }


        binding.fabBorrar.setOnClickListener {
            adapter.borrarTodo()
        }
    }

    // HeroListActivity implementa la función de la interfaz para darle funcionalidad. Esto se hace porque la función
    // de cambiar el texto de arriba del HeroListActivity es una parte de esta activity, pero es triggereada por cada
    // uno de las cards de héroe (los viewHolders).
    override fun abrirDetallesHeroeActivity(nombre: String) {
        binding.textView.text = "se ha pulsado sobre $nombre"
    }

}