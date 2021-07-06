package br.com.zup

import br.com.zup.carro.Carro
import br.com.zup.carro.CarroRepository
import io.micronaut.test.annotation.TransactionMode
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@MicronautTest(
    rollback = false,
    transactionMode = TransactionMode.SINGLE_TRANSACTION,
    transactional = false
)
class CarrosGrpcTest {

    @field:Inject
    lateinit var carroRepository: CarroRepository

    lateinit var carro: Carro

    @BeforeEach
    internal fun setUp() {
        //cenário
        carroRepository.deleteAll()

        //ação
        carro = Carro("Gol", "HPX-1234")
        carroRepository.save(carro)
    }

    @AfterEach
    internal fun tearDown() {
        carroRepository.deleteAll()
    }

    @Test
    fun `deve inserir um novo carro`() {
        //validação
        assertEquals(1, carroRepository.count())
    }

    @Test
    fun `deve encontrar carro pela placa`() {
        //ação
        val existsCarro = carroRepository.existsByPlaca(carro.placa)

        //validação
        assertTrue(existsCarro)
    }
}
