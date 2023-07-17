package com.keepcoding.dragonballfinal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.keepcoding.dragonballfinal.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // Se crea fuera del onCreate para que pueda acceder toda la clase.
    // Luego el onCreate le asigna un valor, el ActivityMainBinding.
    // userTag y passTag no necesitan el lateinit porque comienzan con un valor.
    private lateinit var binding : ActivityMainBinding

    // Tener las strings arriba ayuda a tener el código ordenado y evitar el hardcoding.
    private val userTag = "USER_TAG"
    private val passTag = "PASS_TAG"

    // Creamos el viewModel con el by para que, por ejemplo al rotar, se use
    // el viewModel ya creado y no se cree otro

    private val viewModel : MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llamo al onCreate de la superclase AppCompatActivity

        // Le doy valor al binding iniciado fuera. ActivityMainBinding se genera automáticamente basada
        // en el activity_main.xml. El método inflate nos permite crear vistas a partir de un XML.
        binding =  ActivityMainBinding.inflate(layoutInflater)

        // Función de AppCompatActivity. Le asigna el contenido de la activity a una vista específica,
        // en este caso el root del activity_main.xml, que es el ConstraintLayout.
        setContentView(binding.root)

        // A partir de ahora podemos usar binding.vista (binding.etUser por ej) para acceder con menos código y
        // ahorrando recursos, pues el findViewById es caro.
        with(binding) {
            cargarLoginDePreferencias()
            etUser.doAfterTextChanged { editable -> // Pone un listener que se activa cuando cambia el texto
                editable?.let {// El let permite hacer un bloque de codigo sobre el Editable?, que puede ser null
                    btnLogin.isEnabled = it.toString().isNotEmpty() && etPass.text.isNotEmpty()
                }
            }
            // El problema del doAfterTextChanged es que aunque tengamos guardadas credenciales, al abrir la app,
            // tenemos que cambiar los dos editText para activar el btnLogin
            etPass.doAfterTextChanged { editable ->
                editable?.let {
                    btnLogin.isEnabled = it.toString().isNotEmpty() && etUser.text.isNotEmpty()
                }
            }
            btnLogin.setOnClickListener { // Lo que hace el botón cuando es clicado
                if (viewModel.isUserValid(etUser.text.toString()) &&
                    viewModel.isPassValid(etPass.text.toString()) // El toString se usa porque etUser.text da un editable
                ) {
                    // Necesitamos acceder al contexto del MainActivity porque el this es del binding
                    Toast.makeText(this@MainActivity, "Login Correcto", Toast.LENGTH_LONG).show()
                    lanzarLogin(etUser.text.toString(), etPass.text.toString())
                    if (switchRememberUser.isChecked)
                        guardarLoginEnPreferencias(etUser.text.toString(), etPass.text.toString())
                } else
                    Toast.makeText(this@MainActivity, "Login Fallido", Toast.LENGTH_LONG).show()
            }
            // Estas dos funciones eran para ver como cambiaba el focus en el Logcat
            etUser.setOnFocusChangeListener { view, hasFocus -> // Funciona cuando entra el focus y cuando se pierde
                if (hasFocus)
                    Log.w("MainActivity", "etUser tiene el foco")
                else
                    Log.w("MainActivity", "etUser ha perdido el foco")
            }
            etPass.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus)
                    Log.w("MainActivity", "etPass tiene el foco")
                else
                    Log.w("MainActivity", "etPass ha perdido el foco")
            }
            switchRememberUser.setOnCheckedChangeListener { _, isChecked -> // _ se usa para indicar que el primer argumento no es necesario
                if (!isChecked) borrarPreferencias()
            }
        }
    }

    private fun borrarPreferencias() {
        getPreferences(Context.MODE_PRIVATE).edit().clear().apply()
    }

    private fun guardarLoginEnPreferencias(user: String, pass: String) {
        getPreferences(Context.MODE_PRIVATE).edit()
            .putString(userTag, user)
            .putString(passTag, pass)
            .apply()
    }

    private fun cargarLoginDePreferencias() {
        with(binding) { // El with cambia el contexto al binding, por eso se puede usar etUser dentro del apply
            // MODE_PRIVATE sirve para que solo esta app pueda acceder al archivo del login del getPreferences
            // Seguimos en el scope del binding, y el apply abre un bloque que afecta al objeto SharedPrefences
            getPreferences(Context.MODE_PRIVATE).apply {// el objeto sharedPreferences no puede ser nulo -> usamos apply
                etUser.setText(getString(userTag, "")) // le pone el texto al editText, no se puede acceder con text a secas
                etPass.setText(getString(passTag, ""))
                if (etPass.text.isNotEmpty() && etUser.text.isNotEmpty()) // si el login tiene datos el switch aparece checkeado
                    switchRememberUser.isChecked = true
            }
        }
    }

    // Como viewModel.loguear se conecta a internet y tarda más tiempo no podemos bloquear el Main Thread que usa el
    // móvil. Si lo hicieramos no podríamos ni siquiera clicar botones para salir de ahí. Lanzamos un scope con
    // Dispatchers.IO porque tiene una pool de threads destinada a las acciones que bloquean el thread
    private fun lanzarLogin(user: String, pass: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val state = viewModel.loguear(user, pass) // Esta funcion va en el viewModel porque maneja los datos que necesita la app para loguear
            when(state) {
                // Tenemos que acceder a la clase/objeto de dentro de la sealed class LoginState
                is MainActivityViewModel.LoginState.OnSuccess -> abrirHeroListActivity() // Esta función va aquí porque es un movimiento entre activities
                is MainActivityViewModel.LoginState.OnError -> {
                    withContext(Dispatchers.Main) {// Volvemos al Main Thread para mostrar el Toast
                        // De nuevo llamamos al contexto @MainActivity porque el this pertenece al binding
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }



    private fun abrirHeroListActivity() { // Los intents se usan para abrir activities u otras aplicaciones del móvil
        val intent = Intent(this, HeroListActivity::class.java) // Usamos this porque ya hemos salido del with(binding)
        startActivity(intent)
        finish() // cerramos MainActivity y la quitamos del stack, ya no podemos volver a ella con el botón 'Atrás'
    }
}