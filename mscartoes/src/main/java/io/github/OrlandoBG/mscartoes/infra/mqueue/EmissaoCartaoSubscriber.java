package io.github.OrlandoBG.mscartoes.infra.mqueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.OrlandoBG.mscartoes.domain.Cartao;
import io.github.OrlandoBG.mscartoes.domain.ClienteCartao;
import io.github.OrlandoBG.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import io.github.OrlandoBG.mscartoes.infra.repository.CartaoRepository;
import io.github.OrlandoBG.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class EmissaoCartaoSubscriber {

    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;


    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissao(@Payload String payload){
        try {
            ObjectMapper mapper = new ObjectMapper();
            DadosSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();
            ClienteCartao clienteCartao = new ClienteCartao();
            clienteCartao.setCartao(cartao);
            clienteCartao.setCpf(dados.getCpf());
            clienteCartao.setLimite(dados.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCartao);
        } catch (Exception e) {
            log.error("Erro ao receber solicitação de emissão de cartao: {}", e.getMessage());
        }
    }

}
