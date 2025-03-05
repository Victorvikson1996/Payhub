package com.payhub.transaction.infrastructure

import com.payhub.transaction.domain.Receipt
import org.springframework.data.jpa.repository.JpaRepository


interface ReceiptRepository: JpaRepository<Receipt, Long> {
    fun findByUserId(userId: Long): List<Receipt>
}