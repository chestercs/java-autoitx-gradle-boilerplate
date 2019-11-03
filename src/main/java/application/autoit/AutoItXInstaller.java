package application.autoit;

import autoitx4java.AutoItX;
import com.jacob.com.LibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;


@Component
public class AutoItXInstaller {
    private static final Logger log = LoggerFactory.getLogger(AutoItXInstaller.class);
    private static final Boolean JVM_ARCHITECH_32 = System.getProperty("sun.arch.data.model").contains("32");
    private static final String REGSVR_SERVICE_64 = "/Windows/System32/regsvr32.exe";
    private static final String REGSVR_SERVICE_32 = "/Windows/SysWoW64/regsvr32.exe"; // TODO: Wire this
    private static final Function<Boolean, String> REGSVR_UNINSTALL_SWITCH = unintall -> unintall ? " -u" : "";
    private static final Function<Boolean, String> REGSVR_SILENT_SWITCH = unintall -> unintall ? " -s" : "";
    private static final String AUTOITX_DLL_TO_USE = JVM_ARCHITECH_32 ? "autoit/AutoItX3.dll" : "autoit/AutoItX3_x64.dll";
    private String errorCode = "-1";
    private Boolean skipInstall;
    private Boolean uninstall;
    private Boolean silent;
    private Runtime runtime = Runtime.getRuntime();
    private final Environment env;

    public AutoItXInstaller(Environment env) {
        this.skipInstall = env.getProperty("autoit.installation.skip", Boolean.class, false);
        this.uninstall = env.getProperty("autoit.installation.uninstall", Boolean.class, false);
        this.silent = env.getProperty("autoit.installation.silent", Boolean.class, true);
        this.env = env;
    }

    @Bean
    public AutoItX initAutoIt() throws IOException {
        String JACOB_DLL_TO_USE = JVM_ARCHITECH_32 ? "autoit/jacob-1.19-x86.dll" : "autoit/jacob-1.19-x64.dll";
        String JACOB_DLL_ABSOLUTE_PATH = ResourceUtils.getFile("classpath:" + JACOB_DLL_TO_USE).getAbsolutePath();
        System.setProperty(LibraryLoader.JACOB_DLL_PATH, JACOB_DLL_ABSOLUTE_PATH);
        AutoItX autoItX;

        if (uninstall) {
            installAutoItDll();
        }

        try {
            autoItX = new AutoItX();
        } catch (Exception e) {
            log.error("Failed to initialize Autoit: ", e.toString());
            try {
                installAutoItDll();
            } catch (IOException einstall) {
                log.error("Failed to install Autoit: ", einstall.toString());
            } finally {
                autoItX = new AutoItX();
            }
        }
        return autoItX;
    }

    private void installAutoItDll() throws IOException {
        if (skipInstall) {
            return; // Skip installation
        }

        log.info("Checking AutoIt installation");
        boolean installed = checkIsItInstalled().equals("checker_1");
        log.info("AutoIt already installed: " + installed);
        log.info(logger());

        if (uninstall && installed) {
            install();
            log.info("Autoit uninstalled.");
            System.exit(0);
        }
        if (uninstall && !installed) {
            log.info("Autoit already uninstalled.");
            System.exit(0);
        }
        if (!uninstall) {
            install();
            log.info("Autoit installed.");
        }
    }

    private String install() throws IOException {
        String command = REGSVR_SERVICE_64 +
                REGSVR_UNINSTALL_SWITCH.apply(uninstall) +
                REGSVR_SILENT_SWITCH.apply(silent) + " " +
                '"' + new ClassPathResource(AUTOITX_DLL_TO_USE).getFile().getAbsolutePath() + '"';
        try {
            Process process = runtime.exec(command);
            process.waitFor();
            errorCode = "install_" + process.exitValue();
            return errorCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    private String logger() {
        switch (errorCode) {
            case "install_0":
                return "AutoItX " +
                        (JVM_ARCHITECH_32 ? "32" : "64") + " bit version " +
                        "has been " +
                        (uninstall ? "uninstalled" : "installed") +
                        " - " + "Path: " + AUTOITX_DLL_TO_USE;
            case "install_5":
                return "Error - turn off silent mode";
            case "install_3":
                return "DLL missing from: " + AUTOITX_DLL_TO_USE;

            case "checker_0":
                return "AutoItX " +
                        (JVM_ARCHITECH_32 ? "32" : "64") + " bit version is already installed.";
            case "checker_1":
                return "AutoItX " +
                        (JVM_ARCHITECH_32 ? "32" : "64") + " bit version is not installed.";
            default:
                return "Error code: " + errorCode;
        }
    }

    private String checkIsItInstalled() { // TODO: BUGFIX | This method is not reliable..
        String command = "reg query HKLM\\SOFTWARE\\Classes /s /f " + AUTOITX_DLL_TO_USE;

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                log.info(s);
            }
            errorCode = "checker_" + process.exitValue();
            return errorCode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }
}

