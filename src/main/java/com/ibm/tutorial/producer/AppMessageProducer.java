package com.ibm.tutorial.producer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.developer.confluent.json_model.JobDetailsPayload;

@Component
public class AppMessageProducer {
	
	@Value("${message.topic.name}")
	private String topicName;
	
	private final KafkaTemplate<String, JobDetailsPayload> jobDetailsPayloadTemplate;

	private String jobDetailsPayload_JSON;
	JobDetailsPayload jobDetailsPayload = new JobDetailsPayload();
	
	private Log log = LogFactory.getLog(AppMessageProducer.class);
	
	//constructor injection
	public AppMessageProducer(KafkaTemplate<String, JobDetailsPayload> jobDetailsPayloadTemplate) {
		this.jobDetailsPayloadTemplate = jobDetailsPayloadTemplate;
	}


	
	@EventListener(ApplicationStartedEvent.class)
	public void pushJsonData() {
		try {
			populateJobDetailsPayload();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		String messageType = "PRIVATE";
		String messageName = topicName;
		String pattern = "JSON";
		String version = "v1";
		String source = "rtest_dgw_rule_engine";

		jobDetailsPayload.setMessageType(messageType);
		jobDetailsPayload.setMessageName(messageName);
		jobDetailsPayload.setPattern(pattern);
		jobDetailsPayload.setVersion(version);
		jobDetailsPayload.setSource(source);
		
		log.debug("Generating the kafkaTemplate call..");
		Random random = new Random();
		try {
			while (true) {
				int nextInt = 100000000 + random.nextInt(99999999);
				String correlationId = Integer.toString(nextInt);
				jobDetailsPayload.setCorrelationId(correlationId);
				LocalDateTime now = LocalDateTime.now();
				Timestamp currentDateTime = Timestamp.valueOf(now);
				String messageId = currentDateTime.toString() + ":"
						+ Optional.ofNullable(jobDetailsPayload.getCorrelationId()).orElse("");
				jobDetailsPayload.setMessageId(messageId);
				jobDetailsPayload.setMessageTime(currentDateTime);
				
				log.info("Pushing message to Topic : correlation id: "+ correlationId);
				jobDetailsPayloadTemplate.send(topicName, correlationId, jobDetailsPayload);
				
				try {
					Thread.sleep(3000);
					log.info("Successfully pushed message to Topic");
				} catch (InterruptedException e) {

				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}


	private void populateJobDetailsPayload() throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		File fileJobDetails = new ClassPathResource("json/payload_data/JobDetails.json").getFile();
		if (fileJobDetails != null) {
			String absolutePath = fileJobDetails.getAbsolutePath();
			System.out.println("Before setup() :" + absolutePath);
			// Read File Content
			jobDetailsPayload_JSON = new String(Files.readAllBytes(fileJobDetails.toPath()));

			jobDetailsPayload = objectMapper.readValue(jobDetailsPayload_JSON, JobDetailsPayload.class);
			if (jobDetailsPayload != null) {
				System.out.println("JobDetailsPayload converted");
			}
		}
	}
	

}