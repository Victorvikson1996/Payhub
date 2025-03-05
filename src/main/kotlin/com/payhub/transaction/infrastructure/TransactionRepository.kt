package com.payhub.transaction.infrastructure

import com.payhub.transaction.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository: JpaRepository<Transaction, Long> {
    fun findByUserId(userId: Long): List<Transaction>
}