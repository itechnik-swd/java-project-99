package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO getLabelById(Long id) {
        return labelMapper.map(labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found")));
    }

    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO) {
        if (labelRepository.findByName(labelCreateDTO.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Label " + labelCreateDTO.getName() + " already exists");
        }

        return labelMapper.map(labelRepository.save(labelMapper.map(labelCreateDTO)));
    }

    public LabelDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        labelMapper.update(labelUpdateDTO, label);
        return labelMapper.map(labelRepository.save(label));
    }

    public void deleteLabel(Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
        labelRepository.delete(label);
    }
}
