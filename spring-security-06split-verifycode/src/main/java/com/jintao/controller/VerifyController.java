package com.jintao.controller;

import com.google.code.kaptcha.Producer;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class VerifyController {

    @Autowired
    private Producer producer;

    @GetMapping("/vc.jpg")
    public String getVerifyCode(HttpServletResponse response, HttpSession session) throws IOException {
        //生成验证码
        String text = producer.createText();
        //生成图片
        BufferedImage image = producer.createImage(text);
        session.setAttribute("kaptcha", text);
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", fastByteArrayOutputStream);

        //返回Base64
        return Base64.encodeBase64String(fastByteArrayOutputStream.toByteArray());
    }
}
