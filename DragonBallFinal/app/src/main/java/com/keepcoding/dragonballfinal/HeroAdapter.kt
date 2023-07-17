package com.keepcoding.dragonballfinal

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.keepcoding.dragonballfinal.databinding.ItemHeroBinding

// La clase HeroAdapter es una subclase de la clase RecyclerView.Adapter, que tiene un viewHolder de tipo HeroViewHolder.
// El adapter se encarga de asignarle una view a los datos para poblar el recyclerView
class HeroAdapter(val callback: HeroAdapterInterface): RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    // Se crea la interfaz para que HeroListActivity esté obligada a implementar esa función
    // La pone dentro porque es pequeña, pero lo ideal sería que estuviera en su propio archivo
    interface HeroAdapterInterface {
        fun abrirDetallesHeroeActivity(nombre: String) // Esta función no hace nada `porque la define HeroListActivity
    }

    private var list = mutableListOf<HeroListActivityViewModel.Hero>()

    // Un viewHolder es la view de un item (en este caso cada fila de héroe) de un recyclerView,
    // así como los metadatos para situarlo dentro de él. El viewHolder recibe como parámetros el binding
    // de la vista item_hero.xml y la interfaz. Es una subclase de RecyclerView.ViewHolder, que toma como
    // argumento el root de binding, que suele ser el constraintLayout.
    class HeroViewHolder(val binding: ItemHeroBinding, val callback: HeroAdapterInterface): RecyclerView.ViewHolder(binding.root) {
        // La función bind se usa luego en onBindViewHolder
        fun bind(hero: HeroListActivityViewModel.Hero, position: Int) { // La position la generará la función onBindViewHolder
            with(binding) {
                tvName.text = hero.nombre // El texto de la view item_hero.xml será el nombre del hero que descarguemos de la API
                val bgColorId = if (position % 2 == 0) R.color.dark_orange else R.color.orange
                root.setBackgroundColor( // El root aquí se refiere a la constraintLayout de cada 'card' de hero. Las que son de colores
                    ContextCompat.getColor( // Esta función cambia el color
                        binding.root.context,
                        bgColorId
                    )
                )
                // Lo que pasa cuando pulsamos sobre cada 'card'. El Toast va aquí porque no es parte de ninguna activity,
                // aparece flotando en el móvil. abrirDetallesHeroeActivity() se implementa en HeroListActivity porque
                // modifica una view de allí y tiene sentido.
                root.setOnClickListener {
                    Toast.makeText(root.context, "Se ha pulsado sobre $hero", Toast.LENGTH_LONG).show()
                    callback.abrirDetallesHeroeActivity(hero.nombre)
                }
            }
        }
    }

    override fun getItemCount(): Int { // Esta función es propia del adapter
        return list.size
    }

    // Esta función crea los viewHolders por su cuenta. Usa el layoutInflater de su padre, que es el recyclerView
    // Inflar es que a partir del XML se dé forma a la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder { // También es propia del adapter
        val binding = ItemHeroBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeroViewHolder(binding, callback) // El callback lo recibe como argumento y es la interfaz
    }

    // Esta función propia del adapter se encarga de enlazar los elementos de la lista y los viewHolders que se van creando
    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    // Rellena la lista con héroes a partir de los datos de la API
    fun ponerListaHeroes(listHero : List<HeroListActivityViewModel.Hero>) {
        list = listHero.toMutableList()
        notifyDataSetChanged()
    }

    fun borrarTodo() {
        list.clear()
        notifyDataSetChanged()
    }
}