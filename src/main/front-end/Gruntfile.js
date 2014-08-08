'use strict';

module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({

    mvnSrcDirectory: '../../',
    appPath: '<%= mvnSrcDirectory %>main/webapp/',
    javascriptPath: '<%= appPath %>js/',
    cssPath: '<%= appPath %>css/',
    fontPath: '<%= appPath %>fonts/',

    pkg: grunt.file.readJSON('package.json'),
    bowerrc: grunt.file.readJSON('.bowerrc'),

    concat: {
      options: {
        // Append a short comment of the path for each concatenated source file
        process: function(src, filepath) {
          return '// Source: ' + filepath + '\n' + src;
        }
      },
      app: {
        src: ['app/js/app.js', 'app/js/*/*.js'],
        dest: '<%= javascriptPath %><%= pkg.name %>.js'
      }
    },
    copy: {
      dependencies: {
        files: [{
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular/',
          src: ['angular.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-route/',
          src: ['angular-route.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/js/',
          src: ['bootstrap.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/css/',
          src: ['bootstrap.css', 'bootstrap-theme.css'],
          dest: '<%= cssPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/bootstrap/dist/fonts/',
          src: ['**'],
          dest: '<%= fontPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/jquery/dist/',
          src: ['jquery.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-resource/',
          src: ['angular-resource.js'],
          dest: '<%= javascriptPath %>'
        }, {
          expand: true,
          cwd: '<%= bowerrc.directory %>/angular-mocks/',
          src: ['angular-mocks.js'],
          dest: '<%= javascriptPath %>'
        }]
      },
      statics: {
        files: [{
          expand: true,
          cwd: 'app/',
          src: ['index.html'],
          dest: '<%= appPath %>'
        }, {
          expand: true,
          cwd: 'app/partials',
          src: ['**'],
          dest: '<%= appPath %>partials/'
        }, {
          expand: true,
          cwd: 'app/test_data',
          src: ['**'],
          dest: '<%= appPath %>test_data/'
        }]
      }
    },
    karma: {
      dev: {
        configFile: '<%= mvnSrcDirectory %>test/front-end/karma.conf.js'
      },
      continuous: {
        configFile: '<%= mvnSrcDirectory %>test/front-end/karma.conf.ci.js'
      }
    },
    less: {
      development: {
        options: {
          strictMath: true
        },
        files: {
          '<%= cssPath %>style.css': 'app/less/style.less'
        }
      }
    }
  });

  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-karma');

  grunt.registerTask('build', ['copy', 'concat', 'less'])
  grunt.registerTask('default', ['build']);
  grunt.registerTask('test', ['karma:dev']);
};