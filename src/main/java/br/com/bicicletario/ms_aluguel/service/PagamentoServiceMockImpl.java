package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import org.springframework.stereotype.Service;

@Service
public class PagamentoServiceMockImpl implements PagamentoService {

    /**
     * Implementação MOCK (Falsa) do UC07.
     * Simula a validação de um cartão pela "Administradora CC".
     *
     * Regra de simulação:
     * - Cartões terminados em "9999" serão REPROVADOS.
     * - Outros cartões serão APROVADOS.
     */
    @Override
    public boolean validarCartao(NovoCartaoDeCreditoDTO cartaoDTO) {
        System.out.println("--- MOCK DE PAGAMENTO ---");
        System.out.println("Validando cartão com final: " + cartaoDTO.getNumero().substring(cartaoDTO.getNumero().length() - 4));

        if (cartaoDTO.getNumero().endsWith("9999")) {
            System.out.println("Resultado: Cartão REPROVADO (Mock)");
            // UC07 - Fluxo Alternativo A2: Cartão reprovado
            throw new ValidacaoException("Cartão de crédito reprovado pela administradora.");
        }

        System.out.println("Resultado: Cartão APROVADO (Mock)");
        return true;
    }
}