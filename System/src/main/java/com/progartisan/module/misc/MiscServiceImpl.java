package com.progartisan.module.misc;


import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Context;
import com.progartisan.component.framework.Metadata;
import com.progartisan.component.framework.Service;
import com.progartisan.component.spi.MetadataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

@Named
@RequiredArgsConstructor
@Service(type = Service.Type.Mixed, name = "misc", order = -1)
public class MiscServiceImpl implements com.progartisan.module.misc.api.MiscService {

    private final MetadataProvider metadataProvider;
    private final ResourceLoader resourceLoader;


    @Override
    public String getVersion() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:version.txt");
        return new String(resource.getInputStream().readAllBytes());
    }


    @Override
    public Object testDate(TestDate date) {

        var curDate = Context.getCurrentDate();
        var dateFormat1 = new SimpleDateFormat(Util.datetimeFormatterString);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        var dateFormat2 = new SimpleDateFormat(Util.datetimeFormatterString);
        dateFormat2.setTimeZone(TimeZone.getTimeZone(Context.getTimezone()));
        return Map.of("* currentDate", curDate,
                "currentDateStr UTC", dateFormat1.format(curDate),
                "currentDateStr Local", dateFormat2.format(curDate),
                "* inputDate", date.inputDate,
                "inputDateStr UTC", dateFormat1.format(date.inputDate),
                "inputDateStr2 Local", dateFormat2.format(date.inputDate));
    }

    @Override
    public Metadata.EntityDef getEntityMetadata(String entityName) {
        return metadataProvider.getEntityDef(entityName);
    }
}
