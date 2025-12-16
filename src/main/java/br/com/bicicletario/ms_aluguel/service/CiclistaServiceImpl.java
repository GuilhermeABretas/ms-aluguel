package br.com.bicicletario.ms_aluguel.service;

import br.com.bicicletario.ms_aluguel.api.dto.BicicletaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.CiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.exception.RecursoNaoEncontradoException;
import br.com.bicicletario.ms_aluguel.api.exception.ValidacaoException;
import br.com.bicicletario.ms_aluguel.domain.model.*;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CartaoDeCreditoRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CiclistaServiceImpl implements CiclistaService {

    private final CiclistaRepository ciclistaRepository;
    private final CartaoDeCreditoRepository cartaoRepository;
    private final AluguelRepository aluguelRepository;
    private final PagamentoService pagamentoService;
    private final EmailService emailService;

    public CiclistaServiceImpl(
            CiclistaRepository ciclistaRepository,
            CartaoDeCreditoRepository cartaoRepository,
            AluguelRepository aluguelRepository,
            PagamentoService pagamentoService,
            EmailService emailService) {
        this.ciclistaRepository = ciclistaRepository;
        this.cartaoRepository = cartaoRepository;
        this.aluguelRepository = aluguelRepository;
        this.pagamentoService = pagamentoService;
        this.emailService = emailService;
    }


    @Override
    @Transactional
    public CiclistaDTO cadastrarCiclista(NovoCiclistaDTO dto) {
        validarCadastro(dto.getCiclista());
        pagamentoService.validarCartao(dto.getMeioDePagamento());

        Ciclista ciclista = new Ciclista();
        mapearNovoCiclistaParaEntidade(dto.getCiclista(), ciclista);

        CartaoDeCredito cartao = new CartaoDeCredito();
        mapearNovoCartaoParaEntidade(dto.getMeioDePagamento(), cartao);

        cartao.setCiclista(ciclista);
        ciclista.setCartaoDeCredito(cartao);

        Ciclista ciclistaSalvo = ciclistaRepository.save(ciclista);

        emailService.enviarEmail(
                ciclistaSalvo.getEmail(),
                "Confirme seu cadastro",
                "Bem-vindo! Clique no link para ativar seu cadastro: /ciclista/" + ciclistaSalvo.getId() + "/ativar"
        );

        return new CiclistaDTO(ciclistaSalvo);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return ciclistaRepository.findByEmail(email).isPresent();
    }


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



    @Override
    @Transactional(readOnly = true)
    public CiclistaDTO buscarPorId(Long idCiclista) {
        Ciclista ciclista = buscarCiclistaPeloId(idCiclista);
        return new CiclistaDTO(ciclista);
    }

    @Override
    @Transactional
    public CiclistaDTO atualizarCiclista(Long idCiclista, NovoCiclistaDTO dto) {
        Ciclista ciclista = buscarCiclistaPeloId(idCiclista);
        NovoCiclistaDTO.DadosCiclista dados = dto.getCiclista();


        Optional<Ciclista> emailExistente = ciclistaRepository.findByEmail(dados.getEmail());
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(idCiclista)) {
            throw new ValidacaoException("Email já cadastrado por outro ciclista.");
        }


        ciclista.setNome(dados.getNome());
        ciclista.setNacionalidade(dados.getNacionalidade());
        ciclista.setNascimento(dados.getNascimento());
        ciclista.setUrlFotoDocumento(dados.getUrlFotoDocumento());


        Ciclista atualizado = ciclistaRepository.save(ciclista);


        emailService.enviarEmail(
                atualizado.getEmail(),
                "Atualização de Dados Cadastrais",
                "Seus dados cadastrais foram atualizados com sucesso em nosso sistema."
        );

        return new CiclistaDTO(atualizado);
    }



    @Override
    @Transactional(readOnly = true)
    public boolean permiteAluguel(Long idCiclista) {
        Ciclista ciclista = buscarCiclistaPeloId(idCiclista);


        if (ciclista.getStatus() != StatusCiclista.ATIVO) {
            return false;
        }


        Optional<Aluguel> aluguelAberto = aluguelRepository.findByCiclistaIdAndDataHoraDevolucaoIsNull(idCiclista);
        return aluguelAberto.isEmpty();
    }

    @Override
    public BicicletaDTO obterBicicletaAlugada(Long idCiclista) {

        return null;
    }


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


    private Ciclista buscarCiclistaPeloId(Long idCiclista) {
        return ciclistaRepository.findById(idCiclista)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ciclista não encontrado com ID: " + idCiclista));
    }

    private void mapearNovoCartaoParaEntidade(NovoCartaoDeCreditoDTO dto, CartaoDeCredito entidade) {
        entidade.setNomeTitular(dto.getNomeTitular());
        entidade.setNumero(dto.getNumero());
        entidade.setValidade(dto.getValidade());
        entidade.setCvv(dto.getCvv());
    }

    private void mapearNovoCiclistaParaEntidade(NovoCiclistaDTO.DadosCiclista dto, Ciclista entidade) {
        entidade.setNome(dto.getNome());
        entidade.setNascimento(dto.getNascimento());
        entidade.setCpf(dto.getCpf());
        entidade.setNacionalidade(dto.getNacionalidade());
        entidade.setEmail(dto.getEmail());
        entidade.setSenha(dto.getSenha());
        entidade.setUrlFotoDocumento(dto.getUrlFotoDocumento());
        entidade.setStatus(StatusCiclista.AGUARDANDO_CONFIRMACAO);

        if (dto.getNacionalidade() == Nacionalidade.ESTRANGEIRO && dto.getPassaporte() != null) {
            Passaporte p = new Passaporte();
            p.setNumero(dto.getPassaporte().getNumero());
            p.setValidade(dto.getPassaporte().getValidade());
            p.setPais(dto.getPassaporte().getPais());
            entidade.setPassaporte(p);
        }
    }

    private void validarCadastro(NovoCiclistaDTO.DadosCiclista dto) {
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
            if (dto.getPassaporte() == null || dto.getPassaporte().getNumero() == null) {
                throw new ValidacaoException("Dados do passaporte são obrigatórios para estrangeiros.");
            }
        }
    }
}