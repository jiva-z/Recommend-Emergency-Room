package com.aivle.mini7.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "member") // 테이블 이름을 명시
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 적용
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 45)
    // @Size(min = 5, max = 45) // user_id는 5~45자
    private String userId;

    @Column(nullable = false, length = 255)
    // @Size(min = 8, max = 255) // 비밀번호는 8~255자
    // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$") // 비밀번호는 최소 하나의 대문자, 소문자, 숫자를 포함해야 함
    private String password;

    @Column(nullable = false, length = 45)
    private String name;

    @Column
    // @Pattern(regexp = "^[MF]$") // M 또는 F 값만 허용
    private String gender;

    @Column
    // @Min(0) // 0 이상의 값만 허용 (음수 나이 방지)
    private Integer age;

    // // Getters and Setters
    // public Long getId() {
    //     return id;
    // }

    // public void setId(Long id) {
    //     this.id = id;
    // }

    // public String getUserId() {
    //     return userId;
    // }

    // public void setUserId(String userId) {
    //     this.userId = userId;
    // }

    // public String getPassword() {
    //     return password;
    // }

    // public void setPassword(String password) {
    //     this.password = password;
    // }

    // public String getName() {
    //     return name;
    // }

    // public void setName(String name) {
    //     this.name = name;
    // }

    // public String getGender() {
    //     return gender;
    // }

    // public void setGender(String gender) {
    //     this.gender = gender;
    // }

    // public Integer getAge() {
    //     return age;
    // }

    // public void setAge(Integer age) {
    //     this.age = age;
    // }
}