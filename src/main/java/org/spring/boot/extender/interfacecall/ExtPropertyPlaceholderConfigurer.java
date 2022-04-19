package org.spring.boot.extender.interfacecall;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 * 兼容Spring工程 Spring MVC等非SpringBoot工程
 */
public class ExtPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements Ordered {

    Resource[] locations;
    Resource location;

    public ExtPropertyPlaceholderConfigurer() {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Environment environment=beanFactory.getBean(Environment.class);
        super.postProcessBeanFactory(beanFactory);
        this.addResourceEnvironment(location,environment);
        this.addResourceEnvironment(locations,environment);

    }

    protected void addResourceEnvironment(Resource location,Environment environment){
        if(location!=null){
            ConfigurableEnvironment configurableEnvironment=(ConfigurableEnvironment)environment;
            try {
                configurableEnvironment.getPropertySources().addFirst(new ResourcePropertySource(new EncodedResource(location)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    protected void addResourceEnvironment(Resource[] locations,Environment environment){
         if(locations!=null){
             for(Resource location:locations){
                 this.addResourceEnvironment(location,environment);
             }
         }

    }

    @Override
    public void setLocations(Resource[] locations) {
        this.locations=locations;
        super.setLocations(locations);
    }

    @Override
    public void setLocation(Resource location) {
        this.location=location;
        super.setLocation(location);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }


}
