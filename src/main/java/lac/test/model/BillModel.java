package lac.test.model;

import lac.test.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillModel {

    private Long id;

    @NotBlank
    private Long userId;

    @NotBlank
    private String type;

    @Min(1)
    @NotNull
    private Long amount;

    @NotNull
    private Timestamp dueDate;

    private String stage;

    private String provider;

    public Bill mapToBill() {
        Bill bill = new Bill(userId, type, amount, dueDate, stage, provider);
        bill.setId(id);

        return bill;
    }
}
