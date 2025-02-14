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


import net.maritimeconnectivity.identityregistry.model.database.entities.Device;
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
public class DeviceValidatorTests {
    @Autowired
    private WebApplicationContext context;

    private LocalValidatorFactoryBean validator;

    @Before
    public void init() {
        validator = context.getBean(LocalValidatorFactoryBean.class);
    }

    @Test
    public void validateValidDevice() {
        Device validDevice = new Device();
        validDevice.setMrn("urn:mrn:mcp:device:idp1:testorg:test-device1");
        validDevice.setName("Test Device");

        Set<ConstraintViolation<Device>> violations = validator.validate(validDevice);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validateInvalidDevice() {
        Device invalidDevice = new Device();
        // Invalid mrn - must be set!
        invalidDevice.setMrn(null);
        invalidDevice.setName("Test Device");

        Set<ConstraintViolation<Device>> violations = validator.validate(invalidDevice);
        assertEquals(1, violations.size());
    }

    @Test
    public void validateValidDeviceWithMMS() {
        Device validDevice = new Device();
        validDevice.setMrn("urn:mrn:mcp:device:idp1:testorg:test-device1");
        validDevice.setName("Test Device");
        validDevice.setMrnSubsidiary("urn:mrn:kr:device:testorg:test-device1");
        validDevice.setHomeMMSUrl("https://mms.smartnav.org");

        Set<ConstraintViolation<Device>> violations = validator.validate(validDevice);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validateInvalidMMSUrlOfDeviceWithMMS() {
        Device validDevice = new Device();
        validDevice.setMrn("urn:mrn:mcp:device:idp1:testorg:test-device1");
        validDevice.setName("Test Device");
        validDevice.setMrnSubsidiary("urn:mrn:kr:device:testorg:test-device1");
        validDevice.setHomeMMSUrl("ftp://mms.smartnav.org"); // wrong use of ftp

        Set<ConstraintViolation<Device>> violations = validator.validate(validDevice);
        assertEquals(1, violations.size());
    }

    @Test
    public void validateInvalidSubMRNOfDeviceWithMMS() {
        Device validDevice = new Device();
        validDevice.setMrn("urn:mrn:mcp:device:idp1:testorg:test-device1");
        validDevice.setName("Test Device");
        validDevice.setMrnSubsidiary("urn:mcp:kr:device:testorg:test-device1"); // does not contain the mrn suffix
        validDevice.setHomeMMSUrl("https://mms.smartnav.org");

        Set<ConstraintViolation<Device>> violations = validator.validate(validDevice);
        assertEquals(1, violations.size());
    }

}
