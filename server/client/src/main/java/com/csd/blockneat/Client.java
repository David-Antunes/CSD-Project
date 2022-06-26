package com.csd.blockneat;

import com.csd.blockneat.client.BlockNeatAPI;
import com.csd.blockneat.client.BlockNeatAPIClient;
import com.csd.blockneat.client.ECDSASignature;
import com.csd.blockneat.client.InternalUser;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static final String KEY_STORE_PATH = "config/users.pkcs12";
    public static final String KEY_STORE_PASSWORD = "users";
    static ECDSASignature ec;


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
            case "":
            case "exit":
                break;
            case "cu":
                System.out.println(bna.createUser());
                break;
            case "ca":
                System.out.println(bna.createAccount(command[1]));
                break;
            case "lm":
                System.out.println(bna.loadMoney(command[1], Integer.parseInt(command[2])));
                break;
            case "st":
                System.out.println(bna.sendTransaction(command[1], command[2], Integer.parseInt(command[3])));
                break;
            case "gtv":
                List<String> accounts = new LinkedList<>(Arrays.asList(command).subList(1, command.length));
                System.out.println(bna.getTotalValue(accounts));
                break;
            case "gb":
                System.out.println(bna.getBalance(command[1]));
                break;
            case "ge":
                System.out.println(bna.getExtract(command[1]));
                break;
            case "chu":
                changeUser("config/users.pkcs12", "users", command[1], command[2]);
                break;
            case "ggv":
                System.out.println(bna.getGlobalValue());
                break;
            case "gl":
                System.out.println(bna.getLedger());
            case "help":
            case "h":
                help();
                break;
            default:
                System.out.println("Invalid command");
        }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Invalid command arguments");
        }
    }
    private static void help() {
        System.out.println("\nca <accountId> ---- create account");
        System.out.println("cu ---- create current user in the system");
        System.out.println("chu <userId> <password> ---- Change to another user");
        System.out.println("lm <accountId> <value> ---- Load Money");
        System.out.println("st <from_accountId> <to_accountId> <value> ---- Send Transaction");
        System.out.println("gb <accountId> ---- get Balance");
        System.out.println("ge <accountId> ---- get Extract");
        System.out.println("ggv ---- get global Value");
        System.out.println("gtv <accountId_1> <accountId_2> ... <accountId_N> ---- get total value");
        System.out.println("gl ---- get ledger");
        System.out.println("help");
        System.out.println("exit");
    }
    public static void main(String[] args) {

        System.getProperties().setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
        String url = "https://172.20.0.2:8443";
//        String url = "https://localhost:8080";

        changeUser(KEY_STORE_PATH, KEY_STORE_PASSWORD, "user1", "user1");

        BlockNeatAPI bna = new BlockNeatAPIClient(user, url);

        Scanner in = new Scanner(System.in);
        String[] command;
        do {
            System.out.print(prompt);
            command = in.nextLine().split(" ");
            handleCommand(command, bna);
        } while (!command[0].equals("exit"));
    }

}
