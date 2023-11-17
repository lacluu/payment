package lac.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill extends BaseEntity {

    private Long userId;
    private String type;
    private Long amount;
    private Timestamp dueDate;
    private String stage;
    private String provider;


}
