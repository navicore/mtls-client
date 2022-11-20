package tech.navicore.mtlsclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@SpringBootApplication
@Configuration
public class MtlsClientApplication {

    private static final Logger log = LoggerFactory.getLogger(MtlsClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MtlsClientApplication.class, args);
    }

    @Value("${remote-base-url}")
    private String remoteBaseUrl;

    @Value("${key-password}")
    private String keyPassword;

    @Value("${key-store-password}")
    private String keyStorePassword;

    @Value("${trust-store-password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate()
            throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException, KeyManagementException, UnrecoverableKeyException {

        URL keyStoreURL = Thread.currentThread().getContextClassLoader()
                .getResource("client.p12");

        URL truststoreURL = Thread.currentThread().getContextClassLoader()
                .getResource("client-truststore.p12");

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(truststoreURL, trustStorePassword.toCharArray())
                .loadKeyMaterial(keyStoreURL, keyStorePassword.toCharArray(), keyPassword.toCharArray())
                .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            String src = "fred";
            if (args.length > 0)
                src = args[0];
            String query = String.format("%s/%s", remoteBaseUrl, src);
            Name name = restTemplate.getForObject(query, Name.class);
            log.info("" + name);
            ObjectMapper mapper = new ObjectMapper();
            String indentedString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(name);
            System.out.printf("\n%s\n%n", indentedString);
            System.exit(0);
        };
    }

}
