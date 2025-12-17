package br.com.bicicletario.ms_aluguel.integracao;

import br.com.bicicletario.ms_aluguel.MsAluguelApplication;
import br.com.bicicletario.ms_aluguel.api.dto.NovaDevolucaoDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoAluguelDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCiclistaDTO;
import br.com.bicicletario.ms_aluguel.api.dto.NovoCartaoDeCreditoDTO;
import br.com.bicicletario.ms_aluguel.domain.model.Aluguel;
import br.com.bicicletario.ms_aluguel.domain.model.Nacionalidade;
import br.com.bicicletario.ms_aluguel.domain.repository.AluguelRepository;
import br.com.bicicletario.ms_aluguel.domain.repository.CiclistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MsAluguelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test-integration")
class AluguelIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CiclistaRepository ciclistaRepository;

    @Autowired
    private AluguelRepository aluguelRepository;

    private MockWebServer mockEquipamento;
    private MockWebServer mockExterno;
    private Long idCiclista;
    private final Long ID_TRANCA_INICIO = 10L;
    private final Long ID_TRANCA_FIM = 20L;
    private final Long ID_BICICLETA = 100L;

    private final String BIKE_JSON = "{\"id\": 100, \"marca\": \"Caloi\", \"modelo\": \"Urbana\", \"numero\": \"1A2B3C\", \"status\": \"NOVA\"}";

    @BeforeEach
    void setUp() throws IOException {
        mockEquipamento = new MockWebServer();
        mockExterno = new MockWebServer();

        mockEquipamento.start(9999);
        mockExterno.start(9998);

        aluguelRepository.deleteAll();
        ciclistaRepository.deleteAll();

        try {
            idCiclista = registrarECiclistaAtivar();
        } catch (Exception e) {
            throw new RuntimeException("Falha no setup do Ciclista: " + e.getMessage(), e);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        mockEquipamento.shutdown();
        mockExterno.shutdown();
    }

    private Long registrarECiclistaAtivar() throws Exception {
        NovoCartaoDeCreditoDTO cartaoDTO = new NovoCartaoDeCreditoDTO();
        cartaoDTO.setNomeTitular("Titular Teste");
        cartaoDTO.setNumero("4242424242424242");
        cartaoDTO.setValidade(LocalDate.now().plusYears(1));
        cartaoDTO.setCvv("123");

        NovoCiclistaDTO.DadosCiclista dadosCiclista = new NovoCiclistaDTO.DadosCiclista();
        dadosCiclista.setNome("Integracao Ciclista");
        dadosCiclista.setNascimento(LocalDate.of(2000, 1, 1));
        dadosCiclista.setCpf("13560397740");
        dadosCiclista.setNacionalidade(Nacionalidade.BRASILEIRO);
        dadosCiclista.setEmail("int@teste.com");
        dadosCiclista.setSenha("senha123");
        dadosCiclista.setUrlFotoDocumento("http://foto.com/doc.jpg");
        dadosCiclista.setPassaporte(null);

        NovoCiclistaDTO novoCiclistaDTO = new NovoCiclistaDTO();
        novoCiclistaDTO.setCiclista(dadosCiclista);
        novoCiclistaDTO.setMeioDePagamento(cartaoDTO);

        mockExterno.enqueue(new MockResponse().setResponseCode(200));
        mockExterno.enqueue(new MockResponse().setResponseCode(200));

        String resposta = mockMvc.perform(MockMvcRequestBuilders.post("/ciclista")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoCiclistaDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(MockMvcRequestBuilders.post("/ciclista/{idCiclista}/ativar", id))
                .andExpect(status().isOk());

        return id;
    }


    @Test
    void testRealizarAluguel_IntegracaoSucesso() throws Exception {
        mockEquipamento.enqueue(new MockResponse().setBody(BIKE_JSON).addHeader("Content-Type", "application/json"));
        mockExterno.enqueue(new MockResponse().setResponseCode(200));
        mockEquipamento.enqueue(new MockResponse().setResponseCode(200));
        mockExterno.enqueue(new MockResponse().setResponseCode(200));

        NovoAluguelDTO novoAluguel = new NovoAluguelDTO(idCiclista, ID_TRANCA_INICIO);

        mockMvc.perform(MockMvcRequestBuilders.post("/aluguel")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(novoAluguel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bicicletaId").value(ID_BICICLETA))
                .andExpect(jsonPath("$.trancaInicioId").value(ID_TRANCA_INICIO))
                .andExpect(jsonPath("$.valorCobrado").value(10.0));
    }

    @Test
    void testRealizarDevolucao_SemAtraso_IntegracaoSucesso() throws Exception {
        testRealizarAluguel_IntegracaoSucesso();

        // CORREÇÃO: Busca o único aluguel existente (sem depender do ID)
        List<Aluguel> alugueis = aluguelRepository.findAll();
        Aluguel aluguelAtivo = alugueis.get(0);

        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(30));
        aluguelRepository.save(aluguelAtivo);

        mockExterno.enqueue(new MockResponse().setResponseCode(200));

        NovaDevolucaoDTO devolucaoDTO = new NovaDevolucaoDTO();
        devolucaoDTO.setIdTranca(ID_TRANCA_FIM);
        devolucaoDTO.setIdBicicleta(ID_BICICLETA);

        mockMvc.perform(MockMvcRequestBuilders.post("/devolucao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(devolucaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bicicletaId").value(ID_BICICLETA))
                .andExpect(jsonPath("$.valorExtra").value(0.0))
                .andExpect(jsonPath("$.valorTotal").value(10.0));
    }

    @Test
    void testRealizarDevolucao_ComAtraso_IntegracaoSucesso() throws Exception {
        testRealizarAluguel_IntegracaoSucesso();

        // CORREÇÃO: Busca o único aluguel existente (sem depender do ID)
        List<Aluguel> alugueis = aluguelRepository.findAll();
        Aluguel aluguelAtivo = alugueis.get(0);

        // Força a data de início para 3 horas e 1 minuto atrás (181 minutos)
        aluguelAtivo.setDataHoraInicio(LocalDateTime.now().minusMinutes(181));
        aluguelRepository.save(aluguelAtivo);

        mockExterno.enqueue(new MockResponse().setResponseCode(200));
        mockExterno.enqueue(new MockResponse().setResponseCode(200));

        NovaDevolucaoDTO devolucaoDTO = new NovaDevolucaoDTO();
        devolucaoDTO.setIdTranca(ID_TRANCA_FIM);
        devolucaoDTO.setIdBicicleta(ID_BICICLETA);

        mockMvc.perform(MockMvcRequestBuilders.post("/devolucao")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(devolucaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorExtra").value(10.0))
                .andExpect(jsonPath("$.valorTotal").value(20.0));

        mockExterno.takeRequest(1, TimeUnit.SECONDS);
    }
}