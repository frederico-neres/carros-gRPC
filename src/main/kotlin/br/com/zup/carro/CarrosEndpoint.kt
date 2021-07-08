package br.com.zup.carro

import br.com.zup.CarrosGrpcReply
import br.com.zup.CarrosGrpcRequest
import br.com.zup.CarrosGrpcServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarrosEndpoint(
    private val carroRepository: CarroRepository
): CarrosGrpcServiceGrpc.CarrosGrpcServiceImplBase() {

    override fun adicionar(request: CarrosGrpcRequest?, responseObserver: StreamObserver<CarrosGrpcReply>?) {
        if(carroRepository.existsByPlaca(request?.placa)) {

            responseObserver?.onError(Status.ALREADY_EXISTS
                .withDescription("Carro com placa existente")
                .asRuntimeException())
            return
        }

        val carro = request?.paraCarro()

        try {
            carroRepository.save(carro)
        } catch (ex: ConstraintViolationException) {
            responseObserver?.onError(Status.INVALID_ARGUMENT
                .withDescription("Dados de entrada inválidos")
                .asRuntimeException())
            return
        }

        responseObserver?.onNext(CarrosGrpcReply.newBuilder()
            .setId(carro?.id!!)
            .build())
        responseObserver?.onCompleted()
    }
}

fun CarrosGrpcRequest.paraCarro(): Carro {
    return Carro(modelo = this.modelo, placa = this.placa)
}