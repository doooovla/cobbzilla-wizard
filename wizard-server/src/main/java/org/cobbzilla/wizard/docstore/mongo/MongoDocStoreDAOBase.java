package org.cobbzilla.wizard.docstore.mongo;

import org.cobbzilla.wizard.dao.DAO;
import org.cobbzilla.wizard.model.ResultPage;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** a mongo docstore that also conforms to the DAO interface */
public abstract class MongoDocStoreDAOBase<T extends MongoDocBase> extends MongoDocStore<T> implements DAO<T> {

    @Override
    public <T1> List<T1> query(ResultPage resultPage) {
        throw new IllegalStateException("not supported");
    }

    @Override public Class<? extends Map<String, String>> boundsClass() { return null; }

    @Override
    public T get(Serializable id) {
        return findByUuid(id.toString());
    }

    @Override
    public List<T> findAll() {
        throw new IllegalStateException("not supported");
    }

    @Override
    public boolean exists(Long id) {
        return findOne(MongoDocBase.UUID, id) != null;
    }

    @Override
    public boolean exists(String uuid) {
        return findOne(MongoDocBase.UUID, uuid) != null;
    }

    @Override
    public T create(@Valid T entity) {
        entity.beforeCreate();
        save(entity);
        return entity;
    }

    @Override
    public T createOrUpdate(@Valid T entity) {
        if (entity.getUuid() == null) entity.beforeCreate();
        saveOrUpdate(entity);
        return entity;
    }

    @Override
    public T update(@Valid T entity) {
        if (entity.getUuid() == null) entity.beforeCreate();
        saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void delete(String uuid) {
        delete(get(uuid).getId());
    }

    @Override
    public T findByUniqueField(String field, Object value) {
        List<T> found = findByFilter(field, value);
        if (found.isEmpty()) return null;
        if (found.size() > 1) throw new IllegalStateException("multiple results found for: field="+field+", value="+value+": "+found);
        return found.get(0);
    }

}