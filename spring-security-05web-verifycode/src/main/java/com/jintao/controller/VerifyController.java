package com.jintao.controller;

import com.google.code.kaptcha.Producer;
import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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


    @RequestMapping("/vc.jpg")
    public void verifyCode(HttpServletResponse response, HttpSession session) throws IOException {
        //生成验证码
        String code = producer.createText();
        //保存在session，也可以redis
        session.setAttribute("code", code);
        //生成图片
        BufferedImage image = producer.createImage(code);
        //响应图片
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }
}
