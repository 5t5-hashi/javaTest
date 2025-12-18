package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 示例控制器
 * 备注：
 * 1. @RestController 表示这是一个处理 HTTP 请求的控制器，返回 JSON 或文本数据
 */
@RestController
public class HelloController {

    /**
     * 处理 /hello 请求
     * 备注：当用户访问 http://localhost:8080/hello 时触发
     *
     * @param name 可选参数，例如 /hello?name=Trae
     * @return 返回一段问候语
     */
    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s! This is your first Spring Boot App.", name);
    }

    /**
     * 处理 /user 请求，返回 JSON 数据
     * 备注：演示如何返回对象，Spring Boot 会自动将其转换为 JSON 格式
     *
     * @param name 用户名
     * @param age  年龄
     * @return User 对象（前端会收到 JSON）
     */
    @GetMapping("/user")
    public User getUser(@RequestParam(value = "name", defaultValue = "Guest") String name,
            @RequestParam(value = "age", defaultValue = "18") int age) {
        return new User(name, age, "Java Developer");
    }

    /**
     * 演示 POST 请求：发布文章
     * 前端发送：{"title": "Java真好玩", "content": "我要好好学习"}
     */
    @PostMapping("/article")
    public Article publishArticle(@RequestBody Article article) {
        // 模拟业务逻辑：后端自动补充信息
        article.setAuthor("Trae AI");
        article.setPublishDate("2024-03-21");
        article.setContent(article.getContent() + " [已审核]");
        
        System.out.println("收到新文章投稿：" + article.getTitle());
        return article;
    }

    /**
     * 演示不使用 Lombok 的原生写法：发表评论
     */
    @PostMapping("/comment")
    public Comment publishComment(@RequestBody Comment comment) {
        System.out.println("收到新评论：" + comment.getText());
        return comment;
    }

    /**
     * 评论数据模型（原生 Java 写法，不使用 Lombok）
     * 对比 Article 类，你会发现这里多写了几十行代码
     */
    public static class Comment {
        private String username;
        private String text;

        // 1. 必须手写无参构造函数（为了 JSON 反序列化）
        public Comment() {
        }

        // 2. 必须手写全参构造函数（为了方便 new 对象）
        public Comment(String username, String text) {
            this.username = username;
            this.text = text;
        }

        // 3. 必须手写 Getter（为了 Spring 能读取数据转 JSON）
        public String getUsername() {
            return username;
        }

        public String getText() {
            return text;
        }

        // 4. 必须手写 Setter（为了 Spring 能把 JSON 数据写入对象）
        public void setUsername(String username) {
            this.username = username;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 文章数据模型
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        private String title;
        private String content;
        private String author;
        private String publishDate;
    }

    /**
     * 用户数据模型
     * 备注：使用 Lombok 注解简化代码
     * @Data -> 自动生成 Getter, Setter, toString 等
     * @AllArgsConstructor -> 自动生成包含所有字段的构造函数
     */
    @Data
    @AllArgsConstructor
    public static class User {
        private String name;
        private int age;
        private String role;
    }
}
