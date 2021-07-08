package br.com.zup.carro

import br.com.zup.CarrosGrpcRequest
import br.com.zup.CarrosGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(
    private val ClientGrpc: CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub,
    private val carroRepository: CarroRepository) {


    @BeforeEach
    internal fun setUp() {
        carroRepository.deleteAll()
    }

    @Test
    internal fun `deve cadastrar um novo carro`() {
        // ação
        val response = ClientGrpc.adicionar(CarrosGrpcRequest.newBuilder()
            .setModelo("Gol")
            .setPlaca("HPX-1414").build())

        // validação
        with(response) {
            assertNotNull(id)
            assertTrue(carroRepository.existsById(id))
        }
    }

    @Test
    internal fun `nao deve adicionar carro se a placa ja existente`() {
        // cenário
        val carroExistente = carroRepository.save(Carro(modelo = "Gol", placa = "HPX-1414"))

        // ação
        val error = assertThrows<StatusRuntimeException> {
            val response = ClientGrpc.adicionar(CarrosGrpcRequest.newBuilder()
                .setModelo(carroExistente.modelo)
                .setPlaca(carroExistente.placa).build())
        }

        // validação
        with(error) {
            assertEquals(io.grpc.Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Carro com placa existente", status.description)
        }

    }

    @Test
    internal fun `nao deve adicionar novo carro quando dados de entrada foram invalidos`() {

        // ação
        val error = assertThrows<StatusRuntimeException> {
            val response = ClientGrpc.adicionar(CarrosGrpcRequest.newBuilder()
                .setModelo("")
                .setPlaca("")
                .build())
        }

        // validação
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados de entrada inválidos", status.description)
        }

    }

    @Factory
    class Clients {

        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {
            return CarrosGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}