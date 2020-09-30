package models.vault;

import crypto.Crypto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class VaultTest
{
    static Vault testVault;

    static long startime, elapsed_average = 0, elapsed_sum = 0;

    static FileHandler output;
    private final static Logger logger = Logger.getLogger(VaultTest.class.getName());
    private final static Crypto crypto = Crypto.getInstance();


    static {
        logger.setLevel(Level.INFO);

        try {
            output = new FileHandler("src/test/java/models/vault/log.xml");
            logger.addHandler(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        crypto.deriveKeys("Test".toCharArray());
    }

    void createTestVault(String tag, String algo, String mac) {
        VaultHeaderInfo info = new VaultHeaderInfo(
            "test" + tag,
            "wwww.test.example.com" + tag
        );

        ArrayList<String> options = new ArrayList<>();
        options.add("test: This is a test option" + tag);

        VaultBody body = new VaultBody(
            "Test Individual" + tag,
            "test@example.com" + tag,
            "Testtest_1234" + tag,
            options
        );

        try {
            testVault = Vault.generateInstance(
                algo,
                mac,
                info,
                body
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void decrypt() throws Exception {
        testVault.decryptBody();

        VaultBody body = testVault.getVaultBody();
    }

    // Time functions
    void time(String tag) throws Exception {
        startime = System.currentTimeMillis();
        createTestVault(tag, "AES-256-GCM", null);
        decrypt();

        long elapsed = (System.currentTimeMillis() - startime);
        elapsed_sum += elapsed;
    }

    void timeN(int n) throws Exception {
        for (int i = 0; i < n; i++)
            time( " " + i + "longpaddingstring");

        elapsed_average = elapsed_sum/n;
        logger.info("Elapsed time in millisecond, for n = " + n + " is: " + elapsed_average + " average and " + elapsed_sum + " total.");
    }

    // Tests
    @Test
    void timeAll() throws Exception {
        timeN(10);
        timeN(5);
    }

    @Test
    void useAll() throws Exception {
        createTestVault("", "AES-256-GCM", null);
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());

        createTestVault("", "Camellia-256-GCM", null);
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());

        createTestVault("", "ChaCha20-256-MAC", "HS-256");
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());
        System.out.println(testVault.getVaultBody().getPassword());

        createTestVault("", "AES-256-CBC", "HS-256");
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());

        createTestVault("", "Camellia-256-CBC", "HS-256");
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());

        createTestVault("", "Threefish-256-CBC", "HS-256");
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());

        createTestVault("", "Serpent-256-CBC", "HS-256");
        testVault.clearPlaintext();
        decrypt();
        Assertions.assertEquals("Testtest_1234", testVault.getVaultBody().getPassword());
    }
}