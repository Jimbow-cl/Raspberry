package com.crisis.esp32.service;

import com.crisis.esp32.dao.DatabaseCapteurDao;
import com.crisis.esp32.dao.model.Capteur;
import com.crisis.esp32.utils.Mqtt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.time.LocalDateTime;


/*--------------------------------------------------

- Esp32 est le coeur de la couche métier. C'est ici que nous appelons les méthodes qui permettent de gerer le fonctionnement de l'application.


--------------------------------------------------*/
@Service
// Annotation permettant la gestion des Logs
@Slf4j
public class Esp32Service {

    @Autowired
    private DatabaseCapteurDao databaseCapteurDao;
    @Autowired
    private Mqtt mqtt;
    private final int port;
    private final String mqttPwd;
    private final String host;

    private Esp32Service(
            @Value("${esp.server.port}") int port,
            @Value("${esp.server.mqttPwd}") String mqttPwd
    ) throws UnknownHostException {
        this.host = InetAddress.getLocalHost().getHostAddress();
        this.port = port;
        this.mqttPwd = mqttPwd;
        initCapteurSensor();
    }

    // Récuperation de la dernière entrée dans la BDD
    public String getLastEntry() {
        Capteur capteur = databaseCapteurDao.getLastEntry();
        try {
            return mqtt.encrypt(
                    String.format("%s-%s", capteur.getTemperature(), capteur.getHumidite()),
                    mqttPwd
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Initialisation de l'écoute de l'IP utilisé par  l'ESP32 pour envoyer les données
    private void initCapteurSensor() {
        InetSocketAddress sockAddr = new InetSocketAddress(host, port);

        //create a socket channel and bind to local bind address
        AsynchronousServerSocketChannel serverSock = null;
        try {
            serverSock = AsynchronousServerSocketChannel.open().bind(sockAddr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Accepter la connexion
        serverSock.accept(serverSock, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

            @Override
            public void completed(AsynchronousSocketChannel sockChannel, AsynchronousServerSocketChannel serverSock) {
                serverSock.accept(serverSock, this);
                log.info("Server ready to handle connection");
                //start to read message from the client
                startRead(sockChannel);

            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel serverSock) {
                System.out.println("fail to accept a connection");
            }

        });
    }

    private void startRead(AsynchronousSocketChannel sockChannel) {
        final ByteBuffer buf = ByteBuffer.allocate(20);

        //Lecture des informations reçues de l'ESP32
        sockChannel.read(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            /**
             * some message is read from client, this callback will be called
             */
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                buf.flip();
                String values = new String(buf.array()).trim();
                if (!values.isEmpty()) {
                    try {
                        //Déchiffrage du message
                        String decryptedValues = mqtt.decrypt(values, mqttPwd);
                        System.out.println(decryptedValues);
                        String[] explodedValues = decryptedValues.split("-");
                        Capteur newValues = new Capteur();
                        newValues.setHumidite(Long.parseLong(explodedValues[1]));
                        newValues.setTemperature(Long.parseLong(explodedValues[0]));
                        newValues.setTemps(LocalDateTime.now());
                        databaseCapteurDao.insertEntry(newValues);
                    } catch (Exception e) {
                        System.out.println("wrong password, failed to inject in database");
                    }

                }
                //Prêt à écouter le prochain message
                startRead(channel);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("fail to read message from client");
            }
        });
    }
}

