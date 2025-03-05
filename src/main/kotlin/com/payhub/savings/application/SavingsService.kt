package com.payhub.savings.application

import com.payhub.savings.domain.Savings
import com.payhub.savings.domain.SavingsException
import com.payhub.savings.infrastructure.SavingsRepository
import com.payhub.transaction.application.TransactionService
import org.springframework.stereotype.Service

@Service
class SavingsService (
private val savingsRepository: SavingsRepository,
    private  val transactionService: TransactionService
) {
    fun addSavings(userId: Long, amount: Double): Savings {
        if(amount <= 0 ) throw SavingsException("Amount must be positive")
        val savings = savingsRepository.save(Savings(userId = userId, amount = amount))
        transactionService.createReceipt("Added $amount to saving", null, userId)
        return savings
    }

    fun getSavings(userId: Long): List<Savings> {
        return savingsRepository.findByUserId(userId)
    }
}