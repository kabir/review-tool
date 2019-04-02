package org.overbaard.review.tool.security.github;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_user")
@NamedQuery(name = "GitHubUser.findByGhLogin",
        query = "SELECT u FROM GitHubUser u WHERE u.login = :login",
        hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
public class GitHubUser {
    @Id
    @SequenceGenerator(
            name = "ghUserSequence",
            sequenceName = "gh_user_id_seq",
            allocationSize = 1,
            initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghUserSequence")
    private Integer id;

    @Column(unique = true)
    private String login;

    private String name;

    @Column(name = "site_admin")
    private boolean siteAdmin;

    @Column(name = "avatar_url")
    @JsonbProperty("avatar_url")
    private String avatarUrl;

    public GitHubUser() {
    }

    public GitHubUser(Integer id, String login, String name, String avatarUrl) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isSiteAdmin() {
        return siteAdmin;
    }

    public void setSiteAdmin(boolean siteAdmin) {
        this.siteAdmin = siteAdmin;
    }
}
