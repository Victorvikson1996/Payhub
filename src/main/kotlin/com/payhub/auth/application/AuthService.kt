package com.payhub.auth.application

import com.payhub.auth.domain.AuthException
import com.payhub.auth.domain.User
import com.payhub.auth.infrastructure.UserRepository
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Base64


@Service
class AuthService (private val userRepository: UserRepository,
                   private  val kafkaTemplate: KafkaTemplate<String,  String>,
                   @Value("\${jwt.secret}") private val jwtSecret: String,
                   @Value("\${jwt.expiration}") private val jwtExpiration: Long

    ) {

    private  val passwordEncoder = BCryptPasswordEncoder()

    @CircuitBreaker(name = "payhub", fallbackMethod = "registerFallback")
    fun register(user: User): String {
        if(userRepository.findByUsername(user.username) != null) {
            throw  AuthException("Username already Exists")
        }

        val encodedPassword = passwordEncoder.encode(user.password)
        val newUser = user.copy(password = encodedPassword)
        userRepository.save(newUser)
        val token = generateToken(newUser.username)
        kafkaTemplate.send("user-events", "User${newUser.username} registered")
        return token
    }

    @CircuitBreaker(name = "payhub", fallbackMethod = "loginFallBack")
    fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username) ?: throw  AuthException("Invalid Credentials")
        if(!passwordEncoder.matches(password, user.password)) {
            throw AuthException("Invalid credentials")
        }

        return generateToken(username)
    }

    @CircuitBreaker(name = "payhub", fallbackMethod = "resetPasswordFallback")
    fun resetPassword(username: String, newPassword: String) {
        val user = userRepository.findByUsername(username) ?: throw AuthException("User not found")
        val encodedPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(password = encodedPassword)
        userRepository.save(updatedUser)
        kafkaTemplate.send("user-events", "User $username reset password")
    }


    @CircuitBreaker(name = "payhub", fallbackMethod = "resetUsernameFallback")
    fun resetUsername(oldUsername: String, newUsername: String) {
        if (userRepository.findByUsername(newUsername) != null) {
            throw AuthException("New username already exists")
        }
        val user = userRepository.findByUsername(oldUsername) ?: throw AuthException("User not found")
        val updatedUser = user.copy(username = newUsername)
        userRepository.save(updatedUser)
        kafkaTemplate.send("user-events", "User changed username from $oldUsername to $newUsername")
    }

    private fun generateToken(username: String): String {
        return Jwts.builder().setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }



//    private fun generateToken(username: String): String {
//        val key: Key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
//        return Jwts.builder()
//            .subject(username) // Replaces setSubject
//            .issuedAt(Date()) // Replaces setIssuedAt
//            .expiration(Date(System.currentTimeMillis() + jwtExpiration)) // Replaces setExpiration
//            .signWith(key) // Uses Key instance instead of String
//            .compact()
//    }

    fun registerFallback(user: User, t: Throwable): String = "Registration failed: ${t.message}"
    fun loginFallback(username: String, password: String, t: Throwable): String = "Login failed: ${t.message}"
    fun resetPasswordFallback(username: String, newPassword: String, t: Throwable): String =
        "Password reset failed: ${t.message}"
    fun resetUsernameFallback(oldUsername: String, newUsername: String, t: Throwable): String =
        "Username reset failed: ${t.message}"
}