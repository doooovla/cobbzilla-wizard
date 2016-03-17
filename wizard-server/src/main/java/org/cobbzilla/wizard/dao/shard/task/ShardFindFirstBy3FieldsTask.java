package org.cobbzilla.wizard.dao.shard.task;

import lombok.AllArgsConstructor;
import org.cobbzilla.wizard.dao.shard.SimpleShardTask;
import org.cobbzilla.wizard.dao.shard.SingleShardDAO;
import org.cobbzilla.wizard.model.Identifiable;

import java.util.Set;

public class ShardFindFirstBy3FieldsTask<E extends Identifiable, D extends SingleShardDAO<E>> extends SimpleShardTask<E, D, E> {

    @Override protected E execTask() { return dao.findByUniqueFields(f1, v1, f2, v2, f3, v3); }

    @AllArgsConstructor
    public static class Factory<E extends Identifiable, D extends SingleShardDAO<E>>
            extends ShardTaskFactoryBase<E, D, E, E> {
        private String f1;
        private Object v1;
        private String f2;
        private Object v2;
        private String f3;
        private Object v3;

        @Override public ShardFindFirstBy3FieldsTask<E, D> newTask(D dao) {
            return new ShardFindFirstBy3FieldsTask<>(dao, tasks, f1, v1, f2, v2, f3, v3);
        }
    }

    private String f1;
    private Object v1;
    private String f2;
    private Object v2;
    private String f3;
    private Object v3;

    public ShardFindFirstBy3FieldsTask(D dao, Set<ShardTask<E, D, E, E>> tasks, String f1, Object v1, String f2, Object v2, String f3, Object v3) {
        super(dao, tasks, new ShardResultCollectorBase<E>());
        this.f1 = f1;
        this.v1 = v1;
        this.f2 = f2;
        this.v2 = v2;
        this.f3 = f3;
        this.v3 = v3;
    }
}