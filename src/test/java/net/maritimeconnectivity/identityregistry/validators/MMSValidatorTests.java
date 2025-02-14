/*
 * Copyright 2020 Maritime Connectivity Platform Consortium.
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

import net.maritimeconnectivity.identityregistry.model.database.entities.MMS;
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
public class MMSValidatorTests {
    @Autowired
    private WebApplicationContext context;

    private LocalValidatorFactoryBean validator;

    @Before
    public void init() {
        validator = context.getBean(LocalValidatorFactoryBean.class);
    }

    @Test
    public void validateValidMMS() {
        MMS validMms = new MMS();
        validMms.setMrn("urn:mrn:mcp:mms:idp1:testorg:test-mms1");
        validMms.setName("Test mms");
        validMms.setUrl("http://maritimeconnectivity.net");

        Set<ConstraintViolation<MMS>> violations = validator.validate(validMms);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validateInvalidMMS() {
        MMS invalidMms = new MMS();
        invalidMms.setMrn("urn:mrn:mcp:mms:idp1:testorg:test-mms1");
        invalidMms.setName("Test mms");
        // Invalid url - must be set!
        invalidMms.setUrl(null);

        Set<ConstraintViolation<MMS>> violations = validator.validate(invalidMms);
        assertEquals(1, violations.size());
    }

}
