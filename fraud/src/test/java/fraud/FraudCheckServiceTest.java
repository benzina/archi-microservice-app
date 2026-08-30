package fraud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FraudCheckServiceTest {
    @Mock
    private FraudCheckHistoryRepository fraudCheckHistoryRepository;
    @InjectMocks
    private FraudCheckService fraudCheckService;

    @Test
    void isFraudulentCustomerPersistsHistoryAndReturnsFalseForDemoRules() {
        boolean result = fraudCheckService.isFraudulentCustomer(42);

        assertThat(result).isFalse();
        ArgumentCaptor<FraudCheckHistory> captor = ArgumentCaptor.forClass(FraudCheckHistory.class);
        verify(fraudCheckHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getCustomerId()).isEqualTo(42);
        assertThat(captor.getValue().isFraudster()).isFalse();
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }
}
