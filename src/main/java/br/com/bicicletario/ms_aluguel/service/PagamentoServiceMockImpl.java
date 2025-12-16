package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.CartaoDeCredito;
import org.springframework.stereotype.Service;

@Service
public class PagamentoServiceMockImpl implements PagamentoService {

    @Override
    public void validarCartao(NovoCartaoDeCreditoDTO cartao) {
        System.out.println("[MOCK PAGAMENTO] Validando cartão: " + cartao.getNumero());
    }

    @Override
    public void realizarCobranca(CartaoDeCredito cartao, Double valor) {
        System.out.println("[MOCK PAGAMENTO] Cobrando R$ " + valor + " no cartão final "
                + cartao.getNumero().substring(cartao.getNumero().length() - 4));

        // Se quiser simular falha, descomente:
        // throw new ValidacaoException("Pagamento recusado pela operadora.");
    }
}