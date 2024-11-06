package com.spay.wallet.customer.services;

import com.spay.wallet.account.services.AccountCreationService;
import com.spay.wallet.credentials.Credential;
import com.spay.wallet.credentials.CredentialRepository;
import com.spay.wallet.credentials.TokenResponse;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.credentials.jwt.JwtUtility;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.customer.reqRes.CustomerRegistrationRequest;
import com.spay.wallet.customer.reqRes.CustomerRegistrationResponse;
import com.spay.wallet.customer.utilities.CustomerIdGenerator;
import com.spay.wallet.customer.utilities.FileStore;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.messages.MessageSenderImplementer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class CustomerRegistrationServiceTest {
    @Mock
    private  CustomerRepository customerRepository;
    @Mock
    private  CredentialRepository credentialRepository;
    @Mock
    private  CustomerIdGenerator customerIdGenerator;

    @Mock
    private JwtUtility jwtUtility;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSenderImplementer messageSenderImplementer;

    private CustomerRegistrationService underTest;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Credential> credentialArgumentCaptor;

    @Mock
    private AccountCreationService accountCreationService;

    @Mock
    private FileStore fileStore;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest =  new CustomerRegistrationService(customerRepository,credentialRepository,customerIdGenerator,messageSenderImplementer,passwordEncoder,jwtUtility, accountCreationService,fileStore);
    }


    @Test
    void itShouldRegisterNewCustomer() {
        // Given
        CustomerRegistrationRequest request =  new CustomerRegistrationRequest(
                "Ali",
                "Edle",
                "Mahamuud",
                "Somalia",
                "SO",
                "+251615111111",
                "ali@gmail.com",
                null,
                null
        );
        given(customerRepository.selectCustomerByPhoneNumberOrEmail("","")).willReturn(Optional.empty());
        given(customerRepository.existsByEmail(null)).willReturn(Boolean.FALSE);
        given(customerRepository.existsByPhoneNumber(null)).willReturn(Boolean.FALSE);
        given(customerIdGenerator.generateId()).willReturn(21391232L);
        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().saveAndFlush(customerArgumentCaptor.capture());
        var customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue.getId()).isEqualTo(21391232L);
    }

    @Test
    void itShouldNotSaveNewCustomerIfExistWithGuest() {
        // Given
        CustomerRegistrationRequest request =  new CustomerRegistrationRequest(
                "Ali",
                "Edle",
                "Mahamuud",
                "Somalia",
                "SO",
                "+251615111111",
                "ali@gmail.com",
                null,
                null
        );

       var customer =  Customer.builder()
                .id(234123L)
                .credential(Credential.builder().userType(UserType.CUSTOMER).build())
                .build();
        given(customerRepository.selectCustomerByPhoneNumberOrEmail(any(),any()))
                .willReturn(Optional.of(customer));
        // When
        // Then
        assertThat(underTest.registerNewCustomer(request)).satisfies(customerRegistrationResponse -> {
            assertThat(customerRegistrationResponse).isEqualToComparingFieldByField(new CustomerRegistrationResponse("Otp number has bean sent to you phone number and email",234123L));
        });
        then(customerRepository).should(never()).saveAndFlush(any());
        then(customerIdGenerator).should(never()).generateId();;
    }

    @Test
    void itShouldNotSaveNewCustomerIfEmail() {
        // Given
        CustomerRegistrationRequest request =  new CustomerRegistrationRequest(
                "Ali",
                "Edle",
                "Mahamuud",
                "Somalia",
                "SO",
                "+251615111111",
                "ali@gmail.com",
                null,
                null
        );
        given(customerRepository.selectCustomerByPhoneNumberOrEmail(any(),any())).willReturn(Optional.empty());
        given(customerRepository.existsByEmail(any())).willReturn(Boolean.TRUE);
        given(customerRepository.existsByPhoneNumber(any())).willReturn(Boolean.TRUE);
        // When
        // Then
        assertThatThrownBy(()->underTest.registerNewCustomer(request))
                .isInstanceOf(ApiException.class)
                        .hasMessageContaining("Email is taken");
        then(customerRepository).should(never()).saveAndFlush(any());
        then(customerIdGenerator).should(never()).generateId();;
    }

    @Test
    void itShouldNotSaveNewCustomerIfPhone() {
        // Given
        CustomerRegistrationRequest request =  new CustomerRegistrationRequest(
                "Ali",
                "Edle",
                "Mahamuud",
                "Somalia",
                "SO",
                "+251615111111",
                "ali@gmail.com",
                null,
                null
        );
        given(customerRepository.selectCustomerByPhoneNumberOrEmail(any(),any())).willReturn(Optional.empty());
        given(customerRepository.existsByEmail(any())).willReturn(Boolean.FALSE);
        given(customerRepository.existsByPhoneNumber(any())).willReturn(Boolean.TRUE);
        // When
        // Then
        assertThatThrownBy(()->underTest.registerNewCustomer(request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Phone number is taken");
        then(customerRepository).should(never()).saveAndFlush(any());
        then(customerIdGenerator).should(never()).generateId();;
    }

    @Test
    void itShouldValidateOtp() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317232")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().plusMinutes(10))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        // Then
        assertThatCode(() -> {
            underTest.validateOtp("131323","317232",any());
        }).doesNotThrowAnyException();

    }

    @Test
    void itShouldValidateEmailOptIsWrong() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131324")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().plusMinutes(10))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        // Then
        assertThatThrownBy(() -> {
            underTest.validateOtp("131323","317232",any());
        }).isInstanceOf(ApiException.class)
                .hasMessageContaining("Email otp is incorrect");

    }


    @Test
    void itShouldValidatePhoneOtpIsWrong() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().plusMinutes(10))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        // Then
        assertThatThrownBy(() -> {
            underTest.validateOtp("131323","317232",any());
        }).isInstanceOf(ApiException.class)
                .hasMessageContaining("Phone number otp is incorrect");

    }


    @Test
    void itShouldValidateEmailOtpExpired() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().minusSeconds(2))
                .otpExpireDatePhone(LocalDateTime.now().plusMinutes(10))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        // Then
        assertThatThrownBy(() -> {
            underTest.validateOtp("131323","317232",any());
        }).isInstanceOf(ApiException.class)
                .hasMessageContaining("Email otp is expired");

    }

    @Test
    void itShouldValidatePhoneOtpExpired() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().minusSeconds(1))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        // Then
        assertThatThrownBy(() -> {
            underTest.validateOtp("131323","317232",any());
        }).isInstanceOf(ApiException.class)
                .hasMessageContaining("Phone number otp is expired");

    }


    @Test
    void itShouldResendOtpByEmail() {
        // Given
        Credential credential =  Credential.builder()
                .otpEmail("131323").build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        underTest.resendOtpByEmail(any());
        // Then
        then(credentialRepository).should().save(credentialArgumentCaptor.capture());
        Credential credentialArgumentCaptorValue = credentialArgumentCaptor.getValue();
        assertThat(credentialArgumentCaptorValue.getOtpEmail()).isNotBlank();
    }

    @Test
    void itShouldResendOtpByEmailWithNoPreviousOtp() {
        // Given
        Credential credential =  Credential.builder().build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        assertThatThrownBy(() -> underTest.resendOtpByEmail(any())).isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot resend email otp because theres not previous otp");

    }

    @Test
    void itShouldResendOtpByPhone() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("131323").build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        underTest.resendOtpByPhone(any());
        // Then
        then(credentialRepository).should().save(credentialArgumentCaptor.capture());
        Credential credentialArgumentCaptorValue = credentialArgumentCaptor.getValue();
        assertThat(credentialArgumentCaptorValue.getOtpPhone()).isNotBlank();

    }

    @Test
    void itShouldResendOtpByPhoneWithNoPreviousOtp() {
        // Given
        Credential credential =  Credential.builder().build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        // When
        assertThatThrownBy(() -> underTest.resendOtpByPhone(any())).isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot resend phone otp because theres not previous otp");

    }


    @Test
    void itShouldForgotPasswordByEmailOrPhoneNumber() {
        // Given
        String emailOrPhone = "abdirahman@gmail.com";
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().minusSeconds(1))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        given(customerRepository.findByEmailAndPhoneNumber(emailOrPhone)).willReturn(Optional.of(customer));
        // When
        underTest.forgotPasswordByEmailOrPhoneNumber(emailOrPhone);
        // Then
        then(credentialRepository).should().save(credentialArgumentCaptor.capture());
        Credential credentialArgumentCaptorValue = credentialArgumentCaptor.getValue();
        assertThat(credentialArgumentCaptorValue).satisfies(credential1 -> {
            assertThat(credentialArgumentCaptorValue.getOtpEmail()).isNotBlank();
            assertThat(credentialArgumentCaptorValue.getOtpPhone()).isNotBlank();
            assertThat(credentialArgumentCaptorValue.getOtpExpireDatePhone()).isAfter(LocalDateTime.now());
            assertThat(credentialArgumentCaptorValue.getOtpExpireDateEmail()).isAfter(LocalDateTime.now());
        });

    }

    @Test
    void itShouldForgotPasswordByEmailOrPhoneNumberThrowExceptionWhenCustomerNotFound() {
        // Given
        given(customerRepository.findByEmailAndPhoneNumber(any())).willReturn(Optional.empty());
        // When
        assertThatThrownBy(() -> underTest.forgotPasswordByEmailOrPhoneNumber(any())).isInstanceOf(ApiException.class)
                .hasMessageContaining("The email or phone is not exist");

    }

    @Test
    void itShouldCreatePassword() {
        // Given
        Credential credential =  Credential.builder()
                .otpPhone("317233")
                .otpEmail("131323")
                .otpExpireDateEmail(LocalDateTime.now().plusMinutes(10))
                .otpExpireDatePhone(LocalDateTime.now().plusMinutes(10))
                .build();
        Customer customer =  Customer.builder().credential(credential).build();
        CustomerRegistrationService underTest2 = spy(underTest);
        doReturn(new CustomerRegistrationResponse("",2L)).when(underTest2).validateOtp(any(),any(),any());
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
//        given(jwtUtility.generateJWT(any(),any(), any(),any())).willReturn(new TokenResponse("dnabd","bearer"));
        // When
        var token =  underTest2.createPassword(0L,"abcd1234","131323","317233");
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Credential credentialArgumentCaptorValue = customerArgumentCaptor.getValue().getCredential();
        assertThat(token.accessToken()).isNotBlank();
        assertThat(token.tokenType()).isEqualTo("bearer");
        assertThat(credentialArgumentCaptorValue).satisfies(credential1 -> {
            assertThat(credential1.getOtpEmail()).isNull();
            assertThat(credential1.getOtpPhone()).isNull();
            assertThat(credential1.getOtpExpireDateEmail()).isBefore(LocalDateTime.now());
            assertThat(credential1.getOtpExpireDatePhone()).isBefore(LocalDateTime.now());

        });


    }
}