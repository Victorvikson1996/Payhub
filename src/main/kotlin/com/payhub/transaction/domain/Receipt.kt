package com.payhub.transaction.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.time.LocalDateTime

@Entity
data class Receipt(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val transactionId: Long?,
    val userId: Long,
    val details: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)