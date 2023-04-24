package io.github.OrlandoBG.mscartoes.application;

import io.github.OrlandoBG.mscartoes.application.representation.CartaoSaveRequest;
import io.github.OrlandoBG.mscartoes.application.representation.CartoesPorClienteResponse;
import io.github.OrlandoBG.mscartoes.domain.Cartao;
import io.github.OrlandoBG.mscartoes.domain.ClienteCartao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("cartoes")
public class CartoesController {

    private final CartaoService cartaoService;
    private final ClienteCartaoService clienteCartaoService;

    @GetMapping
    public String status(){
        return "ok";
    }

    @PostMapping
    public ResponseEntity cadastra(@RequestBody CartaoSaveRequest request){
        Cartao cartao = request.toModel();
        cartaoService.save(cartao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "renda")
    public ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda){
        List<Cartao> list = cartaoService.getCartoesRendaMenorIgual(renda);
        return ResponseEntity.ok(list);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<CartoesPorClienteResponse>> getCartoesByCliente(@RequestParam("cpf") String cpf){
        List<ClienteCartao> lista = clienteCartaoService.ListCartoesByCpf(cpf);
        List<CartoesPorClienteResponse> resultlist = lista.stream()
                .map(CartoesPorClienteResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultlist);
    }


}
