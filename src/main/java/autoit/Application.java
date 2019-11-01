package autoit;

import autoitx4java.AutoItX;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AutoItX autoitBean(AutoItXInstaller autoItXInstaller) throws IOException {
        autoItXInstaller.installAutoItDll();
        return autoItXInstaller.initAutoIt();
    }
}