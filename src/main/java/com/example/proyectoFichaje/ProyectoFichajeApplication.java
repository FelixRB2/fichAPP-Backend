package com.example.proyectoFichaje;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProyectoFichajeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoFichajeApplication.class, args);
	}

}
