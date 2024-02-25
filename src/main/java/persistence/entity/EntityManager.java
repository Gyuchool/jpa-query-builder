package persistence.entity;

import persistence.Person;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long id);

    Object persist(Object entity);

    void remove(Object entity);
}