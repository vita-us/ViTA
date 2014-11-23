package de.unistuttgart.vis.vita;

import java.util.Collections;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class RandomBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    public RandomBlockJUnit4ClassRunner(Class<?> klass)
    		throws InitializationError {
    	super(klass);
    }

    protected java.util.List<org.junit.runners.model.FrameworkMethod> computeTestMethods() {
    	java.util.List<org.junit.runners.model.FrameworkMethod> methods = super.computeTestMethods();
    	Collections.shuffle(methods);
    	return methods;
    }

}