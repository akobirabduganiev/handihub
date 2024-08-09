package tech.nuqta.handihub

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableCaching
open class HandihubApplication

fun main(args: Array<String>) {
    SpringApplication.run(HandihubApplication::class.java, *args)
}
