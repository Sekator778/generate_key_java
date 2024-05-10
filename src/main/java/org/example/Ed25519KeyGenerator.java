package org.example;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Ed25519KeyGenerator {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Кількість ядер процесора
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicBoolean found = new AtomicBoolean(false);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                Ed25519KeyPairGenerator keyGenerator = new Ed25519KeyPairGenerator();
                keyGenerator.init(new Ed25519KeyGenerationParameters(new SecureRandom()));

                while (!found.get()) {
                    AsymmetricCipherKeyPair keyPair = keyGenerator.generateKeyPair();
                    Ed25519PublicKeyParameters publicKeyParams = (Ed25519PublicKeyParameters) keyPair.getPublic();
                    Ed25519PrivateKeyParameters privateKeyParams = (Ed25519PrivateKeyParameters) keyPair.getPrivate();

                    byte[] publicKey = publicKeyParams.getEncoded();
                    byte[] privateKey = privateKeyParams.getEncoded();

                    String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey);
                    String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey);

                    if (publicKeyBase64.toLowerCase().startsWith("sekator")) {  // Перевірка з ігноруванням регістру
                        System.out.println("Public Key (Base64): " + publicKeyBase64);
                        System.out.println("Private Key (Base64): " + privateKeyBase64);
                        found.set(true);
                        break;
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
