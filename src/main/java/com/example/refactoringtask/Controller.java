package com.example.refactoringtask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;

@RestController
public class Controller {

    @Autowired
    public UserData userData;

    @Autowired
    public UserLookupRepository userLookupRepository;

    @GetMapping("v1/user")
    public Map user(Authentication authentication) {
        String reqInitId = authentication.getName();
        Map usToAddrMap = new HashMap();
        if (userLookupRepository.findById(valueOf(reqInitId)).isPresent()) {
            List users = userData.findAll();
            if (users.isEmpty()) {
                throw new IllegalStateException("Nothing was found...");
            }
            for (int i = 0; i < users.size(); i++) {
                User user1 = (User) users.get(i);
                usToAddrMap.put(user1.address.city, user1);
            }
        }
        return usToAddrMap;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
    public ResponseEntity illegalStateExceptionHandler(IllegalStateException e) {
        return ResponseEntity.ok().build();
    }
}

@Entity
class User {
    @Id
    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    @ManyToOne
    public Address address;
}

@Entity
class Address {
    @Id
    public Integer id;
    public String city;
    public String street;
}

@Repository
interface UserData extends JpaRepository<User, Integer> {
}

@Repository
interface UserLookupRepository extends PagingAndSortingRepository<UserPrincipal, Integer> {
}

class UserPrincipal {
}

