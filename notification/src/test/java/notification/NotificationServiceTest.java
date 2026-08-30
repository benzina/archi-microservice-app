package notification;

import clients.notification.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendPersistsNotificationWithCustomerEmail() {
        notificationService.send(new NotificationRequest(42, "ada@example.com", "Welcome"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getToCustomerId()).isEqualTo(42);
        assertThat(captor.getValue().getToCustomerEmail()).isEqualTo("ada@example.com");
        assertThat(captor.getValue().getMessage()).isEqualTo("Welcome");
        assertThat(captor.getValue().getSentAt()).isNotNull();
    }
}
