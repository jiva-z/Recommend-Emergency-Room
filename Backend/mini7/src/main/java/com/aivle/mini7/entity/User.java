package com.aivle.mini7.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user") // 테이블 이름을 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 적용
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 45)
    private String userId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(columnDefinition = "ENUM('M', 'F')")
    private String gender;

    @Column(columnDefinition = "INT UNSIGNED")
    private Integer age;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}