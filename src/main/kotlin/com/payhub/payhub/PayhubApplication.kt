package com.payhub.payhub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PayhubApplication

fun main(args: Array<String>) {
	runApplication<PayhubApplication>(*args)
}
