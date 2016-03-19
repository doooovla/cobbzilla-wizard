package org.cobbzilla.wizard.dao.shard.cache;

import org.cobbzilla.wizard.model.shard.Shardable;

public interface CacheableFinder<E extends Shardable> {

    public E find (Object... args);

}
