/*
 * Copyright 2017 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.maritimeconnectivity.identityregistry.validators;

import net.maritimeconnectivity.identityregistry.model.database.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
public class UserValidatorTests {
    @Autowired
    private WebApplicationContext context;

    private LocalValidatorFactoryBean validator;

    @Before
    public void init() {
        validator = context.getBean(LocalValidatorFactoryBean.class);
    }

    @Test
    public void validateValidUser() {
        User validUser = new User();
        validUser.setFirstName("Firstname");
        validUser.setLastName("Lastname");
        validUser.setEmail("user@test.org");
        validUser.setMrn("urn:mrn:mcp:user:idp1:testorg:test-user");

        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validateInvalidUser1() {
        User invalidUser = new User();
        invalidUser.setFirstName("Firstname");
        // Invalid lastname - must be filled
        invalidUser.setLastName(" ");
        // Invalid email
        invalidUser.setEmail("user-test.org");
        invalidUser.setMrn("urn:mrn:mcp:user:idp1:testorg:test-user");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertEquals(2, violations.size());
    }

    @Test
    public void validateInvalidUser2() {
        User invalidUser = new User();
        invalidUser.setFirstName("Firstname");
        invalidUser.setLastName("Lastname");
        invalidUser.setEmail("user@test.org");
        // Invalid mrn, must be in the MRN format
        invalidUser.setMrn("test-user");

        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertEquals(1, violations.size());
    }

}
