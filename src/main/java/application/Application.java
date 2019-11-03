package application;

import autoitx4java.AutoItX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private AutoItX au;

    @Override
    public void run(final String... s) throws Exception {
        // Hint:
        // You can turn off autoit.installation.skip in application.properties,
        // After first run. So it will skip scanning autoit installation

        // AutoIt Test
        au.run("calc.exe");
        // autoItCalculatorTest("Calculator");
        // autoItCopyPasteTest("old", "new");
    }


    private void autoItCalculatorTest(String calculatorAppName) throws InterruptedException {
        au.setOption("SendKeyDownDelay", "20");
        au.run("calc.exe");
        au.winWaitActive(calculatorAppName);
        Thread.sleep(700);

        au.send("96");
        Thread.sleep(700);

        au.send("-");
        Thread.sleep(700);

        au.send("27");
        Thread.sleep(700);

        au.send("{ENTER}", false);
        Thread.sleep(1700);

        log.info("69. nice!");
    }

    private void autoItCopyPasteTest(String fromWindowName, String toWindowName) {
        au.setOption("WinTitleMatchMode", "2");
        au.winActivate(fromWindowName);
        au.winWaitActive(fromWindowName, "", 5);
        au.send("{HOME}", false);
        au.send("+{END}", false);
        au.send("^c", false);
        au.winActivate(toWindowName);
        au.winWaitActive(toWindowName, "", 5);
        au.send("^v", false);
        au.send("{ENTER}", false);
        log.info(au.clipGet());
    }
}
