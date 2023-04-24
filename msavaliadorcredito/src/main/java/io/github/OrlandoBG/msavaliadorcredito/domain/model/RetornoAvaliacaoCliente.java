package io.github.OrlandoBG.msavaliadorcredito.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RetornoAvaliacaoCliente {

    private List<CartaoAprovado> cartoes;

}
