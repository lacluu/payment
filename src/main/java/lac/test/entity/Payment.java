package lac.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    private Long userId;
    private Long billId;
    private Long amountPayment;
    private String stage;
}
