package com.payhub.transaction.adapter

import com.payhub.transaction.application.TransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long,
    val type: String,
    val amount: Double?,
    val state: String, // Could use TransactionState enum directly if appropriate
    val timestamp: LocalDateTime
)

data class ReceiptResponse(
    val id: Long,
    val transactionId: Long?,
    val details: String,
    val timestamp: LocalDateTime
)

@RestController
@RequestMapping("/api/transactions")
class TransactionController(private val transactionService: TransactionService) {


    @GetMapping("/{userId}")
    fun getTransactions(@PathVariable userId: Long): ResponseEntity<List<TransactionResponse>> {
        return try {
            val transactions = transactionService.getTransactions(userId).map {
                requireNotNull(it.id) { "Transaction ID cannot be null" }
                TransactionResponse(
                    id = it.id!!,
                    type = it.type,
                    amount = it.amount,
                    state = it.state.toString(),
                    timestamp = it.timestamp
                )
            }
            ResponseEntity.ok(transactions)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            ResponseEntity.status(500).build()
        }
    }

    @GetMapping("/{userid}/receipts")
    fun getReceipts(@PathVariable userId: Long): ResponseEntity<List<ReceiptResponse>> {
        return try {
            val receipts = transactionService.getReceipts(userId).map {
                requireNotNull(it.id) { "Receipt ID cannot be null" }
                ReceiptResponse(
                    id = it.id!!,
                    transactionId = it.transactionId,
                    details = it.details,
                    timestamp = it.timestamp
                )
            }
            ResponseEntity.ok(receipts)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            ResponseEntity.status(500).build()
        }
    }
}