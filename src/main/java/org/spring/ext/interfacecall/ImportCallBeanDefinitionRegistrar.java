package org.spring.ext.interfacecall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.interfacecall.annotation.InterfaceClient;
import org.spring.ext.interfacecall.exception.InterfaceCallInitException;
import org.spring.ext.interfacecall.handler.CacheHandler;
import org.spring.ext.interfacecall.handler.GetHandler;
import org.spring.ext.interfacecall.handler.PostHandler;
import org.spring.ext.interfacecall.proxy.ProxyController;
import org.spring.ext.interfacecall.proxy.ProxyRegistry;
import org.spring.ext.interfacecall.proxy.ProxyRestTemplate;
import org.spring.ext.interfacecall.proxy.ProxyServlet;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.*;


public class ImportCallBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware,BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportCallBeanDefinitionRegistrar.class);
    private ResourceLoader resourceLoader;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private String[] basePackage;


    public String[] getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String[] basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 默认EnableInterfaceCall注解类所在包下的所有类
     * 或者配置basePackage属性的包下面的所有类
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        List<Object> listResource=new ArrayList<>();
        List<String> basePackages=new ArrayList<>();
        Class<? extends ApiRestTemplate> restTemplateClass= ApiRestTemplate.class;
        MultiValueMap<String, Object>  map=importingClassMetadata.getAllAnnotationAttributes(EnableInterfaceCall.class.getName());
        if(map!=null){
            List<Object> list=map.get("locations");
            if(list!=null){
                for(Object locationsObj:list){
                    String[] locations=(String[])locationsObj;
                    for(Object location:locations){
                        listResource.add(location);
                    }
                }
            }

            List<Object> basePackageList=map.get("basePackage");
            if(basePackageList!=null) {
                for (Object basePackageObject : basePackageList) {
                    String[] basePackageString = (String[]) basePackageObject;
                    for (String base : basePackageString) {
                        basePackages.add(base);
                    }
                }
            }

            List<Object> restTemplateClassList=map.get("restTemplateClass");
            if(basePackageList!=null) {
                for (Object restTemplateClassObj : restTemplateClassList) {
                    restTemplateClass=(Class<? extends ApiRestTemplate>)restTemplateClassObj;
                    break;
                }
            }
        }
        if(basePackage!=null){
            basePackages.addAll(Arrays.asList(basePackage));
        }
        this.registerBean(CallProperties.class,registry);
        this.registerBean(PostHandler.class,registry);
        this.registerBean(GetHandler.class,registry);
        this.registerBean(CacheHandler.class,registry);
        this.registerBean(restTemplateClass,registry);
        this.registerBean(ProxyRestTemplate.class,registry);
        this.registerCallInterfaceHandler(registry,restTemplateClass);


        Resource resource=resourceLoader.getResource("classpath:proxy.properties");
        this.registerProxyServletBean(resource,registry);


        ImportCallBeanDefinitionScanner scanner = new ImportCallBeanDefinitionScanner(registry, classLoader,listResource,beanFactory,restTemplateClass);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(InterfaceClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        String[] packages=basePackages.toArray(new String[basePackages.size()]);
        scanner.doScan(packages);

    }


    private void registerProxyServletBean(Resource resource,BeanDefinitionRegistry registry){

        Map<String, ServletWrappingController> registerHandlers=new HashMap<>();
        Properties prop = new Properties();
        try {
            prop.load(resource.getInputStream());
        } catch (IOException e) {
            throw new InterfaceCallInitException(e);
        }
        Set<String> set=prop.stringPropertyNames();
        for(String name:set) {
            String proxyUrlMapping = name;
            String proxy = prop.getProperty(name);
            if (proxy == null) {
                throw new InterfaceCallInitException("proxy配置不能为空");
            }
            if (proxy.toLowerCase().indexOf("http") == -1 && proxy.toLowerCase().indexOf("https") == -1) {
                throw new InterfaceCallInitException("proxy配置必须以http或者https开头！");
            }

            GenericBeanDefinition proxyControlleGenericBeanDefinition=new GenericBeanDefinition();
            proxyControlleGenericBeanDefinition.setBeanClass(ProxyController.class);
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory.getBean(ProxyRestTemplate.class));
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(proxyUrlMapping);
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(proxy);
            registry.registerBeanDefinition(proxyUrlMapping,proxyControlleGenericBeanDefinition);
            registerHandlers.put(proxyUrlMapping,(ServletWrappingController)beanFactory.getBean(proxyUrlMapping));
        }

        GenericBeanDefinition servletRegistrationBeanGenericBeanDefinition=new GenericBeanDefinition();
        servletRegistrationBeanGenericBeanDefinition.setBeanClass(ProxyRegistry.class);
        servletRegistrationBeanGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(registerHandlers);
        registry.registerBeanDefinition(ProxyRegistry.class.getName(),servletRegistrationBeanGenericBeanDefinition);
    }

    private void registerBean(Class classz,BeanDefinitionRegistry registry){
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(classz);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(classz.getName(),genericBeanDefinition);
    }

    private void registerCallInterfaceHandler(BeanDefinitionRegistry registry,Class restTemplateClass){
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(CallInterfaceHandler.class);
        genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
        genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(restTemplateClass.getName());
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(CallInterfaceHandler.class.getName(),genericBeanDefinition);
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
