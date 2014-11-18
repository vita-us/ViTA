module.exports = function(config) {
  config.set({

    basePath: '../../../',

    files: ['src/main/front-end/app/bower_components/jquery/dist/jquery.js',
        'src/main/front-end/app/bower_components/jquery-mousewheel/jquery.mousewheel.js',
        'src/main/front-end/app/bower_components/angular/angular.js',
        'src/main/front-end/app/bower_components/angular-route/angular-route.js',
        'src/main/front-end/app/bower_components/angular-resource/angular-resource.js',
        'src/main/front-end/app/bower_components/angular-mocks/angular-mocks.js',
        'src/main/front-end/app/bower_components/d3/d3.js',
        'src/main/front-end/app/bower_components/d3-cloud/d3.layout.cloud.js',
        'src/main/front-end/app/js/app.js',
        'src/main/front-end/app/js/*/!(mockBackend).js',
        'src/main/front-end/app/templates/*.html',
        'src/test/front-end/unit/**/*.js'],

    preprocessors: {
      'src/main/front-end/app/templates/*.html': ['ng-html2js']
    },

    ngHtml2JsPreprocessor: {
      stripPrefix: 'src/main/front-end/app/',
      moduleName: 'templates'
    },

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher', 'karma-ng-html2js-preprocessor']

  });
};
