package app.demo.backEnd.model.dto.ProspectDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreationRequest {

    private String firstName;
    private String lastName;
    private String phone;
}