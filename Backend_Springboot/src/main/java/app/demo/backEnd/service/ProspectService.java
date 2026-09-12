package app.demo.backEnd.service;

import java.util.List;

import app.demo.backEnd.model.dto.ProspectDTO.CreationRequest;

public interface ProspectService {

    void create(CreationRequest request);

    void markAsImported(List<Long> ids);

}