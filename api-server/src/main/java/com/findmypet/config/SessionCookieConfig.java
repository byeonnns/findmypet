package com.findmypet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionCookieConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");          // 세션 쿠키 이름
        serializer.setUseBase64Encoding(false);       // Base64 인코딩 끔 (이게 핵심!!)
        serializer.setCookiePath("/");
        return serializer;
    }
}
