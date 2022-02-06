package org.ll;

import lombok.extern.slf4j.Slf4j;
import org.ll.annotation.Excel;
import org.ll.annotation.Field;
import org.ll.svc.impl.PanelServiceImpl;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

@SpringBootApplication @Slf4j
public class StartTools {
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new SpringApplicationBuilder(StartTools.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run(args)
                ;
    }


    @Component
    public class AutoTestProcessor implements ApplicationRunner{

        @Autowired private PanelServiceImpl panelService;
        @Override
        public void run(ApplicationArguments args) throws Exception {
//            panelService.reload("rent");//
//            panelService.startJob();
            TestE t = new TestE();
            log.info("t.name: {}", t.name);
        }
    }

    @Excel("/home/ll/documents/1.xlsx")
    public class TestE{
        @Field(column=1)
        public String name;

    }

}
