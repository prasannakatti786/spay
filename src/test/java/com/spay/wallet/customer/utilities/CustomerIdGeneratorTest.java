package com.spay.wallet.customer.utilities;

import com.spay.wallet.customer.entities.CustomerIdSequence;
import com.spay.wallet.customer.repo.CustomerIdSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import static org.mockito.BDDMockito.given;


class CustomerIdGeneratorTest {



    @Mock
    private CustomerIdSequenceRepository repository;
    private CustomerIdGenerator underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerIdGenerator(repository);
    }


    @Test
    void itShouldGenerateId() {
        // Given
        given(repository.findAll()).willReturn(List.of());
        // When
        Long id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo(2401290001L);

    }

    @Test
    void itShouldGenerateIdWhenMax9999() {
        // Given
        given(repository.findAll()).willReturn(List.of(new CustomerIdSequence(0L,9999L)));
        // When
        Long id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo(2401290001L);

    }

    @Test
    void itShouldGenerateIdWhenMax998() {
        // Given
        given(repository.findAll()).willReturn(List.of(new CustomerIdSequence(0L,9998L)));
        // When
        Long id = underTest.generateId();
        // Then
        assertThat(id).isEqualTo(2401299999L);

    }
}