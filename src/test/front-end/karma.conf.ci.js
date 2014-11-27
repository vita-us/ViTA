var baseConfig = require('./karma.conf.js')

module.exports = function(config) {
  baseConfig(config);

  config.set({

    autoWatch: false,

    singleRun: true,

    browsers: ['PhantomJS'],

    reporters: ['progress', 'junit', 'coverage'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher', 'karma-junit-reporter',
              'karma-coverage', 'karma-ng-html2js-preprocessor'],

    junitReporter: {
      outputFile: 'target/frontend-test-results/TEST-front-end.xml',
      suite: 'unit'
    },

    preprocessors: {
      'src/main/front-end/app/js/**/*.js': ['coverage'],
      'src/main/front-end/app/templates/*.html': ['ng-html2js']
    },

    coverageReporter: {
      type: 'lcovonly',
      dir: 'target/frontend-test-results/',
      subdir: '.'
    }

  });
};
