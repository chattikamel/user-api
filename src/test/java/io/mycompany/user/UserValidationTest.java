package io.mycompany.user;

import io.mycompany.user.api.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.engine.path.PathImpl.createPathFromString;

public class UserValidationTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private User user = User.builder()
            .firstName("toto")
            .lastName("tata")
            .age(40)
            .email("toto.tata@gmail.com")
            .build();



    @Test
    void whenUserIsCreatedWithoutFirstName_then_theValidation_should_fail() {
        User userToValidate = user.toBuilder()
                .firstName("")
                .build();

        verifyOneConstraint(validator.validate(userToValidate),
                createPathFromString("firstName"));
    }

    @Test
    void whenUserIsCreatedWithoutLastName_then_theValidation_should_fail() {
        User userToValidate = user.toBuilder()
                .lastName("")
                .build();

        verifyOneConstraint(validator.validate(userToValidate),
                createPathFromString("lastName"));
    }

    @Test
    void whenUserIsCreatedWithoutAge_then_theValidation_should_fail() {
        User userToValidate = user.toBuilder()
                .age(0)
                .build();

        verifyOneConstraint(validator.validate(userToValidate),
                createPathFromString("age"));
    }

    @Test
    void whenUserIsCreatedWithInvalidEmail_then_theValidation_should_fail() {
        User userToValidate = user.toBuilder()
                .email("ajhqkjhdgmail.Com")
                .build();

        verifyOneConstraint(validator.validate(userToValidate),
                createPathFromString("email"));
    }

    @Test
    void when_validUserIsCreated_then_theValidation_should_succeed() {
        User userToValidate = user.toBuilder()
                .build();

        assertThat(validator.validate(userToValidate))
                .isEmpty();
    }

    private  void verifyOneConstraint(Set<ConstraintViolation<User>> validate, PathImpl firstName) {
        assertThat(validate).isNotEmpty();
        Optional<ConstraintViolation<User>> constraintViolation = validate.stream().findFirst();
        assertThat(constraintViolation).isNotEmpty();
        assertThat(constraintViolation.get().getPropertyPath()).isEqualTo(firstName);
    }
}
