package org.cobbzilla.wizard.dao;

import org.cobbzilla.wizard.model.ResultPage;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public interface DAO<E> {

    Class<E> getEntityClass();

    SearchResults<E> search(ResultPage resultPage);
    SearchResults<E> search(ResultPage resultPage, String entityType);

    E get(Serializable id);

    List<E> findAll();
    E findByUuid(String uuid);
    E findByUniqueField(String field, Object value);
    List<E> findByField(String field, Object value);

    boolean exists(String uuid);

    Iterator<E> iterate(String hsql, List<Object> args);
    void closeIterator(Iterator<E> iterator);

    Object preCreate(@Valid E entity);
    E create(@Valid E entity);
    E createOrUpdate(@Valid E entity);
    E postCreate(E entity, Object context);

    Object preUpdate(@Valid E entity);
    E update(@Valid E entity);
    E postUpdate(@Valid E entity, Object context);

    void delete(String uuid);

}
