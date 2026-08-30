package customer;

import amqp.RabbitMQMessageProducer;
import clients.fraud.FraudCheckResponse;
import clients.fraud.FraudClient;
import clients.notification.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private FraudClient fraudClient;
    @Mock
    private RabbitMQMessageProducer rabbitMQMessageProducer;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void registerCustomerSavesCustomerChecksFraudAndPublishesNotification() {
        ReflectionTestUtils.setField(customerService, "internalExchange", "internal.exchange");
        ReflectionTestUtils.setField(customerService, "internalNotificationRoutingKey", "internal.notification.routing-key");
        doAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(42);
            return customer;
        }).when(customerRepository).saveAndFlush(any(Customer.class));
        when(fraudClient.isFraudster(42)).thenReturn(new FraudCheckResponse(false));

        customerService.registerCustomer(new CustomerRegistrationRequest("Ada", "Lovelace", "ada@example.com"));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).saveAndFlush(customerCaptor.capture());
        assertThat(customerCaptor.getValue().getEmail()).isEqualTo("ada@example.com");

        ArgumentCaptor<NotificationRequest> notificationCaptor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(rabbitMQMessageProducer).publish(
                notificationCaptor.capture(),
                org.mockito.ArgumentMatchers.eq("internal.exchange"),
                org.mockito.ArgumentMatchers.eq("internal.notification.routing-key")
        );
        assertThat(notificationCaptor.getValue().toCustomerId()).isEqualTo(42);
        assertThat(notificationCaptor.getValue().toCustomerEmail()).isEqualTo("ada@example.com");
    }

    @Test
    void registerCustomerDoesNotPublishNotificationWhenCustomerIsFraudulent() {
        doAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(99);
            return customer;
        }).when(customerRepository).saveAndFlush(any(Customer.class));
        when(fraudClient.isFraudster(99)).thenReturn(new FraudCheckResponse(true));

        assertThatThrownBy(() -> customerService.registerCustomer(
                new CustomerRegistrationRequest("Grace", "Hopper", "grace@example.com")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("fraudster");

        verify(rabbitMQMessageProducer, never()).publish(any(), any(), any());
    }
}
