package org.jdk.workmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkmateApplication {

  public static void main(String[] args) {
    SpringApplication.run(WorkmateApplication.class, args);
  }
}
