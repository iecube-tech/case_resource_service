package com.iecube.community.ys;

import com.iecube.community.baseservice.ex.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class YsApi {

    private static final String YS_ACCESS_TOKEN_KEY = "YS_ACCESS_TOKEN_KEY";

    private static final String YS_GET_ACCESS_TOKEN_PATH = "/api/lapp/token/get";

    @Autowired
    private YsConfig ysConfig;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getAccessToken() {
        String token = stringRedisTemplate.opsForValue().get(YS_ACCESS_TOKEN_KEY);
        if (token == null) {
            return refreshAccessToken();
        }
        return token;
    }

    public String refreshAccessToken() {
        String token = this.token();
        stringRedisTemplate.opsForValue().set(YS_ACCESS_TOKEN_KEY, token, 6, TimeUnit.DAYS);
        return token;
    }

    private String token() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("appKey", ysConfig.getAppKey());
        params.add("appSecret", ysConfig.getSecret());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity;
        httpEntity = new HttpEntity<>(params, httpHeaders);

        YsModel result = new RestTemplate().postForObject(getTokenPath(), httpEntity, YsModel.class);
        if (result != null && YsModel.SUCCESS_CODE.equals(result.getCode())) {
            return result.getData().get("accessToken").asText();
        }
        throw new ServiceException("获取 AccessToken 失败");
    }

    private String getTokenPath() {
        return ysConfig.getHost() + YS_GET_ACCESS_TOKEN_PATH;
    }

}
