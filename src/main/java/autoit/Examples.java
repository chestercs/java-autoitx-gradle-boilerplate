package autoit;

import autoitx4java.AutoItX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class Examples {

    private final AutoItX au;
    private static final Logger log = LoggerFactory.getLogger(Examples.class);

    public Examples(AutoItX au) throws InterruptedException, IOException {
        this.au = au;
        main();
    }


    // Hint:
    // You can turn off autoit.skip.dll.installation.scan in application.properties,
    // After first run. So it will skip scanning autoit installation
    private void main() throws InterruptedException, IOException {
        calculatorTest("Calculator");
        // copyPasteTest("old", "new");
    }


    private void calculatorTest(String calculatorAppName) throws InterruptedException {
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

    private void copyPasteTest(String fromWindowName, String toWindowName) {
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