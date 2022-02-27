package org.spring.boot.extender.interfacecall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;

@Configuration
public class ImportCallBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportCallBeanDefinitionRegistrar.class);
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;




    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportCallBeanDefinitionScanner scanner = new ImportCallBeanDefinitionScanner(registry, classLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(InterfaceClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
        scanner.doScan(packages.toArray(new String[packages.size()]));
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
