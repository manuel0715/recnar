/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Administrator
 */
public class EmailSender implements Runnable {

    private String host;
    private String from;
    private String[] to;
    private String subject;
    private String text;
    private String pass;

    public EmailSender(String host, String from, String[] to, String subject, String text, String pass) {
        this.host = host;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.pass = pass;
    }

    @Override
    public void run() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));

            for (String recipient : to) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
