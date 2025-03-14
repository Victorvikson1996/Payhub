package com.payhub.wallet.application

import com.payhub.transaction.application.TransactionService
import com.payhub.transaction.domain.Transaction
import com.payhub.transaction.domain.TransactionState
import com.payhub.wallet.domain.WalletException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.retry.annotation.CircuitBreaker
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class WalletService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val transactionService: TransactionService
) {
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "payhub", fallbackMethod = "addMoneyFallback")
    fun addMoney(userId: Long, amount: Double): Double {
        if(amount <= 0 ) throw  WalletException("Amount must be positive")
        val transaction = transactionService.createTransaction(
            Transaction(userId = userId, type ="ADD_MONEY", amount = amount, state = TransactionState.PENDING)
        )
        val key = "wallet:$userId"
        val currentBalance = redisTemplate.opsForValue().get(key)?.toDouble() ?: 0.0
        val newBalance = currentBalance + amount
        redisTemplate.opsForValue().set(key, newBalance.toString())
        transactionService.updateTransaction(transaction.copy(state = TransactionState.RECEIVED, timestamp = LocalDateTime.now()))
        transactionService.createReceipt("Added $amount to wallet. New balance: $newBalance" , transaction.id!!, userId)
        kafkaTemplate.send("wallet", "Added $amount to wallet of user $userId")
        return newBalance
    }

    fun getBlance(userId: Long): Double {
        val key = "wallet:$userId"
        return redisTemplate.opsForValue().get(key)?.toDouble() ?: throw WalletException("Wallet not found for user $userId")
    }

    fun _setSpendingLimit(userId: Long, limit: Double) {
        if(limit < 0) throw WalletException("Limit must be positive")
        val key = "wallet:$userId"
        val currentBalance = redisTemplate.opsForValue().get(key)?.toDouble() ?: throw WalletException("Wallet not found for user $userId")
        if(currentBalance < limit) throw WalletException("Limit cannot be greater than current balance")
        redisTemplate.opsForValue().set(key, "$currentBalance|$limit")
    }

    fun setSpendingLimit(userId: Long, limit:  Double) {
        if(limit < 0) throw WalletException("Limit cannot be negative")
        redisTemplate.opsForValue().set("limit:$userId", limit.toString())
        transactionService.createReceipt("Set spending limit to $limit", null,  userId)
    }

    fun addmoneyFallback(userId: Long, amount: Double, t: Throwable): Double {
        throw WalletException("Failed to add money to wallet: ${t.message}")
    }






}