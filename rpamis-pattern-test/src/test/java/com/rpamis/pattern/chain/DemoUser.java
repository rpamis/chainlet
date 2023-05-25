package com.rpamis.pattern.chain;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DemoUser
 *
 * @author benym
 * @date 2023/5/25 15:49
 */
@Data
@Builder
public class DemoUser implements Serializable {

    private static final long serialVersionUID = 6545355455196518822L;
    /**
     * name
     */
    private String name;
    /**
     * pwd
     */
    private String pwd;
    /**
     * role
     */
    private String role;
}
