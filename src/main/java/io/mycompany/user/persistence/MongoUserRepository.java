package io.mycompany.user.persistence;

import io.mycompany.user.api.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MongoUserRepository extends ReactiveMongoRepository<User, String> {
}
