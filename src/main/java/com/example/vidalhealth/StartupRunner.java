package com.example.vidalhealth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.vidalhealth.dto.FinalQueryRequest;
import com.example.vidalhealth.dto.WebhookRequest;
import com.example.vidalhealth.dto.WebhookResponse;

@Component
public class StartupRunner implements CommandLineRunner {

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void run(String... args) {

		System.out.println("Application Started...");

		String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

		WebhookRequest request = new WebhookRequest();
		request.setName("Sophiya");
		request.setRegNo("4VM21IS040");
		request.setEmail("sophiyafathima02@gmail.com");

		WebhookResponse response = restTemplate.postForObject(generateUrl, request, WebhookResponse.class);

		if (response == null) {
			System.out.println("Error in response");
			return;
		}

		String accessToken = response.getAccessToken();

		String finalQuery = "SELECT d.DEPARTMENT_NAME, ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE, GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) SEPARATOR ', ') AS EMPLOYEE_LIST FROM DEPARTMENT d JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID WHERE p.AMOUNT > 70000 GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME ORDER BY d.DEPARTMENT_ID DESC;";

		String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", accessToken);

		FinalQueryRequest finalRequest = new FinalQueryRequest(finalQuery);

		HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(finalRequest, headers);

		ResponseEntity<String> result = restTemplate.exchange(submitUrl, HttpMethod.POST, entity, String.class);

		System.out.println("Submission Response:");
		System.out.println(result.getBody());
	}
}
