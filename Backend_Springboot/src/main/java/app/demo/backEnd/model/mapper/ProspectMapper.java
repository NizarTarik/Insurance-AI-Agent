package app.demo.backEnd.model.mapper;

import app.demo.backEnd.model.dto.ProspectDTO.CreationRequest;
import app.demo.backEnd.model.entity.Prospect;
import org.springframework.stereotype.Component;

@Component
public class ProspectMapper {

    public Prospect toEntity(CreationRequest request) {

        return Prospect.builder()
                .prenom(request.getFirstName())
                .nom(request.getLastName())
                .telephone(request.getPhone())
                .build();
    }
}