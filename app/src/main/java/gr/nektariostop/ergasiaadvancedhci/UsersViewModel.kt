package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import gr.nektariostop.ergasiaadvancedhci.data.User
import kotlinx.coroutines.launch

class UsersViewModel: ViewModel() {

    private val userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    private val _activeUserResult = MutableLiveData<Result<List<User>>>()
    val activeUserResult: LiveData<Result<List<User>>> = _activeUserResult

    private val _updateActiveUserResult = MutableLiveData<Result<Boolean>>()
    val updateActiveUserResult: LiveData<Result<Boolean>> = _updateActiveUserResult

    private val _getAllUsersResult = MutableLiveData<Result<List<User>>>()
    val getAllUsersResult: LiveData<Result<List<User>>> = _getAllUsersResult

    var activeUser by mutableStateOf<User?>(null)

    private var _users by mutableStateOf<List<User>>(listOf())

    fun getUserWithID(userId: String): User?{
        return _users.find { it.userId == userId }
    }

    fun getUsers(): List<User>{
        return _users
    }

    fun getAllUsers(){
        viewModelScope.launch{
            _getAllUsersResult.value = userRepository.getUsers()
        }
    }

    fun setAllUsers(users: List<User>){
        _users = users
    }

    fun clearGetAllUsersResult(){
        _getAllUsersResult.value = Result.DoNothing(emptyList())
    }

    fun setActiveUserAfterReq(userList: List<User>){
        activeUser = userList[0]
        println(activeUser)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email.trim(), password.trim())
        }
    }

    fun logout(navigate: () -> Unit){
        activeUser = null
        userRepository.logout()
        _authResult.value = Result.DoNothing(true)
        _activeUserResult.value = Result.DoNothing(emptyList())
        navigate()
    }

    fun getActiveUser(){
        viewModelScope.launch {
            _activeUserResult.value = userRepository.getActiveUser()
        }
    }


    fun updateActiveUser(firstName: String?, lastName: String?, email: String?, gender: String?){

        viewModelScope.launch {

            if(firstName != null) activeUser!!.firstName = firstName

            if(lastName != null) activeUser!!.lastName = lastName

            if(email != null) activeUser!!.email = email

            if(gender != null) activeUser!!.gender = gender

            _updateActiveUserResult.value = userRepository.updateActiveUser(activeUser!!)
        }

    }


    fun clearActiveUserResult(){
        _updateActiveUserResult.value = Result.DoNothing(true)
    }

}