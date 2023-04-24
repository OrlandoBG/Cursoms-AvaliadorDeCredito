package io.github.OrlandoBG.mscartoes.application;

import io.github.OrlandoBG.mscartoes.domain.ClienteCartao;
import io.github.OrlandoBG.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
    private final ClienteCartaoRepository repository;

    public List<ClienteCartao> ListCartoesByCpf(String cpf){
        return repository.findByCpf(cpf);
    }
}
