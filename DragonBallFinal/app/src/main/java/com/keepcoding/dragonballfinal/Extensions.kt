package com.keepcoding.dragonballfinal


// Esto es una ejemplo de una función de extensión de Kotlin que no vimos en su momento.
// Aquí iteramos sobre los caracteres del string
fun String.contarVocales() : Int {
    var contadorVocales = 0
    forEach {
        if (it == 'a')
            contadorVocales++
    }
    return  contadorVocales
}

// En este caso estamos itereando sobre uan lsita de string y sumamos el resutlado de lo que nos da el
// contarVocales del string
fun List<String>.contartVocales() : Int {
    var contadorVocales = 0
    forEach { contadorVocales = it.contarVocales() }
    return  contadorVocales
}