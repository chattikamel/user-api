package io.mycompany.user.api;


import io.mycompany.user.persistence.MongoUserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.status;
import static reactor.core.publisher.Mono.just;

@RestController
@RequestMapping("/user")
public class UserApi {

    @Autowired
    MongoUserRepository userRepository;


    @ResponseBody
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<User> getUsers() {
        return userRepository.findAll();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> addUser(@RequestBody @Valid User user) {
        return just(user)
                .filter(_user -> _user.isMajor() && _user.isFromFrance())
                .flatMap(userRepository::save)
                .map(res -> status(CREATED)
                        .body(format("User created %s", res.id)))
                .switchIfEmpty(just(status(UNAUTHORIZED)
                        .body("Only major and one that lives in france ")
                ));
    }


    @ResponseBody
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUser(@PathVariable String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
    }

}


