package com.example.validtest.utilities
//Clase para constantes de uso general
class Constantes {

    //////////////////
    //DEFAULT VALUES//
    //////////////////
    val DEFAULT = -1
    val HTTP_OK = 200
    val HTTP_UNAUTHORIZED = 401
    val HTTP_FORBBIDEN = 403
    val HTTP_NOT_FOUND = 404

    ///////
    //API//
    ///////
    val URL_API = "https://ws.audioscrobbler.com/2.0/"
    val API_KEY = "829751643419a7128b7ada50de590067"
    val EXITO_CARGA = "Carga de imagen exitosa!"
    val FALLA_CARGA = "Fallo la carga de imagen!"
    val CANCEL_CARGA_I = "Usuario detuvo la carga de imagen"
    val CANCEL_CARGA_T = "Usuario detuvo la carga de Tracks en BD"
    val CANCEL_CARGA_A = "Usuario detuvo la carga de Artists en BD"

    ////////////////
    ///ERROR TAGS///
    ////////////////
    val TAG_GENERAL = "RGM"

    ////////////////
    //////KEYS//////
    ////////////////
    val KEY_ID_OBJETO = "keyId"
    val KEY_SHARED_P = "SharedPApp"
    val KEY_PAIS_T_INT = "PaisTrack"
    val KEY_PAIS_A_INT = "PaisArtist"

    /////////////
    //FRAGMENTS//
    /////////////
    val TOTAL_FRAGMENTS = 2
    val F_TRACK = 0
    val F_ARTIST = 1

    ////////////////////////
    //REGIONES DE BUSQUEDA//
    ///////ISO 31661////////
    ////////////////////////
    val DEFAULT_SPINNER = 2 //Colombia
    val ARGENTINA = "AR"
    val BRAZIL = "BR"
    val COLOMBIA = "CO"
    val ECUADOR = "EC"
    val PERU = "PE"
    val URUGUAY = "UY"
    val VENEZUELA = "VE"
    val REGIONES = arrayListOf(
        ARGENTINA,
        BRAZIL,
        COLOMBIA,
        ECUADOR,
        PERU,
        URUGUAY,
        VENEZUELA
    )
    val ARGENTINA_C = "argentina"
    val BRAZIL_C = "brazil"
    val COLOMBIA_C = "colombia"
    val ECUADOR_C = "ecuador"
    val PERU_C = "peru"
    val URUGUAY_C = "uruguay"
    val VENEZUELA_C = "venezuela"

    val REGIONES_C = arrayListOf(
        ARGENTINA_C,
        BRAZIL_C,
        COLOMBIA_C,
        ECUADOR_C,
        PERU_C,
        URUGUAY_C,
        VENEZUELA_C
    )
}