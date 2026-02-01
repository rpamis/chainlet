/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.test;

import java.io.Serializable;

/**
 * DemoUser
 *
 * @author benym
 * @since 2023/5/25 15:49
 */
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
    
    public DemoUser() {
    }
    
    public DemoUser(String name, String pwd, String role) {
        this.name = name;
        this.pwd = pwd;
        this.role = role;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPwd() {
        return pwd;
    }
    
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String pwd;
        private String role;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder pwd(String pwd) {
            this.pwd = pwd;
            return this;
        }
        
        public Builder role(String role) {
            this.role = role;
            return this;
        }
        
        public DemoUser build() {
            return new DemoUser(name, pwd, role);
        }
    }
}
