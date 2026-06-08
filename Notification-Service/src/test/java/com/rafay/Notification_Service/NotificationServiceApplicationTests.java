package com.rafay.Notification_Service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"AWS_ACCESS_KEY=test-access-key",
		"AWS_SECRET_KEY=test-secret-key",
		"AWS_SES_SENDER=test@example.com"
})
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
