package br.com.zup.carro

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Carro(
    @field:NotBlank @field:Column(nullable = false) val modelo: String,
    @field:NotBlank @field:Column(nullable = true, unique = true) val placa: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}