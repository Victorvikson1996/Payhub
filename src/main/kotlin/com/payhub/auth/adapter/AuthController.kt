package com.payhub.auth.adapter

import com.payhub.auth.application.AuthService
import com.payhub.auth.domain.User
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<Map<String, String>> {
        return  try {
            val token = authService.register(user)
            ResponseEntity.ok(mapOf("token" to token))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("Error" to e.message!!))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: Map<String,  String>): ResponseEntity<Map<String, String>> {
        return try {
            val username = credentials["username"] ?: throw IllegalArgumentException("Username required")
            val password = credentials["password"] ?: throw IllegalArgumentException("Password required")
            val token = authService.login(username, password)
            ResponseEntity.ok(mapOf("token" to token))
        } catch(e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to e.message!!))
        }
    }



    @PostMapping("/reset-password")
    fun resetPassword(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        return try {
            val username = request["username"] ?: throw IllegalArgumentException("Username required")
            val newPassword = request["newPassword"] ?: throw IllegalArgumentException("New password required")
            authService.resetPassword(username, newPassword)
            ResponseEntity.ok(mapOf("message" to "Password reset successful"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message!!))
        }
    }

    @PostMapping("/reset-username")
    fun resetUsername(@RequestBody request: Map<String, String>): ResponseEntity<Map<String, String>> {
        return try {
            val oldUsername = request["oldUsername"] ?: throw IllegalArgumentException("Old username required")
            val newUsername = request["newUsername"] ?: throw IllegalArgumentException("New username required")
            authService.resetUsername(oldUsername, newUsername)
            ResponseEntity.ok(mapOf("message" to "Username reset successful"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message!!))
        }
    }
}