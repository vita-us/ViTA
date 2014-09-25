module.exports = function(config) {
  config.set({

    basePath: '../../../',

    files: ['src/main/front-end/app/bower_components/jquery/dist/jquery.js',
        'src/main/front-end/app/bower_components/angular/angular.js',
        'src/main/front-end/app/bower_components/angular-route/angular-route.js',
        'src/main/front-end/app/bower_components/angular-resource/angular-resource.js',
        'src/main/front-end/app/bower_components/angular-mocks/angular-mocks.js',
        'src/main/front-end/app/bower_components/d3/d3.js',
        'src/main/front-end/app/js/app.js',
        'src/main/front-end/app/js/*/*.js',
        'src/test/front-end/unit/**/*.js'],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher']

  });
};
