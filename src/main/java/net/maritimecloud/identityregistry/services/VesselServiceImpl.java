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
package net.maritimecloud.identityregistry.services;

import net.maritimecloud.identityregistry.model.database.entities.Vessel;
import net.maritimecloud.identityregistry.repositories.VesselRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VesselServiceImpl extends EntityServiceImpl<Vessel> implements EntityService<Vessel> {
    @Autowired
    public void setVesselRepository(VesselRepository VesselRepository) {
        this.repository = VesselRepository;
    }

}
