/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mthizo247.cloud.netflix.zuul.web.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Strategy to resolve zuul properties from route service is using eaureka service discovery
 *
 * @author Ronald Mthombeni
 * @author Salman Noor
 */
public class EurekaPropertiesResolver implements ZuulPropertiesResolver {

    public static final Logger LOG = LoggerFactory.getLogger(EurekaPropertiesResolver.class);
    private DiscoveryClient discoveryClient;
    private ZuulProperties zuulProperties;
    private LoadBalancerClient loadBalancerClient;


    public EurekaPropertiesResolver(DiscoveryClient discoveryClient, ZuulProperties zuulProperties,LoadBalancerClient loadBalancerClient) {
        this.discoveryClient = discoveryClient;
        this.zuulProperties = zuulProperties;
        this.loadBalancerClient=loadBalancerClient;
    }


    @Override
    public String getRouteHost(ZuulWebSocketProperties.WsBrokerage wsBrokerage) {
        ZuulProperties.ZuulRoute zuulRoute = zuulProperties.getRoutes().get(wsBrokerage.getId());
        if (zuulRoute == null || StringUtils.isEmpty(zuulRoute.getServiceId())) {
            return null;
        }
        ServiceInstance serviceInstance= loadBalancerClient.choose(zuulRoute.getServiceId());
        Assert.notNull(serviceInstance, "no serviceInstance for serviceId");
        return serviceInstance.getUri().toString();
    }
}
