package org.cobbzilla.wizard.server;

import org.cobbzilla.wizard.server.config.RestServerConfiguration;

public interface RestServerLifecycleListener<C extends RestServerConfiguration> {

    void beforeStart(RestServer<C> server);
    void onStart(RestServer<C> server);

    void beforeStop(RestServer<C> server);
    void onStop (RestServer<C> server);

}
