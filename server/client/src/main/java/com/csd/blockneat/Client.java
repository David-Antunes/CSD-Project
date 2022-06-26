package com.csd.blockneat;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.client.BlockNeatAPIClient;
import com.csd.blockneat.client.ECDSASignature;
import com.csd.blockneat.client.InternalUser;
import com.csd.blockneat.miner.Miner;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;

public class Client {
    public static final String KEY_STORE_PATH = "config/users.pkcs12";
    public static final String KEY_STORE_PASSWORD = "users";
    static ECDSASignature ec;

    static Miner miner;


    private static void changeUser(String filename, String password, String username, String userPassword) {
        try {
            ec = new ECDSASignature(ECDSASignature.fetchKeyPair(filename, password, username, userPassword));
            user = new InternalUser(username, ec);
            prompt = user.getUsername() + "> ";
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException | KeyStoreException |
                 CertificateException | UnrecoverableKeyException e) {
            System.exit(1);
        }
    }

    static InternalUser user;

    static String prompt;

    private static void handleCommand(String[] command, BlockNeatAPI bna)  {
        try {
            switch (command[0]) {
                case "exit" -> {}
                case "cu" -> System.out.println(bna.createUser());
                case "ca" -> System.out.println(bna.createAccount(command[1]));
                case "lm" -> System.out.println(bna.loadMoney(command[1], Integer.parseInt(command[2])));
                case "st" ->
                        System.out.println(bna.sendTransaction(command[1], command[2], Integer.parseInt(command[3])));
                case "gtv" -> {
                    List<String> accounts = new LinkedList<>(Arrays.asList(command).subList(1, command.length));
                    System.out.println(bna.getTotalValue(accounts));
                }
                case "gb" -> System.out.println(bna.getBalance(command[1]));
                case "ge" -> System.out.println(bna.getExtract(command[1]));
                case "chu" -> changeUser("config/users.pkcs12", "users", command[1], command[2]);
                case "ggv" -> System.out.println(bna.getGlobalValue());
                case "gl" -> System.out.println(bna.getLedger());
                case "help", "h" -> help();
                case "mine" -> mine();
                default -> System.out.println("Invalid command");
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid command arguments");
        }
    }

    private static void mine() {
        miner.toggle();
    }

    private static void help() {
        System.out.println("""
                           
                           ca <accountId> ---- create account
                           cu ---- create current user in the system
                           chu <userId> <password> ---- Change to another user
                           lm <accountId> <value> ---- Load Money
                           st <from_accountId> <to_accountId> <value> ---- Send Transaction
                           gb <accountId> ---- get Balance
                           ge <accountId> ---- get Extract
                           ggv ---- get global Value
                           gtv <accountId_1> <accountId_2> ... <accountId_N> ---- get total value
                           gl ---- get ledger
                           help
                           exit""");
    }
    public static void main(String[] args) {

        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        String url = "https://172.20.0.2:8443";
//        String url = "https://localhost:8080";

        changeUser(KEY_STORE_PATH, KEY_STORE_PASSWORD, "user1", "user1");

        BlockNeatAPI bna = new BlockNeatAPIClient(user, url);

        var minerUser = new InternalUser("user1", ec);
        miner = new Miner(new BlockNeatAPIClient(minerUser, url), minerUser);

        Scanner in = new Scanner(System.in);
        String[] command;
        do {
            System.out.print(prompt);
            command = in.nextLine().split(" ");
            handleCommand(command, bna);
        } while (!command[0].equals("exit"));
    }

}
