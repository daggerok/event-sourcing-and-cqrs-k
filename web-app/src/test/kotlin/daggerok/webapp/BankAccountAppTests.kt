package daggerok.webapp

import daggerok.domain.BackAccountAggregate
import daggerok.domain.command.ActivateBankAccountCommand
import daggerok.domain.command.RegisterBankAccountCommand
import daggerok.domain.query.FindBankAccountActivatedStateResult
import daggerok.domain.query.FindBankAccountRegistrationDateResult
import java.time.Instant
import java.util.UUID
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BankAccountAppTests @Autowired constructor(@LocalServerPort port: Int, val restTemplate: TestRestTemplate) {

    init {
        logger.info { "restTemplate rootUri: ${restTemplate.rootUri}" }
    }

    @Test
    @Order(1)
    fun `should register bank account`() {
        // given
        val request = HttpEntity(
            RegisterBankAccountCommand(
                aggregateId = UUID.fromString("0-0-0-0-1"),
                username = "daggerok",
                password = "P@assw0rd",
            )
        )

        // when
        val response = restTemplate.exchange<BackAccountAggregate>("/register-bank-account", POST, request)
        logger.info { "response: $response" }

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        // and
        val backAccountAggregate = response.body ?: fail("body may not be null")
        logger.info { "backAccountAggregate: $backAccountAggregate" }
        assertThat(backAccountAggregate.state.username).isEqualTo("daggerok")
        assertThat(backAccountAggregate.state.activated).isFalse
    }

    @Test
    @Order(2)
    fun `should verify not activated bank account state`() {
        // given
        val aggregateId = UUID.fromString("0-0-0-0-1")

        // when
        val response = restTemplate.exchange<FindBankAccountActivatedStateResult>(
            "/find-bank-account-activated-state/{aggregateId}", GET, null,
            aggregateId.toString()
        )
        logger.info { "response: $response" }

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // and
        val findBankAccountActivatedStateResult = response.body ?: fail("body may not be null")
        logger.info { "findBankAccountActivatedStateResult: $findBankAccountActivatedStateResult" }
        assertThat(findBankAccountActivatedStateResult.activated).isFalse
    }

    @Test
    @Order(2)
    fun `should get bank account registration date`() {
        // given
        val aggregateId = UUID.fromString("0-0-0-0-1")

        // when
        val response = restTemplate.exchange<FindBankAccountRegistrationDateResult>(
            "/find-bank-account-registration-date/{aggregateId}", GET, null,
            aggregateId.toString()
        )
        logger.info { "response: $response" }

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // and
        val findBankAccountRegistrationDateResult = response.body ?: fail("body may not be null")
        logger.info { "findBankAccountRegistrationDateResult: $findBankAccountRegistrationDateResult" }
        assertThat(findBankAccountRegistrationDateResult.registeredAt).isBefore(Instant.now())
    }

    @Test
    @Order(3)
    fun `should activate bank account`() {
        // given
        val request = HttpEntity(
            ActivateBankAccountCommand(aggregateId = UUID.fromString("0-0-0-0-1"))
        )

        // when
        val response = restTemplate.exchange<BackAccountAggregate>("/activate-bank-account", POST, request)
        logger.info { "response: $response" }

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.ACCEPTED)

        // and
        val backAccountAggregate = response.body ?: fail("body may not be null")
        logger.info { "backAccountAggregate: $backAccountAggregate" }
        assertThat(backAccountAggregate.state.activated).isTrue
    }

    @Test
    @Order(4)
    fun `should verify bank account activated state`() {
        // given
        val aggregateId = UUID.fromString("0-0-0-0-1")

        // when
        val response = restTemplate.exchange<FindBankAccountActivatedStateResult>(
            "/find-bank-account-activated-state/{aggregateId}", GET, null,
            aggregateId.toString()
        )
        logger.info { "response: $response" }

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // and
        val findBankAccountActivatedStateResult = response.body ?: fail("body may not be null")
        logger.info { "findBankAccountActivatedStateResult: $findBankAccountActivatedStateResult" }
        assertThat(findBankAccountActivatedStateResult.activated).isTrue
    }

    companion object : KLogging()
}
