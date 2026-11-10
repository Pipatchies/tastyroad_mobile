package fr.tastyroad.repository

import fr.tastyroad.domain.Comment
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class CommentRepository {
    private val client = AppClient.instance

    suspend fun getCommentsByUser(userId: Int): List<Comment> {
        return client.get("comments/?author=$userId").body()
    }

    suspend fun createComment(
        token: String,
        newComment: Comment,
    ): Comment {
        return client.post("comments/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(newComment)
        }.body()
    }

    suspend fun updateComment(
        token: String,
        commentId: Int,
        updatedComment: Comment,
    ): Comment {
        return client.put("comments/$commentId/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(updatedComment)
        }.body()
    }

    suspend fun deleteComment(
        token: String,
        commentId: Int,
    ): Boolean {
        return client.delete("comments/$commentId/") {
            header("Authorization", "Bearer $token")
        }.status.isSuccess()
    }
}
