package com.spay.wallet.account.utilities;

import com.spay.wallet.account.entities.AccountIdSequence;
import com.spay.wallet.account.repo.AccountIdSequenceRepository;
import com.spay.wallet.properies.WalletProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class AccountIdGeneratorTest {


    @Mock
    private AccountIdSequenceRepository repository;
    @Mock
    private WalletProperties walletProperties;

    @Mock
    private IdDate idDate;
    private AccountIdGenerator underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new AccountIdGenerator(idDate,repository,walletProperties);
    }


    @Test
    void itShouldGenerateId() {
        // Given
        given(repository.findAll()).willReturn(List.of());
        given(walletProperties.getAccountIdSuffix()).willReturn("SP");
        given(idDate.getCurrentDate()).willReturn(LocalDate.now().plusMonths(2));

        // When
        String id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo("42400001SP");

    }

    @Test
    void itShouldGenerateIdWhenDateChanged() {
        // Given
        given(repository.findAll()).willReturn(List.of(new AccountIdSequence(1l,213L, "42100213SP")));
        given(walletProperties.getAccountIdSuffix()).willReturn("SP");
        given(idDate.getCurrentDate()).willReturn(LocalDate.now().plusMonths(2));
        // When
        String id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo("42400001SP");

    }
//
    @Test
    void itShouldGenerateIdWhenMax999AndDateNotChanged() {
        // Given
        given(repository.findAll()).willReturn(List.of(new AccountIdSequence(1l,9999L, "42200213SP")));
        given(walletProperties.getAccountIdSuffix()).willReturn("SP");
        given(idDate.getCurrentDate()).willReturn(LocalDate.now());
        // When
        String id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo("422010000SP");

    }
}