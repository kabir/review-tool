package org.overbaard.review.tool.config.github;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Expose the lazy loaded fields from Organisation when they have been eagerly loaded and are wanted
 *
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
public class OrganisationEagerWrapper extends Organisation{
    private final Organisation delegate;

    public OrganisationEagerWrapper(Organisation delegate) {
        this.delegate = delegate;
    }

    @Override
    public Integer getId() {
        return delegate.getId();
    }

    @Override
    public void setId(Integer id) {
        delegate.setId(id);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public String getToolPrRepo() {
        return delegate.getToolPrRepo();
    }

    @Override
    public void setToolPrRepo(String toolPrRepo) {
        delegate.setToolPrRepo(toolPrRepo);
    }

    @Override
    @JsonbProperty
    public List<MirroredRepository> getMirroredRepositories() {
        return delegate.getMirroredRepositories();
    }

}
