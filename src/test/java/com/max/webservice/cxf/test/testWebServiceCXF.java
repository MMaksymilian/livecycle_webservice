package com.max.webservice.cxf.test;

import com.max.webservice.cxf.service.HelloWorld;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created with IntelliJ IDEA.
 * User: Maksymilian Małek
 * Date: 10.07.12
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class testWebServiceCXF extends AbstractJUnit4SpringContextTests {

    @Autowired
    private HelloWorld helloWorld;

    @Test
    public void test() {
    }
}
