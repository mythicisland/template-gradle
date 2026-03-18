package net.mythicisland.template.api;

import net.mythicisland.template.api.internal.TemplateApiImpl;

public interface TemplateApi extends AutoCloseable {

    static TemplateApi create() {
        return create(TemplateApiOptions.DEFAULT);
    }

    static TemplateApi create(TemplateApiOptions options) {
        return new TemplateApiImpl(options);
    }

    @Override
    void close();

}
