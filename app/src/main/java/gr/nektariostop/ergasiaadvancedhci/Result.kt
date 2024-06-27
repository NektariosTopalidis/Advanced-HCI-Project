package gr.nektariostop.ergasiaadvancedhci

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class DoNothing<out T>(val data: T) : Result<T>()
}




