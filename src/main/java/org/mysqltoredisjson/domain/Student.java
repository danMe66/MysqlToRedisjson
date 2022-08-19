package org.mysqltoredisjson.domain;

import com.redis.om.spring.annotations.Document;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document
public class Student {
}
