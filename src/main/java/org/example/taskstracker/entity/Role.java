package org.example.taskstracker.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@Document(collection = "roles")
public class Role {

    @Id
    private Long id;

    private RoleType authority;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    public GrantedAuthority toAuthority(){
        return new SimpleGrantedAuthority(authority.name());
    }

    public static Role from(RoleType type){
        var role = new Role();
        role.setAuthority(type);
        return role;
    }
}
