package org.overbaard.review.tool.security.github;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */
@Entity
@Table(name = "gh_user")
@NamedQueries({
        @NamedQuery(name = "GitHubUser.findByGhLogin",
                query = "SELECT u FROM GitHubUser u WHERE u.login = :login",
                hints = @QueryHint(name = "org.hibernate.cacheable", value = "true")),
        @NamedQuery(name = "GitHubUser.findAllSiteAdmins",
                query = "SELECT u from GitHubUser u JOIN u.siteAdmin s ORDER BY u.name"
        )
})
@NamedEntityGraphs(
        @NamedEntityGraph(name = "GitHubUser.siteAdmin", attributeNodes = @NamedAttributeNode("siteAdmin"))
)
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

    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private SiteAdmin siteAdmin;



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
        // TODO this is a bit weird
        String jsonUser = JsonbBuilder.create(new JsonbConfig()).toJson(this);
        return Json.createReader(new StringReader(jsonUser)).readObject();
    }
}
