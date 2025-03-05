package com.payhub.bank.adapter

import com.payhub.bank.application.BankAccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bank")
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PostMapping("/{userId}/issue")
    fun issueAccount(@PathVariable userId: Long): ResponseEntity<Map<String, Any>> {
        return try {
            val accountDetails = bankAccountService.issueAccount(userId)
            ResponseEntity.ok(accountDetails)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                mapOf(
                    "error" to e.message!!
                )
            )
        }
    }

}
