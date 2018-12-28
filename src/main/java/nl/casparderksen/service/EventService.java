package nl.casparderksen.service;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Slf4j
public class EventService {

    @Inject
    private EventRepository eventRepository;
}
