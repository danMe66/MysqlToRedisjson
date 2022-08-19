package org.mysqltoredisjson.repositories;

import com.redis.om.spring.repository.RedisDocumentRepository;
import org.mysqltoredisjson.domain.Person;

import java.util.List;

public interface PersonRepository extends RedisDocumentRepository<Person, String> {
  List<Person> findByLastNameAndFirstName(String lastName, String firstName);
}
