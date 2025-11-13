package br.com.bicicletario.ms_aluguel.service;

public interface EmailService {

    /**
     * Simula o envio de um email.
     * @param email Email do destinatário
     * @param assunto Assunto
     * @param mensagem Corpo da mensagem
     */
    void enviarEmail(String email, String assunto, String mensagem);

}