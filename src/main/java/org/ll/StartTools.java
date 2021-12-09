package org.ll;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class StartTools {
    public static void main(String[] args){
        new SpringApplicationBuilder(StartTools.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .banner(Banner.Mode.OFF)
                .run(args)
                ;
    }
}
