package gr.nektariostop.ergasiaadvancedhci

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import gr.nektariostop.ergasiaadvancedhci.data.Comment
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class CommentsRepository (
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
    ) {
        private val instance: FirebaseFirestore by lazy {
            FirebaseFirestore.getInstance()
        }

        private fun instance(): FirebaseFirestore {
            return instance
        }

        suspend fun postComment(comment: Comment): Result<Boolean> {
            return try {

                val tz =  ZoneId.systemDefault()
                val localDateTime = comment.date

                // convert LocalDateTime to Timestamp
                val seconds = localDateTime.atZone(tz).toEpochSecond()
                val nanos = localDateTime.nano
                val timestamp = com.google.firebase.Timestamp(seconds, nanos)

                val newComment = object {
                    val commentId = comment.commentId
                    val alterationId= comment.alterationId
                    val userId = comment.userId
                    val commentText = comment.commentText
                    val date = timestamp
                }

                instance().collection("comments").add(newComment).await()
                Result.Success(true)
            }
            catch (e: Exception) {
                Result.Error(e)
            }
        }

        suspend fun deleteAlterationComments(alterationId: Long): Result<Boolean>{
            return try {
                val query = instance().collection("comments").where(Filter.equalTo("alterationId",alterationId)).get().await()

                query.documents.map {
                    document ->
                    document.reference.delete().await()
                }

                Result.Success(true)
            }
            catch (e: Exception) {
                Result.Error(e)
            }
        }

        suspend fun getComments(): Result<List<Comment>> {
            return try {
                val query = instance().collection("comments").get().await()
                val commentResults = query.documents.map {
                        document ->

                    println(document.data)

                    val timestamp = document.data?.get("date") as com.google.firebase.Timestamp
                    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                    val tz = ZoneId.systemDefault()

                    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), tz)
                    val commentId = document.get("commentId").toString().toLong()
                    val alterationId = document.get("alterationId").toString().toLong()
                    val userId = document.get("userId").toString()
                    val commentText = document.get("commentText").toString()

                    Comment(commentId,alterationId,userId,commentText,date)
                }
                Result.Success(commentResults)
            }
            catch (e: Exception) {
                Result.Error(e)
            }
        }
}