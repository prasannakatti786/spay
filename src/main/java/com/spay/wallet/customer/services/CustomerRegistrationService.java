package com.spay.wallet.customer.services;

import com.spay.wallet.account.services.AccountCreationService;
import com.spay.wallet.credentials.*;
import com.spay.wallet.credentials.jwt.JwtUtility;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.entities.CustomerLevel;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.customer.reqRes.CustomerRegistrationRequest;
import com.spay.wallet.customer.reqRes.CustomerRegistrationResponse;
import com.spay.wallet.customer.reqRes.CustomerUpdateDetailsRequest;
import com.spay.wallet.customer.utilities.CustomerIdGenerator;
import com.spay.wallet.customer.utilities.FileStore;
import com.spay.wallet.exections.ApiException;
import com.spay.wallet.messages.MessageChannel;
import com.spay.wallet.messages.MessagePayload;
import com.spay.wallet.messages.MessageSenderImplementer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerRegistrationService {
    private final CustomerRepository customerRepository;
    private final CredentialRepository credentialRepository;
    private final CustomerIdGenerator customerIdGenerator;
    private final MessageSenderImplementer messageSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final AccountCreationService accountCreationService;
    private final FileStore fileStore;
    private String subject = "OTP";
    private String message = " is your verification code. for your security, do not share this code.";

    public CustomerRegistrationResponse registerNewCustomer(CustomerRegistrationRequest request){
        CustomerRegistrationResponse customer = checkCustomerIsExistingOrGuestType(request);
        if (customer != null) return customer;
        var customerLevel = CustomerLevel.GUEST_CUSTOMER;
        var userType =  UserType.CUSTOMER;
        var credential = createCustomerAccount(request, userType, customerLevel);
        credential.setEnabledTwoFactorAuth(Boolean.TRUE);
        generateOtpAndSave(credential);
        return new CustomerRegistrationResponse("Otp number has bean sent to your phone number and email", Long.parseLong(credential.getUserId()));
    }

    public CustomerRegistrationResponse registerNewAgent(CustomerRegistrationRequest request){
        var customerOptional = customerRepository.selectCustomerByPhoneNumberOrEmail(request.phoneNumber(), request.email());
        if (customerOptional.isPresent())
            throw new ApiException("Phone number or email is already exist",HttpStatus.NOT_ACCEPTABLE);
        var customerLevel = CustomerLevel.GUEST_CUSTOMER;
        var userType =  UserType.AGENT;
        var credential = createCustomerAccount(request, userType, customerLevel);
        return new CustomerRegistrationResponse("Agent Account is created successfully", Long.parseLong(credential.getUserId()));
    }

    public CustomerRegistrationResponse completeAgentRegistration(CustomerRegistrationRequest request){
        var agent = customerRepository.selectCustomerByPhoneNumberOrEmail(request.phoneNumber(), request.email())
                .orElseThrow(() -> new ApiException("This agent account is not exist",HttpStatus.NOT_ACCEPTABLE));
        var credential =  agent.getCredential();
        if(!credential.getUserType().equals(UserType.AGENT))
            throw new ApiException("Agents only accepted",HttpStatus.NOT_ACCEPTABLE);
        if(!agent.getCustomerLevel().equals(CustomerLevel.GUEST_CUSTOMER))
            throw new ApiException("Agent account creation is already completed",HttpStatus.NOT_ACCEPTABLE);
        generateOtpAndSave(credential);
        return new CustomerRegistrationResponse("Otp number has bean sent to your phone number and email",  Long.parseLong(credential.getUserId()));
    }

    private CustomerRegistrationResponse checkCustomerIsExistingOrGuestType(CustomerRegistrationRequest request) {
        var customerUserType = customerRepository.selectCustomerByPhoneNumberOrEmail(request.phoneNumber(), request.email());
        if(customerUserType.isPresent()) {
            var customer = customerUserType.get();
            if (customer.getCustomerLevel().equals(CustomerLevel.GUEST_CUSTOMER)) {
                var credential =  customer.getCredential();
                if(!credential.getUserType().equals(UserType.CUSTOMER))
                    throw new ApiException("Customers only accepted",HttpStatus.NOT_ACCEPTABLE);
                generateOtpAndSave(credential);
                return new CustomerRegistrationResponse("Otp number has bean sent to you phone number and email", customer.getId());
            }
        }
        return null;
    }

    private Credential createCustomerAccount(CustomerRegistrationRequest request, UserType userType, CustomerLevel customerLevel) {
        validatePhoneNumberAndEmailExists(request);
        var id  = customerIdGenerator.generateId();
        Credential credential =  Credential.builder()
                .accountExpiresAt(LocalDateTime.now().plusMonths(3))
                .countFailedLoginAttempts(0)
                .isLocked(Boolean.FALSE)
                .isEnabled(Boolean.TRUE)
                .userId(id.toString()).usernameEmail(request.email()).usernamePhone(request.phoneNumber()).userType(userType).build();
        Customer customer  = Customer.builder()
                .id(id).firstName(StringUtils.capitalize(request.firstName().trim()))
                .middleName(StringUtils.capitalize(request.middleName()).trim())
                .lastName(StringUtils.capitalize(request.lastName()).trim()).country(request.country())
                .customerLevel(customerLevel)
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .countryCode(request.countryCode()).phoneNumber(request.phoneNumber())
                .email(request.email()).credential(credential).joinAt(LocalDateTime.now()).build();
        customerRepository.saveAndFlush(customer);
        return  credential;
    }


    public void uploadProfileImage(String customerId, MultipartFile file){
        if (file == null)
            throw new ApiException("Cannot upload empty profile",HttpStatus.NOT_ACCEPTABLE);
        var customer = getCustomerById(Long.parseLong(customerId));
        var oldImage = customer.getProfilePath();
        var fileName =UUID.randomUUID().toString().concat(".jpg");
        customer.setProfilePath(fileName);
        var resizedProfile = fileStore.resizeImage(file);
        fileStore.storeFile(fileName, resizedProfile);
        customerRepository.save(customer);
        fileStore.deleteFiles(oldImage);
    }






    public void completeCustomerVerification(){
        // TODO complete customer verification
    }


    public CustomerRegistrationResponse validateOtp(String emailOtp, String phoneOtp, Long customerId){
        var customer  = getCustomerById(customerId);
        var credential =  customer.getCredential();
        validateEmailOtp(credential,emailOtp);
//        validatePhoneOtp(credential,phoneOtp);
        return new CustomerRegistrationResponse("Otp is validated successfully, please create password", customer.getId());
    }

    public void validateEmailOtp(Credential credential, String emailOtp){
        if(credential.getOtpExpireDateEmail().isBefore(LocalDateTime.now()))
            throw new ApiException("Email otp is expired",HttpStatus.NOT_ACCEPTABLE);
        if(!emailOtp.equals(credential.getOtpEmail()))
            throw new ApiException("Email otp is incorrect",HttpStatus.NOT_ACCEPTABLE);
    }

    public void validatePhoneOtp(Credential credential, String phoneOtp){
        if(credential.getOtpExpireDatePhone().isBefore(LocalDateTime.now()))
            throw new ApiException("Phone number otp is expired",HttpStatus.NOT_ACCEPTABLE);
        if(!phoneOtp.equals(credential.getOtpPhone()))
            throw new ApiException("Phone number otp is incorrect",HttpStatus.NOT_ACCEPTABLE);
    }

    public void resendOtpByEmail(Long customerId){
        var customer  = getCustomerById(customerId);
        var credential =  customer.getCredential();
        if(credential.getOtpEmail() == null) {
            log.warn("Customer sending email otp without previous otp {}", customer);
            throw new ApiException("Cannot resend email otp because theres not previous otp",HttpStatus.NOT_ACCEPTABLE);
        }
        var emailOtp = CredentialUtil.createOTP(6);
        saveAndSendEmailOtp(credential, emailOtp,credential.getOtpEmail());
    }



    public void resendOtpByPhone(Long customerId){
        var customer  = getCustomerById(customerId);
        var credential = customer.getCredential();
        if(credential.getOtpPhone() == null) {
            log.warn("Customer sending phone otp without previous otp {}", customer);
            throw new ApiException("Cannot resend phone otp because theres not previous otp",HttpStatus.NOT_ACCEPTABLE);
        }
        var phoneOtp = CredentialUtil.createOTP(6);
        credential.setOtpPhone(phoneOtp);
        credential.setOtpExpireDatePhone(LocalDateTime.now().plusMinutes(10));
        credentialRepository.save(credential);
        var messagePayload = MessagePayload.builder().message(phoneOtp+message)
                .sendTo(credential.getUsernameEmail())
                .subject(subject).channel(MessageChannel.SMS).build();
        messageSender.sendMessage(messagePayload);
    }

    public CustomerRegistrationResponse forgotPasswordByEmailOrPhoneNumber(String emailOrPhone){
        var optionalCustomer = customerRepository.findByEmailAndPhoneNumber(emailOrPhone);
        if(optionalCustomer.isEmpty())
            throw new ApiException("The email or phone is not exist",HttpStatus.NOT_ACCEPTABLE);
        var customer  =  optionalCustomer.get();
        var credential = customer.getCredential();
        if(credential.getIsLocked())
            throw new ApiException("Account is been locked",HttpStatus.NOT_ACCEPTABLE);
        generateOtpAndSave(credential);
        return new CustomerRegistrationResponse("Otp number has bean sent to your phone number and email: "+customer.getPhoneNumber()+","+customer.getEmail(), customer.getId());
    }

    public TokenResponse createPassword(Long customerId, String password, String otpEmail, String otpPhone){
        validateOtp(otpEmail,otpPhone,customerId);
        var customer =  getCustomerById(customerId);
        if(CustomerLevel.GUEST_CUSTOMER.equals(customer.getCustomerLevel()))
            customer.setCustomerLevel(CustomerLevel.PARTIAL_CUSTOMER);
        var credential =  customer.getCredential();
        if(credential.getPassword() != null && passwordEncoder.matches(password,credential.getPassword()))
            throw new ApiException("This new password same to old password please change it",HttpStatus.NOT_ACCEPTABLE);
        var sessionId = CredentialUtil.createOTP(20);
        credential.setOtpPhone(null);
        credential.setOtpEmail(null);
        credential.setOtpExpireDatePhone(LocalDateTime.now());
        credential.setOtpExpireDateEmail(LocalDateTime.now());
        credential.setPassword(passwordEncoder.encode(password));
        credential.setCredentialExpireAt(LocalDateTime.now().plusDays(72));
        credential.setSessionId(sessionId);
        customer.setCredential(credential);
        customerRepository.saveAndFlush(customer);
        var jwt =   jwtUtility.generateJWT(credential.getUserType(),sessionId,credential.getId(),customerId.toString());
        var lastName=customer.getLastName() ==null?"":customer.getLastName();
        if(credential.getUserType().equals(UserType.CUSTOMER))
             accountCreationService.createMainAccount(customerId.toString(), String.format("%s %s %s", customer.getFirstName(),customer.getMiddleName(),lastName));
        if (credential.getUserType().equals(UserType.AGENT))
            accountCreationService.createAgentAccount(customerId.toString(), String.format("%s %s %s", customer.getFirstName(),customer.getMiddleName(),lastName));


        return jwt;
    }



    private Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new ApiException("Customer does not exist", HttpStatus.NOT_FOUND));
    }

    private void validatePhoneNumberAndEmailExists(CustomerRegistrationRequest request) {
        validateEmailExists(request.email());
        validatePhoneNumberExists(request.phoneNumber());
    }

    private void validatePhoneNumberExists(String phoneNumber) {
        if(customerRepository.existsByPhoneNumber(phoneNumber))
            throw new ApiException("Phone number is taken", HttpStatus.NOT_ACCEPTABLE);
    }

    private void validateEmailExists(String email) {
        if(customerRepository.existsByEmail(email))
            throw new ApiException("Email is taken", HttpStatus.NOT_ACCEPTABLE);
    }


    public void generateOtpAndSave(Credential credential){
        generateSaveAndSendPhoneOtp(credential);
        generateSaveAndSendEmailOtp(credential);
    }

    public void generateSaveAndSendPhoneOtp(Credential credential){
        var phoneOtp = CredentialUtil.createOTP(6);
        saveAndSendPhoneOtp(credential, phoneOtp,credential.getUsernamePhone());
    }

    private void saveAndSendPhoneOtp(Credential credential, String phoneOtp, String phone) {
        credential.setOtpPhone(phoneOtp);
        credential.setOtpExpireDatePhone(LocalDateTime.now().plusMinutes(10));
        credentialRepository.save(credential);
        var phoneMessage = MessagePayload.builder().message(phoneOtp +message)
                .sendTo(phone)
                .subject(subject).channel(MessageChannel.SMS).build();
        messageSender.sendMessage(phoneMessage);
    }

    public void generateSaveAndSendEmailOtp(Credential credential){
        var emailOtp = CredentialUtil.createOTP(6);
        saveAndSendEmailOtp(credential, emailOtp, credential.getUsernameEmail());
    }

    private void saveAndSendEmailOtp(Credential credential, String emailOtp, String email) {
        credential.setOtpEmail(emailOtp);
        credential.setOtpExpireDateEmail(LocalDateTime.now().plusMinutes(10));
        credentialRepository.save(credential);
        var emailMessage = MessagePayload.builder().message(emailOtp +message)
                .sendTo(email)
                .subject(subject).channel(MessageChannel.EMAIL).build();
        messageSender.sendMessage(emailMessage);
    }

    public Map<String, Object> updateCustomerDetails(Long id, CustomerUpdateDetailsRequest request) {
        boolean sendOtpPhoneChange=false, sendOtpEmailChange=false;
        var customer =  getCustomerById(id);
        if(request.getPhoneNumber() !=  null && !customer.getPhoneNumber().equals(request.getPhoneNumber())) {
            sendOtpPhoneChange = true;
            validatePhoneNumberExists(request.getPhoneNumber().trim());
            customer.setUnConfirmedPhoneNumber(request.getPhoneNumber().trim());
        }
        if(request.getEmail() !=  null && !customer.getEmail().equals(request.getEmail())) {
            sendOtpEmailChange =  true;
            validateEmailExists(request.getEmail().trim());
            customer.setUnConfirmedEmail(request.getEmail().trim());
        }
        if(customer.getCustomerLevel().equals(CustomerLevel.FULL_CUSTOMER)) {
            var credential =  customer.getCredential();
            customerRepository.save(customer);
            if (sendOtpEmailChange){
                generateSaveAndSendEmailOtp(credential);
            }
            if(sendOtpPhoneChange){
//            generateSaveAndSendPhoneOtp(credential);
            }
            return Map.of("message","Account level is full customer you cannot change details except email or phone number", "emailOtp", sendOtpEmailChange,"phoneOtp", sendOtpPhoneChange);
        }
        customer.setFirstName(checkValue(customer.getFirstName(),request.getFirstName()));
        customer.setMiddleName(checkValue(customer.getMiddleName(),request.getMiddleName()));
        customer.setLastName(checkValue(customer.getLastName(),request.getLastName()));
        customer.setGender(request.getGender() == null? customer.getGender(): request.getGender());
        customer.setAddress(checkValue(customer.getAddress(), request.getAddress()));
        customer.setCity(checkValue(customer.getCity(), request.getCity()));
        customer.setCountry(checkValue(customer.getCountry(), request.getCountry()));
        customer.setPostCode(checkValue(customer.getPostCode(), request.getPostCode()));
        customer.setCountryCode(checkValue(customer.getCountryCode(),request.getCountryCode()));
        customer.setDocumentType(request.getDocumentType() == null? customer.getDocumentType():request.getDocumentType());
        customer.setDocumentNumber(request.getDocumentNumber() == null? customer.getDocumentNumber():request.getDocumentNumber());
        var credential =  customer.getCredential();
        customerRepository.save(customer);
        if (sendOtpEmailChange){
            generateSaveAndSendEmailOtp(credential);
        }
        if(sendOtpPhoneChange){
//            generateSaveAndSendPhoneOtp(credential);
        }
        return Map.of("message","Profile details has been change successfully", "emailOtp", sendOtpEmailChange,"phoneOtp", sendOtpPhoneChange);
    }

    public Map<String, Object> verifyPhoneNumberOrEmailUpdate(Long id, String otp, String otpEmail){
        validateOtpRequest(otp, otpEmail);
        boolean sendOtpPhoneChange=false, sendOtpEmailChange=false;
        var customer =  getCustomerById(id);
        var credential =  customer.getCredential();

        if(!StringUtils.isEmpty(otpEmail)){
            sendOtpEmailChange = true;
            validateEmailOtp(credential,otpEmail);
            validateEmailExists(customer.getUnConfirmedEmail());
            var newOtp = CredentialUtil.createOTP(6);
            saveAndSendEmailOtp(credential,newOtp,customer.getUnConfirmedEmail());
        }
        if(!StringUtils.isEmpty(otp)){
//            sendOtpPhoneChange=true;
//            validatePhoneOtp(credential,otp);
//            validatePhoneNumberExists(customer.getUnConfirmedPhoneNumber());
//            var newOtp = CredentialUtil.createOTP(6);
//            saveAndSendPhoneOtp(credential,newOtp,customer.getUnConfirmedPhoneNumber());
        }

        customer.setIsConformedEmailOrPhoneChanges(Boolean.TRUE);
        customer.setCredential(credential);
        customerRepository.save(customer);
        return Map.of("message","We have sent an otp", "emailOtp", sendOtpEmailChange,"phoneOtp", sendOtpPhoneChange);
    }

    public void verifyPhoneNumberOrEmailUpdateComplete(Long id, String otp, String otpEmail){
        validateOtpRequest(otp, otpEmail);
        var customer =  getCustomerById(id);
        if(customer.getIsConformedEmailOrPhoneChanges() == null || !customer.getIsConformedEmailOrPhoneChanges())
            throw new ApiException("Change is not confirmed please confirm your changes",HttpStatus.NOT_ACCEPTABLE);
        var credential =  customer.getCredential();
        if(!StringUtils.isEmpty(otpEmail)){
            validateEmailOtp(credential,otpEmail);
            validateEmailExists(customer.getUnConfirmedEmail().trim());
            customer.setEmail(customer.getUnConfirmedEmail().trim());
            credential.setUsernameEmail(customer.getUnConfirmedEmail().trim());
            credential.setOtpEmail(null);
            credential.setOtpExpireDateEmail(LocalDateTime.now());
            customer.setUnConfirmedEmail(null);
        }
        if(!StringUtils.isEmpty(otp)){
//            validatePhoneOtp(credential,otp);
//            validatePhoneNumberExists(customer.getUnConfirmedPhoneNumber().trim());
//            customer.setPhoneNumber(customer.getUnConfirmedPhoneNumber().trim());
//            credential.setUsernamePhone(customer.getUnConfirmedPhoneNumber().trim());
//            credential.setOtpPhone(null);
//            credential.setOtpExpireDatePhone(LocalDateTime.now());
//            customer.setUnConfirmedPhoneNumber(null);
        }
        customer.setIsConformedEmailOrPhoneChanges(Boolean.FALSE);
        customer.setCredential(credential);
        customerRepository.save(customer);
    }

    private static void validateOtpRequest(String otp, String otpEmail) {
        if(StringUtils.isEmpty(otpEmail) && StringUtils.isEmpty(otp))
            throw new ApiException("Phone otp or email otp is required",HttpStatus.BAD_REQUEST);
    }

    private String checkValue(String old,String newValue){
        if(StringUtils.isEmpty(newValue)) return old;
        return newValue.trim();
    }

    public Boolean enableOrDisableTwoFactorAuth(Long id) {
        var customer = getCustomerById(id);
        var credential = customer.getCredential();
        credential.setEnabledTwoFactorAuth(credential.getEnabledTwoFactorAuth() == null?Boolean.TRUE:!credential.getEnabledTwoFactorAuth());
        credentialRepository.save(credential);
        return credential.getEnabledTwoFactorAuth();
    }


    public void changeMessageChannelOtpChannel(Long id, MessageChannel messageChannel) {
        var customer = getCustomerById(id);
        var credential = customer.getCredential();
        credential.setMessageChannel(messageChannel);
        credentialRepository.save(credential);
    }
}
