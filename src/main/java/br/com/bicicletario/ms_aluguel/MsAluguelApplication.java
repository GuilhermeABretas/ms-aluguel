package br.com.bicicletario.ms_aluguel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsAluguelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAluguelApplication.class, args);
    }

}