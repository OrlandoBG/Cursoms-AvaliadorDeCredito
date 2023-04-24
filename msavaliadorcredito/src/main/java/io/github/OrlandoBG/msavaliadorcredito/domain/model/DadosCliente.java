package io.github.OrlandoBG.msavaliadorcredito.domain.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data

public class DadosCliente {
    private Long id;
    private String nome;
    private Integer idade;

}
