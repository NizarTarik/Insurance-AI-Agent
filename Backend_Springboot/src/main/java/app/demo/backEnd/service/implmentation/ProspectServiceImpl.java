
package app.demo.backEnd.service.implmentation;

import app.demo.backEnd.model.dto.ProspectDTO.CreationRequest;
import app.demo.backEnd.model.entity.Prospect;
import app.demo.backEnd.model.mapper.ProspectMapper;
import app.demo.backEnd.repository.ProspectRepository;
import app.demo.backEnd.service.EmailService;
import app.demo.backEnd.service.ProspectService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProspectServiceImpl implements ProspectService {

    private final ProspectRepository prospectRepository;
    private final ProspectMapper prospectMapper;
    private final EmailService emailService;

    @Override
    public void create(CreationRequest request) {

        Prospect prospect = prospectMapper.toEntity(request);

        prospectRepository.save(prospect);

        // SUCCESS
        emailService.sendNewChatBotUseEmail(
                request.getFirstName(),
                request.getLastName(),
                request.getPhone());
    }

    @Override
    public void markAsImported(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return;

        List<Prospect> prospects = prospectRepository.findAllById(ids);

        for (Prospect p : prospects) {
            p.setIsSMImported(true);
        }

        prospectRepository.saveAll(prospects);
    }
}
