package com.payhub.savings.infrastructure

import com.payhub.savings.domain.Savings
import org.springframework.data.jpa.repository.JpaRepository

interface SavingsRepository: JpaRepository<Savings, Long> {
    fun findByUserId(userId: Long): List<Savings>
}