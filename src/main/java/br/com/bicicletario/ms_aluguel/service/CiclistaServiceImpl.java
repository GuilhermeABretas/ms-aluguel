package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.*;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CiclistaServiceImpl implements CiclistaService {

    @Autowired
    private CiclistaRepository ciclistaRepository;

    @Autowired
    private CartaoDeCreditoRepository cartaoRepository;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private EmailService emailService;


    // --- Lógica UC01 (Nova) ---

    @Override
    @Transactional
    public CiclistaDTO cadastrarCiclista(NovoCiclistaDTO dto) {
        // 1. Validação de Email e CPF/Passaporte (UC01 - R1, A1, A2)
        validarCadastro(dto);

        // 2. Validação do Cartão de Crédito (Mock) (UC01 - Fluxo Principal 7)
        // Se reprovado (termina em 9999), o mock lança ValidacaoException (UC01 - A3)
        pagamentoService.validarCartao(dto.getMeioDePagamento());

        // 3. Mapeia DTOs para Entidades
        Ciclista ciclista = new Ciclista();
        mapearNovoCiclistaParaEntidade(dto, ciclista);

        CartaoDeCredito cartao = new CartaoDeCredito();
        mapearNovoCartaoParaEntidade(dto.getMeioDePagamento(), cartao);

        // Associa as entidades
        cartao.setCiclista(ciclista);
        ciclista.setCartaoDeCredito(cartao);

        // 4. Salva no banco (UC01 - Fluxo Principal 8)
        // O cascade no Ciclista salva o cartão junto.
        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);

        // 5. Envia email (Mock) (UC01 - Fluxo Principal 9)
        emailService.enviarEmail(
                ciclistaSalvo.getEmail(),
                "Confirme seu cadastro",
                "Bem-vindo! Clique no link para ativar seu cadastro: /ciclista/" + ciclistaSalvo.getId() + "/ativar"
        );

        // 6. Retorna DTO (UC01 - Fluxo Principal 10)
        return new CiclistaDTO(ciclistaSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return ciclistaRepository.findByEmail(email).isPresent();
    }

    // --- Lógica UC02 (Já existente) ---

    @Override
    @Transactional
    public CiclistaDTO ativarCiclista(Long idCiclista) {
        Ciclista ciclista = buscarCiclistaPeloId(idCiclista);

        if (ciclista.getStatus() != StatusCiclista.AGUARDANDO_CONFIRMACAO) {
            throw new ValidacaoException(
                    "Ciclista não está aguardando confirmação. Status atual: " + ciclista.getStatus());
        }
        ciclista.setStatus(StatusCiclista.ATIVO);
        ciclista.setDataConfirmacao(LocalDateTime.now());
        Ciclista ciclistaAtivado = ciclistaRepository.save(ciclista);
        return new CiclistaDTO(ciclistaAtivado);
    }

    // --- Lógica UC07 (Já existente) ---

    @Override
    @Transactional(readOnly = true)
    public CartaoDeCreditoDTO buscarCartao(Long idCiclista) {
        buscarCiclistaPeloId(idCiclista);
        CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(idCiclista)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ciclista não possui cartão cadastrado."));
        return new CartaoDeCreditoDTO(cartao);
    }

    @Override
    @Transactional
    public void atualizarCartao(Long idCiclista, NovoCartaoDeCreditoDTO cartaoDTO) {
        Ciclista ciclista = buscarCiclistaPeloId(idCiclista);
        pagamentoService.validarCartao(cartaoDTO);

        CartaoDeCredito cartao = cartaoRepository.findByCiclistaId(idCiclista)
                .orElse(new CartaoDeCredito());

        mapearNovoCartaoParaEntidade(cartaoDTO, cartao);
        cartao.setCiclista(ciclista);
        cartaoRepository.save(cartao);

        emailService.enviarEmail(
                ciclista.getEmail(),
                "Alteração de Cartão de Crédito",
                "Seu cartão de crédito foi alterado com sucesso em nosso sistema."
        );
    }

    // --- MÉTODOS AUXILIARES ---

    private Ciclista buscarCiclistaPeloId(Long idCiclista) {
        return ciclistaRepository.findById(idCiclista)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ciclista não encontrado com ID: " + idCiclista));
    }

    /**
     * Mapeia os dados do DTO de Cartão para a Entidade.
     */
    private void mapearNovoCartaoParaEntidade(NovoCartaoDeCreditoDTO dto, CartaoDeCredito entidade) {
        entidade.setNomeTitular(dto.getNomeTitular());
        entidade.setNumero(dto.getNumero());
        entidade.setValidade(dto.getValidade());
        entidade.setCvv(dto.getCvv());
    }

    private void mapearNovoCiclistaParaEntidade(NovoCiclistaDTO dto, Ciclista entidade) {
        entidade.setNome(dto.getNome());
        entidade.setNascimento(dto.getNascimento());
        entidade.setCpf(dto.getCpf());
        entidade.setNacionalidade(dto.getNacionalidade());
        entidade.setEmail(dto.getEmail());
        entidade.setSenha(dto.getSenha()); // (Em um projeto real, criptografar)
        entidade.setUrlFotoDocumento(dto.getUrlFotoDocumento());
        entidade.setStatus(StatusCiclista.AGUARDANDO_CONFIRMACAO); // Status inicial

        // Mapeia o passaporte
        if (dto.getNacionalidade() == Nacionalidade.ESTRANGEIRO && dto.getPassaporte() != null) {
            Passaporte p = new Passaporte();
            p.setNumero(dto.getPassaporte().getNumero());
            p.setValidade(dto.getPassaporte().getValidade());
            p.setPais(dto.getPassaporte().getPais());
            entidade.setPassaporte(p);
        }
    }


    private void validarCadastro(NovoCiclistaDTO dto) {
        // Validação de duplicidade de email (UC01 - A1)
        if (ciclistaRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ValidacaoException("Email já cadastrado.");
        }

        if (dto.getNacionalidade() == Nacionalidade.BRASILEIRO) {

            if (dto.getCpf() == null || dto.getCpf().isBlank()) {
                throw new ValidacaoException("CPF é obrigatório para brasileiros.");
            }

            if (ciclistaRepository.findByCpf(dto.getCpf()).isPresent()) {
                throw new ValidacaoException("CPF já cadastrado.");
            }
        } else if (dto.getNacionalidade() == Nacionalidade.ESTRANGEIRO) {

            if (dto.getPassaporte() == null ||
                    dto.getPassaporte().getNumero() == null ||
                    dto.getPassaporte().getPais() == null ||
                    dto.getPassaporte().getValidade() == null) {
                throw new ValidacaoException("Dados do passaporte são obrigatórios para estrangeiros.");
            }
        }
    }
}