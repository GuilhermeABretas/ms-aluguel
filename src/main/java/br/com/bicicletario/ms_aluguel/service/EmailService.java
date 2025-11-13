package br.com.bicicletario.ms_aluguel.service;

public interface EmailService {


    void enviarEmail(String email, String assunto, String mensagem);

}