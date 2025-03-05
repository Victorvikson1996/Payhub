package com.payhub.transaction.application


import com.payhub.transaction.domain.Receipt
import com.payhub.transaction.domain.Transaction
import com.payhub.transaction.infrastructure.ReceiptRepository
import com.payhub.transaction.infrastructure.TransactionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val receiptRepository: ReceiptRepository
) {

    @Transactional
    fun createTransaction(transaction: Transaction): Transaction {
        return transactionRepository.save(transaction)
    }

    @Transactional
    fun updateTransaction(transaction: Transaction) {
        transactionRepository.save(transaction)
    }

    @Transactional
    fun createReceipt(details: String, transactionId: Long?, userId: Long): Receipt {
        return receiptRepository.save(Receipt(transactionId = transactionId, userId = userId, details = details))
    }

    fun getTransactions(userId: Long): List<Transaction> {
        return transactionRepository.findByUserId(userId)
    }

    fun getReceipts(userId: Long): List<Receipt> {
        return receiptRepository.findByUserId(userId)
    }

}