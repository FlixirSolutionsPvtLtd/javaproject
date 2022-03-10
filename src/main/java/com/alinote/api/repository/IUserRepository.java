package com.alinote.api.repository;


import com.alinote.api.domains.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface IUserRepository extends MongoRepository<Users, String>, UserCustomRepository {

    List<Users> findByMobileNoAndActiveStatus(String mobileNo, int activeStatus);

    List<Users> findByMobileNoAndEmailAndActiveStatus(String mobileNo, String email, int activeStatus);

    Users findByEmail(String email);

    List<Users> findByEmailAndActiveStatus(String email, int activeStatus);
}
