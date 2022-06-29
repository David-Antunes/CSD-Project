package com.csd.blockneat.workload;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.client.BlockNeatAPIClient;
import com.csd.blockneat.client.InternalUser;
import com.csd.blockneat.utils.ECDSASignature;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Fill {

    public static final int ACCOUNT_NUMBER = 5;
    public static final String ACCOUNT_ID = "account";
    public static final int TRANSACTION_LOAD = 100;

    public static void fill(String endpoint, String filename, String password, String userId, int userNumber) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidKeyException, InterruptedException {
        List<BlockNeatAPI> users = LoadUsers(endpoint,filename,password,userId,userNumber);

        //Create Users
        for (int i = 0; i < userNumber; i++) {
            users.get(i).createUser();
        }

        //Create Accounts With Money
        for (int i = 0; i < userNumber; i++) {
            for (int j = 0; j < ACCOUNT_NUMBER; j++) {
                System.out.println(users.get(i).createAccount(ACCOUNT_ID + i + j));
                System.out.println(users.get(i).loadMoney(ACCOUNT_ID + i + j, 100000));
            }
        }

//        // Generate Random Transactions
//        for(int i = 0; i < TRANSACTION_LOAD; i++) {
//            int randomUser = ThreadLocalRandom.current().nextInt(0, userNumber);
//            int randomAccount = ThreadLocalRandom.current().nextInt(0, ACCOUNT_NUMBER);
//            int randomUserDestination = ThreadLocalRandom.current().nextInt(0, userNumber);
//            int randomAccountDestination = ThreadLocalRandom.current().nextInt(0, ACCOUNT_NUMBER);
//            int value = ThreadLocalRandom.current().nextInt(0, 100);
//            System.out.println(users.get(randomUser).sendTransaction(ACCOUNT_ID + randomUser + randomAccount, ACCOUNT_ID + randomUserDestination + randomAccountDestination, value));
//        }
        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < userNumber; i++) {
            int finalI = i;
            threads.add(new Thread() {
                public void run() {
                    for (int j = 0; j < TRANSACTION_LOAD / userNumber; j++) {
                        int randomAccount = ThreadLocalRandom.current().nextInt(0, ACCOUNT_NUMBER);
                        int randomUserDestination = ThreadLocalRandom.current().nextInt(0, userNumber);
                        int randomAccountDestination = ThreadLocalRandom.current().nextInt(0, ACCOUNT_NUMBER);
                        int value = ThreadLocalRandom.current().nextInt(0, 100);
                        try {
                            System.out.println(users.get(finalI).sendTransaction(ACCOUNT_ID + finalI + randomAccount, ACCOUNT_ID + randomUserDestination + randomAccountDestination, value));
                        } catch (SignatureException | InterruptedException | IOException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }

        for (Thread thread : threads)
            thread.start();
        for (Thread thread : threads)
            thread.join();

        System.out.println("FILL DONE!");
    }

    public static List<BlockNeatAPI> LoadUsers(String endpoint, String filename, String password, String userId, int userNumber) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        List<BlockNeatAPI> users = new ArrayList<>(userNumber);
        List<String> accounts = new ArrayList<>(ACCOUNT_NUMBER * userNumber);
        //Load users to a list
        for (int i = 0; i < userNumber; i++) {
            ECDSASignature ec = new ECDSASignature(ECDSASignature.fetchKeyPair(filename, password, userId + i, userId + i));
            InternalUser user = new InternalUser(userId + i, ec);
            users.add(new BlockNeatAPIClient(user, endpoint));
        }
        return users;
    }

    public static void preLoadBlockNeat(List<BlockNeatAPI> users,int userNumber) throws IOException, SignatureException, InvalidKeyException, InterruptedException {
        //Create Users
        for (int i = 0; i < userNumber; i++) {
            users.get(i).createUser();
        }

        //Create Accounts With Money
        for (int i = 0; i < userNumber; i++) {
            for (int j = 0; j < ACCOUNT_NUMBER; j++) {
                users.get(i).createAccount(ACCOUNT_ID + i + j);
                users.get(i).loadMoney(ACCOUNT_ID + i + j, 100000);
            }
        }
    }
}
