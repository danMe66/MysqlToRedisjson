package org.mysqltoredisjson.repositories;

import com.redis.om.spring.repository.RedisDocumentRepository;
import org.mysqltoredisjson.domain.Student;

public interface StudentRepository extends RedisDocumentRepository<Student, String> {
}
