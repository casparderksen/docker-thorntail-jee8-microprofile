package nl.casparderksen.persistence;

import lombok.extern.slf4j.Slf4j;
import nl.casparderksen.model.Event;
import nl.casparderksen.service.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
class JpaEventRepository implements EventRepository {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Override
    public Event create(Event event) {
        JpaEvent jpaEvent = new JpaEvent().merge(event);
        entityManager.persist(jpaEvent);
        log.debug("created {}", jpaEvent);
        return jpaEvent.toEvent();
    }

    @Override
    public Optional<Event> get(long id) {
        JpaEvent jpaEvent = entityManager.find(JpaEvent.class, id);
        log.debug("retrieved {}", jpaEvent);
        return Optional.ofNullable(jpaEvent).map(JpaEvent::toEvent);
    }

    @Override
    public Optional<Event> update(long id, Event event) {
        JpaEvent jpaEvent = entityManager. find(JpaEvent.class, id);
        if (jpaEvent == null) {
            return Optional.empty();
        }
        jpaEvent.merge(event);
        entityManager.merge(jpaEvent);
        log.debug("updated {}", jpaEvent);
        return Optional.of(jpaEvent.toEvent());
    }

    @Override
    public boolean delete(long id) {
        JpaEvent jpaEvent = entityManager.find(JpaEvent.class, id);
        if (jpaEvent == null) {
            return false;
        }
        entityManager.remove(jpaEvent);
        log.debug("removed {}", jpaEvent);
        return true;
    }

    @Override
    public long getCount() {
        TypedQuery<Long> query = entityManager.createNamedQuery("JpaEvent.countAll", Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Event> getAll() {
        TypedQuery<JpaEvent> query = entityManager.createNamedQuery("JpaEvent.findAll", JpaEvent.class);
        return getEvents(query);
    }

    @Override
    public List<Event> getRange(int start, int size) {
        TypedQuery<JpaEvent> query = entityManager.createNamedQuery("JpaEvent.findAll", JpaEvent.class);
        query.setFirstResult(start);
        query.setMaxResults(size);
        return getEvents(query);
    }

    private List<Event> getEvents(TypedQuery<JpaEvent> query) {
        List<JpaEvent> resultList = query.getResultList();
        return resultList.stream().map(JpaEvent::toEvent).collect(Collectors.toList());
    }
}
