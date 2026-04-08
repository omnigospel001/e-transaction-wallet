//package com.etransaction.tests.services;
//
//import com.etransaction.entity.User;
//import com.etransaction.request.TransferRequest;
//import com.etransaction.request.UserRequest;
//import com.etransaction.service.AuthService;
//import com.etransaction.service.TransferService;
//import com.etransaction.tests.base.BaseTest;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//public class TransferServiceTest extends BaseTest {
//
//    @Autowired
//    private TransferService transferService;
//
//    @Autowired
//    private AuthService authService;
//
//    private User user1;
//    private User user2;
//    private User user3;
//    private User user4;
//    private User user5;
//
//    @Test
//    public void testAccountManagementService() {
//        //when(transferService.makePayment(any(),  any(), any())).thenThrow(IllegalAccessException.class);
//        String idempotencyKey = UUID.randomUUID().toString();
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user1, null, user1.getAuthorities());
//        BigDecimal transferAmount = new BigDecimal("1000000");
//
//        TransferRequest transferRequest = TransferRequest
//                .builder()
//                .amount(transferAmount)
//                .accountNumber()
//                .remark("Transfer for shoes")
//                .build();
//        var result = transferService.makePayment(authentication, idempotencyKey, transferRequest);
//
//        assertNotNull(result);
//        assertEquals(result.firstName(), user1.getFirstName());
//    }
//
//    @BeforeEach
//    public void setUp() {
//        UserRequest userRequest1 = UserRequest.builder()
//                .firstName("Daniel")
//                .lastName("Okafor")
//                .email("daniel.okafor001@gmail.com")
//                .password("Password@123")
//                .phone("08031234567")
//                .build();
//
//        UserRequest userRequest2 = UserRequest.builder()
//                .firstName("Chinedu")
//                .lastName("Eze")
//                .email("chinedu.eze002@gmail.com")
//                .password("SecurePass@456")
//                .phone("08142345678")
//                .build();
//
//        UserRequest userRequest3 = UserRequest.builder()
//                .firstName("Aisha")
//                .lastName("Bello")
//                .email("aisha.bello003@gmail.com")
//                .password("StrongPass@789")
//                .phone("07053456789")
//                .build();
//
//        UserRequest userRequest4 = UserRequest.builder()
//                .firstName("Emeka")
//                .lastName("Umeh")
//                .email("emeka.umeh004@gmail.com")
//                .password("MyPass@321")
//                .phone("09064567890")
//                .build();
//
//        UserRequest userRequest5 = UserRequest.builder()
//                .firstName("Zainab")
//                .lastName("Sule")
//                .email("zainab.sule005@gmail.com")
//                .password("SafePass@654")
//                .phone("08075678901")
//                .build();
//        user1 = authService.register(userRequest1);
//        user2 = authService.register(userRequest2);
//        user3 = authService.register(userRequest3);
//        user4 = authService.register(userRequest4);
//        user5 = authService.register(userRequest5);
//
//    }
//
//}
