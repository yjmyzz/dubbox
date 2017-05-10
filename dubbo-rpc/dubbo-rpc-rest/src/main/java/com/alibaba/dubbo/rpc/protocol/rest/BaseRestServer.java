/**
 * Copyright 1999-2014 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc.protocol.rest;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.jaxrs.FastJsonProvider;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.nio.charset.Charset;

/**
 * @author lishen
 */
public abstract class BaseRestServer implements RestServer {

    public void start(URL url) {
        getDeployment().getMediaTypeMappings().put("json", "application/json");
        getDeployment().getMediaTypeMappings().put("xml", "text/xml");
//        server.getDeployment().getMediaTypeMappings().put("xml", "application/xml");
        getDeployment().getProviderClasses().add(RpcContextFilter.class.getName());
        // TODO users can override this mapper, but we just rely on the current priority strategy of resteasy

        //增加对serialization/charset的解析
        String serialization = url.getParameter("serialization");
        if (StringUtils.isNotEmpty(serialization)) {
            String charset = url.getParameter("charset", "UTF-8");
            if (serialization.equals("fastjson")) {
                getDeployment().setRegisterBuiltin(false);
                getDeployment().getScannedProviderClasses().clear();
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
                fastJsonConfig.setCharset(Charset.forName(charset));
                FastJsonProvider jsonProvider = new FastJsonProvider();
                jsonProvider.setCharset(Charset.forName(charset));
                jsonProvider.setFastJsonConfig(fastJsonConfig);
                ResteasyProviderFactory.getInstance().register(jsonProvider);


                getDeployment().setProviderFactory(ResteasyProviderFactory.getInstance());
            }
        }


        getDeployment().getProviderClasses().add(RpcExceptionMapper.class.getName());

        loadProviders(url.getParameter(Constants.EXTENSION_KEY, ""));

        doStart(url);
    }

    public void deploy(Class resourceDef, Object resourceInstance, String contextPath) {
        if (StringUtils.isEmpty(contextPath)) {
            getDeployment().getRegistry().addResourceFactory(new DubboResourceFactory(resourceInstance, resourceDef));
        } else {
            getDeployment().getRegistry().addResourceFactory(new DubboResourceFactory(resourceInstance, resourceDef), contextPath);
        }
    }

    public void undeploy(Class resourceDef) {
        getDeployment().getRegistry().removeRegistrations(resourceDef);
    }

    protected void loadProviders(String value) {
        for (String clazz : Constants.COMMA_SPLIT_PATTERN.split(value)) {
            if (!StringUtils.isEmpty(clazz)) {
                getDeployment().getProviderClasses().add(clazz.trim());
            }
        }
    }

    protected abstract ResteasyDeployment getDeployment();

    protected abstract void doStart(URL url);
}
