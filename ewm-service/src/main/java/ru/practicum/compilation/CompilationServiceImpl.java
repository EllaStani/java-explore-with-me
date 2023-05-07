package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.FromSizeRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.event.EventJpaRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;

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
        List<Compilation> compilations = compRepository.findCompilationByPinnedIs(pinned, pageable);
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
        Compilation compilation = new Compilation();
        compilation.setPinned(compilationNewDto.getPinned());
        compilation.setTitle(compilationNewDto.getTitle());
        compilation.setEvents(eventRepository.findEventByIdIn(compilationNewDto.getEvents()));
        compRepository.save(compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(int compId, CompilationUpdateDto compilationUpdateDto) {
        Compilation updateCompilation = checkingExistCompilation(compId);

        if (compilationUpdateDto.getPinned() != null) {
            updateCompilation.setPinned(compilationUpdateDto.getPinned());
        }

        if (compilationUpdateDto.getTitle() != null) {
            updateCompilation.setTitle(compilationUpdateDto.getTitle());
        }

        if (!compilationUpdateDto.getEvents().isEmpty() && (compilationUpdateDto.getEvents() != null)) {
            updateCompilation.setEvents(eventRepository.findEventByIdIn(compilationUpdateDto.getEvents()));
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

    private Compilation checkingExistCompilation(int compId) {
        return compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id=%s не найдена", compId)));
    }
}
