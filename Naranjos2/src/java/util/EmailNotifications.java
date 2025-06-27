/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


/**
 *
 * @author Administrator
 */
public class EmailNotifications implements Runnable {
    
    private final String host;
    private final String from;
    private final String[] to;
    private final String subject;
    private final String text;
    private final String pass;
    private final Map<String, String> attachmentsBase64; // Clave: nombre del archivo, Valor: contenido en base64

    public EmailNotifications(String host, String from, String[] to, String subject, String text, String pass, Map<String, String> attachmentsBase64) {
        this.host = host;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.pass = pass;
        this.attachmentsBase64 = attachmentsBase64;
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

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(text);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (attachmentsBase64 != null && !attachmentsBase64.isEmpty()) {
                for (Map.Entry<String, String> entry : attachmentsBase64.entrySet()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    
                    // Decodifica el contenido en base64
                    byte[] fileData = Base64.getDecoder().decode(entry.getValue());
                    
                    // Crea un DataSource a partir del array de bytes decodificado
                    ByteArrayDataSource dataSource = new ByteArrayDataSource(fileData, "application/octet-stream");
                    
                    // Configura el archivo adjunto
                    attachmentPart.setDataHandler(new DataHandler(dataSource));
                    attachmentPart.setFileName(entry.getKey());

                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    
}
