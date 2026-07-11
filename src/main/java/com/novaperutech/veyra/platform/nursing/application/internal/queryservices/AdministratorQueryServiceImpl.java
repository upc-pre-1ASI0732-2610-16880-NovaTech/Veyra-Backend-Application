package com.novaperutech.veyra.platform.nursing.application.internal.queryservices;

import com.novaperutech.veyra.platform.nursing.domain.model.aggregates.Administrator;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAdministratorByIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.queries.GetAdministratorByUserIdQuery;
import com.novaperutech.veyra.platform.nursing.domain.model.valueobjects.UserId;
import com.novaperutech.veyra.platform.nursing.domain.services.AdministratorQueryService;
import com.novaperutech.veyra.platform.nursing.infrastructure.persistence.jpa.repositories.AdministratorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AdministratorQueryServiceImpl implements AdministratorQueryService {
    private final AdministratorRepository administratorRepository;

    public AdministratorQueryServiceImpl(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }


    @Override
    public Optional<Administrator> handle(GetAdministratorByIdQuery query) {
        return administratorRepository.findById(query.id());
    }

    @Override
    public Optional<Administrator> handle(GetAdministratorByUserIdQuery query) {
        return administratorRepository.findByUserId(new UserId(query.userId()));
    }
}
