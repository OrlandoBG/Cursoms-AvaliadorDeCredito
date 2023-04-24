package io.github.OrlandoBG.msclientes.application;

import io.github.OrlandoBG.msclientes.domain.Cliente;
import io.github.OrlandoBG.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional
    public Cliente save(Cliente cliente){
        return repository.save(cliente);
    }

    @Transactional
    public Optional<Cliente> getByCpf(String cpf){
        return repository.findByCpf(cpf);
    }

}
