package com.payhub.transaction.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Transaction (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: Long,
    val type: String,
    val amount: Double? = null,
    @Enumerated(EnumType.STRING)
    val state: TransactionState,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

enum class TransactionState {
    PENDING, PROCESSING, SENT, RECEIVED, SEND
}