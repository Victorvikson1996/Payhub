package com.payhub.wallet.adapter

import com.payhub.wallet.application.WalletService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/wallet")
class WalletController (private val walletService: WalletService) {
    @PostMapping("/{userId}/add-money")
    fun addMoney(@PathVariable userId: Long, @RequestBody request: Map<String, Double>): ResponseEntity<Map<String, Any>> {
        return try {
            val amount = request["amount"]?: throw IllegalArgumentException("Amount required")
            val newBalance = walletService.addMoney(userId, amount)
            ResponseEntity.ok(mapOf("balance" to newBalance))

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message!!))
        }
    }

    @GetMapping("/{userId}/balance")
    fun getBalance(@PathVariable userId: Long): ResponseEntity<Map<String, Any>> {
        return try {
            val balance = walletService.getBalance(userId)
            ResponseEntity.ok(mapOf("balance" to balance))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to e.message!!))
        }
    }

    @PostMapping("/{userId}/set-limit")
    fun setSpendingLimit(@PathVariable userId:Long, @RequestBody request: Map<String, Double>): ResponseEntity<Map<String, String>> {
        return try {
            val limit = request["limit"] ?: throw IllegalArgumentException("Limit required")
            walletService.setSpendingLimit(userId, limit)
            ResponseEntity.ok(mapOf("message" to "limit set to $limit"))
        } catch(e:Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("Error" to e.message!!))
        }
    }
}