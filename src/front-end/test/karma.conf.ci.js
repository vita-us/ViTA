var baseConfig = require('./karma.conf.js')

module.exports = function(config) {
  baseConfig(config);

  config.set({

    autoWatch: false,

    singleRun: true,

    browsers: ['PhantomJS'],
    
    reporters: ['progress', 'junit'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher', 'karma-junit-reporter'],
    
    junitReporter: {
      outputFile: '../../test-results/front-end-test-results.xml',
      suite: 'unit'
    }

  });
};
