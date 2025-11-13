package br.com.bicicletario.ms_aluguel.service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceMockImpl implements EmailService {

    /**
     * Implementação MOCK (Falsa) do UC07.
     * Apenas loga no console e retorna sucesso (não faz nada).
     */
    @Override
    public void enviarEmail(String email, String assunto, String mensagem) {
        System.out.println("--- MOCK DE EMAIL ---");
        System.out.println("Enviando email para: " + email);
        System.out.println("Assunto: " + assunto);
        System.out.println("Mensagem: " + mensagem);
        System.out.println("--- EMAIL ENVIADO (MOCK) ---");
        // Não faz nada, apenas simula sucesso.
    }
}