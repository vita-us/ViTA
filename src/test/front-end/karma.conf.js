module.exports = function(config) {
  config.set({

    basePath: '../../',

    files: ['main/front-end/app/bower_components/angular/angular.js',
        'main/front-end/app/bower_components/angular-route/angular-route.js',
        'main/front-end/app/bower_components/angular-resource/angular-resource.js',
        'main/front-end/app/bower_components/angular-mocks/angular-mocks.js',
        'main/front-end/app/js/**/*.js',
        'test/front-end/unit/**/*.js'],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher']

  });
};
