package com.payhub.auth.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity
data class User(
    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    val id: Long? = null,
    val email: String,
    val phone: String,
    val  username: String,
    val password: String
)