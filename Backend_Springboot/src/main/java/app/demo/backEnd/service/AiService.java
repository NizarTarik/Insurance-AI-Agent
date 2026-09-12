package app.demo.backEnd.service;

import app.demo.backEnd.model.dto.InsuranceIntent;

public interface AiService {

        InsuranceIntent ask(String message);
}
