package com.progartisan.module.uiartisan;

import com.progartisan.component.spi.MetadataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Deprecated
@SpringBootTest
// @ExtendWith(SpringExtension.class)
@Slf4j
public class TestUIArtisanService {

    @Configuration
    static class TestConfig {

        @Autowired
        ResourceLoader resourceLoader;
        @Autowired
        MetadataProvider metadataProvider;

        @Bean
        public UIArtisanServiceImpl uiArtisanService() {
            Parser parser = new Parser();
            return new UIArtisanServiceImpl(resourceLoader, parser, metadataProvider);
        }
    }

    @Autowired
    private UIArtisanServiceImpl uiArtisanService;

    public void testInitUI() throws Exception {
        uiArtisanService.initUI("@user/user.vue");
    }
}
