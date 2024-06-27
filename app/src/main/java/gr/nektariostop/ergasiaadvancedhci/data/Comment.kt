package gr.nektariostop.ergasiaadvancedhci.data

import java.time.LocalDateTime

data class Comment (
    var commentId: Long = 0L,
    val alterationId: Long = 0L,
    val userId: String = "",
    val commentText: String = "",
    val date: LocalDateTime,
){
    constructor(): this(0L,0L,"","", LocalDateTime.now())
}