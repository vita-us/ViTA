package de.unistuttgart.vis.vita.analysis.annotations;

public @interface AnalysisModule {
  Class<?>[] dependencies() default {};
}
