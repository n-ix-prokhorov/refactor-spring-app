package com.example.refactoringtask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {
    @Autowired
    public UserData userData;
    
    @GetMapping("v1/user")
    public Map user() {
        Map usToAddrMap = new HashMap();
        List users = userData.findAll();
        for (int i = 0; i < users.size(); i++) {
            User user1 = (User) users.get(i);
            usToAddrMap.put(user1.address.city, user1);
        }
        return usToAddrMap;
    }
}

@Entity
class User {
    @Id
    public Integer id;
    public String firstName;
    public String lastName;
    public Integer age;
    @ManyToOne
    public Address address;
}

@Repository
interface UserData extends JpaRepository<User, Integer> {}

@Entity
class Address {
    @Id
    public Integer id;
    public String city;
    public String street;
}
