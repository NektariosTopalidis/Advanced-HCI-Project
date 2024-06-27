package gr.nektariostop.ergasiaadvancedhci

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import gr.nektariostop.ergasiaadvancedhci.data.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun instance(): FirebaseFirestore {
        return instance
    }


    fun logout(){
        auth.signOut()
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        }
        catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun updateActiveUser(user: User): Result<Boolean> {
        return try {
            instance().collection("users").document(auth.currentUser!!.uid).set(user).await()
            Result.Success(true)
        }
        catch (e: Exception){

            Result.Error(e)
        }
    }

    suspend fun getUsers(): Result<List<User>> {
        return try {
            val querySnapshot = instance().collection("users").get().await()
            val userResults = querySnapshot.documents.map {
                    document ->
                document.toObject(User::class.java)!!
            }
            Result.Success(userResults)
        }
        catch (e: Exception){
            Result.Error(e)
        }
    }

    suspend fun getActiveUser(): Result<List<User>> {
        return try {
            val querySnapshot = instance().collection("users").where(Filter.equalTo("userId",auth.currentUser?.uid)).get().await()
            val userResults = querySnapshot.documents.map {
                document ->
                document.toObject(User::class.java)!!
            }
            Result.Success(userResults)
        }
        catch (e: Exception){
            Result.Error(e)
        }
    }
}

