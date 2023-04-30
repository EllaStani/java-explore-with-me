package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.event.EventJpaRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationJpaRepository compRepository;
    private final EventJpaRepository eventRepository;
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Compilation> compilations = compRepository.findByPinned(pinned, pageable);
        return CompilationMapper.mapToListCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationById(int compId) {
        Compilation compilation = checkingExistCompilation(compId);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto saveNewCompilation(CompilationNewDto compilationNewDto) {
        Compilation newCompilation = new Compilation();
        newCompilation.setPinned(compilationNewDto.getPinned());
        newCompilation.setTitle(compilationNewDto.getTitle());
        newCompilation.setEvents(compilationNewDto.getEvents()
                        .stream()
                        .map(id -> eventRepository.findById(id).get())
                        .collect(Collectors.toList()));

        compRepository.save(newCompilation);
        return CompilationMapper.mapToCompilationDto(newCompilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(int compId, CompilationNewDto compilationNewDto) {
        Compilation updateCompilation = checkingExistCompilation(compId);

        if (compilationNewDto.getPinned() != null) {
            updateCompilation.setPinned(compilationNewDto.getPinned());
        }

        if (compilationNewDto.getTitle() != null) {
            updateCompilation.setTitle(compilationNewDto.getTitle());
        }

        if (!compilationNewDto.getEvents().isEmpty() && (compilationNewDto.getEvents() != null)) {
            updateCompilation.getEvents().addAll(compilationNewDto.getEvents()
                    .stream()
                    .map(id -> eventRepository.findById(id).get())
                    .collect(Collectors.toList()));
        }


        compRepository.save(updateCompilation);
        return CompilationMapper.mapToCompilationDto(updateCompilation);
    }

    @Transactional
    @Override
    public void deleteCompilationById(int compId) {
        checkingExistCompilation(compId);
        compRepository.deleteById(compId);
    }

    private Compilation checkingExistCompilation(int compId){
        return compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%s не найдена", compId)));
    }
}
