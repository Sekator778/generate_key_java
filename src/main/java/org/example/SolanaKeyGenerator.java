package org.example;

import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SolanaKeyGenerator {
    public static void main(String[] args) throws InterruptedException {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Кількість ядер процесора
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicBoolean found = new AtomicBoolean(false);

        long startTime = System.nanoTime(); // Початок вимірювання часу

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

                    String publicKeyBase58 = Base58.encode(publicKey);
                    String privateKeyBase58 = Base58.encode(privateKey);

                    if (publicKeyBase58.startsWith("ivan")) {
                        System.out.println("Solana Public Key (Base58): " + publicKeyBase58);
                        System.out.println("Solana Private Key (Base58): " + privateKeyBase58);
                        found.set(true);
                        break;
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        long endTime = System.nanoTime(); // Кінець вимірювання часу
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime); // Виміряний час в мілісекундах
        System.out.println("Elapsed time: " + duration + " ms");
    }
}
