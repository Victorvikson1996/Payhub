package com.payhub.card.application

import com.payhub.card.domain.CardException
import com.payhub.transaction.application.TransactionService
import com.payhub.transaction.domain.Transaction
import com.payhub.transaction.domain.TransactionState
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Random

@Service
class CardService (
    private  val redisTemplate: RedisTemplate<String, String>,
    private val transactionService: TransactionService
) {
fun  issueCard(userId: Long): Map<String, String> {
    val key = "card:$userId"
    if (redisTemplate.opsForValue().get(key) != null) {
        throw CardException("Card already issued for user $userId")
    }
    val transaction = transactionService.createTransaction(
        Transaction(
            userId = userId, type = "ISSUE_CARD", state = TransactionState.PROCESSING
        )
    )
    val cardNumber = String.format("%016", Random().nextLong(1000000000000000L))
    val cvv = String.format("03d", Random().nextInt(1000))
    val expiry = "12/27"
    redisTemplate.opsForValue().set(key, "$cardNumber|$cvv|$expiry")
    transactionService.updateTransaction(transaction.copy(state = TransactionState.SENT, timestamp = LocalDateTime.now()))
    transactionService.createReceipt("Issued card: $cardNumber", transaction.id!!,userId )
    return mapOf(
        "cardNumber" to cardNumber,
        "cvv" to cvv,
        "expiry" to expiry
    )
}

private fun generateTestCardNumber(): String {
    return String.format("%016", Random().nextLong(1000000000000000L))

}
    fun setCardLimits(userId: Long, limit: Double): Map<String, Any> {
        val key = "card:$userId"
        val cardDetails = redisTemplate.opsForValue().get(key) ?: throw CardException("Card not issued for user $userId")
        val (cardNumber, cvv, expiry) = cardDetails.split("|")
        val transaction = transactionService.createTransaction(
            Transaction(
                userId = userId, type = "SET_CARD_LIMIT", state = TransactionState.PROCESSING
            )
        )
        redisTemplate.opsForValue().set(key, "$cardNumber|$cvv|$expiry|$limit")
        transactionService.updateTransaction(transaction.copy(state = TransactionState.SENT, timestamp = LocalDateTime.now()))
        transactionService.createReceipt("Set card limit: $limit", transaction.id!!, userId)
        return mapOf(
            "cardNumber" to cardNumber,
            "cvv" to cvv,
            "expiry" to expiry,
            "limit" to limit
        )
    }

    fun setCardLimit(userId: Long, limit: Double) {
        if (limit< 0) throw CardException("Limit must be positive")
        redisTemplate.opsForValue().set("card:$userId:limit", limit.toString())
        transactionService.createReceipt("set card limit: $limit", null, userId)
    }


}



