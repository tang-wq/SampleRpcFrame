package com.twq.rpcFrame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: tangwq
 */
@Data
@Builder
@AllArgsConstructor
public class User implements Serializable {
    String name;
    Integer age;

}
