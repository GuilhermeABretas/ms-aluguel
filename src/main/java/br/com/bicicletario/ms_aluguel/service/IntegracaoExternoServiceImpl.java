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


@Service
@Primary
public class IntegracaoExternoServiceImpl implements EquipamentoService, PagamentoService, EmailService {

    private final EquipamentoClient equipamentoClient;
    private final ExternoClient externoClient;

    public IntegracaoExternoServiceImpl(EquipamentoClient equipamentoClient, ExternoClient externoClient) {
        this.equipamentoClient = equipamentoClient;
        this.externoClient = externoClient;
    }


    @Override
    public Long recuperarBicicletaPorTranca(Long idTranca) {
        try {
            BicicletaDTO bike = equipamentoClient.recuperarBicicletaPorTranca(idTranca);
            return bike != null ? bike.getId() : null;
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public void destrancarTranca(Long idTranca) {
        equipamentoClient.destrancarTranca(idTranca);
    }


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
                cartao.getCiclista().getId(),
                cartao.getNumero()
        );
        try {
            externoClient.realizarCobranca(request);
        } catch (Exception e) {
            throw new ValidacaoException("Falha ao realizar cobrança no cartão.");
        }
    }


    @Override
    public void enviarEmail(String email, String assunto, String mensagem) {
        try {
            externoClient.enviarEmail(new EnviarEmailRequest(email, assunto, mensagem));
        } catch (Exception e) {
            System.err.println("Falha ao enviar email (não bloqueante): " + e.getMessage());
        }
    }
}