package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import org.springframework.stereotype.Service;

@Service
public class PagamentoServiceMockImpl implements PagamentoService {


    @Override
    public boolean validarCartao(NovoCartaoDeCreditoDTO cartaoDTO) {
        System.out.println("--- MOCK DE PAGAMENTO ---");
        System.out.println("Validando cartão com final: " + cartaoDTO.getNumero().substring(cartaoDTO.getNumero().length() - 4));

        if (cartaoDTO.getNumero().endsWith("9999")) {
            System.out.println("Resultado: Cartão REPROVADO (Mock)");

            throw new ValidacaoException("Cartão de crédito reprovado pela administradora.");
        }

        System.out.println("Resultado: Cartão APROVADO (Mock)");
        return true;
    }
}