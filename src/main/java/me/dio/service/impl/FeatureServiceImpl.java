package me.dio.service.impl;

import me.dio.domain.model.Feature;
import me.dio.domain.repository.FeatureRepository;
import me.dio.service.FeatureService;
import me.dio.service.exception.BusinessException;
import me.dio.service.exception.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {

    /**
     * ID de feature utilizado para preservar integridade de dados base.
     */
    private static final Long UNCHANGEABLE_FEATURE_ID = 1L;

    private final FeatureRepository featureRepository;

    private final FeatureService featureService;

    public FeatureServiceImpl(FeatureRepository featureRepository, @Lazy FeatureService featureService) {
        this.featureRepository = featureRepository;
        this.featureService = featureService;
    }

    @Transactional(readOnly = true)
    public List<Feature> findAll() {
        return this.featureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Feature findById(Long id) {
        return this.featureRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Feature create(Feature featureToCreate) {
        if (featureToCreate == null) {
            throw new BusinessException("Feature to create must not be null.");
        }

        this.validateChangeableId(featureToCreate.getId(), "created");

        // Exemplo de validação adicional: evitar duplicidade de descrição
        if (featureRepository.existsByDescription(featureToCreate.getDescription())) {
            throw new BusinessException("This feature description already exists.");
        }

        return this.featureRepository.save(featureToCreate);
    }

    @Transactional
    public Feature update(Long id, Feature featureToUpdate) {
        this.validateChangeableId(id, "updated");

        Feature dbFeature = featureService.findById(id);
        if (!dbFeature.getId().equals(featureToUpdate.getId())) {
            throw new BusinessException("Update IDs must be the same.");
        }

        dbFeature.setIcon(featureToUpdate.getIcon());
        dbFeature.setDescription(featureToUpdate.getDescription());

        return this.featureRepository.save(dbFeature);
    }

    @Transactional
    public void delete(Long id) {
        this.validateChangeableId(id, "deleted");

        Feature dbFeature = featureService.findById(id);
        this.featureRepository.delete(dbFeature);
    }

    private void validateChangeableId(Long id, String operation) {
        if (UNCHANGEABLE_FEATURE_ID.equals(id)) {
            throw new BusinessException("Feature with ID %d can not be %s.".formatted(UNCHANGEABLE_FEATURE_ID, operation));
        }
    }
}

