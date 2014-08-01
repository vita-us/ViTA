module.exports = function(config) {
  config.set({

    basePath: '../',

    files: ['app/bower_components/angular/angular.js',
        'app/bower_components/angular-route/angular-route.js', 'app/js/**/*.js',
        'test/unit/**/*.js'],

    autoWatch: true,

    frameworks: ['jasmine'],

    browsers: ['PhantomJS'],

    plugins: ['karma-jasmine', 'karma-phantomjs-launcher']

  });
};
