package com.payhub.auth.infrastructure

import com.payhub.auth.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface  UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}