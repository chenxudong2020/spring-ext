package org.spring.boot.extender.interfacecall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;


public class ImportCallBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportCallBeanDefinitionRegistrar.class);
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;






    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        List<Object> listResource=new ArrayList<>();
        List<String> basePackages=new ArrayList<>();
        MultiValueMap<String, Object>  map=importingClassMetadata.getAllAnnotationAttributes(EnableInterfaceCall.class.getName());
        if(map!=null){
            List<Object> list=map.get("locations");
            for(Object locationsObj:list){
                String[] locations=(String[])locationsObj;
                for(Object location:locations){
                    listResource.add(location);
                }
            }
            List<Object> basePackageList=map.get("basePackage");
            for(Object basePackageObject:basePackageList){
                String[] basePackageString=(String[])basePackageObject;
                for(String base:basePackageString){
                    basePackages.add(base);
                }
            }
        }

        ImportCallBeanDefinitionScanner scanner = new ImportCallBeanDefinitionScanner(registry, classLoader,listResource);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(InterfaceClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        String[] packages=basePackages.toArray(new String[basePackages.size()]);
        scanner.doScan(packages);

    }





    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
       this.beanFactory=beanFactory;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }
}
