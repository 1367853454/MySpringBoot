package com.hello.spring.boot.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class User implements Serializable {

    private String userName;
}
