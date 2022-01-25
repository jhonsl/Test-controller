package com.sofka.TestControladores.service;

import com.sofka.TestControladores.model.Widget;
import com.sofka.TestControladores.repository.WidgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WidgetServiceImpl implements WidgetService {
    private WidgetRepository repository;

    public WidgetServiceImpl(WidgetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Widget> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Widget> findAll() {
        return repository.findAll();
    }

    @Override
    public Widget save(Widget widget) {
        widget.setVersion(widget.getVersion()+1);
        return repository.save(widget);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}