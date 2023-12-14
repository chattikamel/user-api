package io.mycompany.user.api;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

@Data
@Builder(toBuilder = true)
@Document("users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    String id;

    @NotBlank(message = "First name is mandatory")
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    String lastName;

    String nickName;

    @Positive(message = "Age should be positive")
    int age;

    @Email
    @NotBlank(message = "Email is mandatory")
    String email;

    @Builder.Default
    String gender = "n/a";

    @Builder.Default
    String country = "France";

    List<String> hobbies;

    @JsonIgnore
    public boolean isMajor() {
        return age > 18;
    }

    @JsonIgnore
    public boolean isFromFrance() {
        return equalsAnyIgnoreCase("France", country);
    }
}

