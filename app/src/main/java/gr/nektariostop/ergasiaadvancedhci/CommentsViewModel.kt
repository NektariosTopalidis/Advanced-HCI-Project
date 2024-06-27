package gr.nektariostop.ergasiaadvancedhci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import kotlinx.coroutines.launch

class CommentsViewModel: ViewModel() {

    private var _comments by mutableStateOf(listOf<Comment>())

    private val commentsRepository: CommentsRepository = CommentsRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )

    private val _getCommentsResult = MutableLiveData<Result<List<Comment>>>()
    val getCommentsResult: LiveData<Result<List<Comment>>> = _getCommentsResult

    private val _addCommentsResult = MutableLiveData<Result<Boolean>>()
    val addCommentsResult: LiveData<Result<Boolean>> = _addCommentsResult

    private val _removeCommentsResult = MutableLiveData<Result<Boolean>>()
    val removeCommentsResult: LiveData<Result<Boolean>> = _removeCommentsResult


    fun getAlterationComments(alterationID: Long,numberOfComments: Int?): List<Comment>{
        if (numberOfComments != null){
            return _comments.filter { comment -> comment.alterationId == alterationID }.sortedByDescending { it.date }.take(numberOfComments)
        }
        else{
            return _comments.filter { comment -> comment.alterationId == alterationID }.sortedByDescending { it.date }
        }
    }

    fun deleteAlterationComments(alterationID: Long){
        viewModelScope.launch {
            _comments = _comments.filter { comment -> comment.alterationId != alterationID }
            _removeCommentsResult.value = commentsRepository.deleteAlterationComments(alterationID)
        }
    }

    fun addComment(comment: Comment){
        viewModelScope.launch {
            if (_comments.isNotEmpty()){
                comment.commentId = _comments[_comments.size-1].commentId + 1
            }
            _comments = _comments.plus(comment)
            _addCommentsResult.value = commentsRepository.postComment(comment)
        }
    }

    fun getComments(){
        viewModelScope.launch {
            _getCommentsResult.value = commentsRepository.getComments()
        }
    }

    fun clearAddComment(){
        _addCommentsResult.value = Result.DoNothing(true)
    }

    fun clearGetComments(){
        _getCommentsResult.value = Result.DoNothing(emptyList())
    }

    fun clearRemoveComments(){
        _removeCommentsResult.value = Result.DoNothing(true)
    }

    fun setComments(comments: List<Comment>){

        _comments = comments
    }

}