package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.client.EquipamentoClient;
import br.com.bicicletario.ms_aluguel.api.client.ExternoClient;
import br.com.bicicletario.ms_aluguel.api.dto.BicicletaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.externo.CobrancaRequest;
import br.com.bicicletario.ms_aluguel.api.dto.externo.EnviarEmailRequest;
import br.com.bicicletario.ms_aluguel.api.dto.externo.ValidacaoCartaoRequest;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Implementação REAL que conecta com os microsserviços externos.
 * O @Primary faz o Spring injetar esta classe no lugar dos Mocks.
 */
@Service
@Primary
public class IntegracaoExternoServiceImpl implements EquipamentoService, PagamentoService, EmailService {

    private final EquipamentoClient equipamentoClient;
    private final ExternoClient externoClient;

    public IntegracaoExternoServiceImpl(EquipamentoClient equipamentoClient, ExternoClient externoClient) {
        this.equipamentoClient = equipamentoClient;
        this.externoClient = externoClient;
    }

    // --- EQUIPAMENTO SERVICE ---
    @Override
    public Long recuperarBicicletaPorTranca(Long idTranca) {
        try {
            BicicletaDTO bike = equipamentoClient.recuperarBicicletaPorTranca(idTranca);
            return bike != null ? bike.getId() : null;
        } catch (Exception e) {
            // Se der 404 ou erro, assumimos que não tem bicicleta ou tranca inválida
            return null;
        }
    }

    @Override
    public void destrancarTranca(Long idTranca) {
        equipamentoClient.destrancarTranca(idTranca);
    }

    // --- PAGAMENTO SERVICE ---
    @Override
    public void validarCartao(NovoCartaoDeCreditoDTO cartao) {
        ValidacaoCartaoRequest request = new ValidacaoCartaoRequest(
                cartao.getNomeTitular(),
                cartao.getNumero(),
                cartao.getValidade(),
                cartao.getCvv()
        );
        try {
            externoClient.validarCartao(request);
        } catch (Exception e) {
            throw new ValidacaoException("Cartão inválido ou reprovado pela operadora.");
        }
    }

    @Override
    public void realizarCobranca(CartaoDeCredito cartao, Double valor) {
        CobrancaRequest request = new CobrancaRequest(
                valor,
                cartao.getCiclista().getId(), // Id do ciclista
                cartao.getNumero() // Opcional: número para teste
        );
        try {
            externoClient.realizarCobranca(request);
        } catch (Exception e) {
            throw new ValidacaoException("Falha ao realizar cobrança no cartão.");
        }
    }

    // --- EMAIL SERVICE ---
    @Override
    public void enviarEmail(String email, String assunto, String mensagem) {
        try {
            externoClient.enviarEmail(new EnviarEmailRequest(email, assunto, mensagem));
        } catch (Exception e) {
            System.err.println("Falha ao enviar email (não bloqueante): " + e.getMessage());
        }
    }
}