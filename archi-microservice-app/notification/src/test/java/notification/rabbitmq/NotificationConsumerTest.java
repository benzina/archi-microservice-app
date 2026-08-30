package notification.rabbitmq;

import clients.notification.NotificationRequest;
import notification.NotificationService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationConsumerTest {
    @Test
    void consumerDelegatesRabbitMessageToNotificationService() {
        NotificationService notificationService = mock(NotificationService.class);
        NotificationConsumer consumer = new NotificationConsumer(notificationService);
        NotificationRequest request = new NotificationRequest(42, "ada@example.com", "Welcome");

        consumer.consumer(request);

        verify(notificationService).send(request);
    }
}
