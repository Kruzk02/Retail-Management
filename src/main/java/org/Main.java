package org;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        var helloService = context.getBean(HelloService.class);

        System.out.println(helloService.hello());
    }
}