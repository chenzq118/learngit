package com.tanhua.server.test;

import com.tanhua.autoconfig.template.FaceTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest(classes = AppServerApplication.class)
@RunWith(SpringRunner.class)
public class FaceTest {

    @Autowired
    private FaceTemplate faceTemplate;

    @Test
    public void testFace() throws IOException {
        System.out.println(faceTemplate.detect(Files.readAllBytes(new File("C:\\Users\\Administrator\\Desktop\\2.jpg").toPath())));
    }
}
