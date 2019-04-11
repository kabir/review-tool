package org.overbaard.review.tool.security.github;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.overbaard.review.tool.config.github.Organisation;
import org.overbaard.review.tool.util.EntitySerializer;
import org.overbaard.review.tool.util.MapBuilder;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_user")
@NamedQueries({
        @NamedQuery(name = GitHubUser.Q_FIND_BY_LOGIN,
                query = "SELECT u FROM GitHubUser u WHERE u.login = :login",
                hints = @QueryHint(name = "org.hibernate.cacheable", value = "true")),
        @NamedQuery(name = GitHubUser.Q_FIND_ALL_SITE_ADMINS,
                query = "SELECT u from GitHubUser u JOIN u.siteAdmin s ORDER BY u.name"
        ),
        @NamedQuery(name = GitHubUser.Q_FIND_ORG_ADMINS,
                query = "SELECT u from GitHubUser u JOIN u.adminOfOrganisations o WHERE o.id = :org_id ORDER BY u.name"
        )
})
@NamedEntityGraphs({
        @NamedEntityGraph(name = GitHubUser.G_SITE_ADMIN, attributeNodes = @NamedAttributeNode("siteAdmin")),
        @NamedEntityGraph(name = GitHubUser.G_ADMIN_OF_ORGS, attributeNodes = @NamedAttributeNode("adminOfOrganisations"))
})
@JsonbTypeSerializer(GitHubUser.Serializer.class)
public class GitHubUser {

    public static final String Q_FIND_BY_LOGIN = "GitHubUser.findByGhLogin";
    public static final String Q_FIND_ALL_SITE_ADMINS = "GitHubUser.findAllSiteLogins";
    public static final String Q_FIND_ORG_ADMINS = "GitHubUser.findOrgAdmins";

    public static final String G_SITE_ADMIN = "GitHubUser.siteAdmin";
    public static final String G_ADMIN_OF_ORGS = "GitHubUser.adminOfOrganisations";

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

    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private SiteAdmin siteAdmin;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "admins")
    private Set<Organisation> adminOfOrganisations = new HashSet<>();

    public GitHubUser() {
    }

    public GitHubUser(Integer id, String login, String name, String email, String avatarUrl) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Organisation> getAdminOfOrganisations() {
        return adminOfOrganisations;
    }

    public void setAdminOfOrganisations(Set<Organisation> adminOfOrganisations) {
        this.adminOfOrganisations = adminOfOrganisations;
    }

    @JsonbProperty // Use this format when writing to our REST API
    public String getAvatarUrl() {
        JsonbBuilder.create();
        return avatarUrl;
    }

    @JsonbProperty("avatar_url") // When getting the data from GitHub it uses this format
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public SiteAdmin getSiteAdmin() {
        return siteAdmin;
    }

    public void setSiteAdmin(SiteAdmin siteAdmin) {
        this.siteAdmin = siteAdmin;
    }

    public JsonObject convertToJsonObject() {
        String jsonUser = JsonbBuilder.create().toJson(this);
        return Json.createReader(new StringReader(jsonUser)).readObject();
    }

    public static class Serializer extends EntitySerializer<GitHubUser> {
        public Serializer() {
            super(
                    MapBuilder.<String, Function<GitHubUser, ?>>linkedHashMap()
                            .put("id", o -> o.getId())
                            .put("login", o -> o.getLogin())
                            .put("name", o -> o.getName())
                            .put("email", o -> o.getEmail())
                            .put("avatarUrl", o -> o.getAvatarUrl())
                            .build()
            );

        }
    }
}
