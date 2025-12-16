package br.com.bicicletario.ms_aluguel.service;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceMockImpl implements EmailService {

    @Override
    public void enviarEmail(String destinatario, String assunto, String mensagem) {
        System.out.println("==================================================");
        System.out.println("[MOCK EMAIL] Enviando email para: " + destinatario);
        System.out.println("Assunto: " + assunto);
        System.out.println("Corpo: " + mensagem);
        System.out.println("==================================================");
    }
}