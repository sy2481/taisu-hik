/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nbhy.config;

import com.nbhy.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Zheng Jie
 * @website https://el-admin.vip
 * @description
 * @date 2020-05-18
 **/
@Data
@Slf4j
@Component
public class RsaProperties {

    public static String publicKey;
    public static String privateKey;

    public RsaProperties() throws Exception{
        RsaUtils.RsaKeyPair rsaKeyPair = RsaUtils.generateKeyPair();
        this.publicKey = rsaKeyPair.getPublicKey();
        this.privateKey = rsaKeyPair.getPrivateKey();
        log.info("生成的私钥为>>>>>>>{}",privateKey);
    }

}