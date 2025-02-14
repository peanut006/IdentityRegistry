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
package net.maritimeconnectivity.identityregistry.security;


import net.maritimeconnectivity.identityregistry.model.database.Organization;
import net.maritimeconnectivity.identityregistry.model.database.Role;
import net.maritimeconnectivity.identityregistry.services.OrganizationService;
import net.maritimeconnectivity.identityregistry.services.RoleService;
import net.maritimeconnectivity.identityregistry.utils.AccessControlUtil;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MCKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(MCKeycloakAuthenticationProvider.class);

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private RoleService roleService;

    @Override
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        KeycloakSecurityContext ksc = (KeycloakSecurityContext)token.getCredentials();
        Map<String, Object> otherClaims = ksc.getToken().getOtherClaims();

        Organization org;
        if (otherClaims.containsKey(AccessControlUtil.ORG_PROPERTY_NAME)) {
            String orgMrn = (String) otherClaims.get(AccessControlUtil.ORG_PROPERTY_NAME);
            logger.debug("Found org mrn: {}", orgMrn);
            org = organizationService.getOrganizationByMrnNoFilter(orgMrn);

            if (org != null) {
                if (otherClaims.containsKey(AccessControlUtil.PERMISSIONS_PROPERTY_NAME)) {
                    ArrayList<String> usersPermissions = (ArrayList<String>) otherClaims.get(AccessControlUtil.PERMISSIONS_PROPERTY_NAME);
                    for (String permission : usersPermissions) {
                        String[] auths = permission.split(",");
                        for (String auth : auths) {
                            logger.debug("Looking up role: {}", auth);
                            List<Role> foundRoles = roleService.getRolesByIdOrganizationAndPermission(org.getId(), auth);
                            if (foundRoles != null) {
                                for (Role foundRole : foundRoles) {
                                    logger.debug("Replacing role {}, with: {}", auth, foundRole.getRoleName());
                                    grantedAuthorities.add(new KeycloakRole(foundRole.getRoleName()));
                                }
                            }
                        }
                    }
                }
                if (grantedAuthorities.isEmpty()) {
                    grantedAuthorities.add(new KeycloakRole("ROLE_USER"));
                }
            }
        }
        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapTheAuthorities(grantedAuthorities));
    }

    private Collection<? extends GrantedAuthority> mapTheAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
                ? grantedAuthoritiesMapper.mapAuthorities(authorities)
                : authorities;
    }

}
