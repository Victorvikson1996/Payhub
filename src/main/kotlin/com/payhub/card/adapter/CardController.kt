package com.payhub.card.adapter

import com.payhub.card.application.CardService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/card")
class CardController (private  val cardService: CardService) {

    @PostMapping("/{userId}/issue")
    fun issueCard(@PathVariable userId: Long): ResponseEntity<Map<String, Any>> {
        return try {
            val cardDetails = cardService.issueCard(userId)
            ResponseEntity.ok(cardDetails)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                mapOf(
                    "error" to e.message!!
                )
            )
        }
    }

//    @PostMapping("/{userId}/set-limit")
//    fun setCardLimit(@PathVariable userId: Long, @RequestBody request: Map<String, Double>): ResponseEntity<Map<String, String>> {
//        return  try {
//            val limit = request["limit"] ?: throw IllegalArgumentException("Limit required")
//            val cardDetails = cardService.setCardLimits(userId, limit)
//            ResponseEntity.ok(cardDetails)
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.CONFLICT).body(
//                mapOf(
//                    "error" to e.message!!
//                )
//            )
//        }
//    }

    @PostMapping("/{userId}/set-limit")
    fun setCardLimit(@PathVariable userId: Long, @RequestBody request: Map<String, Double>): ResponseEntity<Map<String, String>> {
        return try {
            val limit = request["limit"] ?: throw IllegalArgumentException("Limit required")
            cardService.setCardLimit(userId, limit)
            ResponseEntity.ok(mapOf("message" to "Card limit set to $limit"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message!!))
        }
    }

}