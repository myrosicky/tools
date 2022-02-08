package org.ll.svc.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service @Slf4j
public class LocationServiceImpl {

    @Autowired private RestTemplate restTemplate;
    @Value("${alibaba.appCode}") private String appCode;

    public String getLocationByIp(String ip){
        log.info("getLocationByIp: {}", ip);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "APPCODE " + appCode);
        HttpEntity requestEntity = new HttpEntity(headers);
        ResponseEntity<String> resp = restTemplate.exchange("https://ipquery.market.alicloudapi.com/query?ip="+ip, HttpMethod.GET, requestEntity, String.class);
        log.info("resp: {}", resp);
        return resp.getBody();
    }
}
