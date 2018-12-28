package nl.casparderksen.service;

import nl.casparderksen.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    Event create(Event event);

    Optional<Event> get(long id);

    Optional<Event> update(long id, Event event);

    boolean delete(long id);

    long getCount();

    List<Event> getAll();

    List<Event> getRange(int start, int size);
}
