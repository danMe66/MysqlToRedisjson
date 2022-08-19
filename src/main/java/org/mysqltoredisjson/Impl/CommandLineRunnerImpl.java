package org.mysqltoredisjson.Impl;

import lombok.extern.slf4j.Slf4j;
import org.mysqltoredisjson.classLoader.ClassUtil;
import org.mysqltoredisjson.domain.Company;
import org.mysqltoredisjson.domain.Person;
import org.mysqltoredisjson.repositories.CompanyRepository;
import org.mysqltoredisjson.repositories.PersonRepository;
import org.mysqltoredisjson.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

@Slf4j
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

//    @Autowired
//    CompanyRepository companyRepo;
//
//    @Autowired
//    PersonRepository personRepo;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public void run(String[] args) throws Exception {
//        companyRepo.deleteAll();
//        Company redis = Company.of("Redis", "https://redis.com", new Point(-122.066540, 37.377690), 526, 2011);
//        redis.setTags(Set.of("fast", "scalable", "reliable"));
//        Company microsoft = Company.of("Microsoft", "https://microsoft.com", new Point(-122.124500, 47.640160), 182268, 1975);
//        microsoft.setTags(Set.of("innovative", "reliable"));
//
//        companyRepo.save(redis);
//        companyRepo.save(redis); // save again to test @LastModifiedDate
//        companyRepo.save(microsoft);
//
//        personRepo.deleteAll();
//        personRepo.save(Person.of("Brian", "Sam-Bodden", "bsb@redis.com"));
//        personRepo.save(Person.of("Guy", "Royse", "guy.royse@redis.com"));
//        personRepo.save(Person.of("Guy", "Korland", "guy.korland@redis.com"));
//
//        Iterable<String> personIterable = personRepo.getIds();
//        log.info(personIterable.toString());

//        //测试实体类自动注入字段
//        studentRepository.save(Student.of("Brian", "Sam-Bodden", "bsb@redis.com"));
//        studentRepository.save(Student.of("Guy", "Royse", "guy.royse@redis.com"));
//        studentRepository.save(Student.of("Guy", "Korland", "guy.korland@redis.com"));


//        Iterable<Student> students = studentRepository.findAll();
//        log.info(students.toString());

        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:student.java.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String string;
        StringBuffer sb = new StringBuffer();
        while ((string = br.readLine()) != null) {
            if (string.startsWith("---")) {
                continue;
            }
            sb.append(string);
        }
        br.close();
        ClassUtil classUtil=new ClassUtil();
        classUtil.convertByJava(sb.toString(),"com.redis.om.spring.annotations.Document.Student","","");
    }
}
