package autoit;

import autoitx4java.AutoItX;
import com.jacob.com.LibraryLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public AutoItX autoitBean() throws IOException {
        String JACOB_DLL_TO_USE = System.getProperty("sun.arch.data.model").contains("32") ? "jacob-1.19-x86.dll" : "jacob-1.19-x64.dll";
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, new ClassPathResource(JACOB_DLL_TO_USE).getFile().getAbsolutePath());
        return new AutoItX();
    }
}
