var baseConfig = require('./karma.conf.js')

module.exports = function(config) {
  baseConfig(config);

  config.set({

    autoWatch: false,

    singleRun: true,

    browsers: ['PhantomJS'],

    reporters: ['progress', 'junit', 'coverage'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher', 'karma-junit-reporter',
              'karma-coverage'],

    junitReporter: {
      outputFile: 'target/surefire-reports/TEST-front-end.xml',
      suite: 'unit'
    },

    preprocessors: {
      'src/main/front-end/app/js/**/*.js': ['coverage']
    },

    coverageReporter: {
      type: 'lcovonly',
      dir: 'target/',
      subdir: '.'
    }

  });
};
