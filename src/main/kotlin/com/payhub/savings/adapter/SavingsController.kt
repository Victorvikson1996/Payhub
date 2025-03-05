package com.payhub.savings.adapter

import com.payhub.savings.application.SavingsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/savings")
class SavingsController(private val savingsService: SavingsService) {

    @PostMapping("/{userId}/add")
    fun addSavings(@PathVariable userId: Long, @RequestBody request: Map<String, Double>): ResponseEntity<Map<String, Any>> {
        return try {
            val amount = request["amount"] ?: throw IllegalArgumentException("Amount required")
            val savings = savingsService.addSavings(userId, amount)
            ResponseEntity.ok(mapOf(
                "id" to savings.id!!,
                "amount" to savings.amount,
                "timestamp" to savings.timestamp
            ))
        } catch(e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message!!))
        }
    }

    @GetMapping("/{userId}")
    fun getSavings(@PathVariable userId: Long): ResponseEntity<List<Map<String, Any>>> {
        return try {
            val savings = savingsService.getSavings(userId).map {
                mapOf(
                    "id" to it.id!!,
                    "amount" to it.amount,
                    "timestamp" to it.timestamp
                )
            }
            ResponseEntity.ok(savings)
        }catch(e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(listOf(mapOf("error" to e.message!!)))
        }
    }
}