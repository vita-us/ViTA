exports.config = {

  specs: ['gui/**/*.js'],

  capabilities: {
    'browserName': 'phantomjs'
  },

  baseUrl: 'http://localhost:8080/',

  framework: 'jasmine'
};
