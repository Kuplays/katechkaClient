package com.example.client;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.client.RestTemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@EnableCircuitBreaker
@RestController
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    @Autowired
    private Chat chat;
    private String userName;

    @Bean
    public RestTemplate rest(RestTemplateBuilder builder) throws  Exception {
        return builder.build();
    }

    @RequestMapping("/chat")
    public String getData() {
        String messages[] = chat.getMessages().split(",");
        for (int i = 0; i < messages.length; i++) {
            System.out.println(messages[i]);
        }
        return chat.getMessages();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setHeadless(false);
        app.run(args);
    }


    @Override
    public void run(String... args) throws Exception {
        JFrame nameInput = new JFrame("ENTER YOUR NAME");
        userName = JOptionPane.showInputDialog(nameInput, "Provide user name");

        JFrame frame = new JFrame("TEST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(null);
        JTextArea tf = new JTextArea("MESSAGES WILL BE HERE SHORTLY...");
        tf.setBounds(0, 0, 500, 200);
        tf.setLineWrap(true);
        tf.setWrapStyleWord(true);
        JButton btn = new JButton("SEND");
        JLabel label = new JLabel("TYPE YOUR MESSAGE");
        label.setBounds(0, 200, 300, 25);
        JTextArea inputMessage = new JTextArea();
        inputMessage.setToolTipText("TYPE MESSAGE...");
        inputMessage.setBounds(0, 225, 300, 150);
        inputMessage.setLineWrap(true);
        inputMessage.setWrapStyleWord(true);
        btn.setBounds(300, 200, 200, 200);

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                if (userName == null) userName = "default";
                                String name = "Shrek " + userName;
                                String message = inputMessage.getText();
                                try {
                                    message = URLEncoder.encode(message, "utf-8");
                                } catch (UnsupportedEncodingException ex) {
                                    ex.printStackTrace();
                                }
                                chat.sendMessage(name, message);
                            }
                        });
                    }
                }).start();
            }
        });
        rootPanel.add(tf);
        rootPanel.add(label);
        rootPanel.add(inputMessage);
        rootPanel.add(btn);
        frame.setContentPane(rootPanel);
        frame.setVisible(true);

        class SwingNonBlocker extends SwingWorker<String, Object> {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    while(true) {
                        Thread.currentThread().sleep(1000);
                        String incomingText = chat.getMessages();
                        if (!incomingText.equals("SERVERS ARE UNAVAILABLE, TRY LATER...")) {
                            incomingText = incomingText.substring(1, incomingText.length() - 1);
                            ArrayList<String> messages = new ArrayList<String>(Arrays.asList(incomingText.split(",")));
                            tf.setText("");
                            for (String message : messages) {
                                tf.append(message);
                                tf.append("\n");
                            }
                        }

                        else tf.setText(incomingText);
                    }

                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }

                return chat.getMessages();
            }
        }
        new SwingNonBlocker().execute();
        //new ChatForm();
        //System.out.println(chat.getMessages());
//        int x = 0;
//        Runnable r = new InputListenNIO();
//        Runnable r2 = new MessagesReaderNIO();
//        Thread t2 = new Thread(r2);
//        Thread t = new Thread(r);
//        t.start();
//        t2.start();


        //System.out.println(s);
    }
}
