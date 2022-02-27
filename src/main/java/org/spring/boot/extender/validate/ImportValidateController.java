package org.spring.boot.extender.validate;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.validate.result.ResultConvertor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

public class ImportValidateController implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(ImportValidateController.class);
    private Environment environment;
    private ResourceLoader resourceLoader;
    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableInterfaceValidate.class.getName()));
        String[] basePackages = annAttr.getStringArray("basePackage");
        Class<? extends ResultConvertor> resultRestMessage = annAttr.getClass("result");
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = new String[]{ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setEnvironment(this.environment);
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RestController.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                Class<?> beanNameClass = null;
                try {
                    beanNameClass = Class.forName(beanDefinition.getBeanClassName());
                } catch (ClassNotFoundException e) {
                    logger.error(String.format("Class not found %s", beanDefinition.getBeanClassName()), e);
                    continue;
                }

                Object bean = beanFactory.getBean(beanNameClass);
                if (null != bean) {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(beanNameClass);
                    enhancer.setCallback(new ValidateHandler(bean, resultRestMessage));
                    Object proxy = enhancer.create();
                    String[] beanNames = ((DefaultListableBeanFactory) beanFactory).getBeanNamesForType(beanNameClass);
                    if (beanNames.length == 1) {
                        defaultListableBeanFactory.removeBeanDefinition(beanNames[0]);
                        defaultListableBeanFactory.registerSingleton(beanNames[0], proxy);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("{} proxy by {}", beanNameClass, proxy);
                    }


                }

            }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
