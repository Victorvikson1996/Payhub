package com.payhub.bank.application

import com.payhub.bank.domain.BankAccountException
import com.payhub.transaction.application.TransactionService
import com.payhub.transaction.domain.Transaction
import com.payhub.transaction.domain.TransactionState
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Random

@Service
class BankAccountService (
    private  val redisTemplate: RedisTemplate<String,String>,
    private val transactionService: TransactionService
) {

    fun issueAccount(userId: Long): Map<String, String> {
        val key = "account:$userId"
        if(redisTemplate.opsForValue().get(key) != null) {
            throw BankAccountException("Account already issued for user $userId")
        }
        val transaction = transactionService.createTransaction(
            Transaction(userId = userId, type = "ISSUE_BANK_ACCOUNT", state = TransactionState.PROCESSING )
        )
        val iban = generateTestIBAN()
        val bic = "PAYHUBXX"
        redisTemplate.opsForValue().set(key, "$iban|$bic")
        transactionService.updateTransaction(transaction.copy(state = TransactionState.SENT, timestamp = LocalDateTime.now()))
        transactionService.createReceipt("Issued account: $iban", transaction.id!!,userId )
        return mapOf(
            "iban" to iban,
            "bic" to bic
        )
    }

    private fun generateTestIBAN(): String {
        val countryCode = "DE"
        val checkDigits = String.format("%02d", Random().nextInt(100))
        val bankCode = "12345678"
        val accountNumber = String.format("%010d", Random().nextInt(1000000000))
        return "$countryCode$checkDigits$bankCode$accountNumber"
    }
}