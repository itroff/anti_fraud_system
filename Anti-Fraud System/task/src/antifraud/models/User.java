package antifraud.models;

import javax.persistence.*;


@Entity(name = "user")
@Table(name = "user")
public class User {

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User() {
        this.role = Role.ADMINISTRATOR;
        this.enabled = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    private String name;


    @Column
    private String password;

    @Column
    private boolean enabled;

    public User(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = Role.ADMINISTRATOR;
        this.enabled = true;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;


    public boolean equals(User another) {
        if (this.userId == another.getUserId()) {
            return true;
        }
        return false;
    }

    //equals() and hashCode() methods
}