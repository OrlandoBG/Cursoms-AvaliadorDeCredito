package io.github.OrlandoBG.msavaliadorcredito.application;

import feign.FeignException;
import io.github.OrlandoBG.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import io.github.OrlandoBG.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import io.github.OrlandoBG.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import io.github.OrlandoBG.msavaliadorcredito.domain.model.*;
import io.github.OrlandoBG.msavaliadorcredito.infra.clients.CartoesResourceClient;
import io.github.OrlandoBG.msavaliadorcredito.infra.clients.ClienteResourceClient;
import io.github.OrlandoBG.msavaliadorcredito.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesResourceClient;
    public final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException  {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesResourceClient.getCartoesByCliente(cpf);
            return SituacaoCliente.builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvalicao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException{
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            List<CartaoAprovado> listaCartoesAprovados = cartoes.stream().map(cartao ->{
                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico =  cartao.getLimiteBasico();
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado =  fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());
            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            String protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch(Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }


}
